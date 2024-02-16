package dev.rhy.mobile.utils

import android.content.Context
import android.content.SharedPreferences

class Cache {
    private var context: Context? = null

    companion object {
        private var self: Cache? = null
        fun instance() : Cache {
            if (self == null) {
                self = Cache()
            }
            return self!!
        }
    }

    private fun getPref(): SharedPreferences {
        return context!!.getSharedPreferences("test", Context.MODE_PRIVATE)
    }

    fun setContext(context: Context) {
        this.context = context
    }

    fun saveToken(token: String) {
        val pref = getPref().edit()
        pref.putString("token", token)
        pref.apply()
    }

    fun getToken(): String {
        return getPref().getString("token", "")!!
    }

    fun saveKey(key: String) {
        val pref = getPref().edit()
        pref.putString("sharedkey", key)
        pref.apply()
    }

    fun getKey() : String {
        return getPref().getString("sharedkey", "")!!
    }
}