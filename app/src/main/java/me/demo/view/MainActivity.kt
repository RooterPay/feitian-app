package me.demo.view

import android.os.Bundle
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.demo.enums.TransactionCode
import me.demo.enums.TransactionType
import me.demo.helpers.DecodeHelper.decodeServerResponse
import me.demo.helpers.HandshakeRequestHelper.createHandShakeRequest
import me.demo.helpers.LogonRequestHelper
import me.demo.helpers.ParsedResponse
import me.demo.helpers.Preferences
import me.demo.helpers.TransactionRequestHelper.createTransactionRequest
import me.demo.helpers.subfields.SubFieldO
import me.demo.helpers.subfields.SubFieldP
import me.dvabi.terminal.databinding.ActivityMainBinding
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.Socket

class MainActivity : BaseEmvActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var writer: BufferedWriter

    private val transactionType = TransactionType.PURCHASE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        startClient()
    }

    private fun setupViews() {
        binding.startLogon.setOnClickListener {
            sendLogonRequest()
        }
        binding.startTransaction.setOnClickListener {
            startTransaction(binding.enterAmount.text.toString(), transactionType)
        }

        binding.enterAmount.addTextChangedListener {
            binding.startTransaction.isVisible = it.toString().isNotEmpty()
        }
    }

    override fun cancelTransaction() {
        Toast.makeText(this, "transaction canceled", Toast.LENGTH_SHORT).show()
    }

    override fun handleCardData(subFieldO: SubFieldO, subFieldP: SubFieldP) {
        sendTransactionRequest(subFieldO, subFieldP)
    }

    private fun startClient() = CoroutineScope(Dispatchers.IO).launch {
        try {
            val socket = Socket("195.66.185.22", 2500)
            println("------------🔗 Client connected to server")

            writer = BufferedWriter(OutputStreamWriter(socket.getOutputStream()))
            setupViews()

            val reader = BufferedReader(InputStreamReader(socket.getInputStream()))
            val buffer = CharArray(1024)
            var charsRead: Int

            while (reader.read(buffer).also { charsRead = it } != -1) {
                val text = String(buffer, 0, charsRead)
                println("------------📥 Client received: $text")
                val decodedResponse = decodeServerResponse(text)
                parseResponse(decodedResponse)
            }

            socket.close()
            println("------------🛑 Client closed connection")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun parseResponse(response: ParsedResponse) = runOnUiThread {
        when (response.getType()) {
            TransactionCode.ERROR -> {
                println("------------ error")
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
            }

            TransactionCode.LOGON -> {
                if (response.isSuccessful()) {
                    sendHandShakeRequest()
                } else {
                    Toast.makeText(this, "Logon Error", Toast.LENGTH_SHORT).show()
                }
            }

            TransactionCode.HANDSHAKE -> {
                if (response.isSuccessful()) {
                    println("------------ handshake success")
                    binding.enterAmount.isVisible = true
                    binding.startLogon.isVisible = false
                } else {
                    Toast.makeText(this, "Handshake Error", Toast.LENGTH_SHORT).show()
                }
            }

            TransactionCode.TRANSACTION -> {
                if (response.isSuccessful()) {
                    println("------------ transaction success")
                    binding.enterAmount.setText("")
                    binding.enterAmount.clearFocus()
                    Toast.makeText(
                        this@MainActivity,
                        "Transaction completed successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(this, "Transaction Error", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun sendLogonRequest() = CoroutineScope(Dispatchers.IO).launch {
        val logonRequest = LogonRequestHelper.createLogonRequest()
        println("------------📥 Client sent: $logonRequest")
        writer.write(logonRequest)
        writer.flush()
        Preferences.incrementTransmissionID()
    }

    private fun sendHandShakeRequest() = CoroutineScope(Dispatchers.IO).launch {
        val handShakeRequest = createHandShakeRequest()
        println("------------📥 Client sent: $handShakeRequest")
        writer.write(handShakeRequest)
        writer.flush()
        Preferences.incrementTransmissionID()
    }

    private fun sendTransactionRequest(subFieldO: SubFieldO, subFieldP: SubFieldP) =
        CoroutineScope(Dispatchers.IO).launch {
            val transactionRequest = createTransactionRequest(subFieldO, subFieldP)
            println("------------📥 Client sent: $transactionRequest")
            writer.write(transactionRequest)
            writer.flush()
            Preferences.incrementTransmissionID()
        }

}