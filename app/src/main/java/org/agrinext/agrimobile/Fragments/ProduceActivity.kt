package org.agrinext.agrimobile.Fragments
import android.accounts.AccountManager
import android.util.Log
import com.raizlabs.android.dbflow.config.FlowManager
import io.frappe.android.Controllers.ListingFragment
import org.agrinext.agrimobile.Activities.ProduceForm
import org.agrinext.agrimobile.BuildConfig
import org.json.JSONArray
import com.raizlabs.android.dbflow.sql.language.SQLite
import org.agrinext.agrimobile.Models.Produce



class ProduceActivity : ListingFragment() {

    override fun setupFilters() {
        val mAccountManager = AccountManager.get(activity.applicationContext)
        val accounts = mAccountManager?.getAccountsByType(BuildConfig.APPLICATION_ID)
        val user = accounts?.get(0)?.name

        val filtersArray = JSONArray()
        // owner filter
        var filterSet = JSONArray().put("owner").put("=").put(user)
        filtersArray.put(filterSet)

        filters = filtersArray
    }

    override fun setupDocType() {
        FlowManager.init(context)

        val produceList = SQLite.select()
                .from(Produce::class.java)
                .queryList()

        Log.d("Produce(s) >>", produceList.toString())
        this.doctype = "Produce"
        this.form = ProduceForm::class.java as Class<Any>
        super.setupDocType()
    }
}
