package com.example.cafemap.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.example.cafemap.R

class StoreDetailFragment : DialogFragment() {

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
        val tvStock = view.findViewById<TextView>(R.id.tvDetailStock)
        val tvRating = view.findViewById<TextView>(R.id.tvDetailRating)
        val btnClose = view.findViewById<Button>(R.id.btnCloseDetail)

        arguments?.let {
            tvName.text = it.getString("name")
            tvDescription.text = it.getString("description")
            tvStock.text = it.getString("stockStatus")
            tvRating.text = it.getFloat("avgRating").toString()
        }

        btnClose.setOnClickListener {
            dismiss()
        }
    }

    override fun onStart() {
        super.onStart()
        // 다이얼로그의 너비를 화면에 맞게 조절
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }
}
