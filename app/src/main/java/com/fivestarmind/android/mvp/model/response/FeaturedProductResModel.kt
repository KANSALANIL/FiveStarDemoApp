package com.fivestarmind.android.mvp.model.response

import com.google.gson.annotations.SerializedName

data class FeaturedProductResModel(

	@field:SerializedName("data")
	val data: FeaturedProductData? = null

	/*@field:SerializedName("data")
	var data: ArrayList<FeaturedProductDetailResModel>? = null*/
)


data class FeaturedProductData(

	@field:SerializedName("per_page")
	val perPage: Int? = null,

	/*@field:SerializedName("data")
	val data: List<DataItem?>? = null,*/

	@field:SerializedName("data")
	var data: ArrayList<FeaturedProductDetailResModel>? = null,

	@field:SerializedName("last_page")
	val lastPage: Int? = null,

	@field:SerializedName("next_page_url")
	val nextPageUrl: Any? = null,

	@field:SerializedName("prev_page_url")
	val prevPageUrl: Any? = null,

	@field:SerializedName("first_page_url")
	val firstPageUrl: String? = null,

	@field:SerializedName("path")
	val path: String? = null,

	@field:SerializedName("total")
	val total: Int? = null,

	@field:SerializedName("last_page_url")
	val lastPageUrl: String? = null,

	@field:SerializedName("from")
	val from: Int? = null,

	@field:SerializedName("links")
	val links: List<ProductCategoryResponseModel.LinksItem?>? = null,

	@field:SerializedName("to")
	val to: Int? = null,

	@field:SerializedName("current_page")
	val currentPage: Int? = null,

	@field:SerializedName("product")
	val product: ProductTitle? = null,
)



class FeaturedProductDetailResModel(

	@field:SerializedName("image")
	val image: String? = null,

	@field:SerializedName("thumbnail")
	val thumbnail: Any? = null,

	@field:SerializedName("featured")
	val featured: Int? = null,

	@field:SerializedName("pdf")
	val pdf: String? = null,

	@field:SerializedName("thumbpath")
	val thumbpath: String? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("id")
	val productId: Int? = null,

	@field:SerializedName("title")
	val productName: String? = null,

	@field:SerializedName("type")
	val type: String? = null,

	@field:SerializedName("temp_path")
	val tempPath: String? = null,

	@field:SerializedName("status")
	val status: String? = null,

	@field:SerializedName("duration")
	val duration: String? = null,

	@SerializedName("product")
	var product: ProductTitle? = null,

	@SerializedName("is_favourite")
	var is_favourite: Boolean? = null
)

data class ProductTitle(
	@field:SerializedName("id")
	var ProductId: String? = null,

	@field:SerializedName("name")
	var name: String? = null,

	)
