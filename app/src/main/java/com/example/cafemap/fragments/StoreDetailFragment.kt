package com.example.cafemap.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.example.cafemap.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class StoreDetailFragment : DialogFragment() {

    companion object {
        fun newInstance(
            name: String,
            description: String,
            stockStatus: String,
            stockCount: Int,
            lastUpdated: Long,
            mapLink: String
        ): StoreDetailFragment {
            val fragment = StoreDetailFragment()
            val args = Bundle()
            args.putString("name", name)
            args.putString("description", description)
            args.putString("stockStatus", stockStatus)
            args.putInt("stockCount", stockCount)
            args.putLong("lastUpdated", lastUpdated)
            args.putString("mapLink", mapLink)
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
        val tvStockStatus = view.findViewById<TextView>(R.id.tvDetailStockStatus)
        val tvStockCount = view.findViewById<TextView>(R.id.tvDetailStockCount)
        val tvLastUpdated = view.findViewById<TextView>(R.id.tvDetailLastUpdated)
        val btnClose = view.findViewById<ImageView>(R.id.btnCloseDetail)
        val btnNaverPlace = view.findViewById<Button>(R.id.btnNaverPlace)

        arguments?.let {
            val name = it.getString("name") ?: ""
            val description = it.getString("description") ?: ""
            val stockStatus = it.getString("stockStatus") ?: ""
            val stockCount = it.getInt("stockCount")
            val lastUpdated = it.getLong("lastUpdated")
            val mapLink = it.getString("mapLink") ?: ""

            tvName.text = name
            tvDescription.text = description
            tvStockStatus.text = getStockStatusText(stockStatus)
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

    private fun getStockStatusText(status: String): String {
        return when (status) {
            "OUT_OF_STOCK" -> "품절"
            "LOW" -> "품절 임박"
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
            minutes < 60 -> "${minutes}분 전"
            hours < 24 -> "${hours}시간 전"
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
