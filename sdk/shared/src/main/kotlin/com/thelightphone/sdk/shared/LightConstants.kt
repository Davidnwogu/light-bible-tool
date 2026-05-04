package com.thelightphone.sdk.shared

object LightConstants {
    const val ACTION_SDK_MARKER = "com.thelightphone.sdk.ACTION_SDK_MARKER"
    const val SDK_VERSION_KEY: String = "com.thelightphone.sdk.SDK_VERSION"

    /** UnifiedPush instance IDs for the two channels */
    const val PUSH_INSTANCE_REMOTE = "light-push"
    const val PUSH_INSTANCE_LOCAL = "light-local"

    /** Message extra sent during registration to signal channel type */
    const val PUSH_CHANNEL_REMOTE = "remote"
    const val PUSH_CHANNEL_LOCAL = "local"
}