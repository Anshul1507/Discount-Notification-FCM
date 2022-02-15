package com.github.Anshul1507.discountnotificationfcm

data class Message(
    val id: String?,
    val label: String?,
    val message: String?,
    val validity: String?,
    val time: String?,
    val isNotificationFixed: Boolean,
)
