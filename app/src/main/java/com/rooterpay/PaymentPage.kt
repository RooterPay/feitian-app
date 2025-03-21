package com.rooterpay

import android.app.AlertDialog
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rooterpay.ui.theme.RooterTheme
import com.rooterpay.utils.Currency
import com.rooterpay.utils.currencyFormatter

class PaymentPage : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val amount = intent.getStringExtra("PAYMENT_AMOUNT") ?: ""
        val description = intent.getStringExtra("PAYMENT_DESCRIPTION") ?: ""
        if (amount.isEmpty()) {
            AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage("Payment amount is missing!")
                .setPositiveButton("OK") { dialog, _ ->
                    dialog.dismiss()
                    finish()  // Ends the current Activity, going back
                }
                .setCancelable(false)
                .show()
        }

        enableEdgeToEdge()
        setContent {
            RooterTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    PaymentScreen(Currency.EUR, amount, description, Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun PaymentScreen(
    currency: Currency,
    amount: String,
    description: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 40.dp),
                horizontalArrangement = Arrangement.Absolute.Right
            ) {
                IconButton(onClick = {}) {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = "Delete",
                        modifier = Modifier
                            .size(32.dp)
                            .clickable { }
                    )
                }
            }

            // Amount display
            Spacer(modifier = Modifier.weight(0.2f))
            Text(
                text = currencyFormatter(currency, amount),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                fontSize = 36.sp
            )

            // Description
            Text(
                text = description,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            // Icon placeholder
            Surface(
                shape = CircleShape,
                border = BorderStroke(2.dp, MaterialTheme.colorScheme.onSurface),
                modifier = Modifier
                    .padding(vertical = 24.dp)
                    .size(100.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "ICON",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Tap to pay text
            Text(
                text = "Tap to pay",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            Spacer(modifier = Modifier.weight(0.3f))

            // Navigation dots
            Row(
                modifier = Modifier
                    .padding(bottom = 24.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(4) {
                    Surface(
                        shape = CircleShape,
                        color = Color.LightGray,
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .size(16.dp)
                    ) {}
                }
            }
        }
    }
}
