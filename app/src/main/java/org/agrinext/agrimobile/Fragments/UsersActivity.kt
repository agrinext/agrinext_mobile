package org.agrinext.agrimobile.Fragments

import io.frappe.android.Controllers.ListingFragment
import org.agrinext.agrimobile.Activities.UsersForm

class UsersActivity : ListingFragment() {

    override fun setupDocType() {
        this.doctype = "User Profile"
        this.form = UsersForm::class.java as Class<Any>
        super.setupDocType()
    }
}
