package com.fivestarmind.android.helper

import com.google.gson.Gson
import java.lang.reflect.Type

/**
 * This is the helper class which contains all the methods or functions regarding Gson serialization/deserialization library.
 */
object GsonHelper {

    /**
     * @param from
     * @param to ("ClassName"::class.java) or (object : TypeToken<"ModelClassName"<"ObjectClassName">>() {}.type e.g., object : TypeToken<GenericModel<Int>>() {}.type)
     */
    internal fun convertJsonStringToJavaObject(from: Any?, to: Type): Any? {
        val jsonString: String = when (from) {
            is String -> from
            else -> convertJavaObjectToJsonString(from)
        }

        var model: Any? = null

        try {
            model = Gson().fromJson(jsonString, to)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return model
    }

    internal fun convertJavaObjectToJsonString(model: Any?): String {
        return Gson().toJson(model)
    }

}