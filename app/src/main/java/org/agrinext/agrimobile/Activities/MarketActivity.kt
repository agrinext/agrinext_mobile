package org.agrinext.agrimobile.Activities

class MarketActivity : ListingActivity() {

    override fun setupDocType() {
        this.doctype = "Produce"
        super.setupDocType()
    }
}
