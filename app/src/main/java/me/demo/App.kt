package me.demo

import android.app.Application

/**
 * @author GuoJirui.
 * @date 2021/4/25.
 * @desc
 */
class App : Application() {

    companion object {
        private lateinit var _instance: App
        val instance: App
            get() = _instance
    }

    override fun onCreate() {
        super.onCreate()
        _instance = this
        //Initialize and bind the service through the service helper class
        SvrHelper.instance().init(this)
        SvrHelper.instance().bindService()
    }


    override fun onTerminate() {
        super.onTerminate()
        //Unbind the service through the service helper class
        SvrHelper.instance().unbindService()
    }
}
