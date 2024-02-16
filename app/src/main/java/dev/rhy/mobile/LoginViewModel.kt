package dev.rhy.mobile

import android.content.Context
import android.util.Base64
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.squareup.moshi.Moshi
import dev.rhy.mobile.client.Client
import dev.rhy.mobile.client.ClientConfig
import dev.rhy.mobile.dto.BiometricData
import dev.rhy.mobile.dto.SharedKeyRequest
import dev.rhy.mobile.dto.SharedKeyResponse
import dev.rhy.mobile.dto.TokenBioRequest
import dev.rhy.mobile.dto.TokenRequest
import dev.rhy.mobile.dto.TokenResponse
import dev.rhy.mobile.utils.Cache
import kotlinx.coroutines.launch
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.util.Calendar
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class LoginViewModel: ViewModel() {

    val liveError: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val liveLogin: MutableLiveData<TokenResponse> by lazy { MutableLiveData<TokenResponse>() }
    val liveHandleLoginBio: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val liveHandleSharedKey: MutableLiveData<SharedKeyResponse> by lazy { MutableLiveData<SharedKeyResponse>() }
    var btnTitle = mutableStateOf("Activate Fingerprint")
    var btnStatus = mutableStateOf(false)

    private val client: Client by lazy { ClientConfig.instance().create(Client::class.java) }

    fun doLogin(phoneNumber: String, pin: String) {
        viewModelScope.launch {
            val md = MessageDigest.getInstance("MD5")
            val hexPin = md.digest(pin.toByteArray()).joinToString(""){ "%02x".format(it) }
            val response = client.requestToken(
                TokenRequest(phoneNumber, hexPin)
            )
            if (response.isSuccessful) {
                liveLogin.value = response.body()
            }
        }
    }

    fun doGetSharedKey() {
        viewModelScope.launch {
            val md = MessageDigest.getInstance("MD5")
            val hexPin = md.digest("123456".toByteArray()).joinToString(""){ "%02x".format(it) }
            val response = client.requestGenerateKey(SharedKeyRequest(hexPin))
            if (response.isSuccessful) {
                liveHandleSharedKey.value = response.body()
            }
        }
    }

    fun actionLoginBio() {
        liveHandleLoginBio.value = ""
    }

    fun doLoginBio() {
        viewModelScope.launch {

            val phone = "081311137368"
            val expiredTime = Calendar.getInstance()
            expiredTime.add(Calendar.YEAR, 1)
            val dataBiometric = BiometricData(expiredTime.timeInMillis, phone)
            val moshi = Moshi.Builder().build()
            val adapter = moshi.adapter(BiometricData::class.java)
            val jsonDataBiometric = Base64.encodeToString(
                adapter.toJson(dataBiometric).toByteArray(StandardCharsets.UTF_8),
                Base64.NO_WRAP)

            val dataSecretKey = Base64.decode(Cache.instance().getKey(), Base64.NO_WRAP);
            val secretKey = SecretKeySpec(dataSecretKey, 0, dataSecretKey.size, "AES")

            val cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            val ivspec = IvParameterSpec("MjAyNC0wMi0wOCAw".toByteArray(StandardCharsets.UTF_8));
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivspec);

            val encryptedData = cipher.doFinal(jsonDataBiometric.toByteArray(StandardCharsets.UTF_8));
            val resultEncoded = Base64.encodeToString(encryptedData, Base64.NO_WRAP)

            val response = client.requestBioToken(TokenBioRequest(phone, resultEncoded))
            if (response.isSuccessful) {
                liveLogin.value = response.body()
            }
        }
    }

    fun actionCheckIfBiometricActivated(context: Context) {
        val key = Cache.instance().getKey()
        if (key.isNotEmpty()) {
            btnStatus.value = false
            btnTitle.value = "Already Active"
        } else {
            btnStatus.value = true
            btnTitle.value = "Activate Fingerprint"
        }
    }
}