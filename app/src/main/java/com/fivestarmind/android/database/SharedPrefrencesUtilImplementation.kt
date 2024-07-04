package com.fivestarmind.android.database

import android.content.Context
import android.content.SharedPreferences
import com.fivestarmind.android.interfaces.Constants
import com.fivestarmind.android.interfaces.SharedPrefrencesUtil

class SharedPrefrencesUtilImplementation(private var context: Context) : SharedPrefrencesUtil {

    private var keyPreference: String? = null
    private
    val sharedPreferences: SharedPreferences
        get() = context.getSharedPreferences(keyPreference, Context.MODE_PRIVATE)

    override fun saveToken(token: String): Boolean {
        val objUser = sharedPreferences.edit().putString(Constants.SharedPref.PREF_AUTH_TOKEN, token).apply()
        return true
    }

    override fun fetchAuthToken(): String {
        return sharedPreferences.getString(Constants.SharedPref.PREF_AUTH_TOKEN, "")!!
    }


}