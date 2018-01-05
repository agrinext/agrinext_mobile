package org.agrinext.agrimobile.Activities

import android.accounts.AccountManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.widget.Toast
import com.github.scribejava.core.model.OAuthRequest
import com.github.scribejava.core.model.Verb
import com.mntechnique.otpmobileauth.auth.AuthReqCallback
import com.mntechnique.otpmobileauth.auth.AuthRequest
import com.mntechnique.otpmobileauth.auth.RetrieveAuthTokenTask
import org.agrinext.agrimobile.Android.EndlessRecyclerViewScrollListener
import org.agrinext.agrimobile.BuildConfig
import org.agrinext.agrimobile.Helpers.ListViewAdapter
import org.agrinext.agrimobile.R
import org.json.JSONArray
import org.json.JSONObject

class ProduceActivity : AppCompatActivity() {
    // we need this variable to lock and unlock loading more
    // e.g we should not load more when volley is already loading,
    // loading will be activated when volley completes loading
    var itShouldLoadMore = true

    // initialize adapter and data structure here
    var recyclerAdapter: ListViewAdapter? = null

    var recyclerModels = JSONArray()

    internal lateinit var mRecyclerView: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listing)

        mRecyclerView = findViewById(R.id.recycler_view)

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        val mLayoutManager = LinearLayoutManager(this)
        mRecyclerView.setLayoutManager(mLayoutManager)
        mRecyclerView.setHasFixedSize(false)

        recyclerAdapter = ListViewAdapter(recyclerModels)
        mRecyclerView.setAdapter(recyclerAdapter)

        mRecyclerView.addOnScrollListener(object: EndlessRecyclerViewScrollListener(mLayoutManager){
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                loadMoreData(page)
            }
        })

        firstLoadData()
    }

    fun loadMoreData(page: Int) {
        Log.d("Page", page.toString())
        val mAccountManager = AccountManager.get(this)
        itShouldLoadMore = false; // lock this guy,(itShouldLoadMore) to make sure,
        // user will not load more when volley is processing another request
        // only load more when  volley is free
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
        produce_endpoint += "\"]]&limit_page_length=5&limit_start=" + (page * 5).toString()

        Log.d("URL",serverURL + produce_endpoint)

        val request = OAuthRequest(Verb.GET, serverURL + produce_endpoint)
        val responseCallback = object : AuthReqCallback {
            override fun onSuccessResponse(s: String) {
                itShouldLoadMore = true
                val response = JSONObject(s)
                // JSON Array from frappe's listing
                //recyclerModels = response.getJSONArray("data")
                for (i in 0 until response.getJSONArray("data").length()) {
                    recyclerModels.put(response.getJSONArray("data").get(i))
                }
                // setAdapterData(recyclerAdapter)

                // specify an adapter
                recyclerAdapter!!.notifyDataSetChanged()
            }

            override fun onErrorResponse(s: String) {
                itShouldLoadMore = true
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

        val retrieveAuthTokenTask = RetrieveAuthTokenTask(applicationContext, accessTokenCallback)
        retrieveAuthTokenTask.execute()
    }

    private fun firstLoadData() {
        val mAccountManager = AccountManager.get(this)
        itShouldLoadMore = false; // lock this guy,(itShouldLoadMore) to make sure,
        // user will not load more when volley is processing another request
        // only load more when  volley is free
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
        produce_endpoint += "\"]]&limit_page_length=5"

        Log.d("URL",serverURL + produce_endpoint)

        val request = OAuthRequest(Verb.GET, serverURL + produce_endpoint)
        val responseCallback = object : AuthReqCallback {
            override fun onSuccessResponse(s: String) {
                Log.d("JSONObject", s)
                val response = JSONObject(s)
                // JSON Array from frappe's listing
                for (i in 0 until response.getJSONArray("data").length()) {
                    recyclerModels.put(response.getJSONArray("data").get(i))
                }

                // specify an adapter
                recyclerAdapter = ListViewAdapter(recyclerModels)
                setAdapterData(recyclerAdapter)
                // mRecyclerView.setAdapter(recyclerAdapter)
                recyclerAdapter!!.notifyDataSetChanged()
            }

            override fun onErrorResponse(s: String) {
                itShouldLoadMore = true
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

        val retrieveAuthTokenTask = RetrieveAuthTokenTask(applicationContext, accessTokenCallback)
        retrieveAuthTokenTask.execute()
    }

    fun setAdapterData(recyclerAdapter: ListViewAdapter?) {
        mRecyclerView.adapter = recyclerAdapter
    }
}
