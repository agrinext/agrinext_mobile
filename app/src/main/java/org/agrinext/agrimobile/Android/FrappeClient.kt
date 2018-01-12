package org.agrinext.agrimobile.Android

import android.content.Context
import android.util.Log
import com.github.scribejava.core.model.OAuthRequest
import com.github.scribejava.core.model.Verb
import com.mntechnique.otpmobileauth.auth.AuthReqCallback
import com.mntechnique.otpmobileauth.auth.AuthRequest
import com.mntechnique.otpmobileauth.auth.RetrieveAuthTokenTask
import org.json.JSONObject
import java.io.IOException
import java.net.URL

/**
 * Created by revant on 31/12/17.
 */

class FrappeClient(ctx: Context){
    val ctx = ctx
    fun getAuthRequest() : AuthRequest {
        val oauth2Scope = ctx.getString(org.agrinext.agrimobile.R.string.oauth2Scope)
        val clientId = ctx.getString(org.agrinext.agrimobile.R.string.clientId)
        val clientSecret = ctx.getString(org.agrinext.agrimobile.R.string.clientSecret)
        val serverURL = ctx.getString(org.agrinext.agrimobile.R.string.serverURL)
        val redirectURI = ctx.getString(org.agrinext.agrimobile.R.string.redirectURI)
        val authRequest = AuthRequest(oauth2Scope, clientId, clientSecret, serverURL, redirectURI)
        return authRequest
    }

    fun getServerURL () : String {
        return ctx.getString(org.agrinext.agrimobile.R.string.serverURL)
    }

    fun getAuthReqCallback(request: OAuthRequest, responseCallback: AuthReqCallback) : AuthReqCallback {
        val authRequest = getAuthRequest()
        val authReqCallback = object : AuthReqCallback {
            override fun onSuccessResponse(s: String) {
                var bearerToken = JSONObject(s)
                if (bearerToken.length() > 0) {
                    authRequest.makeRequest(bearerToken.getString("access_token"), request, responseCallback)
                }
            }
            override fun onErrorResponse(s: String) {
                Log.d("ReqCallbackError", s)
            }
        }
        return authReqCallback
    }

    fun get_all(doctype: String,
                filters: String? = null,
                fields: String? = null,
                limit_page_length: String? = null,
                limit_start: String? = null,
                order_by: String? = null) : OAuthRequest {
        val encoded_doctype = doctype.replace(" ", "%20")
        var requestURL = getServerURL() + "/api/resource/$encoded_doctype?"

        if(!filters.isNullOrEmpty()) {
            requestURL += "filters=$filters&"
        }

        if(!fields.isNullOrEmpty()){
            requestURL += "fields=$fields&"
        }

        if(!limit_page_length.isNullOrEmpty()){
            requestURL += "limit_page_length=$limit_page_length&"
        }

        if(!limit_start.isNullOrEmpty()){
            requestURL += "limit_start=$limit_start&"
        }

        if(!order_by.isNullOrEmpty()) {
            requestURL += "order_by=$order_by&"
        }

        Log.d("requestURL", requestURL)

        val frappeRequest = OAuthRequest(Verb.GET, requestURL)

        return frappeRequest
    }

    fun checkNetworkConnection(): Boolean {

        return NetworkUtils.isWifiConnected(ctx) ||
                NetworkUtils.isMobileConnected(ctx)||
                NetworkUtils.isConnected(ctx)
    }

    fun checkConnection(): Boolean {
        val connectUrl = URL(getServerURL())
        val connection = connectUrl.openConnection()
        connection.connectTimeout = 3000
        try {
            connection.connect()
            return true
        } catch (e: IOException){
            return false
        }
    }

    fun executeRequest(request: OAuthRequest, callback: AuthReqCallback) {
        RetrieveAuthTokenTask(
                context = ctx,
                callback = getAuthReqCallback(request, callback)
        ).execute()
    }
}
