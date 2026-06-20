package com.gymcn.model

/**
 * 场馆模型
 */
data class Venue(
    val id: String,
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val phone: String?,
    val images: List<String>,
    val category: String,
    val facilities: List<String>,
    val businessHours: String?,
    val rating: Double?,
    val distance: Double? = null
)

/**
 * 场馆列表响应
 */
data class VenueListResponse(
    val venues: List<Venue>,
    val total: Int,
    val page: Int,
    val pageSize: Int
)

/**
 * 场馆分类
 */
enum class VenueCategory(val displayName: String) {
    GYM("健身房"),
    SWIMMING("游泳馆"),
    YOGA("瑜伽馆"),
    BADMINTON("羽毛球馆"),
    BASKETBALL("篮球馆"),
    TENNIS("网球场"),
    OTHER("其他")
}