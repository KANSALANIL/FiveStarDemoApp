package com.fivestarmind.android.interfaces

import android.Manifest

interface Constants {

    interface SharedPref {

        companion object {
            const val PREF_AUTH_TOKEN = "pref_auth_token"
        }
    }

    interface Api {

        companion object {
            //24 final
            const val ID: String = "24"
            const val TERMS = "terms-condition"
            const val   PRIVACY_POLICY = "privacy-policy"
            const val FAQ = "faq"
        }

        object MEDIA_TYPE {
            const val EXTENSION_JPG: String = ".jpg"
        }
    }

    interface App {

        companion object {
            const val PRODUCT_ID = "productId"
            const val PRODUCT_NAME = "productName"
            const val PHASE_MODEL = "model"
            const val COUPON = "coupon"
            const val CURRENCY = "USD"
            const val IMAGE = "image"
            const val WEBVIEW_TYPE = "webviewType"
            const val SPACE = ' '
            const  val VIDEO_LINK = "videoLink"
            const  val PDF_LINK = "pdfLink"
            const  val PDF_NAME = "pdfname"
            const  val AUDIO_LINK = "audioLink"
            const  val AUDIO_TITLE = "audioTitle"
            const  val AUDIO_ACTIVITY_NAME = "audioActivityName"
            const  val VIDEO_ACTIVITY_NAME = "videoActivityName"
            const  val AUDIO_DURATION = "audioDuration"
            const  val IS_FAVROITE = "isFavroite"
            const  val AUDIO_THUMPATH = "thumbpath"
            const  val AUDIO_LIST = "audioList"
            const  val SCREEN_TYPE = "screenType"

            const  val TYPE_PLAY_VIDEO = "playVideo"
            const  val TYPE_PDF = "pdf"
            const  val TYPE_AUDIO = "audio"
            const  val TYPE_IMAGE = "image"
            const  val TYPE_BOOKMARK = "bookMark"
            const  val POSITION = "position"
            const  val FROM_NOTIFICATION = "fromNotification"


            const  val VIDEO_THUMB_PATH_LINK = "videoThumbPathLink"
            const  val VIDEO_ID = "videoId"
            const  val SPECIAL_CONTENT = "specialContent"
            const  val AUDIO_ID = "audioId"
            const val IS_MASTERED = "isMastered"
            const val PAYMENT_TOKEN = "paymentToken"

            const val PURCHASE_TYPE = "purchaseType"
            const val PRODUCT_ALREADY_BUY = "alreadyBuy"
            const val EXIST_IN_CART = "existInCart"
            const val CATEGORY_ID = "catagoryId"
            const val TAG_ID = "tagId"
            const val FROM = "from"
            const val MESSAGE_USER_ID = "messageUserid"
            const val MESSAGE_Data = "messageData"
            const val MESSAGE_USER_NAME = "messageUserName"
            const val MESSAGE_USER_IMAGE = "messageUserImage"
            const val CATEGORY_NAME = "categoryName"
            const val TGA_NAME = "tagName"
            const val CATEGORY_ITEMS_COUNT = "itemCount"

            const val PRODUCT_DETAIL_RESPONSE_MODEL = "productDetailResponseModel"
            const val MEMBERSHIP_CODE = "membership_code"
            const val Title = "title"

        }

        object BundleKey {
            const val COMING_FROM = "coming_from"
        }

        object RequestCode {
            const val JOURNAL = 1001
            const val SUGGESTIONS = 1002
            const val MY_ACCOUNT = 1003
            const val PRODUCT_DETAIL = 1004
            const val CART = 1005
            const val PRODUCT_DETAIL_CART = 1006
            const val JOURNAL_PICS = 1008
            const val CAMERA = 2001
            const val GALLERY = 2002
            const val POSITIVE_ALERT = 2004
            const val POSITIVE_NEGATIVE_ALERT = 2005
            const val FORCE_LOGOUT = 2006
            const val PROFILE = 2007
            const val SELECT_VIDEO = 2008
            const val INVALID_COUPON = 2011
            const val TRANSACTIONS = 2012
            const val HOME = 2013
            const val PHASE_VIDEO_LISTING = 2014
            const val VIDEO_PLAYER = 2015
            const val PROGRAM_SCREEN = 2016
            const val MEMBERSHIP_SCREEN = 2017
            const val MEMBERSHIP = 2018
            const val CART_SCREEN = 2019
            const val SELF_ASSESSMENT_SCREEN = 2020
            const val MY_PROGRAM_SCREEN = 2021
            const val PRODUCT_PHASES_LISTING_SCREEN = 2022
            const val MY_FAVORITE_SCREEN = 2023
            const val PROGRAMS_SCREEN = 2024
            const val CONTACT_US = 2025
            const val DELETE_ACCOUNT = 2026
            const val VIDEO_TRIMMER_REQ_CODE = 324
            const val ADD_BOOK_MARK = 325
            const val REMOVE_BOOK_MARK = 326
            const val categorySelectedRequestCode = 327
            const val notificationRequestCode = 328

        }

        object Duration {
            const val TIME = 300
            const val ZERO: Long = 0
            const val ONE_SECOND: Long = 1000
        }

        object DefaultValue {
            const val MAX_IMAGE_SIZE_VIDEO_ORIGINAL = 1080
            const val IMAGE_COMPRESSION_QUALITY_VIDEO_ORIGINAL = 80 // in percentage
        }

        object CouponType {
            const val ALL = "all"
            const val SINGLE = "single"
            const val MULTIPLE = "multiple"
        }

        object Errors {
            const val ERROR_RESPONSE_MISSING_PARAM = "missing_params"
            const val ERROR_RESPONSE_SESSION_EXPIRE = "sessionExpired"
        }

        object Number {
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
            const val THOUSAND = 1000
        }

        object NotificationType {
            const val EMAIL = "EMAIL"
            const val PUSH_NOTIFICATION = "PUSH"
        }

        object PermissionsCode {
            private const val CAMERA = Manifest.permission.CAMERA
            private const val RECORD_AUDIO = Manifest.permission.RECORD_AUDIO
            private const val WRITE_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE
            private const val READ_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE
            val CAMERA_STORAGE = arrayOf(CAMERA, WRITE_STORAGE, READ_STORAGE)
            internal val CAMERA_VIDEO_ARRAY =
                arrayOf(CAMERA, RECORD_AUDIO, WRITE_STORAGE, READ_STORAGE)
        }

        object PublishKey {
            const val PUBLISH_KEY = "pk_test_48lB9k6l8DEFhoFUpyK8VAn800jEk0kQOn"   // Test
//            const val PUBLISH_KEY = "pk_live_myJtce5Dnc2lm2Uh3gCFjycn002McP1gPv"   // Live
        }

        object ProceedClick {
            private var lastClickedMilliseconds: Long = 0L

            internal fun shouldProceedClick(): Boolean {
                var status = false

                if (System.currentTimeMillis() - lastClickedMilliseconds > Duration.TIME) {
                    lastClickedMilliseconds = System.currentTimeMillis()
                    status = true
                }

                return status
            }
        }
    }

    object REGEX {
        const val EMAIL = "^\\w+@\\w+\\..{2,3}(.{2,3})?\$"
        const val PASSWORD =
            "^(?=.*?\\p{Lu})(?=.*?\\p{Ll})(?=.*?\\d)" + "(?=.*?[`~!@#$%^&*()\\-_=+\\\\|\\[{\\]};:'\",<.>/?]).*$"
    }

    object VALUE {
        const val TRUE = App.Number.ONE
        const val FALSE = App.Number.ZERO
    }
}
