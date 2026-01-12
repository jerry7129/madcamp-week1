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
import com.example.cafemap.MainActivity
import com.example.cafemap.R
import com.example.cafemap.Store
import com.example.cafemap.StoreAdapter
import com.example.cafemap.StoreRepository
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.Locale

class StoreListFragment : Fragment(R.layout.fragment_store_list) {
    private val repository = StoreRepository()
    private lateinit var adapter: StoreAdapter

    private var isFabOpen = false
    private lateinit var fabOpen: Animation
    private lateinit var fabClose: Animation

    // ✨ 클래스 멤버 변수로 선언하여 onResume 및 onHiddenChanged에서 접근 가능하게 함
    private var fabMain: FloatingActionButton? = null

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

        fabMain = view.findViewById<FloatingActionButton>(R.id.fabMain)
        val fabAddStore = view.findViewById<FloatingActionButton>(R.id.fabAddStore)
        val fabUpdateStock = view.findViewById<FloatingActionButton>(R.id.fabUpdateStock)
        val fabDeleteStore = view.findViewById<FloatingActionButton>(R.id.fabDeleteStore)

        // ✨ 로그인 여부에 따라 관리 메뉴(FAB) 노출 제어
        updateFabVisibility()

        fabMain?.setOnClickListener {
            fabMain?.let { toggleFab(it, fabAddStore, fabUpdateStock, fabDeleteStore) }
        }



        // 서브 버튼 클릭 리스너 (팝업은 다음 단계에서 구현)
        fabAddStore.setOnClickListener {
            Toast.makeText(requireContext(), "가게 추가 팝업 열기", Toast.LENGTH_SHORT).show() //필요?
            showAddStoreDialog() // 함수 호출
            fabMain?.let { if (isFabOpen) toggleFab(it, fabAddStore, fabUpdateStock, fabDeleteStore) }
        }

        fabDeleteStore.setOnClickListener {
            Toast.makeText(requireContext(), "가게 삭제 팝업 열기", Toast.LENGTH_SHORT).show() //필요?
            showDeleteStoreDialog() // 함수 호출
            fabMain?.let { if (isFabOpen) toggleFab(it, fabAddStore, fabUpdateStock, fabDeleteStore) }
        }

        fabUpdateStock.setOnClickListener {
            Toast.makeText(requireContext(), "재고 수정 팝업 열기", Toast.LENGTH_SHORT).show() //필요?
            showUpdateStockDialog() // 함수 호출
            fabMain?.let { if (isFabOpen) toggleFab(it, fabAddStore, fabUpdateStock, fabDeleteStore) }
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

    // ✨ 프래그먼트가 show/hide 될 때 호출되는 함수 (MainActivity의 show/hide 대응)
    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            // 프래그먼트가 다시 나타날 때(show) 버튼 가시성 업데이트
            updateFabVisibility()
        }
    }

    // ✨ 화면에 다시 돌아올 때마다(탭 전환 등) 로그인 상태를 체크하여 가시성 업데이트
    override fun onResume() {
        super.onResume()
        updateFabVisibility()
    }

    private fun updateFabVisibility() {
        if (MainActivity.isLoggedIn) {
            fabMain?.visibility = View.VISIBLE
        } else {
            fabMain?.visibility = View.GONE
            // 로그아웃 상태에서 메뉴가 열려 있었다면 닫기 처리
            isFabOpen = false
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
        //val etRegion = dialogView.findViewById<EditText>(R.id.etRegion)
        val etLink = dialogView.findViewById<EditText>(R.id.etLink)
        val etDesc = dialogView.findViewById<EditText>(R.id.etDesc)
        val etAddress = dialogView.findViewById<EditText>(R.id.etAddress)
        val btnSearchAddress = dialogView.findViewById<Button>(R.id.btnSearchAddress)
        val etLat = dialogView.findViewById<EditText>(R.id.etDialogLatitude)
        val etLng = dialogView.findViewById<EditText>(R.id.etDialogLongtitude)

        // 주소 검색 버튼 클릭 시 위도/경도 자동 입력 로직
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
                        Toast.makeText(requireContext(), "좌표를 성공적으로 찾았습니다.", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "해당 주소의 좌표를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Geocoder 서비스 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "주소를 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
        }

        val builder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setPositiveButton("등록") { _, _ ->
                val name = etName.text.toString()
                //val region = etRegion.text.toString()
                val address = etAddress.text.toString()
                val link = etLink.text.toString()
                val desc = etDesc.text.toString()
                val lat = etLat.text.toString().toDoubleOrNull() ?: 0.0
                val long = etLng.text.toString().toDoubleOrNull() ?: 0.0

                if (name.isNotEmpty()) {
                    val newStore = Store(id = name, name = name, address = address, mapLink = link, description = desc, latitude = lat, longitude = long)
                    repository.uploadStoreInfo(newStore, {
                        Toast.makeText(requireContext(), "등록 성공!", Toast.LENGTH_SHORT).show()
                        loadStores() // 목록 새로고침
                    }, {
                        Toast.makeText(requireContext(), "실패: ${it.message}", Toast.LENGTH_SHORT).show()
                    })
                }
            }
            .setNegativeButton("취소", null)
        builder.show()
    }

    private fun showDeleteStoreDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_delete_store, null)
        val builder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setPositiveButton("삭제") { _, _ ->
                val delId = dialogView.findViewById<EditText>(R.id.etDeleteStoreId).text.toString()

                if (delId.isNotEmpty()) {
                    repository.deleteStore(storeId = delId, onSuccess = {
                        Toast.makeText(requireContext(), "삭제 성공!", Toast.LENGTH_SHORT).show()
                        loadStores()
                    }, onFailure = {
                        Toast.makeText(requireContext(), "실패: ${it.message}", Toast.LENGTH_SHORT).show()
                    })
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
