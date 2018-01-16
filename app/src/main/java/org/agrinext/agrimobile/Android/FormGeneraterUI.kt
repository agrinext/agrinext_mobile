package org.agrinext.agrimobile.Android

import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import org.agrinext.agrimobile.Activities.FormGeneratorActivity
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.json.JSONException
import org.json.JSONObject

/**
 * Created by revant on 16/1/18.
 */
class FormGeneraterUI(doc: JSONObject, docMeta:JSONObject): AnkoComponent<FormGeneratorActivity> {

    val doc = doc
    val docMeta = docMeta

    override fun createView(ui: AnkoContext<FormGeneratorActivity>) = with(ui) {
        verticalLayout {
            padding = dip(32)

            imageView(android.R.drawable.ic_menu_manage).lparams {
                margin = dip(16)
                gravity = Gravity.CENTER
            }


            button("Error!") {
                onClick {
                    throw JSONException("User Thrown Exception!")
                }
            }
        }
    }
}