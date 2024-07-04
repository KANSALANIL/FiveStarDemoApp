package com.fivestarmind.android.constant


/**
 * The constants which are used in App are listed here
 */
interface AppConst {

    object REQUEST_CODE {
        const val APP_UPDATE_IMMEDIATE = 17362

        const val GOOGLE_PLACE_LOCATION = 12221
        const val GOOGLE_PLACE_COLLEGE_NAME = 12222

        // Defines the id of the loader for later reference
        const val CONTACT_LOADER_ID = 78

        const val EDIT_PROFILE_ACTIVITY = 1111

        const val CREATE_EVENT_ACTIVITY = 1112
        const val UPDATE_EVENT_ACTIVITY = 1113
        const val CREATE_OR_UPDATE_VIP_ROOM = 1114

        const val SELECT_USER_ACTIVITY = 1115

        const val BOOK_EVENT_ACTIVITY = 1116

        const val PAYMENT_ACTIVITY = 1117
        const val BUY_TOKEN_ACTIVITY = 1118
        const val CASH_OUT_TOKEN_ACTIVITY = 1119

        const val STRIPE_CONNECT_ACTIVITY = 1120
        const val PAYPAL_CONNECT_ACTIVITY = 1121

        const val MESSAGE_ACTIVITY = 1122
        const val RC_APP_UPDATE = 105

    }

    object KEY {
        const val TAG_FROM = "tagFrom"
        const val FRAGMENT_NAME = "fragmentName"

        const val WEBVIEW_TYPE = "webviewType"
        const val TOKEN = "token"
        const val EMAIL_ID = "emailId"
        const val OTP = "otp"

        const val VIDEO_FILE = "videoFile"

        const val COVER_IMAGE_FILE = "coverImageFile"

        const val USER_INFO_MODEL = "userInfoModel"

        const val FILTER = "filter"
        const val USER_ID = "userId"
        const val POSITION = "position"
        const val TITLE = "title"
        const val PAGINATED_INFO_MODEL = "paginatedInfoModel"

        const val LOAD_FRAGMENT_MODEL = "loadFragmentModel"

        const val NOTIFICATION_MODEL = "notificationModel"

        const val ID: String = "_id"
        const val TYPE: String = "type"
        const val MODULE_ID: String = "moduleId"
        const val MAIN_MODULE_ID: String = "mainModuleId"
        const val MESSAGE: String = "message"

        const val ADDRESS: String = "address"

        const val EVENT_MODEL = "eventModel"
        const val VIP_ROOM_MODEL = "vipRoomModel"
        const val TOKEN_PACKAGE_MODEL = "tokenPackageModel"
        const val SHOOT_YOUR_SHOT_MODEL = "shootYourShotModel"

        const val SELECT_USER_TYPE: String = "selectUserType"
        const val SELECTED_USERS: String = "selectedUsers"
        const val SELECTED_USER_IDS: String = "selectedUserIds"
        const val MAX_SELECTION: String = "maxSelection"
        const val EXCLUDE_USER_IDS: String = "excludeUserIds"

        const val JOINED: String = "joined"
        const val HOME: String = "home"
        const val IS_FOLLOWERS: String = "isFollowers"

        const val STRIPE_TOKEN = "stripeToken"
        const val USER_FOLLOW_TYPE = "userFollowType"

        const val STRIPE = "stripe"
        const val PAYPAL = "paypal"

        const val CONVERSATION_ID = "conversationId"
        const val USER_PROFILE = "userProfile"

        const val SHOW_TOOLBAR = "SHOW_TOOLBAR"

        const val INVITATION_STATUS_TYPE = "InvitationStatusType"

        const val SUMMER_PROGRAM_ID = "SUMMER_PROGRAM_ID"
    }

    object NUMBER {
        const val MINUS_ONE = -1
        const val ZERO = 0
        const val ONE = 1
        const val TWO = 2
        const val THREE = 3
        const val FOUR = 4
        const val FIVE = 5
        const val SIX = 6
        const val SEVEN = 7
        const val EIGHT = 8
        const val NINE = 9
        const val TEN = 10
        const val ELEVEN = 11
        const val TWELVE = 12
        const val THIRTEEN = 13
        const val FOURTEEN = 14
        const val FIFTEEN = 15
        const val SIXTEEN = 16
        const val SEVENTEEN = 17
        const val EIGHTEEN = 18
        const val NINETEEN = 19
        const val TWENTY = 20
        const val TWENTY_ONE = 21
        const val TWENTY_TWO = 22
        const val TWENTY_THREE = 23
        const val TWENTY_FOUR = 24
        const val TWENTY_FIVE = 25
        const val TWENTY_SIX = 26
        const val TWENTY_SEVEN = 26
        const val TWENTY_EIGHT = 26
        const val TWENTY_NINE = 26
        const val THIRTY = 30
        const val THIRTY_ONE = 31
        const val THIRTY_TWO = 32
        const val THIRTY_THREE = 33
        const val THIRTY_FOUR = 34
        const val THIRTY_FIVE = 35
        const val THIRTY_SIX = 36
        const val THIRTY_SEVEN = 37
        const val THIRTY_EIGHT = 38
        const val THIRTY_NINE = 39
        const val FORTY = 40
        const val FORTY_ONE = 41
        const val FIFTY_NINE = 59

        const val HUNDRED = 100
        const val TWO_HUNDRED = 2 * HUNDRED
        const val TWO_HUNDRED_FIFTY_FIVE = 255
        const val FIVE_HUNDRED = 5 * HUNDRED

        const val THOUSAND = 1000
    }

    object DEFAULT_VALUE {

    }

    object CHAR {
        const val SPACE = ' '
        const val FORWARD_SLASH = '/'
        const val COMMA = ','
        const val COLON = ':'
        const val UNDER_SCORE = '_'
    }

    object VALUE {
        const val TRUE = NUMBER.ONE
        const val FALSE = NUMBER.ZERO

        const val AM = "AM"
        const val PM = "PM"
    }

    object REGEX {
        const val EMAIL = "^\\w+@\\w+\\..{2,3}(.{2,3})?\$"
        const val PASSWORD =
            "^(?=.*?\\p{Lu})(?=.*?\\p{Ll})(?=.*?\\d)" + "(?=.*?[`~!@#$%^&*()\\-_=+\\\\|\\[{\\]};:'\",<.>/?]).*$"
    }

}
