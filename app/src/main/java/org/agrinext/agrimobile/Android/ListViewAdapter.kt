package org.agrinext.agrimobile.Android

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import org.agrinext.agrimobile.R
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.find
import org.json.JSONArray
import org.json.JSONObject

/**
 * Created by revant on 28/12/17.
 */

class ListViewAdapter(val doc_list:JSONArray): RecyclerView.Adapter<ListViewAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Get list_item (and other fields) from ListItemUI
        val name: TextView = itemView.find(R.id.list_item)
        // Bind values to name and other fields above
        fun bind(jsonObject: JSONObject?) {
            print(itemView.toString())
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
        val jsonObject = doc_list.getJSONObject(position)
        holder!!.bind(jsonObject)
    }
}