package com.fivestarmind.android.socket

import android.util.Log
import com.fivestarmind.android.helper.MessageEvent
import com.fivestarmind.android.helper.SharedPreferencesHelper
import com.fivestarmind.android.retrofit.ApiClient
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.greenrobot.eventbus.EventBus
import org.json.JSONObject


class SocketManager {
    private var TAG: String = "SocketManager"
    private var socket: Socket? = null

    init {
        val deviceToken = SharedPreferencesHelper.getAuthToken()

        var map: HashMap<String, String> = HashMap()
        map.put("token", "Bearer " + deviceToken)

        val opts = IO.Options()
        opts.reconnection = true
        ///opts.path = "/5starchat/socket.io"
        opts.path = ApiClient.BASE_SOCKET_PATH
     //   opts.path = "/socket.io"
        opts.auth = map
        opts.timeout = 60 * 1000
        //opts.query = "{token: $deviceToken}"
        opts.transports = arrayOf("websocket")
        opts.upgrade = false


        try {
            Log.d(TAG, "Option: " + opts.auth)
            socket = IO.socket(ApiClient.SOCKET_BASE_URL, opts)
            Log.e(TAG, "SocketUrl: " + ApiClient.SOCKET_BASE_URL)
            Log.d(TAG, "socketConnect: Socket init")

            socket?.connect()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()

        }
    }

    fun connect() {

        try {

            socket!!.on(Socket.EVENT_CONNECT, Emitter.Listener { args ->
                Log.e(TAG, "args: " + args.size)
                Log.d(TAG, "socketConnect>>>: Socket Connected")
            })

            socket!!.on(Socket.EVENT_DISCONNECT, Emitter.Listener { args ->
                //                    Log.d(TAG, "call:" + args[0]);
                Log.d(TAG, "socketConnect: Socket Disconnected")
                //CommonClass.toast(context, "Service disconnect");
            })

            socket!!.on(Socket.EVENT_CONNECT_ERROR, Emitter.Listener { args ->
                Log.d(TAG, "socketConnect: Socket Error")

               /* Log.d(TAG, "call:" + args[0].toString())
                val obj = JSONObject(args[0].toString())
                val message = obj.getString("message")
                if (message.equ als("Unauthenticated.")) {
                    EventBus.getDefault().post("Unauthorized")
                }*/

            })

            socket!!.on("errored", Emitter.Listener { args ->
                Log.d(TAG, "call_errored:" + args[0].toString())

            })

            socket!!.on("subscribed", Emitter.Listener { args ->
                Log.d(TAG, "call_subscribed: " + args[0].toString())

            })

            socket!!.on("thread:new", Emitter.Listener { args ->
                Log.d(TAG, "call_hread:new: " + args[0].toString())
                //EventBus
                EventBus.getDefault().post("threadNew")
            })

            socket!!.on("thread:update", Emitter.Listener { args ->
                Log.d(TAG, "call_thread:update: " + args[0].toString())
            })

            socket!!.on("message", Emitter.Listener { args ->
                Log.d(TAG, "call_message: " + args[0].toString())
                //EventBus
                EventBus.getDefault().post(MessageEvent(args[0].toString()))

            })

        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

    }

    fun disconnect() {
        socket?.disconnect()

    }

    fun isConnected(): Boolean {
        return socket?.connected() ?: false
    }


    /* fun onMessageReceived() {
         socket!!.on("message", Emitter.Listener { args ->
             Log.d(TAG, "call:" + args[0].toString())
             Log.d(TAG, "message: Get message")
             //CommonClass.toast(context, "Service disconnect");
             EventBus.getDefault().post(MessageEvent(args[0].toString()));

         })

     }
 */
    fun sendMessage(message: String) {
        socket?.emit("message", message)
    }

    fun subscribed(threadId: JSONObject) {
        //  socket?.emit("subscribed", threadId)

        socket?.emit("subscribe:thread", threadId)
    }

    fun unSubscribed(threadId: JSONObject) {
        //  socket?.emit("subscribed", threadId)
        socket?.emit("unsubscribe:thread", threadId)

    }
}
