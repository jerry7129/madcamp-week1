package com.example.cafemap

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class StoreAdapter(private var stores: List<Store>, private val onItemClick: (Store) -> Unit) : RecyclerView.Adapter<StoreAdapter.StoreViewHolder>() {

    class StoreViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tvStoreName)
        val address: TextView = view.findViewById(R.id.tvStoreAddress)
        //val region: TextView = view.findViewById(R.id.tvStoreRegion)
        val rating: TextView = view.findViewById(R.id.tvRating)
        val reviewcount: TextView = view.findViewById(R.id.tvRatingCount)
        val stock: TextView = view.findViewById(R.id.tvStock)
        val stockStatus: TextView = view.findViewById(R.id.tvStockStatus)
        val lastUpdated: TextView = view.findViewById(R.id.tvLastUpdated)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoreViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_store, parent, false)
        return StoreViewHolder(view)
    }

    override fun onBindViewHolder(holder: StoreViewHolder, position: Int) {
        val store = stores[position]
        holder.name.text = store.name
        holder.address.text = store.address
        holder.rating.text = "⭐ ${String.format("%.1f", store.avgRating)}"
        holder.reviewcount.text = "(${store.reviewCount})"
        holder.stock.text = "재고: ${store.stockCount}개"
        holder.lastUpdated.text = formatTimeAgo(store.lastUpdated)
        val (statusText, colorStr) = when {
            store.stockCount <= 0 -> "품절" to "#9E9E9E"  // 회색
            store.stockCount <= 5 -> "부족" to "#FF5252"  // 빨강
            store.stockCount <= 15 -> "보통" to "#FFAB40" // 주황
            else -> "여유" to "#4CAF50"                   // 초록
        }
        holder.stockStatus.apply {
            text = statusText
            // 배경색 변경 (XML에서 만든 drawable의 색상을 변경)
            backgroundTintList = android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor(colorStr))
        }
        holder.itemView.setOnClickListener {
            onItemClick(store)
        }
    }

    override fun getItemCount() = stores.size

    // 데이터 업데이트 함수
    fun updateData(newStores: List<Store>) {
        this.stores = newStores
        notifyDataSetChanged()
    }

    fun formatTimeAgo(time: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - time

        return when {
            diff < 60_000 -> "방금 전"
            diff < 3_600_000 -> "약 ${diff / 60_000}분 전"
            diff < 86_400_000 -> "약 ${diff / 3_600_000}시간 전"
            else -> "${diff / 86_400_000}일 전"
        }
    }
}