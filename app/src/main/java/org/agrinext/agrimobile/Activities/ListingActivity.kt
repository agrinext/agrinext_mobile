package org.agrinext.agrimobile.Activities

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import org.agrinext.agrimobile.Android.BaseCompactActivity
import org.agrinext.agrimobile.Helpers.ListViewAdapter
import org.agrinext.agrimobile.R
import org.json.JSONArray
import org.json.JSONObject

class ListingActivity : BaseCompactActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listing)

        val mRecyclerView: RecyclerView = findViewById(R.id.recycler_view)

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        val mLayoutManager = LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // JSON Array from frappe's listing
        var jsonArray = JSONArray()
        for (i in 0..100) {
            val jsonObject = JSONObject()
            jsonObject.put("name","List Item " + i.toString())
            jsonArray.put(jsonObject)
        }

        // specify an adapter
        val mAdapter = ListViewAdapter(jsonArray);
        mRecyclerView.setAdapter(mAdapter);
    }
}
