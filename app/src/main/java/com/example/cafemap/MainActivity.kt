package com.example.cafemap

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.cafemap.fragments.MapFragment
import com.example.cafemap.fragments.StoreListFragment
// 만약 상대방의 파일을 가져왔다면 아래 주석을 풀고 import 하세요
import com.example.cafemap.fragments.TestFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    // 프래그먼트 인스턴스 생성
    // (지도는 상태 유지가 중요하므로 미리 만들어두는 것이 좋습니다)
    private val mapFragment = MapFragment()
    private val testFragment = TestFragment()
    private val storeListFragment = StoreListFragment()

    // 상대방이 만든 DB 테스트용 프래그먼트 (필요하다면 파일을 복사해오고 주석 해제)
    // private val testFragment = TestFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        // 1. 초기 화면 설정 (앱 켜자마자 지도 보여주기)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, mapFragment)
                .commit()
        }

        // 2. 탭 선택 리스너 설정
        bottomNavigationView.setOnItemSelectedListener { item ->
            val selectedFragment = when (item.itemId) {
                R.id.tab_map -> mapFragment

                R.id.nav_test -> testFragment

                R.id.nav_list -> storeListFragment
                // [추가 옵션] 메뉴(xml)에 아이템을 추가해서 테스트 화면을 보고 싶다면 사용
                else -> null
            }

            // 선택된 프래그먼트가 있으면 화면 교체
            selectedFragment?.let { fragment ->
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit()
                true
            } ?: false
        }
    }

    // MapFragment에서 위치 권한 요청 결과를 처리하기 위해 필수 (작성자님 코드 유지)
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // 현재 보여지고 있는 프래그먼트들에게 결과 전달
        supportFragmentManager.fragments.forEach { fragment ->
            if (fragment.isAdded) {
                fragment.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }
        }
    }
}