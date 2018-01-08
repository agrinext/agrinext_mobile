package org.agrinext.agrimobile.Android

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import org.agrinext.agrimobile.R
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.find
import org.json.JSONArray
import org.json.JSONObject

/**
 * Created by revant on 28/12/17.
 */

class ListViewAdapter(var doc_list:JSONArray): RecyclerView.Adapter<ListViewAdapter.ViewHolder>(), Filterable {
    var dataListFiltered = doc_list
    override fun getFilter(): Filter {
        return object : Filter() {
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                dataListFiltered = results?.values as JSONArray
                Log.d("results", dataListFiltered.toString())
                notifyDataSetChanged()
            }

            override fun performFiltering(constraint: CharSequence?): FilterResults {
                Log.d("constraint", constraint.toString())
                val charString = constraint.toString();
                if (charString.isNullOrEmpty()) {
                    dataListFiltered = doc_list
                } else {
                    var filteredList = JSONArray()
                    for (i in 0..doc_list.length() - 1) {
                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if(doc_list.getJSONObject(i).getString("name").toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.put(doc_list.getJSONObject(i))
                        }
                    }
                    dataListFiltered = filteredList
                }

                val filterResults = FilterResults()
                filterResults.values = dataListFiltered

                return filterResults
            }
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Get list_item (and other fields) from ListItemUI
        val name: TextView = itemView.find(R.id.list_item)
        // Bind values to name and other fields above
        fun bind(jsonObject: JSONObject?) {
            name.text = jsonObject?.getString("name")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        return ViewHolder(ListItemUI().createView(AnkoContext.create(parent!!.context, parent)))
    }

    override fun getItemCount(): Int {
        // return listing size
        return dataListFiltered.length()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var p = position
        while(dataListFiltered.length() < p){
            p -= dataListFiltered.length()
        }
        val jsonObject = dataListFiltered.getJSONObject(p)
        holder!!.bind(jsonObject)
    }
}