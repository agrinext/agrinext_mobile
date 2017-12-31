package org.agrinext.agrimobile.Android

import android.app.Application
import org.jetbrains.anko.activityManager


/**
 * Created by revant on 31/12/17.
 */
class ApplicationController : Application() {
    internal var activityVisible = false

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    fun setConnectivityListener(listener: ConnectivityReceiver.ConnectivityReceiverListener) {
        ConnectivityReceiver.connectivityReceiverListener = listener
    }

    fun isActivityAvailable() : Boolean{
        return activityVisible
    }

    fun activityResumed() {
        activityVisible = true
    }

    fun activityPaused() {
        activityVisible = false
    }

    companion object {
        @get:Synchronized
        var instance: ApplicationController? = null
            private set
    }
}
