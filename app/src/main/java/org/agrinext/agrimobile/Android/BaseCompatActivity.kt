package org.agrinext.agrimobile.Android

import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.AsyncTask
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.View.inflate
import org.agrinext.agrimobile.R
import org.jetbrains.anko.alert

/**
 * Created by revant on 6/1/18.
 */

open class BaseCompatActivity : AppCompatActivity(), ConnectivityReceiver.ConnectivityReceiverListener {
    val connectivityReceiver = ConnectivityReceiver()
    val frappeClient = FrappeClient(this)
    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        checkNetworkState()
    }

    private fun checkNetworkState() {
        if (!frappeClient.checkNetworkConnection()) {
            showAlert()
        } else if (frappeClient.checkNetworkConnection()){
            val asyncTask = object : AsyncTask<Void, Void, Boolean>() {
                override fun doInBackground(vararg params: Void?): Boolean{
                    return frappeClient.checkConnection()
                }
                override fun onPostExecute(result: Boolean) {
                    if (!result){
                        showAlert()
                    }
                }
            }
            asyncTask.execute()
        }
    }

    private fun showAlert() {
        alert(getString(R.string.click_ok_when_enabled)) {
            title = getString(R.string.please_enable_net_connection)
            positiveButton(getString(R.string.ok)){
                checkNetworkState()
            }
        }.show().setCancelable(false)
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
