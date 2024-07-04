package com.fivestarmind.android.mvp.model.response

import com.google.gson.annotations.SerializedName

class ProductCategoryResponseModel {
   @SerializedName("data")
    val data: CategoryData? = null

    /* @@SerializedName("data")
    val data = ArrayList<ProductCategoryDataModel>() */


    data class LinksItem(

       @SerializedName("active")
        val active: Boolean? = null,

       @SerializedName("label")
        val label: String? = null,

       @SerializedName("url")
        val url: Any? = null
    )

   

    /*data class DataItem(

       @SerializedName("name")
        val name: String? = null,

       @SerializedName("id")
        val id: Int? = null
    )*/

}
class CategoryData{

    @SerializedName("per_page")
    val perPage: Int? = null

    @SerializedName("data")
    val data: ArrayList<ProductCategoryDataModel>? = null

   @SerializedName("last_page")
    val lastPage: Int? = null

   @SerializedName("next_page_url")
    val nextPageUrl: Any? = null

   @SerializedName("prev_page_url")
    val prevPageUrl: Any? = null

   @SerializedName("first_page_url")
    val firstPageUrl: String? = null

   @SerializedName("path")
    val path: String? = null

   @SerializedName("total")
    val total: Int? = null

   @SerializedName("last_page_url")
    val lastPageUrl: String? = null

   @SerializedName("from")
    val from: Int? = null

   @SerializedName("links")
    val links: List<ProductCategoryResponseModel.LinksItem?>? = null

   @SerializedName("to")
    val to: Int? = null

   @SerializedName("current_page")
    val currentPage: Int? = null
}








