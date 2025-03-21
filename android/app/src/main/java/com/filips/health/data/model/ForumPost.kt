package com.filips.health.data.model

import java.util.Date

data class ForumPost constructor(
    var id: Long = 0,
    var userId: String = "",
    var timestamp: Date = Date(),
    var title: String = "",
    var description: String = "",
    var healthDataId: Long = 0,
    var anonymous: Boolean = false
)