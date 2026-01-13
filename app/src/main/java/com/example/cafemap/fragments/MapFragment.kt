package com.example.cafemap.fragments
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.GradientDrawable // ✨ Drawable 색상 변경용
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast // ✨ Toast 추가
import androidx.activity.result.contract.ActivityResultContracts // ✨ 최신 권한 요청 방식
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
import androidx.core.graphics.toColorInt

class MapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var locationSource: FusedLocationSource
    private lateinit var naverMap: NaverMap
    private var btnLocation: android.widget.ImageButton? = null
    private var progressBar: android.widget.ProgressBar? = null
    // 검색창 뷰 추가
    private var etSearch: EditText? = null
    private var ivSearchIcon: ImageView? = null
    private var cvSearchResults: CardView? = null
    private var rvSearchResults: RecyclerView? = null

    private val storeRepository = StoreRepository()
    private val markers = mutableListOf<Marker>()
    private var storeListener: ListenerRegistration? = null
    // 현재 지도에 표시된 카페 데이터 리스트 유지
    private var currentStores: List<Store> = emptyList()
    // 권한 요청 코드 (1000번)
    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }

    // ✨ 최신 권한 요청 런처 등록
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        if (granted) {
            // 권한 허용 시 Follow 모드 진입
            naverMap.locationTrackingMode = LocationTrackingMode.Follow
            progressBar?.visibility = View.VISIBLE
        } else {
            // 권한 거부 시 안내
            if (!shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                Toast.makeText(requireContext(), "위치 권한이 차단되었습니다. 설정에서 허용해주세요.", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(requireContext(), "위치 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 위치 소스 생성 (this 대신 requireActivity() 사용)
        locationSource = FusedLocationSource(requireActivity(), LOCATION_PERMISSION_REQUEST_CODE)

        // 지도 객체 가져오기 (XML에 있는 MapView가 아니라 FragmentContainerView를 쓸 때 방식)
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
        // 초기 설정
        naverMap.locationSource = locationSource
        naverMap.uiSettings.isLocationButtonEnabled = false
        // 확대/축소 버튼(+,-) 비활성화
        naverMap.uiSettings.isZoomControlEnabled = false

        // 뷰 찾아오기
        btnLocation = view?.findViewById(R.id.btn_location_custom)
        progressBar = view?.findViewById(R.id.progress_loading)
        etSearch = view?.findViewById(R.id.et_search)
        ivSearchIcon = view?.findViewById(R.id.iv_search_icon)
        cvSearchResults = view?.findViewById(R.id.cv_search_results)
        rvSearchResults = view?.findViewById(R.id.rv_search_results)

        rvSearchResults?.layoutManager = LinearLayoutManager(requireContext())

        // 지도 클릭 시 검색 결과 숨기기
        naverMap.setOnMapClickListener { _, _ ->
            hideSearchResults()
        }

        // 버튼 클릭 이벤트
        btnLocation?.setOnClickListener {
            if (checkLocationPermission()) {
                if (naverMap.locationTrackingMode == LocationTrackingMode.Follow) {
                    // 끄는 경우: None으로 바꾸면 addOnOptionChangeListener에서 처리함
                    naverMap.locationTrackingMode = LocationTrackingMode.None
                } else {
                    // 켜는 경우: Follow로 바꾸고 로딩바 표시
                    naverMap.locationTrackingMode = LocationTrackingMode.Follow
                    progressBar?.visibility = View.VISIBLE
                }
            } else {
                // ✨ 권한이 없으면 다시 요청 (런처 사용)
                requestPermissionLauncher.launch(
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                )
            }
        }

        // 위치가 갱신되면 로딩 끝!
        naverMap.addOnLocationChangeListener {
            if (progressBar?.visibility == View.VISIBLE) {
                progressBar?.visibility = View.GONE
            }
        }

        // 버튼 색상 및 로딩바 상태 관리
        naverMap.addOnOptionChangeListener {
            if (naverMap.locationTrackingMode == LocationTrackingMode.Follow) {
                btnLocation?.setColorFilter(Color.BLUE)
            } else {
                btnLocation?.setColorFilter(Color.BLACK)
                progressBar?.visibility = View.GONE
            }
        }

        // 앱 시작 시 권한 체크
        if (checkLocationPermission()) {
            // 권한이 이미 허용되어 있다면 바로 Follow 모드 적용
            naverMap.locationTrackingMode = LocationTrackingMode.Follow
            // 로딩바를 명시적으로 띄워줌
            progressBar?.visibility = View.VISIBLE
        } else {
            // ✨ 권한이 없으면 요청 (런처 사용)
            requestPermissionLauncher.launch(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
            )
        }

        // 검색 기능 설정
        setupSearch()
        // 카페 데이터 실시간 감시 및 마커 표시
        startListeningStores()
    }

    private fun checkLocationPermission(): Boolean {
        val hasFineLocation = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val hasCoarseLocation = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        return hasFineLocation || hasCoarseLocation
    }

    private fun setupSearch() {
        etSearch?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // 검색어 지우면 결과창 숨김
                if (s.isNullOrEmpty()) {
                    cvSearchResults?.visibility = View.GONE
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // 엔터(검색 버튼)를 누르면 검색 결과 표시
        etSearch?.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch()
                true
            } else {
                false
            }
        }

        // 돋보기 아이콘 클릭 시 검색 수행
        ivSearchIcon?.setOnClickListener {
            performSearch()
        }
    }

    private fun performSearch() {
        val query = etSearch?.text.toString().trim()
        if (query.isNotEmpty()) {
            val filtered = currentStores.filter {
                it.name.contains(query, ignoreCase = true) || it.address.contains(query, ignoreCase = true)
            }
            if (filtered.isNotEmpty()) {
                showSearchResults(filtered)
            } else {
                cvSearchResults?.visibility = View.GONE
            }
        }
        hideKeyboard()
        etSearch?.clearFocus()
    }

    private fun hideSearchResults() {
        cvSearchResults?.visibility = View.GONE
        etSearch?.clearFocus()
        hideKeyboard()
    }

    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(etSearch?.windowToken, 0)
    }

    private fun showSearchResults(stores: List<Store>) {
        cvSearchResults?.visibility = View.VISIBLE
        rvSearchResults?.adapter = SearchResultAdapter(stores) { selectedStore ->
            cvSearchResults?.visibility = View.GONE
            // 검색 선택 후 검색창 내용 리셋
            etSearch?.setText("")
            etSearch?.clearFocus()
            hideKeyboard()

            val destination = LatLng(selectedStore.latitude, selectedStore.longitude)
            naverMap.moveCamera(CameraUpdate.scrollTo(destination))
            showStoreInfoDialog(selectedStore)
        }
    }

    private fun startListeningStores() {
        storeListener?.remove()
        storeListener = storeRepository.listenToStores { stores ->
            currentStores = stores
            updateMarkers(stores)
        }
    }

    private fun updateMarkers(stores: List<Store>) {
        // 기존 마커 제거
        markers.forEach { it.map = null }
        markers.clear()

        stores.forEach { store ->
            if (store.latitude != 0.0 && store.longitude != 0.0) {
                // 커스텀 마커 뷰 생성
                val customMarkerView = LayoutInflater.from(requireContext()).inflate(R.layout.item_marker_custom, null)

                // ✅ 수정된 코드: TextView를 찾아서 Background Drawable의 색상 변경
                val tvBadge = customMarkerView.findViewById<TextView>(R.id.tvMarkerStockBadge)
                val ivMain = customMarkerView.findViewById<ImageView>(R.id.ivMarkerMain) // 메인 이미지

                // 재고 수량 설정
                tvBadge.text = store.stockCount.toString()

                // 색상 결정
                val colorStr = when {
                    store.stockCount <= 0 -> "#9E9E9E"  // 품절: 회색
                    store.stockCount <= 5 -> "#FF5252"  // 부족: 빨강
                    store.stockCount <= 15 -> "#FFAB40" // 보통: 주황
                    else -> "#4CAF50"                   // 여유: 초록
                }

                // ✨ Shape Drawable 색상 동적 변경 로직 ✨
                val background = tvBadge.background as? GradientDrawable
                background?.setColor(colorStr.toColorInt()) // 배경색 변경

//                // 가게 이름이나 조건에 따라 다른 이미지를 넣을 수 있습니다.
//                val imageResId = when (store.name) {
//                    "스타벅스" -> R.drawable.ic_cafe_test // 스타벅스 이미지 (파일이 있어야 함)
//                    "이디야" -> R.drawable.ic_cafe_test     // 이디야 이미지
//                    else -> R.drawable.ic_cafe_test // 기본 이미지 (없으면 기본값)
//                }
                val imageResId = R.drawable.ic_marker_test

                // 이미지 뷰에 리소스 설정
                ivMain.setImageResource(imageResId)

                val marker = Marker().apply {
                    position = LatLng(store.latitude, store.longitude)
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
        childFragmentManager.executePendingTransactions()
        if (childFragmentManager.findFragmentByTag("store_detail") != null) return

        val detailFragment = StoreDetailFragment.newInstance(
            store.name,
            store.description,
            store.stockStatus.name,
            store.stockCount,
            store.lastUpdated,
            store.mapLink,
            store.imageUrl // ✨ 이미지 URL 추가 전달

        )
        detailFragment.show(childFragmentManager, "store_detail")
        childFragmentManager.executePendingTransactions()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        storeListener?.remove()
    }

    inner class SearchResultAdapter(
        private val stores: List<Store>,
        private val onClick: (Store) -> Unit
    ) : RecyclerView.Adapter<SearchResultAdapter.ViewHolder>() {

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val tvName: TextView = view.findViewById(R.id.tv_result_name)
            val tvAddress: TextView = view.findViewById(R.id.tv_result_address)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_search_result, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val store = stores[position]
            holder.tvName.text = store.name
            holder.tvAddress.text = store.address
            holder.itemView.setOnClickListener { onClick(store) }
        }

        override fun getItemCount() = stores.size
    }
}