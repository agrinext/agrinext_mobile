package org.agrinext.agrimobile.Helpers

import android.annotation.TargetApi
import android.os.Build
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import org.agrinext.agrimobile.R
import org.jetbrains.anko.*
import org.jetbrains.anko.cardview.v7.cardView

/**
 * Created by revant on 28/12/17.
 */
class ListItemUI : AnkoComponent<ViewGroup> {
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun createView(ui: AnkoContext<ViewGroup>): View {
        // UI list item
        return with(ui) {
            linearLayout {
                lparams(width = matchParent, height = dip(100))
                orientation = LinearLayout.VERTICAL
                cardView {
                    verticalLayout {
                        textView {
                            gravity = Gravity.CENTER
                            id = R.id.list_item
                        }.lparams(width = matchParent, height = matchParent)
                    }.lparams(width = matchParent, height = matchParent)
                }.lparams(height = dip(94), width = matchParent)
            }
        }
    }
}
