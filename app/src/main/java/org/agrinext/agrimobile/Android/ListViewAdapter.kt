package org.agrinext.agrimobile.Android

import android.app.Activity
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import org.agrinext.agrimobile.Activities.FormGeneratorActivity
import org.agrinext.agrimobile.R
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.find
import org.json.JSONArray
import org.json.JSONObject

/**
 * Created by revant on 28/12/17.
 */

class ListViewAdapter(var doc_list:JSONArray, var context: Activity): RecyclerView.Adapter<ListViewAdapter.ViewHolder>(), Filterable {
    fun setLoadDataCallback() {

    }
    val immutable_doc_list = doc_list
    override fun getFilter(): Filter {
        return object : Filter() {
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                doc_list = results?.values as JSONArray
                Log.d("results", doc_list.toString())
                notifyDataSetChanged()
            }

            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filterResults = FilterResults()

                Log.d("constraint", constraint.toString())
                val charString = constraint.toString();
                if (charString.isNullOrEmpty()) {
                    filterResults.values = immutable_doc_list
                } else {
                    var filteredList = JSONArray()
                    for (i in 0..doc_list.length() - 1) {
                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if(doc_list.getJSONObject(i).getString("name").toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.put(doc_list.getJSONObject(i))
                        }
                    }
                    filterResults.values = filteredList
                }

                return filterResults
            }
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Get list_item (and other fields) from ListItemUI
        val name: TextView = itemView.find(ListItemUI.Companion.Ids.listItem)
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
        return doc_list.length()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var p = position
        while(doc_list.length() < p){
            p -= doc_list.length()
        }
        val jsonObject = doc_list.getJSONObject(p)
        holder!!.bind(jsonObject)

        holder.itemView.setOnClickListener() {
            context.startActivity(Intent(context, FormGeneratorActivity::class.java))
        }
    }
}