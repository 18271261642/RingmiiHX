package com.guider.health.apilib.model

data class ChangeWithBanPasswordBean(
        val accountId: Int,
        val bandPwd: String,
        val expired: Int,
        val refreshToken: String,
        val token: String
)