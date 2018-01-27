package org.agrinext.agrimobile.Activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
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

class PhotosActivity : BaseCompatActivity() {

    internal lateinit var mRecyclerView: RecyclerView
    var recyclerAdapter: FilterViewAdapter? = null
    var recyclerModels = JSONArray()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filters)

        // set doctype and meta
        if(intent.hasExtra(DOCTYPE)){
            setupDocType(intent.getStringExtra(DOCTYPE))
        }

        // set filters
        if(intent.hasExtra(ListingActivity.KEY_FILTERS)) {
            this.filters = JSONArray(intent.extras.getString(ListingActivity.KEY_FILTERS))
            recyclerModels = filters as JSONArray
        }

        mRecyclerView = findViewById(R.id.filter_recycler_view)

        val mLayoutManager = GridLayoutManager(this,2)
        mRecyclerView.setLayoutManager(mLayoutManager)
        Log.d("models", recyclerModels.toString())
        Log.d("meta", docMeta.toString())
        recyclerAdapter = FilterViewAdapter(recyclerModels, docMeta?:JSONObject())
        mRecyclerView.adapter = recyclerAdapter

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true)

        bAddFilter.onClick {
            recyclerModels.put(JSONArray().put("name").put("=").put(""))
            recyclerAdapter!!.notifyItemInserted(recyclerModels.length() - 1)
        }

        bSetFilter.onClick {
            var setFilter = false
            for(i in 0 until recyclerModels.length()) {
                Log.d("bSetFilters", recyclerModels.getJSONArray(i).toString())
                if(recyclerModels.getJSONArray(i).length() == 0) {
                    Log.d("bSetFilters", recyclerModels.toString())
                    toast(getString(R.string.please_set_filter))
                    setFilter = false
                } else {
                    setFilter = true
                }
            }
            if(setFilter || recyclerModels.length() == 0){
                val results = Intent()
                results.putExtra(ListingActivity.KEY_FILTERS, recyclerModels.toString())
                results.putExtra(ListingActivity.KEY_DOCTYPE, doctype)
                setResult(Activity.RESULT_OK, results)
                finish()
            }
        }
    }
}
