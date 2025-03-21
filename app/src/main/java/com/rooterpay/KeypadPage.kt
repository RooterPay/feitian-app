package com.rooterpay

import android.content.Intent
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
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rooterpay.ui.theme.RooterTheme
import com.rooterpay.utils.Currency
import com.rooterpay.utils.currencyFormatter

class KeypadPage : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            RooterTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    KeypadScreen(Currency.EUR, Modifier.padding(innerPadding)) // TODO: get the currency from the app wrapper
                }
            }
        }
    }
}

@Composable
fun KeypadScreen(currency: Currency, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var amount by remember { mutableStateOf("0.00") }
    var description by remember { mutableStateOf("") }
    var showDescriptionDialog by remember { mutableStateOf(false) }

    fun isValidAmount(): Boolean {
        return amount.toDoubleOrNull()?.let { it > 0.0 } ?: false
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Pay",
                fontSize = 34.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(start = 8.dp)
            )

            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.Outlined.Menu,
                    contentDescription = "Menu",
                    modifier = Modifier
                        .size(32.dp)
                        .clickable { }
                )
            }
        }

        // Amount display
        Text(
            text = currencyFormatter(currency, amount),
            fontSize = 52.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        // Description
        Text(
            text = if (description.isEmpty()) "+ add description" else description,
            fontSize = 18.sp,
            color = if (description.isEmpty()) Color.Gray else Color.Black,
            modifier = Modifier
                .clickable { showDescriptionDialog = true }
                .padding(bottom = 32.dp)
        )

        // Number pad
        NumberPad(
            onNumberClick = { digit ->
                // Simple implementation - in a real app you'd need more sophisticated handling
                if (amount == "1.00" || amount == "0") {
                    amount = digit
                } else {
                    amount += digit
                }
            },
            onDeleteClick = {
                if (amount.length > 1) {
                    amount = amount.dropLast(1)
                } else {
                    amount = "0"
                }
            }
        )

        Spacer(modifier = Modifier.weight(1f))

        // Charge button
        Button(
            onClick = {
                val intent = Intent(context, PaymentPage::class.java).apply {
                    putExtra("PAYMENT_AMOUNT", amount)
                    putExtra("PAYMENT_DESCRIPTION", description)
                }
                context.startActivity(intent)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(8.dp),
            enabled = isValidAmount()
        ) {
            Text("Charge", fontSize = 22.sp)
        }
    }

    // Description Dialog
    if (showDescriptionDialog) {
        DescriptionDialog(
            initialDescription = description,
            onDismiss = { showDescriptionDialog = false },
            onConfirm = { newDescription ->
                description = newDescription
                showDescriptionDialog = false
            }
        )
    }
}

@Composable
fun NumberPad(onNumberClick: (String) -> Unit, onDeleteClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        // First row: 1, 2, 3
        Row(
            horizontalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            for (i in 1..3) {
                NumberButton(
                    number = i.toString(),
                    onClick = { onNumberClick(i.toString()) }
                )
            }
        }

        // Second row: 4, 5, 6
        Row(
            horizontalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            for (i in 4..6) {
                NumberButton(
                    number = i.toString(),
                    onClick = { onNumberClick(i.toString()) }
                )
            }
        }

        // Third row: 7, 8, 9
        Row(
            horizontalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            for (i in 7..9) {
                NumberButton(
                    number = i.toString(),
                    onClick = { onNumberClick(i.toString()) }
                )
            }
        }

        // Fourth row: 0, delete
        Row(
            horizontalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            NumberButton(
                number = "0",
                onClick = { onNumberClick("0") }
            )

            // Delete button (wider)
            OutlinedButton(
                onClick = onDeleteClick,
                modifier = Modifier
                    .size(width = 136.dp, height = 64.dp)
                    .padding(0.dp),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, Color.Black)
            ) {
                Text(
                    text = "<",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun NumberButton(number: String, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.size(64.dp),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, Color.Black),
        contentPadding = PaddingValues(0.dp)
    ) {
        Text(
            text = number,
            fontSize = 30.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun DescriptionDialog(
    initialDescription: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var tempDescription by remember { mutableStateOf(initialDescription) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Description") },
        text = {
            OutlinedTextField(
                value = tempDescription,
                onValueChange = { tempDescription = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Enter payment description") },
                singleLine = false,
                maxLines = 3
            )
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(tempDescription) }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
