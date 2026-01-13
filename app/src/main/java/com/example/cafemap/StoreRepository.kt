package com.example.cafemap

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import kotlin.math.roundToInt
import com.google.firebase.storage.FirebaseStorage
import android.net.Uri

class StoreRepository {
    private val db = FirebaseFirestore.getInstance()
    private val storeRef = db.collection("stores")
    private val storage = FirebaseStorage.getInstance()

    // 1) 가게 기초 정보 업로드 (새로운 문서 생성)
    fun uploadStoreInfo(store: Store, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        // 💡 로그 추가: 현재 저장하려는 객체에 URL이 들어있는지 확인
        //android.util.Log.d("FirebaseDB", "저장 시도 데이터: $store")
        //android.util.Log.d("FirebaseDB", "이미지 URL 존재 여부: ${store.imageUrl}")
        // id가 비어있다면 자동 생성된 ID를 할당
        val docRef = if (store.id.isEmpty()) storeRef.document() else storeRef.document(store.id)
        docRef.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                // 이미 동일한 ID의 가게가 존재할 경우 예외 처리
                onFailure(Exception("이미 등록된 가게 ID입니다. (ID: ${store.id})"))
            } else {
                // 존재하지 않을 때만 업로드 진행
                val finalStore = store.copy(id = docRef.id)
                docRef.set(finalStore)
                    .addOnSuccessListener { onSuccess() }
                    .addOnFailureListener { onFailure(it) }
            }
        }.addOnFailureListener {
            // 네트워크 오류 등 문서 읽기 자체가 실패한 경우
            onFailure(it)
        }
    }

    // 2) 가게 재고 수정 (특정 필드만 업데이트)
    fun updateStock(storeId: String, newCount: Int) {
        // 로직: 재고 수에 따라 상태(Status)도 함께 변경
        val status = when {
            newCount <= 0 -> StockStatus.OUT_OF_STOCK.name  //상태의 기준은 나중에 수정 바람
            newCount <= 5 -> StockStatus.LOW.name
            newCount <= 20 -> StockStatus.NORMAL.name
            else -> StockStatus.PLENTY.name
        }

        val updates = mapOf(
            "stockCount" to newCount,
            "stockStatus" to status,
            "lastUpdated" to System.currentTimeMillis()
        )

        storeRef.document(storeId).update(updates)
    }
    /*
        // 3) 별점 매기기 (트랜잭션 권장하나, 이해를 돕기 위해 간략화된 업데이트 사용)
        fun updateRating(storeId: String, newRating: Float) {
            storeRef.document(storeId).get().addOnSuccessListener { snapshot ->
                val store = snapshot.toObject<Store>()
                store?.let {
                    val totalScore = (it.avgRating * it.reviewCount) + newRating
                    val newReviewCount = it.reviewCount + 1
                    val newAvg = String.format("%.2f", totalScore / newReviewCount).toFloat()

                    storeRef.document(storeId).update(
                        "avgRating", newAvg,
                        "reviewCount", newReviewCount
                    )
                }
            }
        }

     */

    // 3) 별점 매기기 (트랜잭션 사용)
    fun updateRatingWithTransaction(storeId: String, newRating: Float) {
        val storeDocRef = db.collection("stores").document(storeId)

        db.runTransaction { transaction ->
            // 1. 데이터 읽기 (Read)
            val snapshot = transaction.get(storeDocRef)
            val store = snapshot.toObject(Store::class.java)
                ?: throw Exception("가게 정보가 없습니다.")

            // 2. 새로운 값 계산 (Logic)
            val currentReviewCount = store.reviewCount
            val currentAvgRating = store.avgRating

            val newReviewCount = currentReviewCount + 1
            val totalScore = (currentAvgRating * currentReviewCount) + newRating
            val newAvgRating = ((totalScore / newReviewCount) * 10f).roundToInt() / 10f

            // 3. 데이터 쓰기 (Write)
            transaction.update(storeDocRef, "avgRating", newAvgRating)
            transaction.update(storeDocRef, "reviewCount", newReviewCount)

            // 트랜잭션 결과로 반환하고 싶은 값 (보통 null이나 결과 객체)
            null
        }.addOnSuccessListener {
            println("별점 업데이트 성공!")
        }.addOnFailureListener { e ->
            println("별점 업데이트 실패: $e")
        }
    }

    fun deleteStore(storeId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        if (storeId.isEmpty()) {
            onFailure(Exception("삭제할 가게의 ID가 비어있습니다."))
            return
        }

        storeRef.document(storeId)
            .delete()
            .addOnSuccessListener {
                println("가게 삭제 완료: $storeId")
                onSuccess()
            }
            .addOnFailureListener {
                println("가게 삭제 실패: ${it.message}")
                onFailure(it)
            }
    }

    fun getFilteredStores(
        address: String? = null,
        minRating: Float? = null,
        minStock: Int? = null, // 이 부분이 있는지 확인하고 추가하세요!
        sortBy: String = "lastUpdated",
        onResult: (List<Store>) -> Unit
    ) {
        var query: Query = storeRef

        // 1. 서버 측 필터링 (숫자 데이터 위주)
        if (minRating != null) {
            query = query.whereGreaterThanOrEqualTo("avgRating", minRating)
        }
        // 추가: 재고 필터링 로직
        if (minStock != null) {
            query = query.whereGreaterThanOrEqualTo("stockCount", minStock)
        }

        // 정렬 순서 적용
        query = when (sortBy) {
            "rating" -> query.orderBy("avgRating", Query.Direction.DESCENDING)
            "stock" -> query.orderBy("stockCount", Query.Direction.DESCENDING)
            else -> query.orderBy("lastUpdated", Query.Direction.DESCENDING)
        }

        query.get()
            .addOnSuccessListener { snapshot ->
                val stores = snapshot.toObjects(Store::class.java)

                // 2. 클라이언트 측 필터링 (주소 "포함" 검색)
                val finalResult = if (!address.isNullOrEmpty()) {
                    // address가 포함된(contains) 데이터만 필터링
                    stores.filter { it.address.contains(address, ignoreCase = true) }
                } else {
                    stores
                }

                onResult(finalResult)
            }
            .addOnFailureListener { onResult(emptyList()) }
    }

    fun uploadImage(storeId: String, imageUri: Uri, onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit) {
        //android.util.Log.d("FirebaseDB", "RepositoryFunction1. 입력된 URi: $imageUri")
        val storageRef = storage.reference.child("store_images/$storeId.jpg")
        //android.util.Log.d("FirebaseDB", "RepositoryFunction2. 두 번째 URi: $storageRef")

        storageRef.putFile(imageUri)
            .addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    onSuccess(uri.toString()) // 성공 시 이미지 URL 전달
                }.addOnFailureListener { onFailure(it) }
            }
            .addOnFailureListener { onFailure(it) }
    }

    // 실시간으로 가게 정보를 감시하는 리스너 추가
    fun listenToStores(onResult: (List<Store>) -> Unit): ListenerRegistration {
        return storeRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                onResult(emptyList())
                return@addSnapshotListener
            }
            val stores = snapshot?.toObjects(Store::class.java) ?: emptyList()
            onResult(stores)
        }
    }


}