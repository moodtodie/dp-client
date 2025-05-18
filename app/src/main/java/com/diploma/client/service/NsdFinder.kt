package com.diploma.client.service

import android.content.Context
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.util.Log

class NsdFinder(context: Context) {
    private val nsdManager = context.getSystemService(Context.NSD_SERVICE) as NsdManager
    private val serviceType = "_http._tcp."
    private val serviceName = "shop.local"
    private var discoveryListener: NsdManager.DiscoveryListener? = null

    fun discoverService(onFound: (host: String, port: Int) -> Unit) {
        discoveryListener = object : NsdManager.DiscoveryListener {
            override fun onDiscoveryStarted(regType: String) {
                Log.d("NSD", "Service discovery started")
            }

            override fun onServiceFound(serviceInfo: NsdServiceInfo) {
                if (serviceInfo.serviceType == serviceType && serviceInfo.serviceName.contains(
                        serviceName
                    )
                ) {
                    nsdManager.resolveService(serviceInfo, object : NsdManager.ResolveListener {
                        override fun onServiceResolved(resolvedInfo: NsdServiceInfo) {
                            val hostAddress = resolvedInfo.host.hostAddress
                            val port = resolvedInfo.port
                            Log.d("NSD", "Resolved service at $hostAddress:$port")
                            onFound(hostAddress, port)
                        }

                        override fun onResolveFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {
                            Log.e("NSD", "Resolve failed: $errorCode")
                        }
                    })
                }
            }

            override fun onServiceLost(serviceInfo: NsdServiceInfo) {
                Log.e("NSD", "Service lost: $serviceInfo")
            }

            override fun onDiscoveryStopped(serviceType: String) {
                Log.i("NSD", "Discovery stopped: $serviceType")
            }

            override fun onStartDiscoveryFailed(serviceType: String, errorCode: Int) {
                Log.e("NSD", "Start Discovery failed: Error code:$errorCode")
                nsdManager.stopServiceDiscovery(this)
            }

            override fun onStopDiscoveryFailed(serviceType: String, errorCode: Int) {
                Log.e("NSD", "Stop Discovery failed: Error code:$errorCode")
                nsdManager.stopServiceDiscovery(this)
            }
        }

        nsdManager.discoverServices(serviceType, NsdManager.PROTOCOL_DNS_SD, discoveryListener)
    }

    fun stopDiscovery() {
        discoveryListener?.let { nsdManager.stopServiceDiscovery(it) }
    }
}