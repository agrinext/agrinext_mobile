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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FormGeneraterUI(JSONObject(), JSONObject()).setContentView(this)
    }

    override fun onBackPressed() {

        finish()

    }
}
