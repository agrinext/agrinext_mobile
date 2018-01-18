package org.agrinext.agrimobile.Activities

import android.os.Bundle
import org.agrinext.agrimobile.Android.BaseCompatActivity
import org.agrinext.agrimobile.Android.FiltersUI
import org.jetbrains.anko.setContentView
import org.json.JSONObject

class FiltersActivity : BaseCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(intent.hasExtra(DOCTYPE)){
            setupDocType(intent.getStringExtra(DOCTYPE))
        }

        FiltersUI(this.docMeta!!).setContentView(this)
    }
}
