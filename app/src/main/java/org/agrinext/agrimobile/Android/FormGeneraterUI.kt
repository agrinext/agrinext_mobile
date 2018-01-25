package org.agrinext.agrimobile.Android

import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import org.jetbrains.anko.*
import org.jetbrains.anko.cardview.v7.cardView

class FormGeneraterUI: AnkoComponent<ViewGroup> {

//    val doc = doc
//    val docMeta = docMeta

    override fun createView(ui: AnkoContext<ViewGroup>): View {

        val out = with(ui) {
            cardView {
                lparams(width= matchParent, height=dip(70))

                linearLayout() {
                    lparams(width= matchParent, height=matchParent)
                    weightSum = 1f
                    orientation = LinearLayout.HORIZONTAL

                    textView {
                        id = Ids.fieldName
                        text = "FieldName"
                        textSize = dip(8).toFloat()
                        gravity = Gravity.CENTER
                    }.lparams {
                        weight = 0.30f //not support value
                    }

                    editText {
                        id = Ids.fieldValue
                        setText("FieldValue")
                        textSize = dip(8).toFloat()
                        inputType = 0
                        backgroundResource = android.R.color.transparent
                    }.lparams {
                        weight = 0.70f //not support value
                    }

                }
            }
        }
        return out
    }

    companion object {
        object Ids {
            val fieldName = 0
            val fieldValue = 1
        }
    }
}