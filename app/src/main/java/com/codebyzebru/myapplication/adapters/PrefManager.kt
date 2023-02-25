package com.codebyzebru.myapplication.adapters

import android.content.Context

class PrefManager(context: Context) {

    private val privateMode = 0        //SHARED PREFERENCE MODE
    private val prefName = "SharedPreference"
    private val pref = context. getSharedPreferences(prefName, privateMode)
    private val editor = pref?.edit()

    fun setLogin(isLogin: Boolean) {
        editor?.putBoolean("IS_LOGIN", true)
        editor?.commit()
    }

    fun isLogin(): Boolean {
        return  pref.getBoolean("IS_LOGIN", false)
    }

    //when user logged out, clears the editor
    fun clearData() {
        editor?.clear()
        editor?.commit()
    }

}