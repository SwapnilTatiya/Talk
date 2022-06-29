package com.example.otpapplication

import android.content.Context
import android.content.SharedPreferences

public class PreferenceManager {
    private lateinit var sharedPreferences : SharedPreferences
    constructor(context: Context){
        var constants=Constants()
        sharedPreferences=context.getSharedPreferences(constants.KEY_PREFERENCE_NAME,Context.MODE_PRIVATE)
    }

    public fun putBoolean(key:String,value:Boolean){
        var editor :SharedPreferences.Editor=sharedPreferences.edit()
        editor.putBoolean(key,value)
        editor.apply()
    }
    public fun getBoolean(key:String):Boolean{
        return sharedPreferences.getBoolean(key,false)
    }

    public fun putString(key:String,value:String){
        var editor :SharedPreferences.Editor=sharedPreferences.edit()
        editor.putString(key,value)
        editor.apply()
    }
    public fun getString(key:String): String? {
        return sharedPreferences.getString(key,null)
    }

    public fun clear(){
        var editor :SharedPreferences.Editor=sharedPreferences.edit()
        editor.clear()
        editor.apply()
        return
    }
}