package com.example.cafemap.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.example.cafemap.R
import com.example.cafemap.Store
import com.example.cafemap.StoreRepository
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.util.FusedLocationSource
import com.google.firebase.firestore.ListenerRegistration

class MapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var locationSource: FusedLocationSource
    private lateinit var naverMap: NaverMap
    private var btnLocation: android.widget.ImageButton? = null
    private var progressBar: android.widget.ProgressBar? = null
    
    // 검색창 뷰 추가
    private var etSearch: EditText? = null
    private var ivSearchIcon: ImageView? = null

    private val storeRepository = StoreRepository()
    private val markers = mutableListOf<Marker>()
    private var storeListener: ListenerRegistration? = null
    
    // 현재 지도에 표시된 카페 데이터 리스트 유지
    private var currentStores: List<Store> = emptyList()

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
        etSearch = view?.findViewById(R.id.et_search)
        ivSearchIcon = view?.findViewById(R.id.iv_search_icon)

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
        
        // 검색 기능 설정
        setupSearch()

        // 카페 데이터 실시간 감시 및 마커 표시
        startListeningStores()
    }

    private fun setupSearch() {
        // 검색 아이콘 클릭 시 검색 수행
        ivSearchIcon?.setOnClickListener {
            performSearch()
        }

        // 엔터(검색) 버튼 클릭 시 검색 수행
        etSearch?.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch()
                true
            } else {
                false
            }
        }
    }

    private fun performSearch() {
        val query = etSearch?.text?.toString()?.trim() ?: ""
        if (query.isEmpty()) return

        // 이름이나 지역에 검색어가 포함된 카페들 중 첫 번째 결과 찾기 (유사도 검색 간소화)
        val result = currentStores.firstOrNull { 
            it.name.contains(query, ignoreCase = true) || it.region.contains(query, ignoreCase = true) 
        }

        if (result != null) {
            // 검색 버튼 클릭 시 포커스 해제 (키보드 내림 지원 및 중복 클릭 방지)
            etSearch?.clearFocus()
            
            // 결과가 있을 경우 지도를 해당 위치로 이동
            val destination = LatLng(result.latitude, result.longitude)
            val cameraUpdate = CameraUpdate.scrollTo(destination)
            naverMap.moveCamera(cameraUpdate)

            // 상세 정보 팝업 띄우기
            showStoreInfoDialog(result)
        } else {
            // 결과가 없을 경우 Toast 메시지 출력
            Toast.makeText(requireContext(), "검색 결과가 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startListeningStores() {
        // 기존 리스너가 있다면 제거
        storeListener?.remove()
        
        // 실시간 업데이트 구독
        storeListener = storeRepository.listenToStores { stores ->
            currentStores = stores // 현재 데이터 리스트 업데이트
            updateMarkers(stores)
        }
    }

    private fun updateMarkers(stores: List<Store>) {
        // 기존 마커 제거
        markers.forEach { it.map = null }
        markers.clear()

        stores.forEach { store ->
            if (store.latitude != 0.0 && store.longitude != 0.0) {
                // 커스텀 마커 뷰 생성 (item_marker_custom.xml 사용)
                val customMarkerView = LayoutInflater.from(requireContext()).inflate(R.layout.item_marker_custom, null)
                val tvBadge = customMarkerView.findViewById<TextView>(R.id.tvMarkerStockBadge)
                
                // 재고 수량 설정
                tvBadge.text = store.stockCount.toString()
                
                // 수량에 따른 색상 설정 (item_marker_custom.xml의 배경색을 동적으로 변경)
                val colorStr = when {
                    store.stockCount <= 0 -> "#9E9E9E"  // 품절: 회색
                    store.stockCount <= 5 -> "#FF5252"  // 부족: 빨강
                    store.stockCount <= 15 -> "#FFAB40" // 보통: 주황
                    else -> "#4CAF50"                   // 풍부: 초록
                }
                tvBadge.backgroundTintList = android.content.res.ColorStateList.valueOf(Color.parseColor(colorStr))

                val marker = Marker().apply {
                    position = LatLng(store.latitude, store.longitude)
                    // 커스텀 뷰를 OverlayImage로 변환하여 적용
                    icon = OverlayImage.fromView(customMarkerView)
                    anchor = android.graphics.PointF(0.5f, 0.5f)
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

    private fun showStoreInfoDialog(store: Store) {
        // [중복 팝업 방지 로직]
        // 비동기 commit으로 인해 짧은 시간 내 여러 번 호출될 수 있으므로 대기 중인 트랜잭션을 강제 실행 후 체크
        childFragmentManager.executePendingTransactions()
        if (childFragmentManager.findFragmentByTag("store_detail") != null) return

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
        
        // show() 호출 직후 상태를 반영하여 중복 호출을 확실히 차단
        childFragmentManager.executePendingTransactions()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // 뷰가 파괴될 때 실시간 리스너 해제 (메모리 누수 방지)
        storeListener?.remove()
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
