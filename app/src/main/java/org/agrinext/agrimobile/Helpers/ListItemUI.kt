package org.agrinext.agrimobile.Helpers

import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import org.agrinext.agrimobile.R
import org.jetbrains.anko.*

/**
 * Created by revant on 28/12/17.
 */
class ListItemUI : AnkoComponent<ViewGroup> {
    override fun createView(ui: AnkoContext<ViewGroup>): View {
        // UI list item
        return with(ui) {
            linearLayout {
                lparams(width = matchParent, height = dip(48))
                orientation = LinearLayout.HORIZONTAL
                textView {
                    id = R.id.list_item
                    textSize = 16f
                }
            }
        }
    }
}