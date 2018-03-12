package org.agrinext.agrimobile.Fragments

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import io.frappe.android.Controllers.ListingFragment
import org.agrinext.agrimobile.Activities.MainActivity.Companion.LOCATION
import org.json.JSONArray

class MarketActivity : ListingFragment() {

    override fun setupFilters() {
        val sharedPref: SharedPreferences = activity.getSharedPreferences(LOCATION, Context.MODE_PRIVATE)

        if(sharedPref.contains("location")) {
            val filtersArray = JSONArray()
            // location filter
            Log.d("This is ", sharedPref.getString("location", ""))
            var filterSet = JSONArray().put("location").put("=").put(sharedPref.getString("location", ""))
            filtersArray.put(filterSet)

            filters = filtersArray
        }
    }

    override fun setupDocType() {
        this.doctype = "Produce"
        super.setupDocType()
    }
}
