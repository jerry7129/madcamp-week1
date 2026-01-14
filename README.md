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
| **지도 & 내 위치 추적**<br>실시간 마커 배지 확인<br>Follow 모드로 주변 탐색 | **스마트 검색**<br>지역/카페명 검색 및 이동<br>카페 정보 및 <br>네이버 플레이스 연결 | **리스트 보기 & 정렬**<br>별점/재고순 필터링<br>별점 부여 | **관리자 권한 획득**<br>소셜 로그인 사용<br>Kakao, Google | **카페 및 재고 관리**<br>카페 등록<br>주소->좌표 변환 가능<br>카페 삭제<br>실시간 수량 업데이트 |
| <img src="https://github.com/jerry7129/madcamp-week1/blob/aacf79bb69006402dc9be96b5c5cea0fbb8f9f39/readme_image/cafemap_tap1.png" width="150" /> | <img src="https://github.com/jerry7129/madcamp-week1/blob/aacf79bb69006402dc9be96b5c5cea0fbb8f9f39/readme_image/cafemap_tap1-1.png" width="150" /> | <img src="https://github.com/jerry7129/madcamp-week1/blob/aacf79bb69006402dc9be96b5c5cea0fbb8f9f39/readme_image/cafemap_tap2.png" width="150" /> | <img src="https://github.com/jerry7129/madcamp-week1/blob/aacf79bb69006402dc9be96b5c5cea0fbb8f9f39/readme_image/cafemap_tap3.png" width="150" /> | <img src="https://github.com/jerry7129/madcamp-week1/blob/b62611241a9079d582d758ad5f664941e98f3032/readme_image/cafemap_tap2-1.png" width="150" /> |
|| <img src="https://github.com/jerry7129/madcamp-week1/blob/aacf79bb69006402dc9be96b5c5cea0fbb8f9f39/readme_image/cafemap_tap1-2.png" width="150" /> | <img src="https://github.com/jerry7129/madcamp-week1/blob/b62611241a9079d582d758ad5f664941e98f3032/readme_image/cafemap_tap2-3.png" width="150" /> | <img src="https://github.com/jerry7129/madcamp-week1/blob/4539de4e70cd54ebc285e5d5043b18bdfab98646/readme_image/cafemap_tap3-1.png" width="150" /> | <img src="https://github.com/jerry7129/madcamp-week1/blob/b62611241a9079d582d758ad5f664941e98f3032/readme_image/cafemap_tap2-2.png" width="150" /> |
||||| <img src="https://github.com/jerry7129/madcamp-week1/blob/a9793394413cf00186654c3b0ccf726ca43f68e2/readme_image/cafemap_tap2-4.png" width="150" /> |
||||| <img src="https://github.com/jerry7129/madcamp-week1/blob/a9793394413cf00186654c3b0ccf726ca43f68e2/readme_image/cafemap_tap2-5.png" width="150" /> |

<br>

### 1. 📍 실시간 재고 지도 (Real-time Stock Map)
* **실시간 위치 추적:** 사용자의 현재 위치를 기반으로 지도가 이동하며, 이동 중에도 'Follow 모드'를 통해 지속적으로 위치를 트래킹합니다.
* **직관적인 마커 배지:** 지도 위에 표시된 각 카페 마커에는 현재 남아있는 재고 수량이 배지(Badge) 형태로 실시간 표시되어, 일일이 클릭하지 않아도 재고 현황을 한눈에 파악할 수 있습니다.

### 2. 🔍 스마트 카페 검색 (Smart Search)
* **지역 및 상호 검색:** 특정 지역명이나 카페 이름을 검색하면 해당 위치로 지도가 즉시 이동하여 탐색 시간을 줄여줍니다.
* **상세 정보 및 연동:** 마커를 클릭하면 카페의 기본 정보가 나타나며, '네이버 플레이스' 버튼을 통해 영업시간, 리뷰 등 더 풍부한 외부 정보로 쉽게 접근할 수 있습니다.

### 3. 📋 카페 리스트 & 평가 (Cafe List & Rating)
* **맞춤형 정렬 필터:** 단순히 거리순뿐만 아니라 '재고 많은 순', '별점 높은 순' 등 사용자의 필요에 따라 리스트를 정렬하여 보여줍니다.
* **사용자 참여형 별점:** 사용자가 직접 방문한 카페에 별점을 부여할 수 있는 기능을 제공하여, 객관적인 지표 외에도 사용자들의 만족도를 데이터에 반영합니다.

### 4. 🔐 관리자 모드 (Admin Authentication)
* **소셜 로그인 연동:** Google 및 Kakao 로그인을 지원하여 복잡한 가입 절차 없이 안전하게 인증할 수 있습니다.
* **권한 분리:** 일반 사용자와 관리자를 철저히 구분하여, 인증된 계정만이 데이터(카페 정보, 재고)를 수정할 수 있도록 보안을 강화했습니다.

### 5. 🛠️ 카페 및 재고 관리 (Admin Dashboard)
* **카페 등록 및 좌표 변환:** 관리자가 카페의 주소를 입력하면 Geocoding을 통해 자동으로 위도/경도 좌표로 변환되어 지도 데이터베이스에 등록됩니다.
* **실시간 재고/데이터 수정:** 매장의 재고 수량을 실시간으로 변경하여 사용자에게 즉각 반영하거나, 폐점/등록 취소된 카페를 목록에서 삭제하는 등 앱의 핵심 데이터를 손쉽게 유지 보수할 수 있습니다.

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
