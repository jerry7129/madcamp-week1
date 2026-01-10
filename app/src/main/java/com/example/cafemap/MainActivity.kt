package com.example.cafemap

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.cafemap.fragments.ListFragment
import com.example.cafemap.fragments.MapFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 프래그먼트 인스턴스 생성
        val mapFragment = MapFragment()
        val listFragment = ListFragment()

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        // 1. 초기 화면 설정 (앱 켜자마자 지도 보여주기)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, mapFragment)
                .commit()
        }

        // 2. 탭 선택 리스너 설정
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.tab_map -> {
                    // 지도 탭 클릭 시
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, mapFragment)
                        .commit()
                    true
                }
                R.id.tab_list -> {
                    // 리스트 탭 클릭 시
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, listFragment)
                        .commit()
                    true
                }
                else -> false
            }
        }
    }

    //MapFragment.kt에 권한 결과 전달을 위함.
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}
