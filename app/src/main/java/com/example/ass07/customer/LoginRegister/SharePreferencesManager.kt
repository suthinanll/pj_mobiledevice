package com.example.ass07.customer.LoginRegister

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class SharePreferencesManager(context: Context) {
    private val preferences: SharedPreferences =
        context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)

    var isLoggedIn: Boolean
        get() = preferences.getBoolean(KEY_IS_LOGGED_IN, false)
        set(value) = preferences.edit().putBoolean(KEY_IS_LOGGED_IN, value).apply()

    var userId: Int?
        get() = preferences.getInt(KEY_USER_ID, -1)
        set(value) = preferences.edit().putInt(KEY_USER_ID, value!!).apply()

    var userRole: String?
        get() = preferences.getString(KEY_USER_ROLE, null)
        set(value) = preferences.edit().putString(KEY_USER_ROLE, value).apply()

    var userName: String?
        get() = preferences.getString(KEY_USER_NAME, null)
        set(value) = preferences.edit().putString(KEY_USER_NAME, value).apply()

    var email: String?
        get() = preferences.getString(KEY_USER_EMAIL, null)
        set(value) = preferences.edit().putString(KEY_USER_EMAIL, value).apply()

    var tell_number: String?
        get() = preferences.getString(KEY_USER_TELL, null)
        set(value) = preferences.edit().putString(KEY_USER_TELL, value).apply()

    var petTypeName: String?
        get() = preferences.getString(KEY_PET_TYPE_NAME, null)
        set(value) = preferences.edit().putString(KEY_PET_TYPE_NAME, value).apply()

    fun clearUserAll() {
        preferences.edit().clear().apply()
    }

    fun clearUserLogin() {
        preferences.edit().remove(KEY_IS_LOGGED_IN).apply()
    }

    companion object {
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_ROLE = "user_role"  // ✅ เพิ่ม Key สำหรับ Role
        private const val KEY_USER_NAME = "name"
        private const val KEY_USER_EMAIL = "email"
        private const val KEY_USER_TELL = "tell"
        private const val KEY_PET_TYPE_NAME = "pet_type_name"
    }
}
