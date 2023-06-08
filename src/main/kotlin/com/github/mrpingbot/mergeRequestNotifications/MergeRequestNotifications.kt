package com.github.mrpingbot.mergeRequestNotifications

data class MergeRequestNotifications(
    val mergeRequestId: Long,
    val reviewerId: Long,
    val approve: Boolean = true
)
