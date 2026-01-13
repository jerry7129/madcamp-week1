package com.example.cafemap.fragments

import android.location.Geocoder
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cafemap.R
import com.example.cafemap.Store
import com.example.cafemap.StoreAdapter
import com.example.cafemap.StoreRepository
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.Locale
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.storage.FirebaseStorage
import android.widget.ImageView
import android.content.Intent
import java.io.File
import java.io.FileOutputStream

class StoreListFragment : Fragment(R.layout.fragment_store_list) {
    private val repository = StoreRepository()
    private lateinit var adapter: StoreAdapter

    private var isFabOpen = false
    private lateinit var fabOpen: Animation
    private lateinit var fabClose: Animation

    private var selectedImageUri: Uri? = null // 선택한 이미지 URI 저장용
    private var ivPreview: ImageView? = null // 다이얼로그 내 미리보기용

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            // 선택하자마자 즉시 캐시 파일로 복사하여 영구적인 내부 URI를 생성함
            val internalUri = copyUriToInternalStorage(it)
            if (internalUri != null) {
                selectedImageUri = internalUri
                ivPreview?.apply {
                    visibility = View.VISIBLE
                    setImageURI(internalUri)
                }
            } else {
                Toast.makeText(requireContext(), "이미지 로드 실패", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun copyUriToInternalStorage(uri: Uri): Uri? {
        return try {
            val inputStream = requireContext().contentResolver.openInputStream(uri)
            // 파일명이 중복되지 않도록 타임스탬프 사용 추천
            val tempFile = File(requireContext().cacheDir, "temp_image_${System.currentTimeMillis()}.jpg")
            val outputStream = FileOutputStream(tempFile)

            inputStream?.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
            Uri.fromFile(tempFile)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fabOpen = AnimationUtils.loadAnimation(requireContext(), R.anim.fab_open)
        fabClose = AnimationUtils.loadAnimation(requireContext(), R.anim.fab_close)

        // 뷰 연결
        val etAddress = view.findViewById<EditText>(R.id.etFilterAddress)
        val etRating = view.findViewById<EditText>(R.id.etMinRating)
        val spinnerSort = view.findViewById<Spinner>(R.id.spinnerSort)
        val btnApply = view.findViewById<Button>(R.id.btnApplyFilter)
        val recyclerView = view.findViewById<RecyclerView>(R.id.rvStoreList)

        val fabMain = view.findViewById<FloatingActionButton>(R.id.fabMain)
        val fabAddStore = view.findViewById<FloatingActionButton>(R.id.fabAddStore)
        val fabUpdateStock = view.findViewById<FloatingActionButton>(R.id.fabUpdateStock)
        val fabDeleteStore = view.findViewById<FloatingActionButton>(R.id.fabDeleteStore)

        fabMain.setOnClickListener {
            toggleFab(fabMain, fabAddStore, fabUpdateStock, fabDeleteStore)
        }



        // 서브 버튼 클릭 리스너 (팝업은 다음 단계에서 구현)
        fabAddStore.setOnClickListener {
            Toast.makeText(requireContext(), "가게 추가 팝업 열기", Toast.LENGTH_SHORT).show() //필요?
            showAddStoreDialog() // 함수 호출
            if (isFabOpen) toggleFab(fabMain, fabAddStore, fabUpdateStock, fabDeleteStore)
        }

        fabDeleteStore.setOnClickListener {
            Toast.makeText(requireContext(), "가게 삭제 팝업 열기", Toast.LENGTH_SHORT).show() //필요?
            showDeleteStoreDialog() // 함수 호출
            if (isFabOpen) toggleFab(fabMain, fabAddStore, fabUpdateStock, fabDeleteStore)
        }

        fabUpdateStock.setOnClickListener {
            Toast.makeText(requireContext(), "재고 수정 팝업 열기", Toast.LENGTH_SHORT).show() //필요?
            showUpdateStockDialog() // 함수 호출
            if (isFabOpen) toggleFab(fabMain, fabAddStore, fabUpdateStock, fabDeleteStore)
        }

        // 리사이클러뷰 초기화
        adapter = StoreAdapter(emptyList()) { selectedStore ->
            showRatingDialog(selectedStore) // 아이템 클릭 시 별점 팝업 호출
        }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // 처음 화면 진입 시 전체 리스트 로드
        loadStores()

        // 버튼 클릭 시 필터 적용
        btnApply.setOnClickListener {
            val address = etAddress.text.toString().takeIf { it.isNotEmpty() }
            val minRating = etRating.text.toString().toFloatOrNull()

            // Spinner 선택값에 따른 sortBy 변환
            val sortBy = when (spinnerSort.selectedItemPosition) {
                1 -> "rating"
                2 -> "stock"
                else -> "lastUpdated"
            }

            loadStores(address, minRating, sortBy)
        }
    }


    private fun showRatingDialog(store: Store) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_rating, null)
        val tvName = dialogView.findViewById<TextView>(R.id.tvDialogStoreName)
        val ratingBar = dialogView.findViewById<RatingBar>(R.id.ratingBar)

        tvName.text = store.name

        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setPositiveButton("별점 주기") { _, _ ->
                val newRating = ratingBar.rating // 0.5 단위로 선택된 값
                if (newRating > 0) {
                    repository.updateRatingWithTransaction(store.id, newRating)
                    Toast.makeText(requireContext(), "${newRating}점! 소중한 리뷰 감사합니다.", Toast.LENGTH_SHORT).show()

                    // 데이터 업데이트 반영을 위해 잠시 후 새로고침
                    view?.postDelayed({ loadStores() }, 1000)
                } else {
                    Toast.makeText(requireContext(), "최소 0.5점 이상 선택해주세요.", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("취소", null)
            .show()
    }

    private fun showAddStoreDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_store, null)

        val etName = dialogView.findViewById<EditText>(R.id.etName)
        val etLink = dialogView.findViewById<EditText>(R.id.etLink)
        val etDesc = dialogView.findViewById<EditText>(R.id.etDesc)
        val etAddress = dialogView.findViewById<EditText>(R.id.etAddress)
        val btnSearchAddress = dialogView.findViewById<Button>(R.id.btnSearchAddress)
        val etLat = dialogView.findViewById<EditText>(R.id.etDialogLatitude)
        val etLng = dialogView.findViewById<EditText>(R.id.etDialogLongtitude)
        val btnSelectImage = dialogView.findViewById<Button>(R.id.btnSelectImage)

        // 중요: 현재 다이얼로그의 ivPreview를 전역 변수에 할당
        ivPreview = dialogView.findViewById<ImageView>(R.id.ivDialogPreview)

        // 이전에 선택했던 이미지 정보 초기화 (새 다이얼로그니까)
        selectedImageUri = null

        btnSelectImage.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        btnSearchAddress.setOnClickListener {
            val address = etAddress.text.toString()
            if (address.isNotEmpty()) {
                try {
                    val geocoder = Geocoder(requireContext(), Locale.KOREA)
                    val addresses = geocoder.getFromLocationName(address, 1)
                    if (!addresses.isNullOrEmpty()) {
                        val location = addresses[0]
                        etLat.setText(location.latitude.toString())
                        etLng.setText(location.longitude.toString())
                    }
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "좌표 찾기 실패", Toast.LENGTH_SHORT).show()
                }
            }
        }

        val builder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setPositiveButton("등록", null) // 나중에 아래에서 listener를 다시 설정함 (다이얼로그 닫힘 방지)
            .setNegativeButton("취소", null)

        val alertDialog = builder.create()
        alertDialog.show()

        // 등록 버튼 클릭 시 바로 닫히지 않게 커스텀 리스너 설정
        alertDialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            //android.util.Log.d("FirebaseDB", "1. 등록 버튼 클릭됨") // <- 추가
            val name = etName.text.toString().trim()
            val address = etAddress.text.toString().trim()
            val link = etLink.text.toString().trim()
            val desc = etDesc.text.toString().trim()
            val lat = etLat.text.toString().toDoubleOrNull() ?: 0.0
            val long = etLng.text.toString().toDoubleOrNull() ?: 0.0

            if (name.isEmpty()) {
                //android.util.Log.d("FirebaseDB", "2. 이름이 비어있음") // <- 추가
                etName.error = "가게 이름을 입력하세요"
                return@setOnClickListener
            }

            //android.util.Log.d("FirebaseDB", "3. 이미지 URI 상태: $selectedImageUri") // <- 추가

            // 버튼 비활성화 (중복 클릭 방지)
            alertDialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE).isEnabled = false
            Toast.makeText(requireContext(), "등록 중입니다...", Toast.LENGTH_SHORT).show()

            if (selectedImageUri != null) {
                val finalUploadUri = getFileUriFromGalleryUri(selectedImageUri!!)
                //android.util.Log.d("FirebaseDB", "4. 변환된 URI: $finalUploadUri") // <- 추가
                if (finalUploadUri != null) {
                    repository.uploadImage(
                        storeId = name,
                        imageUri = selectedImageUri!!,  //finalUploadUri로 수정?
                        onSuccess = { imageUrl ->
                            //android.util.Log.d("FirebaseDB", "5. 업로드 성공 URL: $imageUrl")
                            val newStore = Store(
                                id = name, name = name, address = address,
                                mapLink = link, description = desc,
                                latitude = lat, longitude = long,
                                imageUrl = imageUrl
                            )
                            //android.util.Log.d("FirebaseDB", "5. 객체에 담긴 URL: ${newStore.imageUrl}")
                            repository.uploadStoreInfo(
                                store = newStore,
                                onSuccess = {
                                    Toast.makeText(requireContext(), "등록 성공!", Toast.LENGTH_SHORT).show()
                                    loadStores()
                                    alertDialog.dismiss() // 성공했을 때만 닫기
                                },
                                onFailure = { e ->
                                    alertDialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE).isEnabled = true
                                    Toast.makeText(requireContext(), "정보 등록 실패: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                            )
                        },
                        onFailure = { e ->
                            alertDialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE).isEnabled = true
                            Toast.makeText(requireContext(), "이미지 업로드 실패: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    )
                } else {
                    Toast.makeText(requireContext(), "파일 처리 오류", Toast.LENGTH_SHORT).show()
                    alertDialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE).isEnabled = true
                }
            } else {
                val newStore = Store(
                    id = name, name = name, address = address,
                    mapLink = link, description = desc,
                    latitude = lat, longitude = long, imageUrl = ""
                )
                repository.uploadStoreInfo(
                    store = newStore,
                    onSuccess = {
                        Toast.makeText(requireContext(), "등록 성공!", Toast.LENGTH_SHORT).show()
                        loadStores()
                        alertDialog.dismiss()
                    },
                    onFailure = { e ->
                        alertDialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE).isEnabled = true
                        Toast.makeText(requireContext(), "등록 실패: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }

    private fun showDeleteStoreDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_delete_store, null)
        val builder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setPositiveButton("삭제") { _, _ ->
                val delId = dialogView.findViewById<EditText>(R.id.etDeleteStoreId).text.toString()

                if (delId.isNotEmpty()) {
                    repository.deleteStore(
                        storeId = delId,
                        onSuccess = {
                            Toast.makeText(requireContext(), "삭제 성공!", Toast.LENGTH_SHORT).show()
                            loadStores() // 성공 시 목록 새로고침
                        },
                        onFailure = { exception ->
                            Toast.makeText(requireContext(), "실패: ${exception.message}", Toast.LENGTH_SHORT).show()
                        }
                    )

                }
            }
            .setNegativeButton("취소", null)
        builder.show()
    }

    private fun showUpdateStockDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_update_stock, null)
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setPositiveButton("수정") { _, _ ->
                val id = dialogView.findViewById<EditText>(R.id.etTargetId).text.toString()
                val count = dialogView.findViewById<EditText>(R.id.etNewCount).text.toString().toIntOrNull()

                if (id.isNotEmpty() && count != null) {
                    repository.updateStock(id, count)
                    Toast.makeText(requireContext(), "재고 수정 요청됨", Toast.LENGTH_SHORT).show()
                    // Firestore 반영 시간에 따라 약간 뒤에 리스트 갱신
                    view?.postDelayed({ loadStores() }, 1000)
                }
            }
            .setNegativeButton("취소", null)
            .show()
    }

    private fun toggleFab(main: FloatingActionButton, add: FloatingActionButton, stock: FloatingActionButton, delete: FloatingActionButton) {
        if (isFabOpen) {
            // 메뉴 닫기
            main.setImageResource(android.R.drawable.ic_input_add)
            add.startAnimation(fabClose)
            stock.startAnimation(fabClose)
            delete.startAnimation(fabClose)
            add.visibility = View.GONE
            stock.visibility = View.GONE
            delete.visibility = View.GONE
            add.isClickable = false
            stock.isClickable = false
            delete.isClickable = false
            isFabOpen = false
        } else {
            // 메뉴 열기
            main.setImageResource(android.R.drawable.ic_menu_close_clear_cancel)
            add.startAnimation(fabOpen)
            stock.startAnimation(fabOpen)
            delete.startAnimation(fabOpen)
            add.visibility = View.VISIBLE
            stock.visibility = View.VISIBLE
            delete.visibility = View.VISIBLE
            add.isClickable = true
            stock.isClickable = true
            delete.isClickable = true
            isFabOpen = true
        }
    }
    private fun getFileUriFromGalleryUri(uri: Uri): Uri? {
        return try {
            val inputStream = requireContext().contentResolver.openInputStream(uri)
            // 앱의 캐시 디렉토리에 temp_upload.jpg라는 이름으로 저장
            val tempFile = File(requireContext().cacheDir, "temp_upload.jpg")
            val outputStream = FileOutputStream(tempFile)

            inputStream?.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
            Uri.fromFile(tempFile)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }


    private fun loadStores(address: String? = null, minRating: Float? = null, sortBy: String = "lastUpdated") {
        // 매개변수 이름을 직접 지정하여 호출 (Named Arguments)
        repository.getFilteredStores(
            address = address,
            minRating = minRating,
            minStock = null, // 이제 Repository에 추가했으므로 에러가 나지 않습니다.
            sortBy = sortBy
        ) { stores ->
            adapter.updateData(stores)
        }
    }
}
