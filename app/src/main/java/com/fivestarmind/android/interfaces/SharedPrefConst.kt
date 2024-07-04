package com.fivestarmind.android.interfaces

/**
 * The constants which are used regarding SharedPreferences are listed here
 */
interface SharedPrefConst {

    object PREF {
        const val LOGIN_CREDENTIAL = "prefLoginCredential"
    }

    object KEY {
        const val START_TIME = "startTime"
        const val END_TIME = "endTime"
        const val APP_ID = "appID"
        const val NAME = "name"
        const val SUB_ROLE_NAME = "subRoleName"
        const val IMAGE = "image"
        const val AUTH_TOKEN = "token"
        const val LOGO_IMAGE = "logoImage"
        const val USER_INFO_MODEL = "userInfoModel"
        const val VIDEO_MUTE_STATUS = "videoMuteStatus"
        const val REMEMBER_ME_MODEL = "rememberMeModel"
        const val AUDIO_ID = "audioID"
        const val NOTIFICATION_ID = "notificationId"
        const val BADGE_COUNT = "badgeCount"
        const val SENDER_ID = "messageSenderId"
    }

}