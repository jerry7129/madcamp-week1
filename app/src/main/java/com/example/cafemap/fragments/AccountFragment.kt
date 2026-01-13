package com.example.cafemap.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.cafemap.BuildConfig
import com.example.cafemap.MainActivity
import com.example.cafemap.R
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.common.util.Utility
import com.kakao.sdk.user.UserApiClient
import kotlinx.coroutines.launch

class AccountFragment : Fragment() {

    private var tvUserEmail: TextView? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var credentialManager: CredentialManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Firebase Auth 및 Credential Manager 초기화
        auth = FirebaseAuth.getInstance()
        credentialManager = CredentialManager.create(requireContext())

        // ✨ [중요] 키 해시 확인을 위한 로그 추가
        Log.d("KAKAO", "KeyHash: ${Utility.getKeyHash(requireContext())}")

        val layoutLogin = view.findViewById<View>(R.id.layout_login)
        val layoutProfile = view.findViewById<View>(R.id.layout_profile)

        val btnGoogleLogin = view.findViewById<View>(R.id.btn_google_login)
        val btnKakaoLogin = view.findViewById<View>(R.id.btn_kakao_login)
        val btnGuestLogin = view.findViewById<Button>(R.id.btn_guest_login)
        val btnLogout = view.findViewById<Button>(R.id.btn_logout)
        tvUserEmail = view.findViewById<TextView>(R.id.tv_user_email)

        // 자동 로그인 체크
        checkAutoLogin(layoutLogin, layoutProfile)

        // 구글 로그인 (Credential Manager 방식)
        btnGoogleLogin.setOnClickListener {
            signInWithGoogle()
        }

        // 카카오 로그인
        btnKakaoLogin.setOnClickListener {
            loginWithKakao()
        }

        btnGuestLogin.setOnClickListener {
            performLoginSuccess("Guest")
        }

        btnLogout.setOnClickListener {
            performLogout(layoutLogin, layoutProfile)
        }
    }

    private fun checkAutoLogin(loginLayout: View, profileLayout: View) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            MainActivity.isLoggedIn = true
            performLoginSuccess(currentUser.displayName ?: currentUser.email ?: "사용자", isAutoLogin = true)
        } else {
            UserApiClient.instance.me { user, _ ->
                if (user != null) {
                    MainActivity.isLoggedIn = true
                    performLoginSuccess(user.kakaoAccount?.profile?.nickname ?: "관리자", isAutoLogin = true)
                } else {
                    updateUI(loginLayout, profileLayout)
                }
            }
        }
    }

    private fun signInWithGoogle() {
        val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(BuildConfig.GOOGLE_WEB_CLIENT_ID)
            .build()

        val request: GetCredentialRequest = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        lifecycleScope.launch {
            try {
                val result = credentialManager.getCredential(
                    request = request,
                    context = requireContext(),
                )
                val credential = result.credential
                
                // ✨ 수정: 구체적인 라이브러리 타입 체크 대신 createFrom 사용 권장되나, 명시적 캐스팅 보완
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                firebaseAuthWithGoogle(googleIdTokenCredential.idToken)
                
            } catch (e: Exception) {
                Log.e("GOOGLE", "Credential Manager Error", e)
                Toast.makeText(requireContext(), "로그인 실패: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    performLoginSuccess(user?.displayName ?: "Google 사용자")
                } else {
                    Log.e("GOOGLE", "Firebase auth failed", task.exception)
                    Toast.makeText(requireContext(), "Firebase 인증 실패: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun loginWithKakao() {
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error == null && token != null) {
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

    private fun performLogout(loginLayout: View, profileLayout: View) {
        auth.signOut()
        lifecycleScope.launch {
            credentialManager.clearCredentialState(ClearCredentialStateRequest())
        }
        UserApiClient.instance.logout { 
            MainActivity.isLoggedIn = false
            Toast.makeText(requireContext(), "로그아웃되었습니다.", Toast.LENGTH_SHORT).show()
            updateUI(loginLayout, profileLayout)
        }
    }

    private fun performLoginSuccess(userName: String, isAutoLogin: Boolean = false) {
        MainActivity.isLoggedIn = true
        view?.let { v ->
            val layoutLogin = v.findViewById<View>(R.id.layout_login)
            val layoutProfile = v.findViewById<View>(R.id.layout_profile)
            tvUserEmail?.text = userName
            updateUI(layoutLogin, layoutProfile)
            if (!isAutoLogin && isResumed) {
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