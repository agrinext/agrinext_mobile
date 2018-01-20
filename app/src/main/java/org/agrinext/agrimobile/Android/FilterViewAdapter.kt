package org.agrinext.agrimobile.Android

import android.R
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.find
import org.json.JSONArray
import org.json.JSONObject
import java.util.ArrayList

/**
 * Created by revant on 28/12/17.
 */

class FilterViewAdapter(var filterList:JSONArray): RecyclerView.Adapter<FilterViewAdapter.ViewHolder>() {
    val immutableFilterlist = filterList
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        // Get list_item (and other fields) from ListItemUI
        val spinner: Spinner = itemView.find(FiltersItemUI.Companion.Ids.docFieldSpinner)
        // Bind values to name and other fields above
        fun bind(jsonArray: JSONArray?) {
            var list = ArrayList<String>()

            for(i in 0 until jsonArray?.length()!! - 1){
                list.add(jsonArray.getString(i))
            }
            val spinnerAdapter = ArrayAdapter<String>(itemView.context, R.layout.simple_spinner_item, list)
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = spinnerAdapter
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
        val jsonArray = filterList.getJSONArray(p)
        holder!!.bind(jsonArray)
    }
}