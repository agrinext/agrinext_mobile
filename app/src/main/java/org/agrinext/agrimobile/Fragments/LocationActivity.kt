package org.agrinext.agrimobile.Fragments

import io.frappe.android.Controllers.ListingFragment

class LocationActivity : ListingFragment() {

    override fun setupDocType() {
        this.doctype = "Location"
        super.setupDocType()
    }
}
