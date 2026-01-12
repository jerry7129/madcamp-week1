package com.example.cafemap

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.cafemap.fragments.AccountFragment
import com.example.cafemap.fragments.MapFragment
import com.example.cafemap.fragments.StoreListFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    // 로그인 상태를 관리하는 전역 변수 (임시)
    companion object {
        var isLoggedIn: Boolean = false
    }

    // 프래그먼트 인스턴스 생성
    // (지도는 상태 유지가 중요하므로 미리 만들어두는 것이 좋습니다)
    private val mapFragment = MapFragment()
    // test 삭제
    //private val testFragment = TestFragment()
    private val storeListFragment = StoreListFragment()
    private val accountFragment = AccountFragment()

    // 현재 보고 있는 프래그먼트를 관리하기 위한 변수
    private var activeFragment: Fragment = mapFragment

    // 상대방이 만든 DB 테스트용 프래그먼트 (필요하다면 파일을 복사해오고 주석 해제)
    // private val testFragment = TestFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        // 1. 초기 화면 설정 (앱 켜자마자 지도 보여주기)
        // 상태 유지를 위해 모든 프래그먼트를 미리 add하고, 지도만 show 상태로 둡니다.
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, accountFragment).hide(accountFragment)
                .add(R.id.fragment_container, storeListFragment).hide(storeListFragment)
                // test 삭제
                //.add(R.id.fragment_container, testFragment).hide(testFragment)
                .add(R.id.fragment_container, mapFragment)
                .commit()
        }

        // 2. 탭 선택 리스너 설정
        bottomNavigationView.setOnItemSelectedListener { item ->
            val selectedFragment = when (item.itemId) {
                R.id.tab_map -> mapFragment

                // test 삭제
                //R.id.nav_test -> testFragment

                R.id.nav_list -> storeListFragment
                R.id.nav_account -> accountFragment
                // [추가 옵션] 메뉴(xml)에 아이템을 추가해서 테스트 화면을 보고 싶다면 사용
                else -> null
            }

            // 선택된 프래그먼트가 있으면 replace 대신 show/hide를 사용하여 상태 유지
            selectedFragment?.let { fragment ->
                if (fragment != activeFragment) {
                    supportFragmentManager.beginTransaction()
                        .hide(activeFragment)
                        .show(fragment)
                        .commit()
                    activeFragment = fragment
                }
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
