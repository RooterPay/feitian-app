package me.demo.view

import java.text.SimpleDateFormat
import java.util.Arrays
import java.util.Date
import java.util.Locale

object POSRequestHelper {

    fun createLogonRequest(): String {
        val tpdu = createTPDU()
        val headers = createHeaders("50")
        val delimiter = DecodeHelper.convertHexBytesToAsciiString("1C")
        val end = "h0010030111"

        val temp = tpdu + headers + delimiter + end
        val hilen = (temp.length / 256).toChar()
        val lowlen = (temp.length % 256).toChar()

        val logonRequest = "$hilen$lowlen$temp"

        println("-----------logonRequest: $logonRequest")
        return logonRequest
    }

    fun createHandShakeRequest(): String {
        val tpdu = createTPDU()
        val headers = createHeaders("95")
        val delimiter = DecodeHelper.convertHexBytesToAsciiString("1C")
        val end = "h0010030111"

        val temp = tpdu + headers + delimiter + end
        val hilen = (temp.length / 256).toChar()
        val lowlen = (temp.length % 256).toChar()

        val handShakeRequest = "$hilen$lowlen$temp"

        println("-----------handShake request: $handShakeRequest")
        return handShakeRequest
    }

    private fun createTPDU(): String {
        val hexList: List<String> = mutableListOf("60", "00", "14", "00", "00")

        val sb = StringBuilder()
        for (hex in hexList) {
            sb.append(DecodeHelper.convertHexBytesToAsciiString(hex))
        }

        val tpdu = sb.toString()
        //        System.out.println("-----------tpdu: " + tpdu);
        return tpdu
    }

    private fun createHeaders(transactionCode: String): String {
        val now = Date()

        val deviceType = "9."
        val transmissionNumber = "31"
        val terminalID = "T1001870        "
        val employeeID = "      "
        val currentDate = SimpleDateFormat("yyMMdd", Locale.getDefault()).format(now)
        val currentTime = SimpleDateFormat("HHmmss", Locale.getDefault()).format(now)
        val messageType = "A"
        val messageSubType = "O"
//        val transactionCode = "50" ili "95"
        val processingFlag1 = "1"
        val processingFlag2 = "5"
        val processingFlag3 = "0"
        val responseCode = "000"

        val hexList = Arrays.asList(
            deviceType,
            transmissionNumber,
            terminalID,
            employeeID,
            currentDate,
            currentTime,
            messageType,
            messageSubType,
            transactionCode,
            processingFlag1,
            processingFlag2,
            processingFlag3,
            responseCode
        )

        val headers = DecodeHelper.appendStrings(hexList)
        //        System.out.println("-----------headers: " + headers);
        return headers
    }

    fun createOptionals(): String {
        val optionals = ""
        //        System.out.println("-----------optionals: " + optionals);
        return optionals
    }
}
