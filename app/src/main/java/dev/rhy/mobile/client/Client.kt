package dev.rhy.mobile.client

import dev.rhy.mobile.dto.SharedKeyRequest
import dev.rhy.mobile.dto.SharedKeyResponse
import dev.rhy.mobile.dto.TokenBioRequest
import dev.rhy.mobile.dto.TokenRequest
import dev.rhy.mobile.dto.TokenResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.PATCH
import retrofit2.http.POST

interface Client {
    @POST("auth/token")
    suspend fun requestToken(@Body data: TokenRequest): Response<TokenResponse>

    @POST("auth/biometric")
    suspend fun requestBioToken(@Body data: TokenBioRequest): Response<TokenResponse>

    @PATCH("auth/biometric/sharedkey")
    suspend fun requestGenerateKey(@Body data: SharedKeyRequest): Response<SharedKeyResponse>
}