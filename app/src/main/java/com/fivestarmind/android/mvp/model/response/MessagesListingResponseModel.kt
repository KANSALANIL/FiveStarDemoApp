package com.fivestarmind.android.mvp.model.response

import com.google.gson.annotations.SerializedName

data class MessagesListingResponseModel(

	@field:SerializedName("data")
	val data:MessagesListingData? = null
)

data class MessagesItem(

	@field:SerializedName("threadId")
	var threadId: String? = null,

	@field:SerializedName("createdAt")
	var createdAt: String? = null,

	@field:SerializedName("senderUserId")
	var senderUserId: String? = null,

	@field:SerializedName("meta")
	val meta: Meta? = null,

	@field:SerializedName("_id")
	var id: String? = null,

	@field:SerializedName("type")
	val type: String? = null,

	@field:SerializedName("content")
	var content: String? = null,

	@field:SerializedName("receiverUserId")
	var receiverUserId: String? = null
)

data class Role(

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("id")
	val id: Int? = null
)

data class MessagesListingData(

	@field:SerializedName("threads")
	val threads: ArrayList<ThreadsItem>? = null
)

data class ThreadsItem(

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("recipientOnline")
	val recipientOnline: Boolean? = null,

	@field:SerializedName("name")
	val name: String = "",

	@field:SerializedName("recipient")
	val recipient: Recipient? = null,

	@field:SerializedName("messages")
	val messages: List<MessagesItem?>? = null,

	@field:SerializedName("unreadCount")
	val unreadCount: Int = 0,

	@field:SerializedName("_id")
	val id: String? = null,

	@field:SerializedName("type")
	val type: String? = null,

	@field:SerializedName("ownerId")
	val ownerId: String? = null,

	@field:SerializedName("receiverUserId")
	val receiverUserId: String? = null,

	@field:SerializedName("updatedAt")
	val updatedAt: String? = null
)

data class Meta(
	val any: Any? = null
)


data class Recipient(

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
