package com.example.cafemap.fragments

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cafemap.R
import com.example.cafemap.StoreAdapter
import com.example.cafemap.StoreRepository
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import com.google.android.material.floatingactionbutton.FloatingActionButton
import android.widget.TextView
import android.widget.RatingBar
import com.example.cafemap.Store           // 'Store' 클래스 인식용

class StoreListFragment : Fragment(R.layout.fragment_store_list) {
    private val repository = StoreRepository()
    private lateinit var adapter: StoreAdapter

    private var isFabOpen = false
    private lateinit var fabOpen: Animation
    private lateinit var fabClose: Animation

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fabOpen = AnimationUtils.loadAnimation(requireContext(), R.anim.fab_open)
        fabClose = AnimationUtils.loadAnimation(requireContext(), R.anim.fab_close)

        // 뷰 연결
        val etRegion = view.findViewById<EditText>(R.id.etFilterRegion)
        val etRating = view.findViewById<EditText>(R.id.etMinRating)
        val spinnerSort = view.findViewById<Spinner>(R.id.spinnerSort)
        val btnApply = view.findViewById<Button>(R.id.btnApplyFilter)
        val recyclerView = view.findViewById<RecyclerView>(R.id.rvStoreList)

        val fabMain = view.findViewById<FloatingActionButton>(R.id.fabMain)
        val fabAddStore = view.findViewById<FloatingActionButton>(R.id.fabAddStore)
        val fabUpdateStock = view.findViewById<FloatingActionButton>(R.id.fabUpdateStock)

        fabMain.setOnClickListener {
            toggleFab(fabMain, fabAddStore, fabUpdateStock)
        }



        // 서브 버튼 클릭 리스너 (팝업은 다음 단계에서 구현)
        fabAddStore.setOnClickListener {
            Toast.makeText(requireContext(), "가게 추가 팝업 열기", Toast.LENGTH_SHORT).show() //필요?
            showAddStoreDialog() // 함수 호출
            if (isFabOpen) toggleFab(fabMain, fabAddStore, fabUpdateStock)
        }

        fabUpdateStock.setOnClickListener {
            Toast.makeText(requireContext(), "재고 수정 팝업 열기", Toast.LENGTH_SHORT).show() //필요?
            showUpdateStockDialog() // 함수 호출
            if (isFabOpen) toggleFab(fabMain, fabAddStore, fabUpdateStock)
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
            val region = etRegion.text.toString().takeIf { it.isNotEmpty() }
            val minRating = etRating.text.toString().toFloatOrNull()

            // Spinner 선택값에 따른 sortBy 변환
            val sortBy = when (spinnerSort.selectedItemPosition) {
                1 -> "rating"
                2 -> "stock"
                else -> "lastUpdated"
            }

            loadStores(region, minRating, sortBy)
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
        val builder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setPositiveButton("등록") { _, _ ->
                val name = dialogView.findViewById<EditText>(R.id.etName).text.toString()
                val region = dialogView.findViewById<EditText>(R.id.etRegion).text.toString()
                val link = dialogView.findViewById<EditText>(R.id.etLink).text.toString()
                val desc = dialogView.findViewById<EditText>(R.id.etDesc).text.toString()
                val lat = dialogView.findViewById<EditText>(R.id.etDialogLatitude).text.toString().toDoubleOrNull() ?: 0.0
                val long = dialogView.findViewById<EditText>(R.id.etDialogLongtitude).text.toString().toDoubleOrNull() ?: 0.0

                if (name.isNotEmpty()) {
                    val newStore = Store(id = name, name = name, region = region, mapLink = link, description = desc, latitude = lat, longitude = long)
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

    private fun toggleFab(main: FloatingActionButton, add: FloatingActionButton, stock: FloatingActionButton) {
        if (isFabOpen) {
            // 메뉴 닫기
            main.setImageResource(android.R.drawable.ic_input_add)
            add.startAnimation(fabClose)
            stock.startAnimation(fabClose)
            add.visibility = View.GONE
            stock.visibility = View.GONE
            add.isClickable = false
            stock.isClickable = false
            isFabOpen = false
        } else {
            // 메뉴 열기
            main.setImageResource(android.R.drawable.ic_menu_close_clear_cancel)
            add.startAnimation(fabOpen)
            stock.startAnimation(fabOpen)
            add.visibility = View.VISIBLE
            stock.visibility = View.VISIBLE
            add.isClickable = true
            stock.isClickable = true
            isFabOpen = true
        }
    }


    private fun loadStores(region: String? = null, minRating: Float? = null, sortBy: String = "lastUpdated") {
        // 매개변수 이름을 직접 지정하여 호출 (Named Arguments)
        repository.getFilteredStores(
            region = region,
            minRating = minRating,
            minStock = null, // 이제 Repository에 추가했으므로 에러가 나지 않습니다.
            sortBy = sortBy
        ) { stores ->
            adapter.updateData(stores)
        }
    }
}
