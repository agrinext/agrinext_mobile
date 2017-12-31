package org.agrinext.agrimobile.Helpers

import android.text.TextUtils
import android.accounts.Account
import android.accounts.AccountManager
import android.content.Context
import android.net.ConnectivityManager
import org.agrinext.agrimobile.BuildConfig
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern


/**
 * Created by revant on 31/12/17.
 */

fun checkNetworkConnection(context: Context): Boolean {
    var haveConnectedWifi = false
    var haveConnectedMobile = false

    val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val netInfo = cm.allNetworkInfo
    for (ni in netInfo) {
        if (ni.typeName.equals("WIFI", ignoreCase = true))
            if (ni.isConnected)
                haveConnectedWifi = true
        if (ni.typeName.equals("MOBILE", ignoreCase = true))
            if (ni.isConnected)
                haveConnectedMobile = true
    }
    return haveConnectedWifi || haveConnectedMobile
}