package me.demo;

import android.app.Application;


/**
 * @author GuoJirui.
 * @date 2021/4/25.
 * @desc
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //Initialize and bind the service through the service helper class
        SvrHelper.instance().init(this);
        SvrHelper.instance().bindService();
    }


    @Override
    public void onTerminate() {
        super.onTerminate();
        //Unbind the service through the service helper class
        SvrHelper.instance().unbindService();
    }

}
