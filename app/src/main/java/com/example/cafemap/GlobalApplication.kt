package com.example.cafemap

import android.app.Application
import com.kakao.sdk.common.KakaoSdk

class GlobalApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // 카카오 SDK 초기화
        // local.properties의 KAKAO_NATIVE_APP_KEY 값을 BuildConfig를 통해 가져옵니다.
        KakaoSdk.init(this, BuildConfig.KAKAO_NATIVE_APP_KEY)
    }
}