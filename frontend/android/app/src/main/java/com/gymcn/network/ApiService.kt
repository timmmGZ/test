package com.gymcn.network

import com.gymcn.model.ApiResponse
import com.gymcn.model.LoginRequest
import com.gymcn.model.LoginResponse
import com.gymcn.model.User
import com.gymcn.model.Venue
import com.gymcn.model.VenueListResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * API 接口定义
 */
interface ApiService {

    // ==================== 认证相关 ====================
    
    /**
     * 发送验证码
     */
    @POST("api/v1/auth/sms/send")
    suspend fun sendSmsCode(@Body phone: Map<String, String>): ApiResponse<Unit>
    
    /**
     * 登录
     */
    @POST("api/v1/auth/login")
    suspend fun login(@Body request: LoginRequest): ApiResponse<LoginResponse>
    
    /**
     * 获取当前用户信息
     */
    @GET("api/v1/user/profile")
    suspend fun getProfile(): ApiResponse<User>

    // ==================== 场馆相关 ====================
    
    /**
     * 获取场馆列表
     */
    @GET("api/v1/venues")
    suspend fun getVenues(
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20,
        @Query("category") category: String? = null,
        @Query("latitude") latitude: Double? = null,
        @Query("longitude") longitude: Double? = null
    ): ApiResponse<VenueListResponse>
    
    /**
     * 获取场馆详情
     */
    @GET("api/v1/venues/{id}")
    suspend fun getVenueDetail(@Path("id") venueId: String): ApiResponse<Venue>

    // ==================== 预约相关 ====================
    
    /**
     * 预约场馆
     */
    @POST("api/v1/bookings")
    suspend fun createBooking(@Body request: Map<String, Any>): ApiResponse<Unit>
    
    /**
     * 获取预约记录
     */
    @GET("api/v1/bookings")
    suspend fun getBookings(
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20
    ): ApiResponse<Any> // TODO: 定义 Booking 模型
}