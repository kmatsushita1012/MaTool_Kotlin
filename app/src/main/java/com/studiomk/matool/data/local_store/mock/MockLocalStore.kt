package com.studiomk.matool.data.local_store.mock

import com.studiomk.matool.domain.contracts.local_store.LocalStore

class MockLocalStore: LocalStore {
    override fun getString(key: String): String? = ""
    override fun getBoolean(key: String): Boolean = true
    override fun getData(key: String): ByteArray? = null
    override fun getDouble(key: String): Double = 0.0
    override fun getInt(key: String): Int = 0
    override fun remove(key: String) {}
    override fun setString(value: String?, key: String) {}
    override fun setBoolean(value: Boolean?, key: String) {}
    override fun setData(value: ByteArray?, key: String) {}
    override fun setDouble(value: Double?, key: String) {}
    override fun setInt(value: Int?, key: String) {}
}