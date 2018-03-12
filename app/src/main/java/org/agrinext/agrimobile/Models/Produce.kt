package org.agrinext.agrimobile.Models

import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import com.raizlabs.android.dbflow.structure.BaseModel
import org.agrinext.agrimobile.Databases.ProduceDatabase
import org.json.JSONObject

@Table(database = ProduceDatabase::class)
class Produce : BaseModel() {
    @Column
    @PrimaryKey
    var name: String? = null

    @Column
    var produce_name: String = String()

    @Column
    var item_link: String = String()

    @Column
    var disable:Int = 0

    @Column
    var produce_date_string: String = String()

    @Column
    var expire_on_string: String = String()

    @Column
    var qty:Double = 0.0

    @Column
    var expected_price:Double = 0.0

    @Column
    var last_sale_price:Double = 0.0

    fun parse(produceJson:JSONObject): Produce {
        var produce = Produce()
        produce.name = produceJson.getString("name")
        produce.produce_name = produceJson.getString("produce_name")
        produce.item_link = produceJson.getString("item")
        produce.disable = produceJson.getInt("disable")
        produce.produce_date_string = produceJson.getString("produce_date")
        produce.expire_on_string = produceJson.getString("expire_on")
        produce.qty = produceJson.getDouble("qty")
        produce.expected_price = produceJson.getDouble("expected_price")
        produce.last_sale_price = produceJson.getDouble("last_sale_price")
        return produce
    }

    override fun toString(): String {
        val out = "name: ${name}\n" +
                "produce_name: ${produce_name}\n" +
                "item_link: ${item_link}\n" +
                "disable: ${disable.toString()}\n" +
                "produce_date_string: ${produce_date_string}\n" +
                "expire_on_string: ${expire_on_string}\n" +
                "qty: ${qty}\n" +
                "expected_price: ${expected_price}\n" +
                "last_sale_price: ${last_sale_price}\n"
        return out
    }
}
