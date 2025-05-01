package me.demo.helpers

import me.demo.view.DecodeHelper.convertHexBytesToAsciiString

object POSRequestHelper {

    val delimiter = convertHexBytesToAsciiString("1C")
    val endChar = convertHexBytesToAsciiString("03")

    fun createTPDU(): String {
        val hexList: List<String> = mutableListOf("60", "00", "14", "00", "00")

        val sb = StringBuilder()
        for (hex in hexList) {
            sb.append(convertHexBytesToAsciiString(hex))
        }

        val tpdu = sb.toString()
        //        System.out.println("-----------tpdu: " + tpdu);
        return tpdu
    }

}
