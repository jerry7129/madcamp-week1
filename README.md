# 🍪 Cafe Map: 두쫀쿠맵을 잇는 디저트 지도

> **KAIST MadCamp 2025 Winter - Week 1 Project**
> **개발 기간:** 2026.01.08 ~ 2026.01.14

<br>

## 🧑‍💻 팀원 소개 (Team Members)

| 이름 (Name) | 역할 (Role) | 학교/학과 (Affiliation) | GitHub |
| :---: | :---: | :---: | :---: |
| **김승관** | Android Developer<br>(Map, UI/UX, Auth) | 한양대학교<br>컴퓨터소프트웨어학부 | [@jerry7129](https://github.com/jerry7129) |
| **김현우** | Android Developer<br>(Firebase) | KAIST<br>전기전자공학부 | [@GithubID](https://github.com/GithubID) |

<br>

## 💡 프로젝트 소개 (Project Introduction)
**Cafe Map**은 품절 대란을 일으키는 유행 디저트의 **실시간 재고를 지도에서 한눈에 확인하는 앱**입니다.
기존의 '두바이 쫀득 쿠키 맵'에서 영감을 받아, 사장님과 고객을 연결하는 플랫폼으로 확장했습니다.

* **Target User:** 헛걸음 없이 핫한 디저트를 먹고 싶은 **고객**, 효과적인 홍보가 필요한 **카페 사장님**
* **Key Value:** 실시간 재고 확인(Green/Red 마커), 위치 기반 카페 탐색, 간편한 입점 신청

<br>

## 🛠️ 기술 스택 (Tech Stack)

### 🎨 Frontend (Android)
| 구분 | 상세 내용 (Details) |
| :---: | :--- |
| **Language** | <img src="https://img.shields.io/badge/Kotlin-7F52FF?style=flat&logo=Kotlin&logoColor=white"/> **(Coroutines)**<br>- 비동기 처리를 위한 코루틴 활용 |
| **UI / UX** | <img src="https://img.shields.io/badge/Material%20Design%203-757575?style=flat&logo=materialdesign&logoColor=white"/> <img src="https://img.shields.io/badge/Android%20Jetpack-4285F4?style=flat&logo=android&logoColor=white"/><br>- **Theme:** "Warm Cafe" (Brown & Beige Palette)<br>- **Navigation:** Fragment-based (Show/Hide Pattern)<br>- **Jetpack:** Fragment KTX, Lifecycle, ViewBinding |
| **Security** | <img src="https://img.shields.io/badge/API%20Security-4CAF50?style=flat&logo=android&logoColor=white"/><br>- `local.properties` & `BuildConfig`를 활용한 API Key 관리 |

<br>

### 📍 Map & Location
| 구분 | 상세 내용 (Details) |
| :---: | :--- |
| **Map SDK** | <img src="https://img.shields.io/badge/Naver%20Maps-03C75A?style=flat&logo=Naver&logoColor=white"/> **(v3.23.0)**<br>- 커스텀 마커 배지 구현 (Elevation & Shadow 적용)<br>- 실시간 재고량 시각화 (Color Coded Markers) |
| **Location** | <img src="https://img.shields.io/badge/Google%20Location-4285F4?style=flat&logo=googlemaps&logoColor=white"/><br>- Google Play Services Location (`FusedLocationSource`) |

<br>

### ☁️ Backend & Authentication
| 구분 | 상세 내용 (Details) |
| :---: | :--- |
| **Platform** | <img src="https://img.shields.io/badge/Firebase-FFCA28?style=flat&logo=Firebase&logoColor=black"/> |
| **Database** | <img src="https://img.shields.io/badge/Cloud%20Firestore-FFCA28?style=flat&logo=Firebase&logoColor=black"/><br>- `SnapshotListener`를 활용한 실시간 재고 동기화 |
| **Auth** | <img src="https://img.shields.io/badge/Credential%20Manager-4285F4?style=flat&logo=Google&logoColor=white"/> <img src="https://img.shields.io/badge/Kakao%20Login-FFCD00?style=flat&logo=KakaoTalk&logoColor=black"/><br>- **Google:** Android 최신 Credential Manager API 적용<br>- **Kakao:** Kakao SDK v2 (카카오싱크 대응)<br>- **Guest:** 익명 인증을 통한 관리자 모드 체험 기능 |

<br>

## 📱 주요 기능 (Key Features)

| 1. 실시간 재고 지도 | 2. 카페 검색 | 3. 카페 리스트 | 4. 관리자 모드 | 5. 카페 리스트 (관리자) |
| :---: | :---: | :---: | :---: | :---: |
| **지도 & 내 위치 추적**<br>실시간 마커 배지 확인<br>Follow 모드로 주변 탐색 | **스마트 검색**<br>지역/카페명 검색 및 이동<br>카페 정보 및 <br>네이버 플레이스 연결 | **리스트 보기 & 정렬**<br>별점/재고순 필터링<br>별점 부여 | **관리자 권한 획득**<br>소셜 로그인 사용<br>Kakao, Google | **카페 및 재고 관리**<br>카페 등록<br>주소->위도경도 **개발 기간:** 2026.01.08 ~ 2026.01.14

<br>

## 🧑‍💻 팀원 소개 (Team Members)

| 이름 (Name) | 역할 (Role) | 학교/학과 (Affiliation) | GitHub |
| :---: | :---: | :---: | :---: |
| **김승관** | Android Developer<br>(Map, UI/UX, Auth) | 한양대학교<br>컴퓨터소프트웨어학부 | [@jerry7129](https://github.com/jerry7129) |
| **김현우** | Android Developer<br>(Firebase) | KAIST<br>전기전자공학부 | [@GithubID](https://github.com/GithubID) |

<br>

## 💡 프로젝트 소개 (Project Introduction)
**Cafe Map**은 품절 대란을 일으키는 유행 디저트의 **실시간 재고를 지도에서 한눈에 확인하는 앱**입니다.
기존의 '두바이 쫀득 쿠키 맵'에서 영감을 받아, 사장님과 고객을 연결하는 플랫폼으로 확장했습니다.

* **Target User:** 헛걸음 없이 핫한 디저트를 먹고 싶은 **고객**, 효과적인 홍보가 필요한 **카페 사장님**
* **Key Value:** 실시간 재고 확인(Green/Red 마커), 위치 기반 카페 탐색, 간편한 입점 신청

<br>

## 🛠️ 기술 스택 (Tech Stack)

### 🎨 Frontend (Android)
| 구분 | 상세 내용 (Details) |
| :---: | :--- |
| **Language** | <img src="https://img.shields.io/badge/Kotlin-7F52FF?style=flat&logo=Kotlin&logoColor=white"/> **(Coroutines)**<br>- 비동기 처리를 위한 코루틴 활용 |
| **UI / UX** | <img src="https://img.shields.io/badge/Material%20Design%203-757575?style=flat&logo=materialdesign&logoColor=white"/> <img src="https://img.shields.io/badge/Android%20Jetpack-4285F4?style=flat&logo=android&logoColor=white"/><br>- **Theme:** "Warm Cafe" (Brown & Beige Palette)<br>- **Navigation:** Fragment-based (Show/Hide Pattern)<br>- **Jetpack:** Fragment KTX, Lifecycle, ViewBinding |
| **Security** | <img src="https://img.shields.io/badge/API%20Security-4CAF50?style=flat&logo=android&logoColor=white"/><br>- `local.properties` & `BuildConfig`를 활용한 API Key 관리 |

<br>

### 📍 Map & Location
| 구분 | 상세 내용 (Details) |
| :---: | :--- |
| **Map SDK** | <img src="https://img.shields.io/badge/Naver%20Maps-03C75A?style=flat&logo=Naver&logoColor=white"/> **(v3.23.0)**<br>- 커스텀 마커 배지 구현 (Elevation & Shadow 적용)<br>- 실시간 재고량 시각화 (Color Coded Markers) |
| **Location** | <img src="https://img.shields.io/badge/Google%20Location-4285F4?style=flat&logo=googlemaps&logoColor=white"/><br>- Google Play Services Location (`FusedLocationSource`) |

<br>

### ☁️ Backend & Authentication
| 구분 | 상세 내용 (Details) |
| :---: | :--- |
| **Platform** | <img src="https://img.shields.io/badge/Firebase-FFCA28?style=flat&logo=Firebase&logoColor=black"/> |
| **Database** | <img src="https://img.shields.io/badge/Cloud%20Firestore-FFCA28?style=flat&logo=Firebase&logoColor=black"/><br>- `SnapshotListener`를 활용한 실시간 재고 동기화 |
| **Auth** | <img src="https://img.shields.io/badge/Credential%20Manager-4285F4?style=flat&logo=Google&logoColor=white"/> <img src="https://img.shields.io/badge/Kakao%20Login-FFCD00?style=flat&logo=KakaoTalk&logoColor=black"/><br>- **Google:** Android 최신 Credential Manager API 적용<br>- **Kakao:** Kakao SDK v2 (카카오싱크 대응)<br>- **Guest:** 익명 인증을 통한 관리자 모드 체험 기능 |

<br>

## 📱 주요 기능 (Key Features)

| 1. 실시간 재고 지도 | 2. 카페 검색 | 3. 카페 리스트 | 4. 관리자 모드 | 5. 카페 리스트 (관리자) |
| :---: | :---: | :---: | :---: | :---: |
| **지도 & 내 위치 추적**<br>실시간 마커 배지 확인<br>Follow 모드로 주변 탐색 | **스마트 검색**<br>지역/카페명 검색 및 이동<br>카페 정보 및 <br>네이버 플레이스 연결 | **리스트 보기 & 정렬**<br>별점/재고순 필터링<br>별점 부여 | **관리자 권한 획득**<br>소셜 로그인 사용<br>Kakao, Google | **카페 및 재고 관리**<br>카페 등록<br>주소->좌표 변환 가능<br>카페 삭제<br>실시간 수량 업데이트 |
| <img src="https://github.com/jerry7129/madcamp-week1/blob/aacf79bb69006402dc9be96b5c5cea0fbb8f9f39/readme_image/cafemap_tap1.png" width="150" /> | <img src="https://github.com/jerry7129/madcamp-week1/blob/aacf79bb69006402dc9be96b5c5cea0fbb8f9f39/readme_image/cafemap_tap1-1.png" width="150" /> | <img src="https://github.com/jerry7129/madcamp-week1/blob/aacf79bb69006402dc9be96b5c5cea0fbb8f9f39/readme_image/cafemap_tap2.png" width="150" /> | <img src="https://github.com/jerry7129/madcamp-week1/blob/aacf79bb69006402dc9be96b5c5cea0fbb8f9f39/readme_image/cafemap_tap3.png" width="150" /> | <img src="https://github.com/jerry7129/madcamp-week1/blob/b62611241a9079d582d758ad5f664941e98f3032/readme_image/cafemap_tap2-1.png" width="150" /> |
|| <img src="https://github.com/jerry7129/madcamp-week1/blob/aacf79bb69006402dc9be96b5c5cea0fbb8f9f39/readme_image/cafemap_tap1-2.png" width="150" /> | <img src="https://github.com/jerry7129/madcamp-week1/blob/b62611241a9079d582d758ad5f664941e98f3032/readme_image/cafemap_tap2-3.png" width="150" /> | <img src="https://github.com/jerry7129/madcamp-week1/blob/4539de4e70cd54ebc285e5d5043b18bdfab98646/readme_image/cafemap_tap3-1.png" width="150" /> | <img src="https://github.com/jerry7129/madcamp-week1/blob/b62611241a9079d582d758ad5f664941e98f3032/readme_image/cafemap_tap2-2.png" width="150" /> |

<br>

### 🗺️ 1. 실시간 재고 지도 (Real-time Map)
* **네이버 지도 연동:** 네이버 지도를 기반으로 카페 위치를 직관적인 마커로 표시합니다.
* **스마트 배지 (Smart Badge):** 마커 위에 부착된 배지 색상으로 재고 상태를 즉시 파악할 수 있습니다.
    * 🔘 **Gray:** 재고 품절 (OUT_OF_STOCK)
    * 🔴 **Red:** 재고 부족 (Low)
    * 🟠 **Orange:** 재고 여유 (Normal)
    * 🟢 **Green:** 재고 여유 (Plenty)
* **내 위치 추적 (Location Tracking):** 실시간 위치 추적(Follow 모드)을 지원하여 내 주변에 있는 카페를 빠르게 탐색합니다.

### 🔎 2. 스마트 검색 & 리스트 (Search & Filter)
* **스마트 검색:** 카페 이름이나 '성수동', '강남' 같은 지역명을 검색하면, 지도 카메라가 해당 위치로 즉시 애니메이션 이동합니다.
* **가게 리스트:** 지도뿐만 아니라 리스트 형태로도 전체 카페 목록을 확인할 수 있습니다.
* **정렬 및 필터:** 사용자가 원하는 기준으로 카페를 정렬할 수 있습니다.
    * ⭐ **별점순:** 인기 있는 카페 확인
    * 📦 **재고순:** 현재 구매 가능한 카페 우선 확인
    * 🕒 **업데이트순:** 가장 최근 정보를 갱신한 카페 확인

### 🔐 3. 관리자 모드 (Admin Mode)
* **소셜 로그인 (Social Login):** Google, Kakao 계정을 연동하여 안전하게 관리자 권한을 획득합니다.
* **가게 관리 (CRUD):** 관리자 권한으로 신규 카페 정보를 추가하거나, 기존 정보를 수정 및 삭제할 수 있습니다.
* **실시간 재고 수정:** 복잡한 과정 없이 버튼 하나로 실시간 재고 수량을 업데이트하여 사용자들에게 최신 정보를 제공합니다.

<br>

## ⚙️ 설치 및 실행 방법 (Installation)

이 프로젝트는 API Key 설정이 필요합니다.

1. **레포지토리 클론**
    ```bash
    git clone [https://github.com/jerry7129/madcamp-week1.git](https://github.com/jerry7129/madcamp-week1.git)
    ```
2. **`local.properties` 설정** (프로젝트 루트 경로)
    * 네이버 지도 클라이언트 ID와 Secret Key가 필요합니다.
    ```properties
    sdk.dir=...
    NAVERMAP_CLIENT_ID=네이버_클라이언트_ID
    KAKAO_NATIVE_APP_KEY=카카오_네이티브_앱_키
    GOOGLE_WEB_CLIENT_ID=구글_웹_클라이언트_ID
    ```
3. **`google-services.json` 추가**
    * Firebase 프로젝트 설정 파일(`google-services.json`)을 `app/` 폴더 안에 위치시켜야 합니다.
4. **Android Studio에서 실행**
    * `Sync Project with Gradle Files` 클릭 후 `Run` (Min SDK: 24)
