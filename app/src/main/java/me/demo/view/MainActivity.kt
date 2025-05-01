package me.demo.view

import android.os.Bundle
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.demo.enums.TransactionType
import me.demo.helpers.HandshakeRequestHelper.createHandShakeRequest
import me.demo.helpers.LogonRequestHelper
import me.demo.helpers.Preferences
import me.demo.helpers.subfields.SubFieldO
import me.demo.helpers.subfields.SubFieldP
import me.demo.helpers.TransactionRequestHelper.createTransactionRequest
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

    private val amount = "5"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.startBtn.setOnClickListener {
            startTransaction(amount, transactionType)
        }

        startClient()
    }

    override fun cancelTransaction() {
        Toast.makeText(this, "transaction canceled", Toast.LENGTH_SHORT).show()
    }

    override fun handleCardData(subFieldO: SubFieldO, subFieldP: SubFieldP) {
        sendTransactionRequest(subFieldO, subFieldP)
    }

    private fun startClient() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val socket = Socket("195.66.185.22", 2500)
                println("------------🔗 Client connected to server")

                writer = BufferedWriter(OutputStreamWriter(socket.getOutputStream()))

                sendLogonRequest()

                val reader = BufferedReader(InputStreamReader(socket.getInputStream()))
                val buffer = CharArray(1024)
                var charsRead: Int

                while (reader.read(buffer).also { charsRead = it } != -1) {
                    val text = String(buffer, 0, charsRead)
                    println("------------📥 Client received: $text")

                    val logonResponse = "AO50"
                    val handShakeResponse = "AO95"
                    val success = "000"
                    val approvedAT = "007"

                    //fixme

                    if (text.contains(logonResponse)) {
                        val parts = text.split(logonResponse)
                        val responseCodes = parts[1]
                        if (responseCodes.contains(success) && responseCodes.contains(approvedAT)) {
                            sendHandShakeRequest()
                        }
                    } else if (text.contains(handShakeResponse)) {
                        val parts = text.split(handShakeResponse)
                        val responseCodes = parts[1]
                        if (responseCodes.contains(success) && responseCodes.contains(approvedAT)) {
                            println("------------ handshake success")
                        }
                    }
                }

                socket.close()
                println("------------🛑 Client closed connection")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun sendLogonRequest() {
        val logonRequest = LogonRequestHelper.createLogonRequest()
        println("------------📥 Client sent: $logonRequest")
        writer.write(logonRequest)
        writer.flush()
        Preferences.incrementTransmissionID()
    }

    private fun sendHandShakeRequest() {
        val handShakeRequest = createHandShakeRequest()
        println("------------📥 Client sent: $handShakeRequest")
        writer.write(handShakeRequest)
        writer.flush()
        Preferences.incrementTransmissionID()
    }

    private fun sendTransactionRequest(subFieldO: SubFieldO, subFieldP: SubFieldP) {
        val transactionRequest = createTransactionRequest(subFieldO, subFieldP)
        println("------------📥 Client sent: $transactionRequest")
        writer.write(transactionRequest)
        writer.flush()
        Preferences.incrementTransmissionID()
    }

}