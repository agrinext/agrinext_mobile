package org.agrinext.agrimobile.Activities

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import kotlinx.android.synthetic.main.activity_filters.*
import org.agrinext.agrimobile.Android.BaseCompatActivity
import org.agrinext.agrimobile.Android.FilterViewAdapter
import org.agrinext.agrimobile.Android.ListViewAdapter
import org.agrinext.agrimobile.R
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.toast
import org.json.JSONArray
import org.json.JSONObject

class FiltersActivity : BaseCompatActivity() {

    internal lateinit var mRecyclerView: RecyclerView
    var recyclerAdapter: FilterViewAdapter? = null
    var recyclerModels = JSONArray()
    var filters: JSONArray = JSONArray()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filters)

        //set doctype and meta
        if(intent.hasExtra(DOCTYPE)){
            setupDocType(intent.getStringExtra(DOCTYPE))
        }

        mRecyclerView = findViewById(R.id.filter_recycler_view)

        val mLayoutManager = LinearLayoutManager(this)
        mRecyclerView.setLayoutManager(mLayoutManager)

        recyclerAdapter = FilterViewAdapter(recyclerModels, docMeta!!)
        mRecyclerView.adapter = recyclerAdapter

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true)

        bAddFilter.onClick {
            var jsonArray = JSONArray()
            recyclerModels.put(jsonArray)
            recyclerAdapter!!.notifyItemInserted(recyclerModels.length() - 1)
        }

        bSetFilter.onClick {
            Log.d("bSetFilters", recyclerModels.toString())
            toast("Set Filters!")
        }
    }
}
