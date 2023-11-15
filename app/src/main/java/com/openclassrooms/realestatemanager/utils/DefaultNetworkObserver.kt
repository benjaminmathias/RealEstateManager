package com.openclassrooms.realestatemanager.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET
import android.net.NetworkRequest
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class DefaultNetworkObserver @Inject constructor(
    @ApplicationContext val context: Context
) : NetworkObserver {

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    override val isConnected: Flow<Boolean> = callbackFlow {
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                Log.d("NetworkObserver", "Network available")
                trySend(true)
            }

            override fun onLost(network: Network) {
                trySend(false)
                Log.d("NetworkObserver", "Network lost")
                super.onLost(network)
            }
        }
        val request = NetworkRequest.Builder()
            .addCapability(NET_CAPABILITY_INTERNET)
            .apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    addCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
                }
            }
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .build()
        trySend(ConnectedCompat.isConnected(connectivityManager))
        connectivityManager.registerNetworkCallback(request, callback)

        awaitClose {
            connectivityManager.unregisterNetworkCallback(callback)
        }
    }
}

object ConnectedCompat {

    private val IMPL: ConnectedCompatImpl

    init {
        IMPL = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            MarshMallowImpl
        } else {
            BaseImpl
        }
    }

    fun isConnected(connectivityManager: ConnectivityManager) =
        IMPL.isConnected(connectivityManager)

    internal interface ConnectedCompatImpl {
        fun isConnected(connectivityManager: ConnectivityManager): Boolean
    }

    object BaseImpl : ConnectedCompatImpl {
        @Suppress("DEPRECATION")
        override fun isConnected(connectivityManager: ConnectivityManager): Boolean =
            connectivityManager.activeNetworkInfo?.isConnected ?: false

    }

    object MarshMallowImpl : ConnectedCompatImpl {
        @RequiresApi(Build.VERSION_CODES.M)
        override fun isConnected(connectivityManager: ConnectivityManager): Boolean =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
                ?.hasCapability(NET_CAPABILITY_INTERNET) == true
    }
}

