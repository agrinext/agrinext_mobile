package org.agrinext.agrimobile.Activities

import android.accounts.AccountManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.widget.Toast
import com.github.scribejava.core.model.OAuthRequest
import com.github.scribejava.core.model.Verb
import com.mntechnique.otpmobileauth.auth.AuthReqCallback
import com.mntechnique.otpmobileauth.auth.AuthRequest
import com.mntechnique.otpmobileauth.auth.RetrieveAuthTokenTask
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import org.agrinext.agrimobile.BuildConfig
import org.agrinext.agrimobile.Helpers.ListViewAdapter
import org.agrinext.agrimobile.R
import org.jetbrains.anko.accountManager
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.json.JSONArray
import org.json.JSONObject

class ProduceActivity : AppCompatActivity() {
    internal lateinit var mRecyclerView: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listing)

        mRecyclerView = findViewById(R.id.recycler_view)

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        val mLayoutManager = LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        val accessTokenCallback = getAccessTokenCallback()
        val ratt = RetrieveAuthTokenTask(applicationContext, accessTokenCallback)
        ratt.execute()
    }

    private fun getAccessTokenCallback(): AuthReqCallback {
        val mAccountManager = AccountManager.get(this)

        val oauth2Scope = resources.getString(org.agrinext.agrimobile.R.string.oauth2Scope)
        val clientId = resources.getString(org.agrinext.agrimobile.R.string.clientId)
        val clientSecret = resources.getString(org.agrinext.agrimobile.R.string.clientSecret)
        val serverURL = resources.getString(org.agrinext.agrimobile.R.string.serverURL)
        val redirectURI = resources.getString(org.agrinext.agrimobile.R.string.redirectURI)
        val authEndpoint = resources.getString(org.agrinext.agrimobile.R.string.authEndpoint)
        val tokenEndpoint = resources.getString(org.agrinext.agrimobile.R.string.tokenEndpoint)

        val authRequest = AuthRequest(
                applicationContext,
                oauth2Scope, clientId, clientSecret, serverURL,
                redirectURI, authEndpoint, tokenEndpoint)

        val accounts = mAccountManager.getAccountsByType(BuildConfig.APPLICATION_ID)
        var produce_endpoint = "/api/resource/Add%20Produce?filters=[[\"owner\",\"=\",\""
        produce_endpoint += accounts[0].name
        produce_endpoint += "\"]]"
        Log.d("URL",serverURL + produce_endpoint)
        val request = OAuthRequest(Verb.GET, serverURL + produce_endpoint)
        val responseCallback = object : AuthReqCallback {
            override fun onSuccessResponse(s: String) {
                val response = JSONObject(s)
                Log.d("Produce!",response.toString())
                // JSON Array from frappe's listing
                var jsonArray = response.getJSONArray("data")

                // specify an adapter
                val mAdapter = ListViewAdapter(jsonArray);
                mRecyclerView.setAdapter(mAdapter);
            }

            override fun onErrorResponse(s: String) {
                Toast.makeText(applicationContext,"Error parsing response", Toast.LENGTH_LONG).show()
            }
        }

        val accessTokenCallback = object : AuthReqCallback {
            override fun onSuccessResponse(s: String) {
                var bearerToken = JSONObject(s)
                if (bearerToken.length() > 0) {
                    authRequest.makeRequest(bearerToken.getString("access_token"), request, responseCallback)
                }
            }
            override fun onErrorResponse(s: String) {
                Log.d("CallbackError", s)
            }
        }
        return accessTokenCallback
    }
}
