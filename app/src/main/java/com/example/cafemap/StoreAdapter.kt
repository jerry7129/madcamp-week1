package com.example.cafemap

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class StoreAdapter(private var stores: List<Store>) : RecyclerView.Adapter<StoreAdapter.StoreViewHolder>() {

    class StoreViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tvStoreName)
        val region: TextView = view.findViewById(R.id.tvStoreRegion)
        val rating: TextView = view.findViewById(R.id.tvRating)
        val stock: TextView = view.findViewById(R.id.tvStock)
        val lastUpdated: TextView = view.findViewById(R.id.tvLastUpdated)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoreViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_store, parent, false)
        return StoreViewHolder(view)
    }

    override fun onBindViewHolder(holder: StoreViewHolder, position: Int) {
        val store = stores[position]
        holder.name.text = store.name
        holder.region.text = store.region
        holder.rating.text = "⭐ ${String.format("%.1f", store.avgRating)}"
        holder.stock.text = "재고: ${store.stockCount}개"
        holder.lastUpdated.text = formatTimeAgo(store.lastUpdated)
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
            diff < 3_600_000 -> "${diff / 60_000}분 전"
            diff < 86_400_000 -> "${diff / 3_600_000}시간 전"
            else -> "${diff / 86_400_000}일 전"
        }
    }
}