package com.bs23.githubrepositories.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Repository(
    @Json(name = "total_count")
    val totalCount: Int,
    @Json(name = "items")
    val items: List<Items>,
)

@JsonClass(generateAdapter = true)
data class Items(
    @Json(name = "name")
    val repoName: String,
    @Json(name = "full_name")
    val fullName: String = "",
    @Json(name = "stargazers_count")
    val stargazersCount: Int,
    @Json(name = "owner")
    val owner: Owner,
)

@JsonClass(generateAdapter = true)
data class Owner(
    @Json(name = "avatar_url")
    val avatarUrl: String,
)
