package org.agrinext.agrimobile.Activities
import android.accounts.AccountManager
import org.agrinext.agrimobile.BuildConfig
import org.json.JSONArray

class ProduceActivity : ListingActivity() {

    override fun setupFilters() {
        val mAccountManager = AccountManager.get(activity)
        val accounts = mAccountManager?.getAccountsByType(BuildConfig.APPLICATION_ID)
        val user = accounts?.get(0)?.name

        val filtersArray = JSONArray()
        // owner filter
        var filterSet = JSONArray().put("owner").put("=").put(user)
        filtersArray.put(filterSet)

        filters = filtersArray
    }

    override fun setupDocType() {
        this.doctype = "Produce"
        super.setupDocType()
    }
}