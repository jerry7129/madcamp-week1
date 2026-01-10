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

class StoreListFragment : Fragment(R.layout.fragment_store_list) {
    private val repository = StoreRepository()
    private lateinit var adapter: StoreAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 뷰 연결
        val etRegion = view.findViewById<EditText>(R.id.etFilterRegion)
        val etRating = view.findViewById<EditText>(R.id.etMinRating)
        val spinnerSort = view.findViewById<Spinner>(R.id.spinnerSort)
        val btnApply = view.findViewById<Button>(R.id.btnApplyFilter)
        val recyclerView = view.findViewById<RecyclerView>(R.id.rvStoreList)

        // 리사이클러뷰 초기화
        adapter = StoreAdapter(emptyList())
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