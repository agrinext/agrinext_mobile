package org.agrinext.agrimobile.Activities

import android.app.Activity
import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import com.mntechnique.otpmobileauth.auth.AuthReqCallback
import org.agrinext.agrimobile.R
import org.jetbrains.anko.toast
import org.json.JSONArray
import org.json.JSONObject
import android.content.Intent
import android.support.v4.app.Fragment
import android.view.*
import android.widget.*
import org.jetbrains.anko.find
import org.jetbrains.anko.sdk25.coroutines.onClick
import java.util.ArrayList
import android.widget.AdapterView.OnItemSelectedListener
import org.agrinext.agrimobile.Android.*
import android.view.MenuInflater
import kotlinx.android.synthetic.main.activity_listing.*
import kotlinx.android.synthetic.main.content_main.*
import org.jetbrains.anko.support.v4.find
import org.jetbrains.anko.support.v4.toast


open class ListingActivity : Fragment() {

    internal lateinit var mRecyclerView: RecyclerView
    var recyclerAdapter: ListViewAdapter? = null
    var recyclerModels = JSONArray()
    var searchView : SearchView? = null
    var progressBar: ProgressBar? = null
    var filters: JSONArray = JSONArray()
    var loadServerData = false
    var order_by:String? = "modified+desc"
    var sortOrder: String? = "desc"
    var doctype: String? = null
    var doctypeMetaJson = JSONObject()

    companion object {
        val DOCTYPE_META = "DOCTYPE_META"
        val KEY_DOCTYPE = "doctype"
        val KEY_FILTERS = "filters"
        val SET_DOCTYPE = 400
        val SET_DOCTYPE_FILTERS = 401

    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        return inflater?.inflate(R.layout.activity_listing, null)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupDocType()

        setupFilters()

        setupView()

        setupSortSpinner()

        setupSortOrder()

        setRecycleViewScrollListener()
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.list_view, menu)
        // Associate searchable configuration with the SearchView
        val searchManager = activity.getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView = menu?.findItem(R.id.action_search)?.actionView as SearchView
        searchView?.setSearchableInfo(searchManager.getSearchableInfo(activity.componentName))
        searchView?.setMaxWidth(Integer.MAX_VALUE)
        // listening to search query text change
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                // filter recycler view when query submitted
                recyclerModels = JSONArray()

                // name like query filter
                setupFilters()
                filters.put(JSONArray().put("name").put("like").put("%$query%"))

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
                setupFilters()
                loadData(filters=filters!!)
                setRecycleViewScrollListener()
                return false
            }
        })
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            R.id.action_search -> return true
            R.id.action_sort -> {
                sortLayout.visibility = View.VISIBLE
                return true
            }
            else -> return false
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when(requestCode){
            SET_DOCTYPE -> {
                if (resultCode == Activity.RESULT_OK && data != null)
                    this.doctype = data?.extras?.getString(KEY_DOCTYPE)
            }
            SET_DOCTYPE_FILTERS -> {
                if (resultCode == Activity.RESULT_OK && data != null){
                    this.doctype = data?.extras?.getString(KEY_DOCTYPE)
                    this.filters = JSONArray(data?.extras?.getString(KEY_FILTERS))
                }
            }
        }
    }

    open fun setupDocType() {
        if (doctype == null) {
            this.doctype = "Note"
        }

        val keyDocTypeMeta = StringUtil.slugify(this.doctype) + "_meta"
        var pref = activity.getSharedPreferences(DOCTYPE_META, 0)
        val editor = pref.edit()
        val doctypeMetaString = pref.getString(keyDocTypeMeta, null)
        if (doctypeMetaString != null){
            this.doctypeMetaJson = JSONObject(doctypeMetaString)
        } else {
            FrappeClient(activity).retrieveDocTypeMeta(editor, keyDocTypeMeta, this.doctype)
        }
    }

    fun setupView() {
        mRecyclerView = activity.findViewById(R.id.recycler_view)
        progressBar = activity.findViewById(R.id.edit_progress_bar)
        progressBar?.visibility = View.VISIBLE
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true)
    }

    open fun setupFilters() {
        this.filters = JSONArray()
    }

    fun setupSortSpinner() {
        var list = ArrayList<String>().apply {
            add(getString(R.string.last_modified_on))
            add(getString(R.string.sort_name))
            add(getString(R.string.created_on))
            add(getString(R.string.most_used))
        }
        var spinner = activity.findViewById<Spinner>(R.id.spinner)
        val spinnerAdapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, list)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = spinnerAdapter
        spinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>, selectedItemView: View, position: Int, id: Long) {
                recyclerModels = JSONArray()
                loadData(filters = filters!!)
                setRecycleViewScrollListener()
            }

            override fun onNothingSelected(parentView: AdapterView<*>) {
                // Nothing selected
            }

        }
    }

    fun setRecycleViewScrollListener() {
        // use a linear layout manager
        val mLayoutManager = LinearLayoutManager(activity)
        mRecyclerView.setLayoutManager(mLayoutManager)

        mRecyclerView.addOnScrollListener(object: EndlessRecyclerViewScrollListener(mLayoutManager){
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                if(loadServerData){
                    loadData(page, filters!!)
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                sortLayout.visibility = View.GONE
            }
        })
    }

    fun loadData(page: Int? = null,
                 filters: JSONArray,
                 limit_page_length:String = "5") {

        if(recyclerModels.length() == 0) loadServerData = true

        // limit_start is page * limit_page_length or 0
        val limit_start = ((page?.times(limit_page_length.toInt()))?:0).toString()

        // make progress bar visible while loading data
        progressBar?.visibility = View.VISIBLE

        // set order
        val sortSpinner = activity.find<Spinner>(R.id.spinner)
        val spinnerLabel = sortSpinner.selectedItem.toString()
        var spinnerField:String? = "modified"

        when(spinnerLabel){
            getString(R.string.last_modified_on) -> spinnerField="modified"
            getString(R.string.sort_name) -> spinnerField="name"
            getString(R.string.created_on) -> spinnerField="creation"
            getString(R.string.most_used) -> spinnerField="idx"
            else -> spinnerField="modified"
        }

        order_by = "$spinnerField+$sortOrder"

        val request = FrappeClient(activity).get_all(
                doctype = doctype!!,
                filters = filters.toString(),
                limit_page_length = limit_page_length,
                limit_start = limit_start,
                order_by = order_by
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
            FrappeClient(activity).executeRequest(request, responseCallback)
        }
    }

    fun setupSortOrder() {
        val sortOrderButton = find<Button>(R.id.sortOrderButton)
        sortOrderButton.onClick {
            recyclerModels = JSONArray()
            // change icon
            when(sortOrder) {
                "desc" -> sortOrderButton.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_keyboard_arrow_down,0)
                "asc" -> sortOrderButton.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_keyboard_arrow_up,0)
            }
            // name like query filter
            sortOrder = if (sortOrder == "desc") "asc" else "desc"
            loadData(filters = filters!!)
            setRecycleViewScrollListener()
        }
    }
}