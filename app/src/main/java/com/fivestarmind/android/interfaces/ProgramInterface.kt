package com.fivestarmind.android.interfaces

interface ProgramInterface {
    fun onItemSelected(position: Int, productId: Int)
    fun onListEmpty(isEmpty : Boolean)
    fun onProductCategorySelected(position: Int, productId: Int, categoryName: String)
}