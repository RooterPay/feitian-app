package me.demo.helpers

import android.content.Context
import android.content.SharedPreferences
import me.demo.App

class Preferences {

    companion object {

        private const val PREFS = "C_PREFS"
        private const val TRANSMISSION_ID = "TRANSMISSION_ID"

        private val preferences: SharedPreferences
            get() = App.instance.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

        fun getTransmissionID() = String.format("%02d", preferences.getInt(TRANSMISSION_ID, 0))
        fun incrementTransmissionID() {
            val id = preferences.getInt(TRANSMISSION_ID, 0)
            if (id == 99) {
                preferences.edit().putInt(TRANSMISSION_ID, 0).apply()
            } else {
                preferences.edit().putInt(TRANSMISSION_ID, id + 1).apply()
            }
        }
    }
}