package org.agrinext.agrimobile.Activities

import android.app.DatePickerDialog
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.*
import com.mntechnique.otpmobileauth.auth.AuthReqCallback
import org.agrinext.agrimobile.Android.*
import org.agrinext.agrimobile.Frappe.DocField
import org.agrinext.agrimobile.R
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import android.util.Log
import android.view.ViewTreeObserver


class FormGeneratorActivity : BaseCompatActivity() {

    internal lateinit var mRecyclerView: RecyclerView
    var recyclerAdapter: FormViewAdapter? = null
    var recyclerModels = ArrayList<DocField>()
    var docname: String = ""
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

        // make progress bar visible while loading data
        progressBar = this.findViewById(R.id.delay_progress_bar)
        progressBar?.visibility = View.VISIBLE

        // set meta
        if (intent.hasExtra("DocType")) {
            setupDocType(intent.getStringExtra("DocType"))
        }

        // if docname, fetch doc data
        if (intent.hasExtra("DocName")) {
            this.docname = intent.getStringExtra("DocName")
            fetchDoc(this.docname!!)
        }

        validateDocMeta()

        setupRecycler()
    }

    // Add DocField object of Meta data
    fun validateDocMeta() {
        val fields = docMeta?.getJSONArray("fields")!!
        var pushDocMeta: DocField

        for (i in 0 until fields.length()-1){
            pushDocMeta = DocField(fields.getJSONObject(i))
            if(pushDocMeta.fieldname != null && !excludeName.contains(pushDocMeta!!.fieldname))
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
        doAsync {
            FrappeClient(this@FormGeneratorActivity).executeRequest(request, responseCallback)
        }
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

        val vto = mRecyclerView.getViewTreeObserver() // wait for all views to be loaded
        vto.addOnGlobalLayoutListener(ViewTreeObserver.OnGlobalLayoutListener {
            if (mRecyclerView.adapter.itemCount == recyclerModels.size
                    && docname.isBlank()) {
                progressBar?.visibility = View.GONE
                viewIterator()
            }
        })

    }

    override fun onBackPressed() {
        finish()
    }

    fun viewIterator(){

        var holderArray = FormViewAdapter.holderArray
        for (i in 0..holderArray.size - 1) {
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

        if (viewType == "EditText" && (jsonObject.fieldtype == "DateTime" || jsonObject.fieldtype == "Date")) {
            val value = holder.value as EditText
            if (docname.isNotBlank()) value.setText(docData.getString(jsonObject.fieldname))
            displayCalender(value)
            value.inputType = 0
        } else if (viewType == "EditText") {
            val value = holder.value as EditText
            if (docname.isNotBlank()) value.setText(docData.getString(jsonObject.fieldname))
            value.inputType = 0
        } else if (viewType == "TextView") {
            val value = holder.value as TextView
            if (docname.isNotBlank()) value.text = docData.getString(jsonObject.fieldname)
        } else if (viewType == "CheckBox") {
            var value = holder.value as CheckBox
            if (docname.isNotBlank()) value.setChecked(docData.getInt(jsonObject.fieldname) == 1)
        }

    }

    fun displayCalender(value: EditText) {
        // Opens up a Calendar dialog box if the fieldtype is DateTime or Date

        var cal = Calendar.getInstance()

        val dateSetListener = object : DatePickerDialog.OnDateSetListener {
            override fun onDateSet(view: DatePicker, year: Int, monthOfYear: Int,
                                   dayOfMonth: Int) {
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                val myFormat = "yyyy-MM-dd" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.ENGLISH)
                value.setText(sdf.format(cal.time))
            }
        }

        var dateDialog = DatePickerDialog(this@FormGeneratorActivity,
                dateSetListener,
                // set DatePickerDialog to point to today's date when it loads up
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH))

        value.setOnClickListener(View.OnClickListener {
            dateDialog.show()
        })

        value.setOnFocusChangeListener(View.OnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                dateDialog.show()
            }
        })
    }
}
