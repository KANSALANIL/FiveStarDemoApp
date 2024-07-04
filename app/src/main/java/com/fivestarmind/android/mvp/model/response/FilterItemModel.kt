package com.fivestarmind.android.mvp.model.response

class FilterItemModel(var name: String) {
    private var filterName = name

    fun getFilterName(): String {
        return filterName
    }
}