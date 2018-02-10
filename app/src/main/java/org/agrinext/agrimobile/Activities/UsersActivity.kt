package org.agrinext.agrimobile.Activities

import io.frappe.android.Controllers.ListingFragment

class UsersActivity : ListingFragment() {

    override fun setupDocType() {
        this.doctype = "User Profile"
        super.setupDocType()
    }
}
