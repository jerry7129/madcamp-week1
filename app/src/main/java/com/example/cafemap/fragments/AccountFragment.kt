package com.example.cafemap.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.cafemap.MainActivity
import com.example.cafemap.R
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.common.util.Utility
import com.kakao.sdk.user.UserApiClient

class AccountFragment : Fragment() {

    private var tvUserEmail: TextView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ✨ [중요] 키 해시 확인을 위한 로그 추가
        // 앱 실행 후 Logcat에서 "KeyHash"를 검색하세요.
        Log.d("KAKAO", "KeyHash: ${Utility.getKeyHash(requireContext())}")

        val layoutLogin = view.findViewById<View>(R.id.layout_login)
        val layoutProfile = view.findViewById<View>(R.id.layout_profile)

        val btnKakaoLogin = view.findViewById<View>(R.id.btn_kakao_login)
        val btnGuestLogin = view.findViewById<Button>(R.id.btn_guest_login)
        val btnLogout = view.findViewById<Button>(R.id.btn_logout)
        tvUserEmail = view.findViewById<TextView>(R.id.tv_user_email)

        // 자동 로그인 체크: 이미 토큰이 있다면 사용자 정보 요청
        UserApiClient.instance.me { user, error ->
            if (user != null) {
                MainActivity.isLoggedIn = true
                performLoginSuccess(user.kakaoAccount?.profile?.nickname ?: "관리자")
            } else {
                updateUI(layoutLogin, layoutProfile)
            }
        }

        // 카카오 로그인 구현
        btnKakaoLogin.setOnClickListener {
            val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
                if (error != null) {
                    Log.e("KAKAO", "카카오계정으로 로그인 실패", error)
                    // 에러 메시지 상세 출력
                    Toast.makeText(requireContext(), "로그인 실패: ${error.message}", Toast.LENGTH_LONG).show()
                } else if (token != null) {
                    Log.i("KAKAO", "카카오계정으로 로그인 성공")
                    // 로그인 성공 후 사용자 정보 가져오기
                    UserApiClient.instance.me { user, _ ->
                        performLoginSuccess(user?.kakaoAccount?.profile?.nickname ?: "카카오 사용자")
                    }
                }
            }

            if (UserApiClient.instance.isKakaoTalkLoginAvailable(requireContext())) {
                UserApiClient.instance.loginWithKakaoTalk(requireContext()) { token, error ->
                    if (error != null) {
                        if (error is ClientError && error.reason == ClientErrorCause.Cancelled) return@loginWithKakaoTalk
                        UserApiClient.instance.loginWithKakaoAccount(requireContext(), callback = callback)
                    } else if (token != null) {
                        UserApiClient.instance.me { user, _ ->
                            performLoginSuccess(user?.kakaoAccount?.profile?.nickname ?: "카카오 사용자")
                        }
                    }
                }
            } else {
                UserApiClient.instance.loginWithKakaoAccount(requireContext(), callback = callback)
            }
        }

        btnGuestLogin.setOnClickListener {
            performLoginSuccess("Guest")
        }

        btnLogout.setOnClickListener {
            UserApiClient.instance.logout { 
                MainActivity.isLoggedIn = false
                Toast.makeText(requireContext(), "로그아웃되었습니다.", Toast.LENGTH_SHORT).show()
                updateUI(layoutLogin, layoutProfile)
            }
        }
    }

    private fun performLoginSuccess(userName: String) {
        MainActivity.isLoggedIn = true
        
        // UI 업데이트를 위해 뷰가 살아있는지 확인
        view?.let { v ->
            val layoutLogin = v.findViewById<View>(R.id.layout_login)
            val layoutProfile = v.findViewById<View>(R.id.layout_profile)
            tvUserEmail?.text = userName
            
            updateUI(layoutLogin, layoutProfile)
            
            // 토스트 메시지는 한 번만 출력되도록 처리 (자동 로그인 시에는 생략 가능)
            if (isResumed) {
                Toast.makeText(requireContext(), "$userName 님, 환영합니다!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateUI(loginLayout: View?, profileLayout: View?) {
        if (MainActivity.isLoggedIn) {
            loginLayout?.visibility = View.GONE
            profileLayout?.visibility = View.VISIBLE
        } else {
            loginLayout?.visibility = View.VISIBLE
            profileLayout?.visibility = View.GONE
        }
    }
}