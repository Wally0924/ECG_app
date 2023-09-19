package com.example.navigation_test

class MemberViewModel {
    private var _mbName = mutableMapOf<String, String>()

    init {
        _mbName = mutableMapOf()
    }


    fun updateData(key: String, value: String) {
        _mbName[key] = value
    }

    fun getListData(key: String): String {
        return _mbName[key] ?: ""
    }
}