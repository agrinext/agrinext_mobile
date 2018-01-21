package org.agrinext.agrimobile.Android

import android.R
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.find
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.json.JSONArray
import org.json.JSONObject
import java.util.ArrayList

/**
 * Created by revant on 28/12/17.
 */

class FilterViewAdapter(filterList:JSONArray): RecyclerView.Adapter<FilterViewAdapter.ViewHolder>() {
    var immutableFilterlist = JSONArray()
    var filterList = JSONArray()
    var docMeta = JSONObject()
    init {
        immutableFilterlist = filterList
        this.filterList = filterList
    }

    constructor(filterList: JSONArray, docMeta:JSONObject) : this(filterList) {
        immutableFilterlist = filterList
        this.filterList = filterList
        this.docMeta = docMeta
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val context = itemView.context
        // Get list_item (and other fields) from ListItemUI
        val spinner: Spinner = itemView.find(FiltersItemUI.Companion.Ids.docFieldSpinner)
        // Bind values to name and other fields above

        val bRemoveFilter = itemView.find<Button>(FiltersItemUI.Companion.Ids.removeFilter)

        fun bind(filtersArray: JSONArray?, fieldsArray: JSONArray) {

            Log.d("filtersArray", filtersArray.toString())
            var list = ArrayList<String>().apply {
                add(context.resources.getString(org.agrinext.agrimobile.R.string.sort_name))
                add(context.resources.getString(org.agrinext.agrimobile.R.string.last_modified_on))
                add(context.resources.getString(org.agrinext.agrimobile.R.string.created_on))
                add(context.resources.getString(org.agrinext.agrimobile.R.string.most_used))
            }

            for(i in 0 until fieldsArray?.length()!! - 1){
                list.add(fieldsArray.getString(i))
            }
            val spinnerAdapter = ArrayAdapter<String>(itemView.context, R.layout.simple_list_item_1, list)
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

        val fieldsArray = JSONArray()
        val fields = docMeta?.getJSONArray("fields")!!

        for (i in 0 until fields.length() - 1){
            if(fields.getJSONObject(i).has("label")){
                fieldsArray.put(fields.getJSONObject(i).getString("label"))
            }
        }
        val filtersArray = filterList.getJSONArray(p)

        holder!!.bRemoveFilter.onClick {
            filterList.remove(p)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position,filterList.length())
        }

        holder!!.bind(filtersArray, fieldsArray)
    }
}