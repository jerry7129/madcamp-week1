package com.example.cafemap.fragments

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.DialogFragment
import com.example.cafemap.R
import com.bumptech.glide.Glide

class StoreDetailFragment : DialogFragment() {

    companion object {
        fun newInstance(
            name: String,
            description: String,
            stockStatus: String,
            stockCount: Int,
            lastUpdated: Long,
            mapLink: String,
            imageUrl: String // ✨ 추가
        ): StoreDetailFragment {
            val fragment = StoreDetailFragment()
            val args = Bundle()
            args.putString("name", name)
            args.putString("description", description)
            args.putString("stockStatus", stockStatus)
            args.putInt("stockCount", stockCount)
            args.putLong("lastUpdated", lastUpdated)
            args.putString("mapLink", mapLink)
            args.putString("imageUrl", imageUrl) // ✨ 추가
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 배경을 투명하게 설정하여 CardView의 둥근 모서리가 잘 보이도록 함
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        return inflater.inflate(R.layout.fragment_store_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvName = view.findViewById<TextView>(R.id.tvDetailName)
        val tvDescription = view.findViewById<TextView>(R.id.tvDetailDescription)
        val cvStockStatusBadge = view.findViewById<CardView>(R.id.cvStockStatusBadge)
        val tvStockStatus = view.findViewById<TextView>(R.id.tvDetailStockStatus)
        val tvStockCount = view.findViewById<TextView>(R.id.tvDetailStockCount)
        val tvLastUpdated = view.findViewById<TextView>(R.id.tvDetailLastUpdated)
        val btnClose = view.findViewById<ImageView>(R.id.btnCloseDetail)
        val btnNaverPlace = view.findViewById<Button>(R.id.btnNaverPlace)
        val ivStoreImage = view.findViewById<ImageView>(R.id.ivStoreImage)



        arguments?.let {
            val name = it.getString("name") ?: ""
            val description = it.getString("description") ?: ""
            val stockStatus = it.getString("stockStatus") ?: ""
            val stockCount = it.getInt("stockCount")
            val lastUpdated = it.getLong("lastUpdated")
            val mapLink = it.getString("mapLink") ?: ""
            val imageUrl = it.getString("imageUrl") ?: ""

            if (imageUrl.isNotEmpty()) {
                Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_marker_test) // 로딩 중 이미지
                    .into(ivStoreImage) // 이미지뷰에 표시
            } else {
                ivStoreImage.visibility = View.GONE // 이미지가 없으면 숨김
            }

            tvName.text = name
            tvName.isSelected = true
            // description box에서 큰 따옴표 제거
            tvDescription.text = description

            // 상태 텍스트 설정
            tvStockStatus.text = getStockStatusText(stockStatus)
            // 상태에 따른 배지(CardView) 색상 및 텍스트 색상 설정
            updateStockBadgeStyle(cvStockStatusBadge, tvStockStatus, stockStatus)

            tvStockCount.text = "• ${stockCount}개 남음"
            tvLastUpdated.text = "● ${formatTime(lastUpdated)} 업데이트"

            btnNaverPlace.setOnClickListener {
                if (mapLink.isNotEmpty()) {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(mapLink))
                    startActivity(intent)
                }
            }
        }

        btnClose.setOnClickListener {
            dismiss()
        }
    }

    private fun updateStockBadgeStyle(badge: CardView, textView: TextView, status: String) {
        val (bgColor, textColor) = when (status) {
            "OUT_OF_STOCK" -> "#E0E0E0" to "#757575"  // 품절: 회색
            "LOW" -> "#FFEBEB" to "#FF4D4D"           // 부족: 연한 빨강
            "NORMAL" -> "#FFF3E0" to "#FF9800"        // 보통: 연한 주황
            "PLENTY" -> "#E8F5E9" to "#4CAF50"        // 여유: 연한 초록
            else -> "#F5F5F5" to "#9E9E9E"
        }

        badge.setCardBackgroundColor(Color.parseColor(bgColor))
        textView.setTextColor(Color.parseColor(textColor))
    }

    private fun getStockStatusText(status: String): String {
        return when (status) {
            "OUT_OF_STOCK" -> "품절"
            "LOW" -> "부족"
            "NORMAL" -> "보통"
            "PLENTY" -> "여유"
            else -> status
        }
    }

    private fun formatTime(timestamp: Long): String {
        val diff = System.currentTimeMillis() - timestamp
        val minutes = diff / (1000 * 60)
        val hours = minutes / 60
        val days = hours / 24

        return when {
            minutes < 1 -> "방금 전"
            minutes < 60 -> "약 ${minutes}분 전"
            hours < 24 -> "약 ${hours}시간 전"
            else -> "${days}일 전"
        }
    }

    override fun onStart() {
        super.onStart()
        // 다이얼로그의 너비를 화면에 맞게 조절
        dialog?.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.95).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }
}
