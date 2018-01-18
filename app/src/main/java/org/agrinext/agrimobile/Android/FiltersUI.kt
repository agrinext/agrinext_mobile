package org.agrinext.agrimobile.Android

import android.util.Log
import android.view.Gravity
import org.agrinext.agrimobile.Activities.FiltersActivity
import org.agrinext.agrimobile.Activities.FormGeneratorActivity
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.json.JSONException
import org.json.JSONObject

/**
 * Created by revant on 18/1/18.
 */
class FiltersUI(docMeta: JSONObject): AnkoComponent<FiltersActivity> {

    val docMeta = docMeta

    override fun createView(ui: AnkoContext<FiltersActivity>) = with(ui) {
         scrollView {
             verticalLayout {
                padding = dip(10)
                val fields = docMeta.getJSONArray("fields")
                for(i in 0 until fields.length() - 1){
                    val label =
                    textView {
                        gravity = Gravity.CENTER
                        if(fields.getJSONObject(i).has("label")){
                            if(!fields.getJSONObject(i).getString("label").isNullOrEmpty())
                                Log.d("fieldlabel", fields.getJSONObject(i).getString("label"))
                                text = fields.getJSONObject(i).getString("label")
                        }
                    }
                }


            }
        }
    }
}