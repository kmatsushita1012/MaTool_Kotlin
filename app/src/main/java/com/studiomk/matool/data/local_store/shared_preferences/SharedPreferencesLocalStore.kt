package com.studiomk.matool.data.local_store.shared_preferences

import android.content.Context
import android.content.SharedPreferences
import com.studiomk.matool.domain.contracts.local_store.LocalStore
import androidx.core.content.edit

class SharedPreferencesLocalStore(
    context: Context
) : LocalStore {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("matool_prefs", Context.MODE_PRIVATE)

    override fun getString(key: String): String? = prefs.getString(key, null)

    override fun getBoolean(key: String): Boolean = prefs.getBoolean(key, false)

    override fun getData(key: String): ByteArray? =
        prefs.getString(key, null)?.let { android.util.Base64.decode(it, android.util.Base64.DEFAULT) }

    override fun getDouble(key: String): Double =
        Double.fromBits(prefs.getLong(key, 0L))

    override fun getInt(key: String): Int = prefs.getInt(key, 0)

    override fun remove(key: String) {
        prefs.edit { remove(key) }
    }

    override fun setString(value: String?, key: String) {
        prefs.edit { putString(key, value) }
    }

    override fun setBoolean(value: Boolean?, key: String) {
        prefs.edit { putBoolean(key, value ?: false) }
    }

    override fun setData(value: ByteArray?, key: String) {
        val encoded = value?.let { android.util.Base64.encodeToString(it, android.util.Base64.DEFAULT) }
        prefs.edit { putString(key, encoded) }
    }

    override fun setDouble(value: Double?, key: String) {
        prefs.edit { putLong(key, value?.toRawBits() ?: 0L) }
    }

    override fun setInt(value: Int?, key: String) {
        prefs.edit { putInt(key, value ?: 0) }
    }
}