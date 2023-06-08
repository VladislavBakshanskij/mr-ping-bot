package com.github.mrpingbot.common

data class PagedResult<T>(
    val page: Int,
    val pageSize: Int,
    val total: Long,
    val items: List<T>
)
