package com.fivestarmind.android.helper

import android.util.Log
import com.fivestarmind.android.R
import com.fivestarmind.android.application.AppController
import com.fivestarmind.android.database.SharedPreferencesManager
import com.fivestarmind.android.interfaces.Constants
import com.fivestarmind.android.interfaces.SharedPrefConst
import com.fivestarmind.android.mvp.model.RememberMeModel
import com.fivestarmind.android.mvp.model.response.UserLoginResponseModel

/**
 * This is the helper class which contains all the methods or functions regarding SharedPreferences.
 */
object SharedPreferencesHelper {
    private var TAG = javaClass.simpleName

    /**
     * Here storing authToken from login response of loggedIn user in SharedPreferences
     *
     * @param authToken authToken which contains authToken of loggedIn user
     */
    internal fun storeAuthToken(authToken: String) {
        Log.d(TAG, "storeAuthToken: authToken- $authToken")

        SharedPreferencesManager.with(context = AppController.mInstance!!)!!.edit()
            .putString(SharedPrefConst.KEY.AUTH_TOKEN, authToken)
            .apply()
    }


    /**
     * It gives authToken of the current logged in user
     *
     * @return authToken
     */
    internal fun getAuthToken(): String {
        var authToken: String? =
            SharedPreferencesManager.with(context = AppController.mInstance!!)!!
                .getString(SharedPrefConst.KEY.AUTH_TOKEN, "")

        if (null == authToken)
            authToken = AppController.mInstance!!.getString(R.string.blank_string)

        return authToken
    }

    /**
     * Here storing userInfoModel from login response of loggedIn user in SharedPreferences
     *
     * @param userInfoModel userInfoModel which contains user data
     */
    internal fun storeUserInfoModel(userInfoModel: UserLoginResponseModel) {
        Log.d(TAG, "storeUserInfoModel")

        val value: String = GsonHelper.convertJavaObjectToJsonString(userInfoModel)
        Log.d(TAG, "storeUserInfoModel: value- $value")

        SharedPreferencesManager.with(context = AppController.mInstance!!)!!.edit()
            .putString(SharedPrefConst.KEY.USER_INFO_MODEL, value)
            .apply()
    }

    /**
     * Here storing rememberMe model of the current logged in user
     *
     *@param rememberMeModel rememberMeModel which contains rememberMeStatus with email and password
     */
    internal fun storeRememberMeModel(rememberMeModel: RememberMeModel) {
        Log.d(TAG, "storeRememberMeModel")
        val value: String = GsonHelper.convertJavaObjectToJsonString(rememberMeModel)
        Log.d(TAG, "storeRememberMeModel: value- $value")

        SharedPreferencesManager.with(context = AppController.mInstance!!)!!.edit()
            .putString(SharedPrefConst.KEY.REMEMBER_ME_MODEL, value)
            .apply()
    }

    /**
     * It gives userInfoModel details of the current logged in user
     *
     * @return userInfoModel
     */
    internal fun getUserInfoModel(): UserLoginResponseModel? {
        val value: String? =
            SharedPreferencesManager.with(context = AppController.mInstance!!)!!
                .getString(SharedPrefConst.KEY.USER_INFO_MODEL, "")

        var userInfoModel: UserLoginResponseModel? = null

        if (!value.isNullOrBlank())
            userInfoModel =
                GsonHelper.convertJsonStringToJavaObject(
                    value,
                    UserLoginResponseModel::class.java
                ) as UserLoginResponseModel?

        return userInfoModel
    }

    /**
     * It gives user id of the current logged in user
     *
     * @return user id
     */
    internal fun getUserId(): String {
        var userId: String = Constants.App.SPACE.toString()
        val userInfoModel: UserLoginResponseModel? =
            getUserInfoModel()
        if (userInfoModel?.user != null) {
            userInfoModel.user.id?.apply {
                userId = this.toString()
            }
        }

        return userId
    }

    /**
     * It gives rememberMe model of the current logged in user
     *
     * @return rememberMe model
     */
    internal fun getRememberMeModel(): RememberMeModel? {
        val value: String? =
            SharedPreferencesManager.with(context = AppController.mInstance!!)!!
                .getString(SharedPrefConst.KEY.REMEMBER_ME_MODEL, "")

        var rememberMeModel: RememberMeModel? = null

        if (!value.isNullOrBlank())
            rememberMeModel =
                GsonHelper.convertJsonStringToJavaObject(
                    value,
                    RememberMeModel::class.java
                ) as RememberMeModel?

        return rememberMeModel
    }

    /**
     * save user name
     */
    internal fun storeUserName(name: String) {
        Log.d(TAG, "storeUserInfo: Name- $name")

        SharedPreferencesManager.with(context = AppController.mInstance!!)!!.edit()
            .putString(SharedPrefConst.KEY.NAME, name)
            .apply()
    }

    /**
     * get user name
     */
    internal fun getUserName(): String {
        var userName: String? =
            SharedPreferencesManager.with(context = AppController.mInstance!!)!!
                .getString(SharedPrefConst.KEY.NAME, "")

        if (null == userName)
            userName = AppController.mInstance!!.getString(R.string.blank_string)

        return userName
    }

    /**
     * save user name
     */
    internal fun storeSubRoleName(name: String) {
        Log.d(TAG, "storeUserInfo: Name- $name")

        SharedPreferencesManager.with(context = AppController.mInstance!!)!!.edit()
            .putString(SharedPrefConst.KEY.SUB_ROLE_NAME, name)
            .apply()
    }

    /**
     * get user name
     */
    internal fun getSubRoleName(): String {
        var subRoleName: String? =
            SharedPreferencesManager.with(context = AppController.mInstance!!)!!
                .getString(SharedPrefConst.KEY.SUB_ROLE_NAME, "")

        if (null == subRoleName)
            subRoleName = AppController.mInstance!!.getString(R.string.blank_string)

        return subRoleName
    }

    /**
     * save user image
     */
    internal fun storeUserImage(image: String) {
        Log.d(TAG, "storeUserInfo: Image- $image")

        SharedPreferencesManager.with(context = AppController.mInstance!!)!!.edit()
            .putString(SharedPrefConst.KEY.IMAGE, image)
            .apply()
    }

    /**
     * get user image
     */
    internal fun getUserImage(): String {
        var userImage: String? =
            SharedPreferencesManager.with(context = AppController.mInstance!!)!!
                .getString(SharedPrefConst.KEY.IMAGE, "")

        if (null == userImage)
            userImage = AppController.mInstance!!.getString(R.string.blank_string)

        return userImage
    }



    /**
     * Here removing user's all details from SharedPreference
     */
    internal fun removeAllDetails() {
        SharedPreferencesManager.with(context = AppController.mInstance!!)!!.apply {
            edit().apply {
                remove(SharedPrefConst.KEY.USER_INFO_MODEL)
                remove(SharedPrefConst.KEY.AUTH_TOKEN)
                remove(SharedPrefConst.KEY.NAME)
                remove(SharedPrefConst.KEY.IMAGE)
                apply()
            }
        }
    }

    /**
     * save app start time
     */
    internal fun storeAppStartTime(startTime: String) {
        Log.d(TAG, "storeStartTime: startTime- $startTime")

        SharedPreferencesManager.with(context = AppController.mInstance!!)!!.edit()
            .putString(SharedPrefConst.KEY.START_TIME, startTime)
            .apply()
    }

    /**
     * get start time
     */
    internal fun getAppStartTime(): String {
        var startTime: String? =
            SharedPreferencesManager.with(context = AppController.mInstance!!)!!
                .getString(SharedPrefConst.KEY.START_TIME, "")

        if (null == startTime)
            startTime = AppController.mInstance!!.getString(R.string.blank_string)

        return startTime
    }


    /**
     * save app end time
     */
    internal fun storeAppEndtime(endTime: String) {
        Log.d(TAG, "storeEndtime: endTime- $endTime")

        SharedPreferencesManager.with(context = AppController.mInstance!!)!!.edit()
            .putString(SharedPrefConst.KEY.END_TIME, endTime)
            .apply()
    }

    /**
     * get app end time
     */
    internal fun getAppEndTime(): String {
        var endTime: String? =
            SharedPreferencesManager.with(context = AppController.mInstance!!)!!
                .getString(SharedPrefConst.KEY.END_TIME, "")

        if (null == endTime)
            endTime = AppController.mInstance!!.getString(R.string.blank_string)

        return endTime
    }



    /**
     * save app end time
     */
    internal fun storeAppID(appID: String) {
        Log.d(TAG, "storeAppID: appID- $appID")

        SharedPreferencesManager.with(context = AppController.mInstance!!)!!.edit()
            .putString(SharedPrefConst.KEY.APP_ID, appID)
            .apply()
    }

    /**
     * get app end time
     */
    internal fun getAppID(): String {
        var appID: String? =
            SharedPreferencesManager.with(context = AppController.mInstance!!)!!
                .getString(SharedPrefConst.KEY.APP_ID, "")

        if (null == appID)
            appID = AppController.mInstance!!.getString(R.string.blank_string)

        return appID
    }


    /**
     * save Audio Id
     */
    internal fun saveAudioId(appID: String) {
        Log.d(TAG, "storeAppID: appID- $appID")

        SharedPreferencesManager.with(context = AppController.mInstance!!)!!.edit()
            .putString(SharedPrefConst.KEY.AUDIO_ID, appID)
            .apply()
    }

    /**
     * get Audio Id
     */
    internal fun getAudioId(): String {
        var appID: String? =
            SharedPreferencesManager.with(context = AppController.mInstance!!)!!
                .getString(SharedPrefConst.KEY.AUDIO_ID, "")

        if (null == appID)
            appID = AppController.mInstance!!.getString(R.string.blank_string)

        return appID
    }

    internal fun saveBadgeCount(badgeCount: Int) {
        Log.d(TAG, "saveBadgeCount:- $badgeCount")

        SharedPreferencesManager.with(context = AppController.mInstance!!)!!.edit()
            .putInt(SharedPrefConst.KEY.BADGE_COUNT, badgeCount)
            .apply()
    }


    internal fun getBadgeCount(): Int {
        var badgeCount: Int? =
            SharedPreferencesManager.with(context = AppController.mInstance!!)!!
                .getInt(SharedPrefConst.KEY.BADGE_COUNT, 0)

        if (null == badgeCount)
            badgeCount = 0

        return badgeCount
    }



    internal fun storeLogoImage(logoImage: String) {
        Log.d(TAG, "storeLogoImage: logoImageUrl- $logoImage")

        SharedPreferencesManager.with(context = AppController.mInstance!!)!!.edit()
            .putString(SharedPrefConst.KEY.LOGO_IMAGE, logoImage)
            .apply()
    }


    /**
     * It gives authToken of the current logged in user
     *
     * @return authToken
     */
    internal fun getLogoImage(): String {
        var logoImage: String? =
            SharedPreferencesManager.with(context = AppController.mInstance!!)!!
                .getString(SharedPrefConst.KEY.LOGO_IMAGE, "")

        if (null == logoImage)
            logoImage = AppController.mInstance!!.getString(R.string.blank_string)

        return logoImage
    }


    /**
     * save Audio Id
     */
    internal fun saveNotificationId(notificationId: String) {
        Log.d(TAG, "notificationId:- $notificationId")

        SharedPreferencesManager.with(context = AppController.mInstance!!)!!.edit()
            .putString(SharedPrefConst.KEY.NOTIFICATION_ID, notificationId)
            .apply()
    }

    /**
     * get Audio Id
     */
    internal fun getNotificationId(): String {
        var notificationId: String? =
            SharedPreferencesManager.with(context = AppController.mInstance!!)!!
                .getString(SharedPrefConst.KEY.NOTIFICATION_ID, "")

        if (null == notificationId)
            notificationId = AppController.mInstance!!.getString(R.string.blank_string)

        return notificationId
    }




    //

    /**
     * save Audio Id
     */
    internal fun saveMessageSenderId(senderId: String) {
        Log.d(TAG, "notificationId:- $senderId")

        SharedPreferencesManager.with(context = AppController.mInstance!!)!!.edit()
            .putString(SharedPrefConst.KEY.SENDER_ID, senderId)
            .apply()
    }

    /**
     * get Audio Id
     */
    internal fun getMessageSenderId(): String {
        var senderId: String? =
            SharedPreferencesManager.with(context = AppController.mInstance!!)!!
                .getString(SharedPrefConst.KEY.SENDER_ID, "")

        if (null == senderId)
            senderId = AppController.mInstance!!.getString(R.string.blank_string)

        return senderId
    }
}