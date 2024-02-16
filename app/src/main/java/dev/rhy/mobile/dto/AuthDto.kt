package dev.rhy.mobile.dto

import com.squareup.moshi.Json


data class TokenRequest(
    @field:Json(name ="phone_number")
    val phoneNumber: String,
    val pin: String
)

data class TokenBioRequest(
    @field:Json(name ="phone_number")
    val phoneNumber: String,
    val data: String
)

data class BiometricData(val expiredTime: Long, val phoneNumber: String)

data class TokenResponse(
    @field:Json(name = "access_token")
    val accessToken: String,

    @field:Json(name = "refresh_token")
    val refreshToken: String
)

data class SharedKeyRequest(
    val pin: String
)

data class SharedKeyResponse(
    @field:Json(name = "shared_key")
    val sharedKey: String
)