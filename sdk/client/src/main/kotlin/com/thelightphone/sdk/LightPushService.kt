package com.thelightphone.sdk

import com.thelightphone.sdk.shared.LightConstants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.unifiedpush.android.connector.FailedReason
import org.unifiedpush.android.connector.PushService
import org.unifiedpush.android.connector.data.PushEndpoint
import org.unifiedpush.android.connector.data.PushMessage

class LightPushService : PushService() {
    private val pushManager by lazy { LightPushManager(applicationContext) }
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    override fun onNewEndpoint(
        endpoint: PushEndpoint,
        instance: String
    ) {
        if (LightConstants.PUSH_INSTANCE_REMOTE == instance) {
            serviceScope.launch {
                pushManager.updatePushCredentials(pushEndpoint = endpoint)
            }
        }
    }

    override fun onMessage(
        message: PushMessage,
        instance: String
    ) {
        if (LightConstants.PUSH_INSTANCE_REMOTE == instance) {
            serviceScope.launch {
                LightSdkRegistry.entryPoint?.onPushNotification(message.content)
            }
        }
        // TODO handle local messages directly from LightOS/emulator
    }

    override fun onRegistrationFailed(
        reason: FailedReason,
        instance: String
    ) {
        if (LightConstants.PUSH_INSTANCE_REMOTE == instance) {
            serviceScope.launch {
                pushManager.clearPushCredentials()
            }
        }
    }

    override fun onUnregistered(instance: String) {
        if (LightConstants.PUSH_INSTANCE_REMOTE == instance) {
            serviceScope.launch {
                pushManager.clearPushCredentials()
            }
        }
    }
}