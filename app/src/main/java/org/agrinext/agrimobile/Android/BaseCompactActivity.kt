package org.agrinext.agrimobile.Android

import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import org.agrinext.agrimobile.Helpers.checkNetworkConnection
import org.agrinext.agrimobile.R
import org.jetbrains.anko.alert

/**
 * Created by revant on 6/1/18.
 */

open class BaseCompactActivity : AppCompatActivity(), ConnectivityReceiver.ConnectivityReceiverListener {
    val connectivityReceiver = ConnectivityReceiver()

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        checkNetworkState()
    }

    private fun checkNetworkState() {
        if (!checkNetworkConnection(this)) {
            alert(getString(R.string.click_ok_when_enabled)) {
                title = getString(R.string.please_enable_net_connection)
                positiveButton(getString(R.string.ok)){
                    checkNetworkState()
                }
            }.show().setCancelable(false)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        registerReceiver(connectivityReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
        ApplicationController.instance?.setConnectivityListener(this)
    }

    override fun onPause(){
        super.onPause()
        unregisterReceiver(connectivityReceiver)
        ApplicationController.instance?.activityPaused()
    }

    override fun onResume() {
        super.onResume()
        ApplicationController.instance?.setConnectivityListener(this);
        ApplicationController.instance?.activityResumed()
        registerReceiver(connectivityReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
    }
}
