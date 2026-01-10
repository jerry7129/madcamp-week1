package com.example.cafemap.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment

class ListFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 테스트용으로 텍스트뷰 하나만 있는 뷰 반환
        val textView = TextView(context)
        textView.text = "여기는 카페 리스트 화면입니다"
        textView.textSize = 30f
        return textView
    }
}