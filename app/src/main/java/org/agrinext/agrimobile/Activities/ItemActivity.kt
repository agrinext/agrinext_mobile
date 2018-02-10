package org.agrinext.agrimobile.Activities

import io.frappe.android.Controllers.ListingFragment

class ItemActivity : ListingFragment() {

    override fun setupDocType() {
        this.doctype = "Item"
        super.setupDocType()
    }
}
