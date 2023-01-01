package com.bs23.githubrepositories.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class ApiError(
    @Json(name = "error")
    val message: String
)
