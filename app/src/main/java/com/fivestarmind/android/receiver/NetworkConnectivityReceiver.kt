package com.fivestarmind.android.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build

/**
 * This is a BroadcastReceiver class used for listen to Internet connection.
 */
class NetworkConnectivityReceiver : BroadcastReceiver() {

    companion object {
        var connectivityReceiverListener: ConnectivityReceiverListener? = null
    }

    override fun onReceive(context: Context, arg1: Intent) {
        connectivityReceiverListener?.onNetworkConnectionChanged(
            isConnected = isConnectedOrConnecting(context = context)
        )
    }

    private fun isConnectedOrConnecting(context: Context): Boolean {
        var result = false
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            cm?.run {
                cm.getNetworkCapabilities(cm.activeNetwork)?.run {
                    result = when {
                        hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                        hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                        hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                        else -> false
                    }
                }
            }
        } else {
            cm?.run {
                cm.activeNetworkInfo?.run {
                    if (type == ConnectivityManager.TYPE_WIFI
                        || type == ConnectivityManager.TYPE_MOBILE
                    ) {
                        result = true
                    }
                }
            }
        }

        return result
    }

    interface ConnectivityReceiverListener {
        fun onNetworkConnectionChanged(isConnected: Boolean)
    }

}
