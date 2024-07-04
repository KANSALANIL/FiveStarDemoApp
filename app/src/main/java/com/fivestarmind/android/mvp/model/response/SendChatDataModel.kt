package com.fivestarmind.android.mvp.model.response

import com.google.gson.annotations.SerializedName

class SendChatDataModel {
/*{
    "receiverUserId": "66",
    "content": "My last message",
    "type": "TEXT"
}*/
	@SerializedName("receiverUserId")
	var receiverUserId: Int = 0

	@SerializedName("content")
	var content: String = ""

	@SerializedName("type")
	var type: String = ""

}