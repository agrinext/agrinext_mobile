package org.agrinext.agrimobile.Android

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.find
import org.json.JSONArray
import org.json.JSONObject

class FormViewAdapter(filterList: JSONArray): RecyclerView.Adapter<FormViewAdapter.ViewHolder>() {

    var filterList = filterList
    var docMeta = JSONObject()

    constructor(filterList: JSONArray, docMeta: JSONObject) : this(filterList) {
        this.filterList = filterList
        this.docMeta = docMeta
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val label: TextView = itemView.find(FormGeneraterUI.Companion.Ids.fieldName)
        val value: EditText = itemView.find(FormGeneraterUI.Companion.Ids.fieldValue)

    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): FormViewAdapter.ViewHolder {
        return FormViewAdapter.ViewHolder(FormGeneraterUI().createView(AnkoContext.create(parent!!.context, parent)))
    }

    override fun getItemCount(): Int {
        // return listing size
        return filterList.length()
    }

    override fun onBindViewHolder(holder: FormViewAdapter.ViewHolder, position: Int) {
        holder.label.text = filterList[position].toString()
        holder.value.setText("Value")
    }
}