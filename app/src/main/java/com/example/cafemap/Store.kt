package com.example.cafemap

// Firebase NoSQL에서 사용하기 좋게 Enum 대신 String이나 기본 타입을 권장하기도 하지만,
// 현재 구조를 유지하려면 아래와 같이 작성합니다.
enum class StockStatus {
    OUT_OF_STOCK, LOW, NORMAL, PLENTY
}

data class Store(
    val id: String = "",            // id는 자료 다시 수정할 때 필수임. Main Activity에서 설정해주면 좋음. 대표적으로 가게 name 그대로 사용하기?
    val name: String = "",          // 가게 등록 시 입력 필수 사항 or 네이버 지도에서 선택 시 자동으로 할당
    val mapLink: String = "",       // 가게 등록 시 입력 필수 사항 or 네이버 지도에서 선택 시 자동으로 할당
    val description: String = "",   // 가게 등록 시 입력 선택 사항
    val region: String = "",        // 가게 등록 시 입력 필수 사항 or 네이버 지도에서 선택 시 자동으로 할당
    var stockCount: Int = 0,
    var stockStatus: StockStatus = StockStatus.OUT_OF_STOCK,
    var lastUpdated: Long = System.currentTimeMillis(),
    var avgRating: Float = 0.0f,
    var reviewCount: Int = 0
) {
    /*
    fun updateStock(count: Int) {   // StoreRepository에서 구현되고, 여기에선 사용 안 될 수도
        this.stockCount = count
        this.stockStatus = when {
            count <= 0 -> StockStatus.OUT_OF_STOCK      // 상태 판단 기준 설정 바람
            count <= 5 -> StockStatus.LOW               // 상태 판단 기준 설정 바람
            count <= 20 -> StockStatus.NORMAL           // 상태 판단 기준 설정 바람
            else -> StockStatus.PLENTY
        }
        this.lastUpdated = System.currentTimeMillis()
    }
    */

    /*
    fun updateAverageRating(newRating: Float) {
        val totalScore = (this.avgRating * this.reviewCount) + newRating
        this.reviewCount++
        this.avgRating = String.format("%.2f", totalScore / this.reviewCount).toFloat()
    }

     */
}