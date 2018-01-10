package org.agrinext.agrimobile.Activities

import android.accounts.Account
import android.accounts.AccountManager
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.Menu
import android.support.v7.widget.SearchView
import com.mntechnique.otpmobileauth.auth.AuthReqCallback
import org.agrinext.agrimobile.Android.BaseCompatActivity
import org.agrinext.agrimobile.Android.EndlessRecyclerViewScrollListener
import org.agrinext.agrimobile.BuildConfig
import org.agrinext.agrimobile.Android.FrappeClient
import org.agrinext.agrimobile.Android.ListViewAdapter
import org.agrinext.agrimobile.R
import org.jetbrains.anko.toast
import org.json.JSONArray
import org.json.JSONObject
import android.app.SearchManager
import android.content.Context
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.ArrayAdapter
import java.util.ArrayList


class ProduceActivity : BaseCompatActivity() {
    var recyclerAdapter: ListViewAdapter? = null
    var recyclerModels = JSONArray()
    var searchView : SearchView? = null
    var mAccountManager: AccountManager? = null
    var accounts: Array<Account>? = null
    internal lateinit var mRecyclerView: RecyclerView
    var progressBar: ProgressBar? = null
    var filters: String? = null
    var user:String? = null
    var loadServerData = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listing)
        mAccountManager = AccountManager.get(this)
        accounts = mAccountManager?.getAccountsByType(BuildConfig.APPLICATION_ID)
        user = accounts?.get(0)?.name

        val filtersArray = JSONArray()
        // owner filter
        var filterSet = JSONArray().put("owner").put("=").put(user)
        filtersArray.put(filterSet)

        filters = filtersArray.toString()

        mRecyclerView = findViewById(R.id.recycler_view)
        progressBar = findViewById(R.id.edit_progress_bar)
        progressBar?.visibility = View.VISIBLE
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true)
        var list = ArrayList<String>()
        list.add("Mathura")
        list.add("Delhi")
        list.add("Agra")
        var spinner = findViewById<Spinner>(R.id.spinner)
        val spinnerAdapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = spinnerAdapter
        spinnerAdapter.add("Mumbai")
        spinnerAdapter.notifyDataSetChanged()
        setRecycleViewScrollListener()
        loadServerData = true
        loadData(filters = filters!!)
    }

    private fun setRecycleViewScrollListener() {
        // use a linear layout manager
        val mLayoutManager = LinearLayoutManager(this)
        mRecyclerView.setLayoutManager(mLayoutManager)

        mRecyclerView.addOnScrollListener(object: EndlessRecyclerViewScrollListener(mLayoutManager){
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                if(loadServerData){
                    loadData(page, filters!!)
                }
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.list_view, menu)
        // Associate searchable configuration with the SearchView
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView = menu?.findItem(R.id.action_search)?.actionView as SearchView
        searchView?.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView?.setMaxWidth(Integer.MAX_VALUE)
        // listening to search query text change
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                // filter recycler view when query submitted
                recyclerModels = JSONArray()
                val filtersArray = JSONArray()

                // owner filter
                var filterSet = JSONArray().put("owner").put("=").put(user)
                filtersArray.put(filterSet)

                // name like query filter
                filterSet = JSONArray().put("name").put("like").put("%$query%")
                filtersArray.put(filterSet)

                filters = filtersArray.toString()
                loadData(filters=filters!!)
                return true
            }

            override fun onQueryTextChange(query: String): Boolean {
                // filter recycler view when text is changed
                /*
                recyclerModels = JSONArray()
                filters = "[[\"owner\",\"=\",\"$user\"],[\"name\",\"like\",\"%$query%\"]]"
                loadData(filters=filters!!)
                */
                return false
            }
        })

        searchView?.setOnCloseListener(object :SearchView.OnCloseListener {
            override fun onClose(): Boolean {
                recyclerModels = JSONArray()

                var filtersArray = JSONArray()
                val filterSet = JSONArray().put("owner").put("=").put(user)
                filtersArray.put(filterSet)

                filters = filtersArray.toString()
                loadData(filters=filters!!)
                setRecycleViewScrollListener()
                return false
            }
        })
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id = item?.getItemId()
        return if (id == R.id.action_search) {
            true
        } else super.onOptionsItemSelected(item)
    }

    fun loadData(page:Int? = null, filters:JSONArray, limit_page_length: String = "5"){
        loadData(page, filters.toString(), limit_page_length)
    }

    fun loadData(page:Int? = null, filters:JSONObject, limit_page_length: String = "5"){
        loadData(page, filters.toString(), limit_page_length)
    }

    fun loadData(page: Int? = null, filters: String, limit_page_length:String = "5") {
        val limit_start = ((page?.times(limit_page_length.toInt()))?:0).toString()
        progressBar?.visibility = View.VISIBLE

        Log.d("Filters", filters)
        Log.d("Limit_page_length", limit_page_length)
        Log.d("limit_start", limit_start)

        val request = FrappeClient(this).get_all(
                doctype = "Add Produce",
                filters = filters,
                limit_page_length = limit_page_length,
                limit_start = limit_start
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
                loadServerData = true
                progressBar?.visibility = View.GONE
            }

            override fun onErrorResponse(s: String) {
                loadServerData =  false
                progressBar?.visibility = View.VISIBLE
                toast(R.string.somethingWrong)
            }
        }
        if(loadServerData) {
            FrappeClient(this).executeRequest(request, responseCallback)
        }
    }
}