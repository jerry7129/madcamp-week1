package com.example.cafemap.fragments

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.cafemap.R

class StoreDetailFragment : Fragment(R.layout.fragment_store_detail) {

    companion object {
        fun newInstance(name: String, description: String, stockStatus: String, avgRating: Float): StoreDetailFragment {
            val fragment = StoreDetailFragment()
            val args = Bundle()
            args.putString("name", name)
            args.putString("description", description)
            args.putString("stockStatus", stockStatus)
            args.putFloat("avgRating", avgRating)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvName = view.findViewById<TextView>(R.id.tvDetailName)
        val tvDescription = view.findViewById<TextView>(R.id.tvDetailDescription)
        val tvStock = view.findViewById<TextView>(R.id.tvDetailStock)
        val tvRating = view.findViewById<TextView>(R.id.tvDetailRating)
        val btnClose = view.findViewById<Button>(R.id.btnCloseDetail)

        arguments?.let {
            tvName.text = it.getString("name")
            tvDescription.text = it.getString("description")
            tvStock.text = "재고 상태: ${it.getString("stockStatus")}"
            tvRating.text = "평점: ${it.getFloat("avgRating")}"
        }

        btnClose.setOnClickListener {
            parentFragmentManager.beginTransaction().remove(this).commit()
        }
    }
}
