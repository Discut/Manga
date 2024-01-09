package com.discut.manga.ui.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow


/**
 * from tachiyomi
 */
data class NetworkState(
    val isConnected: Boolean,
    val isValidated: Boolean,
    val isWifi: Boolean,
) {
    val isOnline =
        isConnected && isValidated
}

@Suppress("DEPRECATION")
fun Context.activeNetworkState(): NetworkState {
    val connectivityManager = getSystemService(ConnectivityManager::class.java)
    val wifiManager = getSystemService(WifiManager::class.java)
    val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
    return NetworkState(
        isConnected = connectivityManager.activeNetworkInfo?.isConnected ?: false,
        isValidated = capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
            ?: false,
        isWifi = wifiManager.isWifiEnabled && capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ?: false,
    )
}

fun Context.networkStateFlow() = callbackFlow {
    val connectivityManager = getSystemService(ConnectivityManager::class.java)
    val networkCallback = object : NetworkCallback() {
        override fun onCapabilitiesChanged(
            network: Network,
            networkCapabilities: NetworkCapabilities
        ) {
            trySend(activeNetworkState())
        }

        override fun onLost(network: Network) {
            trySend(activeNetworkState())
        }
    }

    connectivityManager.registerDefaultNetworkCallback(networkCallback)
    awaitClose {
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }
}
