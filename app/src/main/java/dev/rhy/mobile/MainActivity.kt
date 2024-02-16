package dev.rhy.mobile

import android.content.Context
import android.content.Intent
import android.graphics.fonts.FontStyle
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.rhy.mobile.ui.theme.BioDemoTheme
import dev.rhy.mobile.utils.Cache

class MainActivity : ComponentActivity() {

    val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BioDemoTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Android Dev", viewModel)
                }
            }
        }

        viewModel.liveHandleSharedKey.observe(this) {
            Cache.instance().saveKey(it.sharedKey)
            Toast.makeText(this, "Key Saved", Toast.LENGTH_LONG).show()
        }

        viewModel.actionCheckIfBiometricActivated(this)
    }
}

@Composable
fun Greeting(name: String, viewModel: LoginViewModel) {

    val context = LocalContext.current as MainActivity

    Column (
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(120.dp))
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
        Spacer(modifier = Modifier.height(30.dp))
        Column (modifier = Modifier
            .fillMaxSize()
            .weight(0.5f)
            .padding(horizontal = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Text( text = "Welcome Home", color = Color.Black,
                fontSize = 30.sp,
                textAlign = TextAlign.Center)
            Text( text = name, fontSize = 30.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(30.dp))
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    viewModel.doGetSharedKey()
                },
                enabled = viewModel.btnStatus.value
            ) {
                Text(text = viewModel.btnTitle.value)
            }
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                val intent = Intent(context, LoginActivity::class.java)
                context.startActivity(intent)
                context.finish()
            }) {
                Text(text = "Logout")
            }
        }
        Row (modifier = Modifier
            .fillMaxSize()
            .weight(0.5f)){
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    BioDemoTheme {
        Greeting("Android Dev", LoginViewModel())
    }
}