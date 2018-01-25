package org.agrinext.agrimobile.Activities

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import org.agrinext.agrimobile.Android.*
import org.agrinext.agrimobile.Frappe.DocField
import org.agrinext.agrimobile.R
import org.json.JSONArray


class FormGeneratorActivity : BaseCompatActivity() {

    internal lateinit var mRecyclerView: RecyclerView
    var recyclerAdapter: FormViewAdapter? = null
    var recyclerModels = ArrayList<DocField>()
    var docname: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form)

        // set docname and meta
        if(intent.hasExtra("DocType") && intent.hasExtra("DocName")){
            setupDocType(intent.getStringExtra("DocType"))
            this.docname = intent.getStringExtra("DocName")
        }

        val fields = docMeta?.getJSONArray("fields")!!
        var pushDocMeta: DocField
        for(i in 0 until fields.length()-1) {
            pushDocMeta = DocField(fields.getJSONObject(i))
            if(pushDocMeta.fieldname!=null)
                recyclerModels.add(pushDocMeta)
        }


        mRecyclerView = findViewById(R.id.form_recycler_view)

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
}
