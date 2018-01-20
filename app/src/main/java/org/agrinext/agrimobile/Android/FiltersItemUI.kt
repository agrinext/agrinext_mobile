package org.agrinext.agrimobile.Android

import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewManager
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import org.agrinext.agrimobile.Activities.FiltersActivity
import org.agrinext.agrimobile.Frappe.DocField
import org.agrinext.agrimobile.R
import org.jetbrains.anko.*
import org.jetbrains.anko.cardview.v7.cardView
import org.json.JSONObject
import java.util.ArrayList

/**
 * Created by revant on 18/1/18.
 */
class FiltersItemUI: AnkoComponent<ViewGroup> {

    override fun createView(ui: AnkoContext<ViewGroup>): View {
        // UI list item
        val out = with(ui){
            linearLayout {
                lparams(width = matchParent, height = dip(200))
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER
                cardView {
                    verticalLayout {
                        spinner {
                            id = Ids.docFieldSpinner
                        }

                        spinner {
                            var list = ArrayList<String>().apply {
                                add("Equal")
                                add("Like")
                                add("In")
                                add("Not In")
                                add("Not Equal")
                                add("Not Like")
                                add("Between")
                                add(">")
                                add("<")
                                add(">=")
                                add("<=")
                            }
                            val spinnerAdapter = ArrayAdapter<String>(ctx, android.R.layout.simple_list_item_1, list)
                            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                            adapter = spinnerAdapter
                        }
                    }.lparams(width = matchParent, height = matchParent)
                }.lparams(height = dip(190), width = matchParent)
            }
        }
        return out
    }

    companion object {
        object Ids {
            val fieldName = 0
            val listItem = 1
            val docFieldSpinner = 2
        }
    }
    /*
    (docMeta: JSONObject)
    this.docMeta = docMeta
    override fun createView(ui: AnkoContext<FiltersActivity>) = with(ui) {
        scrollView {
                verticalLayout {
                    val fields = docMeta.getJSONArray("fields")
                    for(i in 0 until fields.length() - 1){
                        val f = fields.getJSONObject(i)
                        val field = DocField(f)
                        gravity = Gravity.CENTER
                        if(!field.label.isNullOrEmpty()){
                            linearLayout {
                                orientation = LinearLayout.VERTICAL
                                padding = dip(10)
                                textView {
                                    gravity = Gravity.CENTER
                                    text = field.label
                                    applyRecursively {
                                        setTextAppearance(R.style.TextAppearance_AppCompat_Medium)
                                    }
                                }

                                spinner {
                                    var list = ArrayList<String>().apply {
                                        add("Equal")
                                        add("Like")
                                        add("In")
                                        add("Not In")
                                        add("Not Equal")
                                        add("Not Like")
                                        add("Between")
                                        add(">")
                                        add("<")
                                        add(">=")
                                        add("<=")
                                    }
                                    val spinnerAdapter = ArrayAdapter<String>(ctx, android.R.layout.simple_list_item_1, list)
                                    spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                    adapter = spinnerAdapter
                                }
                            }
                        }
                    }
                }
            }
    }

    */
}