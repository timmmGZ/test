package com.gymcn.model

/**
 * 用户模型
 */
data class User(
    val id: String,
    val phone: String,
    val name: String?,
    val avatar: String?,
    val companyId: String?,
    val companyName: String?,
    val cardType: String?,
    val createdAt: String?
)

/**
 * 登录请求
 */
data class LoginRequest(
    val phone: String,
    val code: String
)

/**
 * 登录响应
 */
data class LoginResponse(
    val token: String,
    val user: User
)

/**
 * 通用 API 响应
 */
data class ApiResponse<T>(
    val code: Int,
    val message: String,
    val data: T?
) {
    fun isSuccess(): Boolean = code == 200
    
    fun getDataOrThrow(): T {
        if (!isSuccess()) throw ApiException(code, message)
        return data ?: throw ApiException(-1, "Data is null")
    }
}

/**
 * API 异常
 */
class ApiException(val code: Int, message: String) : Exception(message)