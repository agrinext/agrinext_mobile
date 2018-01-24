package org.agrinext.agrimobile.Frappe

import android.content.Context
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

/**
 * Created by revant on 24/1/18.
 */
class FieldUtils {
    fun getFieldnameFromLabel(context:Context, doctypeMetaJson:JSONObject, label: String) : String {

        var fields = JSONArray()
        try {
            fields = doctypeMetaJson.getJSONArray("fields")
        } catch (e: JSONException) {
            fields = JSONArray()
        }

        var spinnerField = ""

        for (i in 0 until fields.length() - 1){
            if(fields.getJSONObject(i).has("label") && fields.getJSONObject(i).getString("label") == label){
                spinnerField = fields.getJSONObject(i).getString("fieldname")
                break
            }
        }

        if (spinnerField.isNullOrEmpty()){
            when(label){
                context.resources.getString(org.agrinext.agrimobile.R.string.sort_name) -> spinnerField = "name"
                context.resources.getString(org.agrinext.agrimobile.R.string.last_modified_on) -> spinnerField = "modified"
                context.resources.getString(org.agrinext.agrimobile.R.string.created_on) -> spinnerField = "creation"
                context.resources.getString(org.agrinext.agrimobile.R.string.most_used) -> spinnerField = "idx"
                context.resources.getString(org.agrinext.agrimobile.R.string.created_by) -> spinnerField = "owner"
                context.resources.getString(org.agrinext.agrimobile.R.string.modified_by) -> spinnerField = "modified_by"
            }
        }

        return spinnerField
    }
    fun getExpressionFromLabel(label: String) : String {
        var expressionValue = ""
        when(label){
            "Equal" -> expressionValue = "="
            "Like" -> expressionValue = "like"
            "In" -> expressionValue = "in"
            "Not In" -> expressionValue = "not in"
            "Not Equal" -> expressionValue = "!="
            "Not Like" -> expressionValue = "not like"
            "Between" -> expressionValue = "between"
            ">" -> expressionValue = ">"
            "<" -> expressionValue = "<"
            ">=" -> expressionValue = ">="
            "<=" -> expressionValue = "<="
        }

        return expressionValue
    }
}