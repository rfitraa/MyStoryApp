package com.dicoding.mystoryapp.data

import android.content.Context
import com.dicoding.mystoryapp.response.LoginResult

class Preference(context: Context) {
    private val preference = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)

    companion object{
        private const val PREFERENCE_NAME = "pref_name"
        private const val USER_ID = "user_id"
        private const val NAME = "name"
        private const val TOKEN = "token"
    }

    fun setData(value: LoginResult) {
        val edit = preference.edit()
        edit.putString(USER_ID, value.userId)
        edit.putString(NAME, value.name)
        edit.putString(TOKEN, value.token)
        edit.apply()
    }

    fun getData(): LoginResult {
        val userid = preference.getString(USER_ID, null)
        val name = preference.getString(NAME, null)
        val token = preference.getString(TOKEN, null)
        return LoginResult(userid, name, token)
    }

    fun clearData(){
        val edit = preference.edit().clear()
        edit.apply()
    }
}