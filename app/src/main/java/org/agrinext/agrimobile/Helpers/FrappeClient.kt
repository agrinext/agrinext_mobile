package org.agrinext.agrimobile.Helpers

import android.content.Context
import android.net.ConnectivityManager
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.github.scribejava.core.model.OAuthRequest
import com.github.scribejava.core.model.Verb
import com.mntechnique.otpmobileauth.auth.AuthReqCallback
import com.mntechnique.otpmobileauth.auth.AuthRequest
import org.json.JSONObject
import java.net.URLEncoder

/**
 * Created by revant on 31/12/17.
 */

class FrappeClient(ctx: AppCompatActivity){
    val ctx = ctx

    fun getAuthRequest() : AuthRequest {
        val oauth2Scope = ctx.resources.getString(org.agrinext.agrimobile.R.string.oauth2Scope)
        val clientId = ctx.resources.getString(org.agrinext.agrimobile.R.string.clientId)
        val clientSecret = ctx.resources.getString(org.agrinext.agrimobile.R.string.clientSecret)
        val serverURL = ctx.resources.getString(org.agrinext.agrimobile.R.string.serverURL)
        val redirectURI = ctx.resources.getString(org.agrinext.agrimobile.R.string.redirectURI)
        val authEndpoint = ctx.resources.getString(org.agrinext.agrimobile.R.string.authEndpoint)
        val tokenEndpoint = ctx.resources.getString(org.agrinext.agrimobile.R.string.tokenEndpoint)

        val authRequest = AuthRequest(
                ctx.applicationContext,
                oauth2Scope, clientId, clientSecret, serverURL,
                redirectURI, authEndpoint, tokenEndpoint)

        return authRequest
    }

    fun getServerURL () : String {
        return ctx.resources.getString(org.agrinext.agrimobile.R.string.serverURL)
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
                limit_start: String? = null) : OAuthRequest {
        val encoded_doctype = doctype.replace(" ", "%20")
        var requestURL = getServerURL() + "/api/resource/$encoded_doctype?"

        if(filters!=null) {
            requestURL += "filters=$filters&"
        }

        if(fields!=null){
            requestURL += "fields=$fields&"
        }

        if(limit_page_length!=null){
            requestURL += "limit_page_length=$limit_page_length&"
        }

        if(limit_start!=null){
            requestURL += "limit_start=$limit_start&"
        }

        Log.d("requestURL", requestURL)

        val frappeRequest = OAuthRequest(Verb.GET, requestURL)

        return frappeRequest
    }
}

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

