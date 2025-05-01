package me.demo.view

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.ftpos.library.smartpos.emv.CandidateAIDInfo
import com.ftpos.library.smartpos.errcode.ErrCode
import com.ftpos.library.smartpos.util.EncodeConversionUtil
import com.jirui.logger.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.demo.enums.TransactionType
import me.demo.helpers.HandshakeRequestHelper.createHandShakeRequest
import me.demo.helpers.LogonRequestHelper.createLogonRequest
import me.demo.helpers.Preferences
import me.demo.helpers.fields.SubFieldO
import me.demo.helpers.fields.SubFieldP
import me.demo.helpers.TransactionRequestHelper.createTransactionRequest
import me.dvabi.terminal.databinding.ActivityMainBinding
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.Socket

class MainActivity : BaseEmvActivity() {

    private lateinit var binding: ActivityMainBinding
    private val transactionType = TransactionType.PURCHASE

    private val amount = "5"

    private lateinit var writer: BufferedWriter

    private fun startClient2() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val socket = Socket("195.66.185.22", 2500)
                println("------------🔗 Client connected to server")

                writer = BufferedWriter(OutputStreamWriter(socket.getOutputStream()))

                startLogon()

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

                    if (text.contains(logonResponse)) {
                        val parts = text.split(logonResponse)
                        val responseCodes = parts[1]
                        if (responseCodes.contains(success) && responseCodes.contains(approvedAT)) {
                            startHandShake()
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

    private fun startLogon() {
        val logonRequest = createLogonRequest()
        println("------------📥 Client sent: $logonRequest")

        writer.write(logonRequest)
        writer.flush()

        Preferences.incrementTransmissionID()
    }

    private fun startHandShake() {
        val handShakeRequest = createHandShakeRequest()
        println("------------📥 Client sent: $handShakeRequest")
        writer.write(handShakeRequest)
        writer.flush()
        Preferences.incrementTransmissionID()
    }

    private fun startTransaction(subFieldO: SubFieldO, subFieldP: SubFieldP) {

        val transactionRequest = createTransactionRequest(subFieldO, subFieldP)
        println("------------📥 Client sent: $transactionRequest")
        writer.write(transactionRequest)
        writer.flush()

        Preferences.incrementTransmissionID()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.startBtn.setOnClickListener {
            startTransaction(amount, transactionType)
        }

//        startServer()

        // Delay slightly to make sure server is ready
        CoroutineScope(Dispatchers.IO).launch {
            delay(1000)
            startClient2()
        }

    }

    override fun cancelTransaction() {
        Toast.makeText(this, "otkazana transakcija", Toast.LENGTH_SHORT).show()
    }

    //ovo je samo u slucaju da chip na kartici ima vise aplikacija
    override fun doAppSelect(list: List<CandidateAIDInfo>?) {
        if (list.isNullOrEmpty()) return
        Handler(Looper.getMainLooper())
            .post {
                val listApp: MutableList<String> = ArrayList()
                //Get the application name that needs to be displayed from CandidateAID
                for (i in list.indices) {
                    listApp.add(
                        EncodeConversionUtil.EncodeConversion(
                            list[i].applicationLabel_tag50,
                            list[i].codeTableIndex_tag9F11
                        )
                    )
                }

                if (list.size > 1) {
                    Toast.makeText(this, "more than 1 app", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "only 1 app", Toast.LENGTH_SHORT).show()
                }
            }
    }

    //ovo treba da se zove kad se zavrsi transakcija
    override fun doEndProcess(code: Int, data: String) {
        Toast.makeText(this, "zavrsena transakcija", Toast.LENGTH_SHORT).show()

        Logger.d("onEndProcess: 0x" + Integer.toHexString(code) + " - " + ErrCode.toString(code))
        //            iemv.respondEvent(null);
//        Logger.d("Trans Card Type: 0x%02X", iemv.transCardtype)

        //if ("F360".equals(Build.MODEL)) {
        //led.ledDefault();
//        led.ledCardIndicator(0x00, 0x00, 0x00, 0x00)
//        led.ledCardIndicator(0x00, 0x00, 0x00, 0x00)
    }

    override fun onDisplayInfo(subFieldO: SubFieldO, subFieldP: SubFieldP) {
        startTransaction(subFieldO, subFieldP)
    }

}