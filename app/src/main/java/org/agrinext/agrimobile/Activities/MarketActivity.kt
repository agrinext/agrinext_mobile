package org.agrinext.agrimobile.Activities

import io.frappe.android.Controllers.ListingFragment

class MarketActivity : ListingFragment() {

    override fun setupDocType() {
        this.doctype = "Produce"
        super.setupDocType()
    }
}
