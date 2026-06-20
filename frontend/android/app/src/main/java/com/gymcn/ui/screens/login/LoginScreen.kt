package com.gymcn.ui.screens.login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gymcn.model.LoginRequest
import com.gymcn.network.ApiService
import com.gymcn.network.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 登录页面 ViewModel
 * 用于管理 UI 状态和业务逻辑
 */
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val apiService: ApiService,
    private val tokenManager: TokenManager
) : ViewModel() {

    var uiState by mutableStateOf(LoginUiState())
        private set

    fun updatePhone(phone: String) {
        uiState = uiState.copy(phone = phone, errorMessage = null)
    }

    fun updateCode(code: String) {
        uiState = uiState.copy(code = code, errorMessage = null)
    }

    fun sendSmsCode() {
        if (uiState.phone.length != 11) {
            uiState = uiState.copy(errorMessage = "请输入正确的手机号")
            return
        }

        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true)
            
            runCatching {
                apiService.sendSmsCode(mapOf("phone" to uiState.phone))
            }.fold(
                onSuccess = {
                    uiState = uiState.copy(
                        isLoading = false,
                        isCodeSent = true,
                        countdown = 60,
                        errorMessage = null
                    )
                    startCountdown()
                },
                onFailure = {
                    uiState = uiState.copy(
                        isLoading = false,
                        errorMessage = it.message ?: "发送验证码失败"
                    )
                }
            )
        }
    }

    private fun startCountdown() {
        viewModelScope.launch {
            repeat(60) {
                kotlinx.coroutines.delay(1000)
                uiState = uiState.copy(countdown = uiState.countdown - 1)
            }
            uiState = uiState.copy(isCodeSent = false)
        }
    }

    fun login(onSuccess: () -> Unit) {
        if (uiState.phone.length != 11) {
            uiState = uiState.copy(errorMessage = "请输入正确的手机号")
            return
        }
        if (uiState.code.length != 6) {
            uiState = uiState.copy(errorMessage = "请输入6位验证码")
            return
        }

        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true)

            runCatching {
                val response = apiService.login(LoginRequest(uiState.phone, uiState.code))
                response.getDataOrThrow()
            }.fold(
                onSuccess = { loginResponse ->
                    tokenManager.saveToken(loginResponse.token)
                    tokenManager.saveUserId(loginResponse.user.id)
                    uiState = uiState.copy(isLoading = false)
                    onSuccess()
                },
                onFailure = {
                    uiState = uiState.copy(
                        isLoading = false,
                        errorMessage = it.message ?: "登录失败"
                    )
                }
            )
        }
    }
}

/**
 * 登录页面 UI 状态
 */
data class LoginUiState(
    val phone: String = "",
    val code: String = "",
    val isLoading: Boolean = false,
    val isCodeSent: Boolean = false,
    val countdown: Int = 0,
    val errorMessage: String? = null
)

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState

    LoginScreenContent(
        uiState = uiState,
        onPhoneChange = { viewModel.updatePhone(it) },
        onCodeChange = { viewModel.updateCode(it) },
        onSendCode = { viewModel.sendSmsCode() },
        onLogin = { viewModel.login(onLoginSuccess) }
    )
}

@Preview(showBackground = false)
@Composable
fun LoginScreenPreview() {
    LoginScreenContent(
        uiState = LoginUiState(phone = "13800138000"),
        onPhoneChange = {},
        onCodeChange = {},
        onSendCode = {},
        onLogin = {}
    )
}

@Composable
private fun LoginScreenContent(
    uiState: LoginUiState,
    onPhoneChange: (String) -> Unit,
    onCodeChange: (String) -> Unit,
    onSendCode: () -> Unit,
    onLogin: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Logo
        Text(
            text = "GymCN",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "广州健身一卡通",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(48.dp))

        // 手机号输入
        OutlinedTextField(
            value = uiState.phone,
            onValueChange = onPhoneChange,
            label = { Text("手机号") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 验证码输入
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = uiState.code,
                onValueChange = onCodeChange,
                label = { Text("验证码") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.weight(1f)
            )

            Button(
                onClick = onSendCode,
                enabled = !uiState.isCodeSent && !uiState.isLoading,
                modifier = Modifier.width(120.dp)
            ) {
                Text(
                    if (uiState.isCodeSent) "${uiState.countdown}s"
                    else "获取验证码"
                )
            }
        }

        // 错误提示
        uiState.errorMessage?.let { error ->
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 登录按钮
        Button(
            onClick = onLogin,
            enabled = !uiState.isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("登录")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "登录即表示同意《用户协议》和《隐私政策》",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
