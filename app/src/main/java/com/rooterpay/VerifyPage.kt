package com.rooterpay

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.ui.platform.LocalContext
import com.rooterpay.ui.theme.RooterTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class VerifyPage : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RooterTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    VerifyScreen(Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun VerifyScreen(modifier: Modifier = Modifier) {
    var code by remember { mutableStateOf("") }
    var countdown by remember { mutableStateOf(60) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // Start countdown timer
    LaunchedEffect(key1 = Unit) {
        while (countdown > 0) {
            delay(1000)
            countdown--
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Login",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 48.dp)
        )

        Text(
            text = "Code",
            fontSize = 18.sp,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(bottom = 8.dp)
        )

        // 6-digit code input boxes
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Using a single BasicTextField but displaying as 6 separate boxes
            BasicTextField(
                value = code,
                onValueChange = { newValue ->
                    if (newValue.length <= 6 && newValue.all { it.isDigit() }) {
                        code = newValue
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                textStyle = TextStyle(fontSize = 20.sp, textAlign = TextAlign.Center)
            ) { innerTextField ->
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    for (i in 0 until 6) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(48.dp)
                                .border(1.dp, Color.Black, RoundedCornerShape(8.dp))
                        ) {
                            Text(
                                text = if (i < code.length) code[i].toString() else "",
                                fontSize = 20.sp
                            )
                        }
                    }
                }
            }
        }

        // Resend button with countdown
        if (countdown == 0) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            ) {
                Text(
                    text = "Click HERE",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.primary,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier.clickable(enabled = countdown == 0) {
                        // Resend code logic
                        scope.launch {
                            // Call your API to resend the code
                            countdown = 60 // Reset countdown
                        }
                    }
                )
                Text(
                    text = " to send the email again.",
                    fontSize = 16.sp
                )
            }
        } else {
            Text(
                text = "Resend in ${countdown}s...",
                fontSize = 16.sp,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(bottom = 24.dp)
            )
        }

        // Cancel and Enter buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = { /* Handle cancel button click */ },
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
                    .padding(end = 8.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color.Black
                ),
                border = BorderStroke(1.dp, Color.Black)
            ) {
                Text("Cancel", fontSize = 18.sp)
            }

            Button(
                onClick = {
                    val intent = Intent(context, KeypadPage::class.java)
                    context.startActivity(intent)
                },
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
                    .padding(start = 8.dp),
                shape = RoundedCornerShape(8.dp),
                enabled = code.length == 6
            ) {
                Text("Enter", fontSize = 18.sp)
            }
        }
    }
}
