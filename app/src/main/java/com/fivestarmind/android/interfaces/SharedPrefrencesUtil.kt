package com.fivestarmind.android.interfaces

interface SharedPrefrencesUtil {
    fun saveToken(token: String): Boolean
    fun fetchAuthToken(): String
}