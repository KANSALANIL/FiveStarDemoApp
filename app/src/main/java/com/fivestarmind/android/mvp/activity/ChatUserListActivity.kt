package com.fivestarmind.android.mvp.activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.NotificationManagerCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.fivestarmind.android.R
import com.fivestarmind.android.application.AppController
import com.fivestarmind.android.database.DatabaseHelper
import com.fivestarmind.android.enum.DialogEventType
import com.fivestarmind.android.enum.ItemClickType
import com.fivestarmind.android.helper.MessageEvent
import com.fivestarmind.android.helper.SharedPreferencesHelper
import com.fivestarmind.android.interfaces.Constants
import com.fivestarmind.android.interfaces.DialogSesstionExpiredListener
import com.fivestarmind.android.interfaces.RecyclerViewItemListener
import com.fivestarmind.android.mvp.adapter.UserChatAdapter
import com.fivestarmind.android.mvp.dialog.DialogSessionExpired
import com.fivestarmind.android.mvp.model.response.MessagesListingResponseModel
import com.fivestarmind.android.mvp.model.response.ThreadIdResponse
import com.fivestarmind.android.mvp.model.response.ThreadsItem
import com.fivestarmind.android.mvp.presenter.MessagesListingPresenter
import kotlinx.android.synthetic.main.activity_chat_user_list.chatListAudioPlayerView
import kotlinx.android.synthetic.main.activity_chat_user_list.etChatSerach
import kotlinx.android.synthetic.main.activity_chat_user_list.rvUserChat
import kotlinx.android.synthetic.main.activity_chat_user_list.tvNoDataFound
import kotlinx.android.synthetic.main.activity_contact_center.etSearch
import kotlinx.android.synthetic.main.layout_home_mini_player.ivExoIcon
import kotlinx.android.synthetic.main.layout_home_mini_player.progressBarExo
import kotlinx.android.synthetic.main.layout_home_mini_player.tvAudioTitleName
import kotlinx.android.synthetic.main.layout_toolbar.ivBackMenu
import kotlinx.android.synthetic.main.layout_toolbar.tvTitle
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONObject
import java.util.Locale

class ChatUserListActivity : BaseActivity(), RecyclerViewItemListener,
    DialogSesstionExpiredListener,
    MessagesListingPresenter.ResponseCallBack {

    private lateinit var presenter: MessagesListingPresenter
    private var userChatAdapter: UserChatAdapter? = null

    private var messagesList: ArrayList<ThreadsItem>? = null
    private var filterMessagesList: ArrayList<ThreadsItem>? = null
    var mNewText: String? = null
    var showBackIcon: String? = ""
    var showLoader: Boolean? = true
    var dialogSessionExpired: DialogSessionExpired? = null
    private var databaseHelper = DatabaseHelper(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_user_list)
        initData()
        getIntentFrom()
        clickListener()
        searchCoach()
        initRecyclerView()
        callMessageingListingApi()
        /*initializeMiniAudioPlayer(
            chatListAudioPlayerView,
            tvAudioTitleName,
            ivExoIcon,
            progressBarExo
        )*/
        if(AppController.simpleExoplayer!!.isPlaying){
            AppController.simpleExoplayer!!.pause()
        }


    }

    override fun onResume() {
        super.onResume()
        etChatSerach.setText("")
        callMessageingListingApi()
        clearNotificationFromStack()
        /*initializeMiniAudioPlayer(
            chatListAudioPlayerView,
            tvAudioTitleName,
            ivExoIcon,
            progressBarExo
        )*/
        if(AppController.simpleExoplayer!!.isPlaying){
            AppController.simpleExoplayer!!.pause()
        }


    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        EventBus.getDefault().unregister(this)
        super.onStop()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onNewMessageReceivedEvent(event: MessageEvent) {
        Log.d(TAG, "onNewMessageReceivedEvent: ${event.message}")
        if (shouldProceedClick()) {
            showLoader = false
            callMessageingListingApi()
        }

        //here call get message listing Api
        /*  if (event.equals("threadNew")) {
              callMessageingListingApi()
          }*/

    }

    fun getIntentFrom() {
        ivBackMenu.visibility = View.GONE

        if (intent.hasExtra(Constants.App.FROM) && null != intent.getStringExtra(Constants.App.FROM)) {
            showBackIcon = intent.getStringExtra(Constants.App.FROM)!!
            if (showBackIcon.equals("ShowBackButton")) {
                ivBackMenu.visibility = View.VISIBLE

            } else {
                ivBackMenu.visibility = View.GONE
                showBackIcon = ""

            }

        }

    }

    override fun onBackPressed() {
        if (dialogSessionExpired != null) {
            dialogSessionExpired!!.dismiss()
        }

        if (showBackIcon.equals("ShowBackButton")) {
            clickOnBackPress()

        } else {

            this.parent.onBackPressed()

        }


    }

    override fun onDestroy() {
        super.onDestroy()
        if (dialogSessionExpired != null) {
            dialogSessionExpired!!.dismiss()
        }
    }


    private fun initData() {
        presenter = MessagesListingPresenter(this)
        messagesList = ArrayList()
        clearNotificationFromStack()
        setTitle()

    }

    fun clearNotificationFromStack() {
        NotificationManagerCompat.from(baseContext).cancelAll()

        if (AppController.simpleExoplayer!!.isPlaying) {
            AppController.simpleExoplayer!!.pause()
            AppController.simpleExoplayer!!.play()
        }

        if (!AppController.simpleExoplayer!!.isPlaying) {
            AppController.simpleExoplayer!!.pause()
        }
    }

    private fun callMessageingListingApi() {
        if (isInternetConnected(shouldShowToast = true)) {
            if (showLoader!!) {
                showProgressDialog("")
            }
            presenter.hitMessagesListingApi("Bearer " + SharedPreferencesHelper.getAuthToken())

        }


    }

    /**
     * Click event on views
     */
    private fun clickListener() {
        ivBackMenu.setOnClickListener(clickListener)
        //ivChat.setOnClickListener(clickListener)

    }

    private var clickListener = View.OnClickListener { view ->
        if (shouldProceedClick())
            when (view.id) {
                R.id.ivBackMenu -> clickOnBackPress()
                //   R.id.ivChat -> gotoChatActivity()
            }
    }

    /**
     * here call click On BackPress
     */
    private fun clickOnBackPress() {
        onBackPressedDispatcher.onBackPressed()
    }


    private fun gotoChatActivity(data: ThreadsItem) {
        if (data.id != null) {

            val subscribedObject = JSONObject()
            subscribedObject.put("threadId", data.id)
            Log.d(TAG, "subscribedObject: " + subscribedObject.toString())

            AppController.mSocketManager!!.subscribed(subscribedObject)
        }
        if (data.recipient == null) {
            return
        }

        startActivity(
            Intent(
                this@ChatUserListActivity,
                ChatActivity::class.java
            ).putExtra(Constants.App.MESSAGE_USER_ID, data.recipient.id)
                .putExtra(Constants.App.MESSAGE_USER_NAME, data.recipient.name)
                .putExtra(Constants.App.MESSAGE_USER_IMAGE, data.recipient.profileImg)
        )

        // finish()
    }

    private fun setTitle() {
        tvTitle.text = getString(R.string.chats)
    }


    /**
     * Here recyclerViews are setup with it's adapter and it's listeners
     */
    private fun initRecyclerView() {
        userChatAdapter =
            UserChatAdapter(this@ChatUserListActivity, messagesList, this)

        rvUserChat.apply {
            layoutManager = LinearLayoutManager(
                this@ChatUserListActivity, LinearLayoutManager.VERTICAL, false
            )
            adapter = userChatAdapter
        }
    }

    override fun onRecyclerViewItemClick(
        itemClickType: ItemClickType,
        model: Any?,
        position: Int,
        view: View
    ) {
        when (itemClickType) {
            ItemClickType.DETAIL -> {
                model as ThreadsItem
                gotoChatActivity(model)
            }

            else -> {
                showToast("wrong click type")
            }
        }

    }

    override fun onMessagesListingResponseSuccess(response: MessagesListingResponseModel) {

        if (this@ChatUserListActivity.baseContext != null) {
            runOnUiThread {

                hideProgressDialog()
                messagesList!!.clear()
                showLoader = true
                if (response.data!!.threads!!.size > 0) {
                    tvNoDataFound.visibility = View.GONE
                    messagesList!!.addAll(response.data.threads!!)
                    Log.d(TAG, "onMessagesListingResponseSuccess1:" + messagesList)


                } else {
                    tvNoDataFound.visibility = View.VISIBLE
                    Log.d(TAG, "onMessagesListingResponseSuccess2:" + messagesList)

                }

                userChatAdapter!!.notifyDataSetChanged()


            }

        }

    }

    override fun onGetThreadIdResponseSuccess(response: ThreadIdResponse) {

    }

    override fun onGetChatListingResponseSuccess(response: ThreadIdResponse) {

    }

    override fun onSendChatResponseSuccess(message: String) {

    }

    override fun onFailureResponse(message: String) {
        if (this@ChatUserListActivity.baseContext != null) {
            // refreshLayout.isRefreshing = false

            runOnUiThread {
                if (this@ChatUserListActivity.baseContext != null) {
                    hideProgressDialog()

                    if (message.equals("Unauthorized")) {

                        if (dialogSessionExpired == null) {

                            dialogSessionExpired = DialogSessionExpired(
                                listener = this,
                                message = getString(R.string.session_expired),
                                context = this,

                                )
                        }

                        if (dialogSessionExpired!!.isShowing && dialogSessionExpired != null) {
                            dialogSessionExpired!!.dismiss()
                        }

                        try {


                            if (!isFinishing && !isDestroyed && !dialogSessionExpired!!.isShowing) {
                                dialogSessionExpired!!.show()
                            }

                        } catch (e: WindowManager.BadTokenException) {
                            //use a log message
                            Log.d(TAG, "BadTokenException" + e.message)
                        }

                    } else {
                        messagesList!!.clear()
                        tvNoDataFound.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun searchCoach() {
        etChatSerach.afterTextChanged { clickOnSearch(it) }
        etChatSerach.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch()
                return@OnEditorActionListener true
            }
            false
        })
    }

    private fun performSearch() {
        etSearch.clearFocus()
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(etSearch.windowToken, 0)
    }


    private fun clickOnSearch(it: String) {
        Log.d(TAG, "clickOnSearch>> :  " + it)

        if (it.length > 1) {

            filterMessagesList = filter(messagesList!!, it)
            userChatAdapter!!.setFilter(filterMessagesList!!)

        } else if (it.length == 0) {
            tvNoDataFound.visibility =View.GONE

            userChatAdapter!!.setFilter(messagesList!!)
        }
    }

    private fun filter(dataList: ArrayList<ThreadsItem>, newText: String): ArrayList<ThreadsItem> {
        mNewText = newText.lowercase(Locale.getDefault())
        var text: String
        filterMessagesList = ArrayList()
        for (threadsItem in dataList) {
            text = threadsItem.toString().lowercase(Locale.getDefault())
            if (text.contains(mNewText!!)) {
                filterMessagesList!!.add(threadsItem)
                tvNoDataFound.visibility =View.GONE

            }
            if (filterMessagesList!!.size>0){
                tvNoDataFound.visibility =View.GONE

                //Toast.makeText(ChatUserListActivity@this, "No Data found", Toast.LENGTH_SHORT).show()
            }else{
                filterMessagesList!!.clear()
                tvNoDataFound.visibility =View.VISIBLE


            }
        }
        return filterMessagesList!!
    }

    fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
        this.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(editable: Editable?) {
                afterTextChanged.invoke(editable.toString())
            }
        })
    }

    override fun onDialogSesstionExpiredListener(dialogEventType: DialogEventType) {
        Log.d(TAG, "onDialogSesstionExpiredListener:")

        when (dialogEventType) {
            DialogEventType.POSITIVE -> clearLocalDB()

            else -> {}
        }
    }

    fun clearLocalDB() {

        if (!isFinishing && dialogSessionExpired != null && dialogSessionExpired!!.isShowing) {
            dialogSessionExpired!!.dismiss()
        }
        onSessionExpiredClearLocalDB(databaseHelper)
        // updateLoginStatus()

        startActivity(
            Intent(
                this@ChatUserListActivity,
                SignInActivity::class.java
            ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        )
        finish()

    }


}