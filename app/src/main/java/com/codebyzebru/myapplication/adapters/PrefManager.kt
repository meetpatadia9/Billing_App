package com.codebyzebru.myapplication.adapters

import android.content.Context
import android.content.SharedPreferences

class PrefManager(context: Context) {

    //SHARED PREFERENCE MODE
    val private_mode = 0


    private val pref_name = "SharedPreference"
    private val is_login = "is_login"

    val pref = context. getSharedPreferences(pref_name, private_mode)
    val editor = pref?.edit()

    fun setLogin(isLogin: Boolean) {
        editor?.putBoolean("IS_LOGIN", true)
        editor?.commit()
    }

    fun isLogin(): Boolean {
        return  pref.getBoolean("IS_LOGIN", false)
    }

    fun clearData() {
        editor?.clear()
        editor?.commit()
    }

}