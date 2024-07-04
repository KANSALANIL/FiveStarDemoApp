package com.fivestarmind.android.mvp.model.response

import com.google.gson.annotations.SerializedName

data class UserLoginResponseModel(
	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("user")
	val user: User? = null,

	@field:SerializedName("token")
	val token: String? = null
)

data class User(

	@field:SerializedName("role_id")
	val roleId: Int? = null,

	@field:SerializedName("name")
	var name: String? = null,

	@field:SerializedName("background_img")
	val backgroundImg: Any? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("logo_image")
	val logoImage: Any? = null,

	@field:SerializedName("profile_img")
	val profileImage: String? = null,

	@field:SerializedName("status")
	val status: Int? = null,

	/*@field:SerializedName("org_user")
	val orgUser: ArrayList<OrgUser>? = null*/

	@field:SerializedName("org_user_data")
	val orgUser:OrgUserData? = null


)
/*data class OrgUser(
	@field:SerializedName("organization_id")
	val organizationId: Int,
)*/

data class OrgUserData(
	@field:SerializedName("organization_id")
	val organizationId: Int,

	@field:SerializedName("user_subrole")
	val user_subrole: ArrayList<UserSubRole_>? = null
)

data class UserSubRole_(
	@field:SerializedName("name")
	val name: String = ""

)