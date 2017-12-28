package org.agrinext.agrimobile.Activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import org.agrinext.agrimobile.Helpers.ListViewAdapter
import org.agrinext.agrimobile.R
import org.json.JSONArray
import org.json.JSONObject

class ListingActivity : AppCompatActivity() {
    internal lateinit var doc_list: JSONArray
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listing)

        val mRecyclerView = findViewById(R.id.recycler_view) as RecyclerView

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        val mLayoutManager = LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // JSON Array from frappe's listing
        var jsonArray = JSONArray()
        val jsonObject = JSONObject()
        jsonObject.put("name","List Item")
        jsonArray.put(jsonObject)

        // specify an adapter
        val mAdapter = ListViewAdapter(jsonArray);
        mRecyclerView.setAdapter(mAdapter);
    }
}
