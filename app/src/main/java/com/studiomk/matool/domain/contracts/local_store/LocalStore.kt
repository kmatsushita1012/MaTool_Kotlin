package com.studiomk.matool.domain.contracts.local_store

interface LocalStore {
    fun getString(key: String): String?
    fun getBoolean(key: String): Boolean
    fun getData(key: String): ByteArray?
    fun getDouble(key: String): Double
    fun getInt(key: String): Int
    fun remove(key: String)
    fun setString(value: String?, key: String)
    fun setBoolean(value: Boolean?, key: String)
    fun setData(value: ByteArray?, key: String)
    fun setDouble(value: Double?, key: String)
    fun setInt(value: Int?, key: String)
}

