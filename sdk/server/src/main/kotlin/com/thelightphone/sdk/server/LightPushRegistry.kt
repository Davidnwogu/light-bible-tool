package com.thelightphone.sdk.server

import android.util.Log

/**
 * In-memory store of push registrations.
 * Maps token -> (packageName, endpoint).
 */
object LightPushRegistry {

    private const val TAG = "LightPushRegistry"

    data class Registration(
        val packageName: String,
        val endpoint: String,
        val channel: String? = null,
        val vapid: String? = null,
    )

    private val registrations = mutableMapOf<String, Registration>()

    /**
     * return the POST endpoint that the calling tool's application server should use to
     * get UnifiedPush through Light's server, down to LightOS/emulator, then over to Tool
     */
    var endpointFetcher: (token: String, vapid: String?) -> String? = { token, vapid ->
        Log.e(TAG, "no endpoint fetch function provided - defaulting to localhost.")
        "http://localhost/$token"
    }

    fun register(token: String, packageName: String, endpoint: String, channel: String? = null, vapid: String? = null) {
        registrations[token] = Registration(packageName, endpoint, channel, vapid)
    }

    fun remove(token: String): Registration? {
        return registrations.remove(token)
    }

    fun get(token: String): Registration? {
        return registrations[token]
    }

    fun getAll(): Map<String, Registration> {
        return registrations.toMap()
    }
}
