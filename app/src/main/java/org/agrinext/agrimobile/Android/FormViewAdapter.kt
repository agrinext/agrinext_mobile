package org.agrinext.agrimobile.Android

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import org.agrinext.agrimobile.Frappe.DocField
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.find
import org.json.JSONArray
import org.json.JSONObject
import org.w3c.dom.Text
import java.util.*

class FormViewAdapter(filterList: ArrayList<DocField>) : RecyclerView.Adapter<FormViewAdapter.ViewHolder>() {

    var filterList = filterList
    var docMeta = JSONObject()
    var position: Int = -1

    constructor(filterList: ArrayList<DocField>, docMeta: JSONObject) : this(filterList) {
        this.filterList = filterList
        this.docMeta = docMeta
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val label: TextView = itemView.find(FormGeneraterUI.Companion.Ids.fieldName)
        var value: View = itemView.find<View>(FormGeneraterUI.Companion.Ids.fieldValue)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): FormViewAdapter.ViewHolder {
        return FormViewAdapter.ViewHolder(FormGeneraterUI(filterList[getCurrentPosition()]).createView(AnkoContext.create(parent!!.context, parent)))
    }

    override fun getItemCount(): Int {
        // return listing size
        return filterList.size
    }

    fun getCurrentPosition(): Int {
        return this.position + 1
    }

    override fun onBindViewHolder(holder: FormViewAdapter.ViewHolder, position: Int) {
        this.position = position
        var jsonObject = filterList[position]
        holder.label.text = jsonObject.label
        val viewType = holder.value.javaClass.simpleName
        if(viewType == "EditText") {
            val value = holder.value as EditText
            value.setText("Edit Value")
        }
        else {
            val value = holder.value as TextView
            value.text = "Non Edit Value"
        }
    }
}