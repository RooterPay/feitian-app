package com.rooterpay

import android.app.Application
import com.rooterpay.utils.ApiService
import com.rooterpay.utils.Currency

class ApplicationWrapper : Application() {
    var defaultCurrency: Currency = Currency.EUR

    companion object {
        lateinit var apiService: ApiService
    }

    override fun onCreate() {
        super.onCreate()
        apiService = ApiService()
    }
}
