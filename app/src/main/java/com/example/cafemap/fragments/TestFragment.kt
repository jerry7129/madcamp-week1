package com.example.cafemap.fragments

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.cafemap.R
import com.example.cafemap.Store
import com.example.cafemap.StoreRepository

class TestFragment : Fragment(R.layout.fragment_test) {
    private val repository = StoreRepository()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 뷰 연결
        val etStoreName = view.findViewById<EditText>(R.id.etStoreName)
        val etStoreMapLink = view.findViewById<EditText>(R.id.etStoreMapLink)
        val etStoreRegion = view.findViewById<EditText>(R.id.etStoreRegion)
        val etStoreDescription = view.findViewById<EditText>(R.id.etStoreDescription)
        val btnAddStore = view.findViewById<Button>(R.id.btnAddStore)

        val etStoreIdForStock = view.findViewById<EditText>(R.id.etStoreIdForStock)
        val etStockCount = view.findViewById<EditText>(R.id.etStockCount)
        val btnUpdateStock = view.findViewById<Button>(R.id.btnUpdateStock)

        val etStoreIdForRating = view.findViewById<EditText>(R.id.etStoreIdForRating)
        val etRating = view.findViewById<EditText>(R.id.etRating)
        val btnRateStore = view.findViewById<Button>(R.id.btnRateStore)

        // 1. 가게 추가 테스트
        btnAddStore.setOnClickListener {
            val name = etStoreName.text.toString()
            val link = etStoreMapLink.text.toString()
            val region = etStoreRegion.text.toString()
            val description = etStoreDescription.text.toString()
            if (name.isNotEmpty()) {
                val newStore = Store(
                    id = name,
                    name = name,
                    region = region,
                    mapLink = link,
                    description = description
                ) // 기본값 예시
                repository.uploadStoreInfo(newStore, {
                    Toast.makeText(requireContext(), "가게 추가 성공!", Toast.LENGTH_SHORT).show()
                    etStoreName.text.clear()
                    etStoreMapLink.text.clear()
                    etStoreRegion.text.clear()
                    etStoreDescription.text.clear()

                }, {
                    Toast.makeText(requireContext(), "에러: ${it.message}", Toast.LENGTH_SHORT).show()
                })
            }
        }

        // 2. 재고 수정 테스트
        btnUpdateStock.setOnClickListener {
            val id = etStoreIdForStock.text.toString()
            val count = etStockCount.text.toString().toIntOrNull()
            if (id.isNotEmpty() && count != null) {
                repository.updateStock(id, count)
                Toast.makeText(requireContext(), "재고 수정 요청됨", Toast.LENGTH_SHORT).show()
                etStoreIdForStock.text.clear()
                etStockCount.text.clear()
            }
        }

        // 3. 별점 매기기 테스트 (트랜잭션)
        btnRateStore.setOnClickListener {
            val id = etStoreIdForRating.text.toString()
            val rating = etRating.text.toString().toFloatOrNull()
            if (id.isNotEmpty() && rating != null) {
                repository.updateRatingWithTransaction(id, rating)
                Toast.makeText(requireContext(), "별점 업데이트 시도 중...", Toast.LENGTH_SHORT).show()
                etStoreIdForRating.text.clear()
                etRating.text.clear()
            }
        }

    }
}