package org.agrinext.agrimobile.Activities

import com.raizlabs.android.dbflow.config.FlowManager
import io.frappe.android.Controllers.FormGeneratorActivity
import org.agrinext.agrimobile.Models.Produce
import java.util.*

/**
 * Created by revant on 27/2/18.
 */
class ProduceForm:FormGeneratorActivity(){
    override fun runOnCreate() {
        super.runOnCreate()
    }

    fun createTestProduct() {
        val adapter = FlowManager.getModelAdapter(Produce::class.java)
        val produce = Produce()
        produce.name = UUID.randomUUID().toString()
        produce.last_sale_price = 0.0
        produce.expected_price = 1.0
        produce.qty = 1.0
        produce.expire_on_string = "2018-04-30"
        produce.produce_date_string = "2018-04-30"
        produce.disable = 0
        produce.item_link = "Wheat"
        produce.produce_name = "Wheat-1"
        adapter.insert(produce)
    }
}