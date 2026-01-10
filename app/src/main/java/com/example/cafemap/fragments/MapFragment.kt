package com.example.cafemap.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.example.cafemap.R
import com.example.cafemap.Store
import com.example.cafemap.StoreRepository
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.util.FusedLocationSource
import com.naver.maps.map.util.MarkerIcons

class MapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var locationSource: FusedLocationSource
    private lateinit var naverMap: NaverMap
    private var btnLocation: android.widget.ImageButton? = null
    private var progressBar: android.widget.ProgressBar? = null

    private val storeRepository = StoreRepository()
    private val markers = mutableListOf<Marker>()

    // 권한 요청 코드 (1000번)
    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. 위치 소스 생성 (this 대신 requireActivity() 사용)
        locationSource = FusedLocationSource(requireActivity(), LOCATION_PERMISSION_REQUEST_CODE)

        // 2. 지도 객체 가져오기 (XML에 있는 MapView가 아니라 FragmentContainerView를 쓸 때 방식)
        // 만약 XML에 <fragment ... class="...MapFragment" /> 로 되어있다면 아래 방식 사용
        val fm = childFragmentManager
        val mapFragment = fm.findFragmentById(R.id.map_fragment) as com.naver.maps.map.MapFragment?
            ?: com.naver.maps.map.MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.map_fragment, it).commit()
            }

        // 비동기로 맵 준비
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap

        // 1. 초기 설정
        naverMap.locationSource = locationSource
        naverMap.uiSettings.isLocationButtonEnabled = false

        // 2. 뷰 찾아오기
        btnLocation = view?.findViewById(R.id.btn_location_custom)
        progressBar = view?.findViewById(R.id.progress_loading)

        // 3. 버튼 클릭 이벤트
        btnLocation?.setOnClickListener {
            if (naverMap.locationTrackingMode == LocationTrackingMode.Follow) {
                // 끄는 경우: None으로 바꾸고 로딩바 숨김
                naverMap.locationTrackingMode = LocationTrackingMode.None
                progressBar?.visibility = View.GONE
            } else {
                // 켜는 경우: Follow로 바꾸고 ★로딩바 표시★
                naverMap.locationTrackingMode = LocationTrackingMode.Follow
                progressBar?.visibility = View.VISIBLE
            }
        }

        // 4. ★ 위치가 갱신되면 로딩 끝!
        // Follow 모드를 켜고 -> GPS가 잡혀서 지도가 움직이면 -> 그때 로딩바를 끕니다.
        naverMap.addOnLocationChangeListener { location ->
            // 위치가 잡혔으니 로딩바 숨김
            if (progressBar?.visibility == View.VISIBLE) {
                progressBar?.visibility = View.GONE
            }
        }

        // 5. 버튼 색상 관리 (Follow일 때 파란색)
        naverMap.addOnOptionChangeListener {
            if (naverMap.locationTrackingMode == LocationTrackingMode.Follow) {
                btnLocation?.setColorFilter(Color.BLUE)
            } else {
                btnLocation?.setColorFilter(Color.BLACK)
                // 혹시 모드 변경으로 꺼졌을 때를 대비해 로딩바 숨김
                progressBar?.visibility = View.GONE
            }
        }

        // 카페 데이터 가져오기 및 마커 표시
        fetchAndDisplayStores()
    }

    private fun fetchAndDisplayStores() {
        storeRepository.getFilteredStores { stores ->
            // 기존 마커 제거
            markers.forEach { it.map = null }
            markers.clear()

            stores.forEach { store ->
                if (store.latitude != 0.0 && store.longitude != 0.0) {
                    val marker = Marker().apply {
                        position = LatLng(store.latitude, store.longitude)
                        captionText = store.name
                        icon = MarkerIcons.BLACK
                        iconTintColor = Color.RED
                        map = naverMap
                    }

                    marker.setOnClickListener {
                        showStoreInfoDialog(store)
                        true
                    }
                    markers.add(marker)
                }
            }
        }
    }

    private fun showStoreInfoDialog(store: Store) {
        val detailFragment = StoreDetailFragment.newInstance(
            store.name,
            store.description,
            store.stockStatus.name,
            store.stockCount,
            store.lastUpdated,
            store.mapLink
        )
        // DialogFragment로 띄우기 (childFragmentManager 사용)
        detailFragment.show(childFragmentManager, "store_detail")
    }

    // 6. 권한 허용 시 처리 (onRequestPermissionsResult)
    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (locationSource.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
            if (locationSource.isActivated) {
                // 권한 허용됨 -> 바로 Follow 모드 및 ★로딩바 표시★
                naverMap.locationTrackingMode = LocationTrackingMode.Follow
                progressBar?.visibility = View.VISIBLE
            } else {
                naverMap.locationTrackingMode = LocationTrackingMode.None
            }
            return
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}
