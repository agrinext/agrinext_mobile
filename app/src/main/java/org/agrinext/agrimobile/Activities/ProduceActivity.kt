package org.agrinext.agrimobile.Activities

import android.accounts.AccountManager
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.mntechnique.otpmobileauth.auth.AuthReqCallback
import com.mntechnique.otpmobileauth.auth.RetrieveAuthTokenTask
import org.agrinext.agrimobile.Android.BaseCompatActivity
import org.agrinext.agrimobile.Android.EndlessRecyclerViewScrollListener
import org.agrinext.agrimobile.BuildConfig
import org.agrinext.agrimobile.Android.FrappeClient
import org.agrinext.agrimobile.Android.ListViewAdapter
import org.agrinext.agrimobile.R
import org.jetbrains.anko.toast
import org.json.JSONArray
import org.json.JSONObject

class ProduceActivity : BaseCompatActivity() {
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
        //mRecyclerView.setAdapter(recyclerAdapter)

        mRecyclerView.addOnScrollListener(object: EndlessRecyclerViewScrollListener(mLayoutManager){
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                loadData(page)
            }
        })

        loadData()
    }

    fun loadData(page: Int? = null) {
        val mAccountManager = AccountManager.get(this)
        val accounts = mAccountManager.getAccountsByType(BuildConfig.APPLICATION_ID)

        val filters = "[[\"owner\",\"=\",\"" + accounts[0].name + "\"]]"

        val request = FrappeClient(this).get_all(
                doctype = "Add Produce",
                filters = filters,
                limit_page_length = "5",
                limit_start = (page?.times(5)).toString()
        )

        val responseCallback = object : AuthReqCallback {
            override fun onSuccessResponse(s: String) {
                val response = JSONObject(s)
                // JSON Array from frappe's listing
                for (i in 0 until response.getJSONArray("data").length()) {
                    recyclerModels.put(response.getJSONArray("data").get(i))
                }
                if (page != null){
                   // Notify an adapter
                    recyclerAdapter!!.notifyDataSetChanged()
                } else if (page == null) {
                    // specify and add an adapter
                    recyclerAdapter = ListViewAdapter(recyclerModels)
                    mRecyclerView.adapter = recyclerAdapter
                }
            }

            override fun onErrorResponse(s: String) {
                toast(R.string.somethingWrong)
            }
        }

        FrappeClient(this).executeRequest(request, responseCallback)
    }
}