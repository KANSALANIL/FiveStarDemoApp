package com.fivestarmind.android.mvp.model.response

import com.google.gson.annotations.SerializedName

data class ThreadIdResponse(

	@field:SerializedName("data")
	val data: ThreadIdDataResponse? = null
)

data class ThreadIdDataResponse(

	@field:SerializedName("thread")
	val thread: ChatThread? = null,

	@field:SerializedName("users")
	val users: ArrayList<UsersItem>? = null,

	@field:SerializedName("messages")
	val messages: ArrayList<MessagesItem>? = null,
)

data class ChatThread(

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("recipientOnline")
	val recipientOnline: Boolean? = null,

	@field:SerializedName("ownerName")
	val ownerName: String? = null,

	@field:SerializedName("name")
	val name: Any? = null,

	@field:SerializedName("messages")
	val messages: ArrayList<MessagesItem>? = null,

	@field:SerializedName("_id")
	val id: String? = null,

	@field:SerializedName("blockedUsers")
	val blockedUsers: List<Any?>? = null,

	@field:SerializedName("type")
	val type: String? = null,

	@field:SerializedName("ownerId")
	val ownerId: String? = null,

	@field:SerializedName("users")
	val users: List<String?>? = null,

	@field:SerializedName("receiverUserId")
	val receiverUserId: String? = null,

	@field:SerializedName("updatedAt")
	val updatedAt: String? = null
)

data class UsersItem(

	@field:SerializedName("profile_img")
	val profileImg: String? = null,

	@field:SerializedName("role")
	val role: Role? = null,

	@field:SerializedName("role_id")
	val roleId: Int? = null,

	@field:SerializedName("user_subrole_id")
	val userSubroleId: Int? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("created_by")
	val createdBy: Int? = null,

	@field:SerializedName("email")
	val email: String? = null,

	@field:SerializedName("status")
	val status: Int? = null,

	@field:SerializedName("user_subrole")
	val userSubrole: UserSubrole? = null
)

