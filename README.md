# 🍪 Cafe Map: 두쫀쿠맵을 잇는 디저트 지도

> **KAIST MadCamp 2025 Winter - Week 1 Project**
> **개발 기간:** 2026.01.08 ~ 2026.01.14

<br>

## 🧑‍💻 팀원 소개 (Team Members)

| 이름 (Name) | 역할 (Role) | 학교/학과 (Affiliation) | GitHub |
| :---: | :---: | :---: | :---: |
| **김승관** | Android Developer<br>(Map, UI/UX) | 한양대학교<br>컴퓨터소프트웨어학부 | [@jerry7129](https://github.com/jerry7129) |
| **김현우** | Android Developer<br>(Firebase, Auth) | [학교/학과 입력] | [@GithubID](https://github.com/GithubID) |

<br>

## 💡 프로젝트 소개 (Project Introduction)
**Cafe Map**은 품절 대란을 일으키는 유행 디저트의 **실시간 재고를 지도에서 한눈에 확인하는 앱**입니다.
기존의 '두바이 쫀득 쿠키 맵'에서 영감을 받아, 사장님과 고객을 연결하는 플랫폼으로 확장했습니다.

* **Target User:** 헛걸음 없이 핫한 디저트를 먹고 싶은 **고객**, 효과적인 홍보가 필요한 **카페 사장님**
* **Key Value:** 실시간 재고 확인(Green/Red 마커), 위치 기반 카페 탐색, 간편한 입점 신청

<br>

## 🛠️ 기술 스택 (Tech Stack)

| 구분 | 기술 (Stack) |
| :---: | :--- |
| **Language** | <img src="https://img.shields.io/badge/Kotlin-7F52FF?style=flat&logo=Kotlin&logoColor=white"/> |
| **IDE** | <img src="https://img.shields.io/badge/Android Studio-3DDC84?style=flat&logo=Android Studio&logoColor=white"/> |
| **Backend / DB** | <img src="https://img.shields.io/badge/Firebase-FFCA28?style=flat&logo=Firebase&logoColor=black"/> <img src="https://img.shields.io/badge/Firestore-FFCA28?style=flat&logo=Firebase&logoColor=black"/> |
| **Map API** | <img src="https://img.shields.io/badge/Naver Maps-03C75A?style=flat&logo=Naver&logoColor=white"/> |
| **Library** | <img src="https://img.shields.io/badge/Glide-181717?style=flat&logo=Glide&logoColor=white"/> <img src="https://img.shields.io/badge/Credential Manager-4285F4?style=flat&logo=Google&logoColor=white"/> |
| **Collaboration** | <img src="https://img.shields.io/badge/Notion-000000?style=flat&logo=Notion&logoColor=white"/> <img src="https://img.shields.io/badge/Git-F05032?style=flat&logo=Git&logoColor=white"/> |

<br>

## 📱 주요 기능 (Key Features)

| 1. 지도 & 재고 확인 | 2. 카페 검색 & 리스트 | 3. 사장님 모드 (재고 관리) |
| :---: | :---: | :---: |
| <img src="https://via.placeholder.com/200x400?text=Map+Screen" width="200" /> | <img src="https://via.placeholder.com/200x400?text=Search+Screen" width="200" /> | <img src="https://via.placeholder.com/200x400?text=Admin+Screen" width="200" /> |
| **실시간 재고 마커 표시**<br>재고 수량에 따라 마커 색상 변경<br>(품절: 회색, 여유: 초록) | **지역/카페명 검색**<br>원하는 카페를 리스트로 확인하고<br>지도 위치로 바로 이동 | **실시간 재고 수정**<br>사장님 권한으로 로그인 시<br>간편하게 재고 수량 업데이트 |

* **Tab 1 (Map):** 네이버 지도 API를 활용하여 사용자 위치 기반으로 카페 마커를 표시합니다. `OverlayImage`와 `ShapeDrawable`을 활용해 재고 상태를 직관적인 UI로 표현했습니다.
* **Tab 2 (Search):** 등록된 카페 리스트를 검색하고, 상세 정보를 확인할 수 있습니다.
* **Feature (Auth):** 구글/카카오 소셜 로그인을 지원하며, `Admin` 권한이 있는 사용자는 카페 등록 요청 및 재고 관리가 가능합니다.

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
    NAVER_CLIENT_ID=여러분의_CLIENT_ID
    NAVER_CLIENT_SECRET=여러분의_SECRET_KEY
    ```
3. **`google-services.json` 추가**
    * Firebase 프로젝트 설정 파일(`google-services.json`)을 `app/` 폴더 안에 위치시켜야 합니다.
4. **Android Studio에서 실행**
    * `Sync Project with Gradle Files` 클릭 후 `Run` (Min SDK: 24)
