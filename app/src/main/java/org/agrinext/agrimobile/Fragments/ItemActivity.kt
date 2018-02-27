package org.agrinext.agrimobile.Fragments

import io.frappe.android.Controllers.ListingFragment

class ItemActivity : ListingFragment() {

    override fun setupDocType() {
        this.doctype = "Item"
        super.setupDocType()
    }
}
