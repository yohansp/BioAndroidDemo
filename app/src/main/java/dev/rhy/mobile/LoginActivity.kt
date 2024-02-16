package dev.rhy.mobile

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import dev.rhy.mobile.ui.theme.BioDemoTheme
import dev.rhy.mobile.utils.Cache
import java.util.concurrent.Executor

class LoginActivity: FragmentActivity() {

    val viewModel: LoginViewModel by viewModels()
    lateinit var executor: Executor
    lateinit var biometricPrompt: BiometricPrompt
    lateinit var promptInfo: BiometricPrompt.PromptInfo
    lateinit var biometricManager: BiometricManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // setup
        setupBiometric()

        // init observer
        viewModel.liveLogin.observe(this) { token ->
            Cache.instance().saveToken(token.accessToken)
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            this.finish()
        }

        viewModel.liveHandleLoginBio.observe(this) {
            biometricPrompt.authenticate(promptInfo)
        }

        setContent {
            BioDemoTheme {
                Surface (
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ){
                    loginForm(viewModel)
                }
            }
        }
    }

    private fun setupBiometric() {
        biometricManager = BiometricManager.from(this)
        when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG
                or BiometricManager.Authenticators.DEVICE_CREDENTIAL)) {
            BiometricManager.BIOMETRIC_SUCCESS ->
                Log.d("BIO", "---> App can use biometric")
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE ->
                Log.d("BIO", "---> No biometric feature available in this device")
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE ->
                Log.d("BIO", "---> Biometric feature are currently unavailable")
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                    putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED, BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL)
                }
                val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                    if (result.resultCode == Activity.RESULT_OK) {
                    }
                }
                resultLauncher.launch(enrollIntent)
            }
        }

        executor = ContextCompat.getMainExecutor(this)
        biometricPrompt = BiometricPrompt(this, executor, object:BiometricPrompt.AuthenticationCallback(){
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                Log.d("BIO", "error $errString")
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                Log.d("BIO", "failed")
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                Log.d("BIO", "success")

                // start request to BE
                viewModel.doLoginBio()
            }
        })
        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Login Dengan Biometric")
            .setSubtitle("Silahkan Anda Login")
            .setNegativeButtonText("Use Password")
            .build()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun loginForm(viewModel: LoginViewModel) {

    var phoneNumber by remember { mutableStateOf("081311137368") }
    var pin by remember { mutableStateOf("123456") }

    Column (
        verticalArrangement = Arrangement.Top,
        modifier = Modifier
            .padding(horizontal = 30.dp, vertical = 30.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 0.dp, vertical = 50.dp),
            horizontalArrangement = Arrangement.Center
        ){
            Card(modifier = Modifier.size(100.dp),
                shape = CircleShape,
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 3.dp
                )
            ) {
                Image(
                    alignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize(),
                    painter = painterResource(id = R.drawable.biometric),
                    contentDescription = ""
                )
            }
        }
        Spacer(modifier = Modifier.height(50.dp))
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = phoneNumber,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Phone),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent
            ),
            trailingIcon = {Icon(
                imageVector = Icons.Default.Phone,
                contentDescription = null
            )},
            onValueChange = {
                phoneNumber = it
            },
            label = {
                Text("Phone Number")
            }
        )
        Spacer(modifier = Modifier.height(10.dp))
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = pin,
            visualTransformation = PasswordVisualTransformation(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent
            ),
            onValueChange = {
                pin = it
            },
            label = {
                Text("Pin")
            },
            placeholder = { Text(text = "Input pin")}
        )
        Spacer(modifier = Modifier.height(50.dp))
        Row (
            modifier = Modifier.fillMaxWidth()
        ){
            OutlinedButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.5f),
                onClick = {
                    viewModel.doLogin(phoneNumber,pin)
                }
            ) {
                Text(text = "Login")
            }
            Spacer(modifier = Modifier.width(15.dp))
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.5f),
                onClick = {
                    viewModel.actionLoginBio()
                }
            ) {
                Text(text = "Login Fingerint")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginPreview() {
    BioDemoTheme {
        loginForm(LoginViewModel())
    }
}