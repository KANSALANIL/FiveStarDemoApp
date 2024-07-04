package com.fivestarmind.android.mvp.model.response

import com.google.gson.annotations.SerializedName

data class UsersListingResponseModel(

	@field:SerializedName("data")
	val data: ArrayList<UsersDataItemListing>? = null
)

data class UserSubrole(

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("subrole_id")
	val subroleId: Int? = null,

	@field:SerializedName("created_by")
	val createdBy: Int? = null,

	@field:SerializedName("status")
	val status: Int? = null
)

data class OrgUserItem(

	@field:SerializedName("user_id")
	val userId: Int? = null,

	@field:SerializedName("user_subrole_id")
	val userSubroleId: Int? = null,

	@field:SerializedName("organization_id")
	val organizationId: Int? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("status")
	val status: Int? = null,

	@field:SerializedName("user_subrole")
	val userSubrole: UserSubrole? = null
)

data class UsersDataItemListing(

	@field:SerializedName("profile_img")
	val profileImg: String? = null,

	@field:SerializedName("role_id")
	val roleId: Int? = null,

	@field:SerializedName("user_subrole_id")
	val userSubroleId: Int? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("org_user")
	val orgUser: List<OrgUserItem?>? = null,

	@field:SerializedName("created_by")
	val createdBy: Int? = null,

	@field:SerializedName("email")
	val email: String? = null,

	@field:SerializedName("status")
	val status: Int? = null
)
