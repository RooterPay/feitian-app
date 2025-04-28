package me.demo.view

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.ftpos.library.smartpos.emv.CandidateAIDInfo
import com.ftpos.library.smartpos.errcode.ErrCode
import com.ftpos.library.smartpos.util.EncodeConversionUtil
import com.jirui.logger.Logger
import com.ryccoatika.socketclient.SocketClient
import com.ryccoatika.socketclient.SocketClientCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.demo.enums.TransactionType
import me.demo.view.POSRequestHelper.createHandShakeRequest
import me.demo.view.POSRequestHelper.createLogonRequest
import me.dvabi.terminal.databinding.ActivityMainBinding
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.ServerSocket
import java.net.Socket

class MainActivity : BaseEmvActivity() {

    private lateinit var binding: ActivityMainBinding
    private val transactionType = TransactionType.PURCHASE

    private val amount = "5"

    private val port = 12345

    private fun startServer() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val serverSocket = ServerSocket(port)
                println("------------🟢 Server started on port $port")

                val clientSocket = serverSocket.accept()
                println("------------🔵 Server accepted connection")

                val input = DataInputStream(clientSocket.getInputStream())
                val output = DataOutputStream(clientSocket.getOutputStream())

                val length = input.readInt() // read message length
                val messageBytes = ByteArray(length)
                input.readFully(messageBytes) // read full message

                val message = String(messageBytes)
                println("------------📥 Server received: $message")

                // Send response
                val response = "Hello from server!"
                println("------------📥 Server sent: $message")

                val responseBytes = message.toByteArray()
                output.writeInt(responseBytes.size)
                output.write(responseBytes)

                clientSocket.close()
                serverSocket.close()
                println("------------🛑 Server closed")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun startClient() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
//                val socket = Socket("195.66.185.22", 2500)
                val socket = Socket("127.0.0.1", port)
                println("------------🔗 Client connected")

                val output = DataOutputStream(socket.getOutputStream())
                val input = DataInputStream(socket.getInputStream())

                val request = createLogonRequest()
                println("------------📥 Client sent: $request")

//                val message = "Hello from client!"
//                println("------------📥 Client sent: $message")

                val messageBytes = request.toByteArray()

                output.writeInt(messageBytes.size) // send length
                output.write(messageBytes)         // send data

                val responseLength2 = input.readUTF()
                val responseLength = input.readInt()
                val responseBytes = ByteArray(responseLength)
                input.readFully(responseBytes)

                val response = String(responseBytes)
                println("------------📥 Client received: $response")

//                socket.close()
                println("------------🛑 Client closed")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun startClient2() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val socket = Socket("195.66.185.22", 2500)
//                val socket = Socket("127.0.0.1", port)
                println("------------🔗 Client connected to server")

                val writer = BufferedWriter(OutputStreamWriter(socket.getOutputStream()))

                val logonRequest = createLogonRequest()
                println("------------📥 Client sent: $logonRequest")
//
                writer.write(logonRequest)
                writer.flush()

                val reader = BufferedReader(InputStreamReader(socket.getInputStream()))
                val buffer = CharArray(1024)
                var charsRead: Int

                while (reader.read(buffer).also { charsRead = it } != -1) {
                    val text = String(buffer, 0, charsRead)
                    println("------------📥 Client received: $text")

                    val cleaned = text.filter { it.isLetterOrDigit() || it.isWhitespace() || it in listOf('.', '`', 'T') }
//                    println("------------Parsed: $cleaned")

                    startHandShake(writer)

                }

                socket.close()
                println("------------🛑 Client closed connection")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun startHandShake(writer: BufferedWriter) {

        val handShakeRequest = createHandShakeRequest()
        println("------------📥 Client sent: $handShakeRequest")
        writer.write(handShakeRequest)
        writer.flush()
    }

    private fun newSocketClient() {

        val socketClient = SocketClient("195.66.185.22", 2500)
        socketClient.setSocketClientCallback(object : SocketClientCallback {
            override fun onConnected() {
                // when connected to the server
                println("------------ onConnected")
                socketClient.sendMessage(createLogonRequest())
            }

            override fun onConnectionFailure(e: Exception) {
                println("------------ onConnectionFailure")
                // any failure occurs would be thrown here
            }

            override fun onDisconnected() {
                println("------------ onDisconnected")
                // called when client call disconnect() or server has gone
            }

            override fun onMessageReceived(message: String) {
                println("------------ onMessageReceived")
                // message received from server
            }
        })
        socketClient.connect()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.startBtn.setOnClickListener {
//            startTransaction(amount, transactionType)
        }

//        CoroutineScope(Dispatchers.IO).launch {
//            newSocketClient()
//        }

        startServer()

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
                val listApp: MutableList<String?> = ArrayList()
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
    override fun doEndProcess(code: Int, data: String?) {
        Toast.makeText(this, "zavrrsena transakcija", Toast.LENGTH_SHORT).show()

        Logger.d("onEndProcess: 0x" + Integer.toHexString(code) + " - " + ErrCode.toString(code))
        //            iemv.respondEvent(null);
//        Logger.d("Trans Card Type: 0x%02X", iemv.transCardtype)

        //if ("F360".equals(Build.MODEL)) {
        //led.ledDefault();
//        led.ledCardIndicator(0x00, 0x00, 0x00, 0x00)
//        led.ledCardIndicator(0x00, 0x00, 0x00, 0x00)
    }
}