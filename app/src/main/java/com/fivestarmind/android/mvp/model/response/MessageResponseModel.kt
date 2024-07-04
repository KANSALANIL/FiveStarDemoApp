package com.fivestarmind.android.mvp.model.response

import com.google.gson.annotations.SerializedName

data class MessageResponseModel(

	@field:SerializedName("sender")
	val sender: Sender? = null,

	@field:SerializedName("message")
	val message: Message? = null,

	@field:SerializedName("ts")
	val ts: Long? = null
)

data class Message(

	@field:SerializedName("threadId")
	val threadId: String? = null,

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("senderUserId")
	val senderUserId: String? = null,

	@field:SerializedName("meta")
	val meta: Meta? = null,

	@field:SerializedName("_id")
	val id: String? = null,

	@field:SerializedName("type")
	val type: String? = null,

	@field:SerializedName("content")
	val content: String? = null,

	@field:SerializedName("receiverUserId")
	val receiverUserId: String? = null
)
data class Sender(

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
