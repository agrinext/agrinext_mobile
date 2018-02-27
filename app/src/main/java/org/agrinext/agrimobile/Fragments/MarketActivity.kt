package org.agrinext.agrimobile.Fragments

import io.frappe.android.Controllers.ListingFragment

class MarketActivity : ListingFragment() {

    override fun setupDocType() {
        this.doctype = "Produce"
        super.setupDocType()
    }
}
