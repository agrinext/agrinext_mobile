package org.agrinext.agrimobile.Activities

import android.os.Bundle
import org.agrinext.agrimobile.Android.BaseCompatActivity
import org.agrinext.agrimobile.Android.FormGeneraterUI
import org.agrinext.agrimobile.Android.FrappeClient
import org.agrinext.agrimobile.Android.StringUtil
import org.jetbrains.anko.setContentView
import org.json.JSONObject
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.util.Log


class FormGeneratorActivity : BaseCompatActivity() {

    var docMeta: JSONObject? = null
    var doctype: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*if(intent.hasExtra(DOCTYPE)){
            setupDocType(intent.getStringExtra(DOCTYPE))
        }*/
        FormGeneraterUI(JSONObject(), JSONObject()).setContentView(this)
    }

    override fun onBackPressed() {
        finish()
    }

    open fun setupDocType(doctype:String) {
        Log.d("DOC", doctype)
        this.doctype = doctype
        val keyDocTypeMeta = StringUtil.slugify(this.doctype) + "_meta"
        var pref = getSharedPreferences(ListingActivity.DOCTYPE_META, 0)
        val editor = pref.edit()
        val doctypeMetaString = pref.getString(keyDocTypeMeta, null)
        if (doctypeMetaString != null){
            this.docMeta = JSONObject(doctypeMetaString)
        } else {
            FrappeClient(applicationContext).retrieveDocTypeMeta(editor, keyDocTypeMeta, this.doctype)
        }
    }

    /*

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == AppCompatActivity.RESULT_OK) {
            if(requestCode == SHOW_FORM_REQUEST){
                //setupDocType(data?.getStringExtra(DOCTYPE)!!)
            }
        }
    }

    */

    companion object {
        val SHOW_FORM_REQUEST = 500
        val DOCTYPE = "doctype"
    }
}
