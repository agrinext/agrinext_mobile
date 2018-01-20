package org.agrinext.agrimobile.Android

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.find
import org.json.JSONArray
import org.json.JSONObject

/**
 * Created by revant on 28/12/17.
 */

class FilterViewAdapter(var filterList:JSONArray): RecyclerView.Adapter<FilterViewAdapter.ViewHolder>() {
    val immutableFilterlist = filterList

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Get list_item (and other fields) from ListItemUI
        val name: TextView = itemView.find(FiltersItemUI.Companion.Ids.fieldName)
        // Bind values to name and other fields above
        fun bind(jsonObject: JSONObject?) {
            name.text = "abc"//jsonObject?.getString("name")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        return ViewHolder(FiltersItemUI().createView(AnkoContext.create(parent!!.context, parent)))
    }

    override fun getItemCount(): Int {
        // return listing size
        return filterList.length()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var p = position
        while(filterList.length() < p){
            p -= filterList.length()
        }
        val jsonObject = filterList.getJSONObject(p)
        holder!!.bind(jsonObject)
    }
}