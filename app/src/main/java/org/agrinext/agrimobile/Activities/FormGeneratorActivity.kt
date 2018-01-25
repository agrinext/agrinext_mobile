package org.agrinext.agrimobile.Activities

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import com.mntechnique.otpmobileauth.auth.AuthReqCallback
import org.agrinext.agrimobile.Android.*
import org.agrinext.agrimobile.Frappe.DocField
import org.agrinext.agrimobile.R
import org.jetbrains.anko.toast
import org.json.JSONArray
import org.json.JSONObject


class FormGeneratorActivity : BaseCompatActivity() {

    internal lateinit var mRecyclerView: RecyclerView
    var recyclerAdapter: FormViewAdapter? = null
    var recyclerModels = ArrayList<DocField>()
    var docname: String? = null
    var progressBar: ProgressBar? = null
    var excludeName = ArrayList<String>().apply{
        add("produce_name")
    }

    companion object {
        var docData = JSONObject()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form)

        // set docname and meta
        if(intent.hasExtra("DocType") && intent.hasExtra("DocName")){
            setupDocType(intent.getStringExtra("DocType"))
            this.docname = intent.getStringExtra("DocName")
        }

        // make progress bar visible while loading data
        progressBar = this.findViewById(R.id.delay_progress_bar)
        progressBar?.visibility = View.VISIBLE

        fetchDoc(this.docname!!)

        validateDocMeta()

        setupRecycler()
    }

    // Add DocField object of Meta data
    fun validateDocMeta() {
        val fields = docMeta?.getJSONArray("fields")!!
        var pushDocMeta: DocField

        for(i in 0 until fields.length()-1) {
            pushDocMeta = DocField(fields.getJSONObject(i))
            if(pushDocMeta.fieldname!=null && !excludeName.contains(pushDocMeta!!.fieldname))
                recyclerModels.add(pushDocMeta)
        }
    }

    // Call server and fetch data for the clicked item
    fun fetchDoc(docname: String) {
        val filters = JSONArray().put(JSONArray().put("name").put("=").put(docname))
        val request = FrappeClient(this).get_all(
                doctype = this.doctype!!,
                filters = filters.toString(),
                fields = "[\"*\"]"
        )

        val responseCallback = object : AuthReqCallback {
            override fun onSuccessResponse(result: String) {
                docData = JSONObject(JSONObject(result).getJSONArray("data").get(0).toString())
                this@FormGeneratorActivity.findViewById<TextView>(R.id.docname).setText(docname)
                viewIterator()
            }

            override fun onErrorResponse(error: String) {
                toast(R.string.somethingWrong)
            }
        }

        FrappeClient(this).executeRequest(request, responseCallback)
    }

    fun setupRecycler() {
        mRecyclerView = findViewById(R.id.form_recycler_view)
        mRecyclerView.visibility = View.INVISIBLE

        val mLayoutManager = LinearLayoutManager(this)
        mRecyclerView.setLayoutManager(mLayoutManager)

        recyclerAdapter = FormViewAdapter(recyclerModels, docMeta!!)
        mRecyclerView.adapter = recyclerAdapter

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true)
    }

    override fun onBackPressed() {
        finish()
    }

    fun viewIterator(){
        var holderArray = FormViewAdapter.holderArray
        for(i in 0..holderArray.size-1) {
            updateFieldData(holderArray[i])
        }
        // make progress bar visible while loading data
        progressBar?.visibility = View.GONE
        mRecyclerView.visibility = View.VISIBLE
    }

    // Set value for the created fields
    fun updateFieldData(holder: FormViewAdapter.ViewHolder) {

        var jsonObject = recyclerModels[holder.position]
        holder.label.text = jsonObject.label + " : "
        val viewType = holder.value.javaClass.simpleName

        if(viewType == "EditText") {
            val value = holder.value as EditText
            value.setText(docData.getString(jsonObject.fieldname))
        }
        else if(viewType == "TextView") {
            val value = holder.value as TextView
            value.text = docData.getString(jsonObject.fieldname)
        }
        else if(viewType == "CheckBox") {
            var value = holder.value as CheckBox
            value.setChecked(docData.getInt(jsonObject.fieldname)==1)
        }
    }
}
