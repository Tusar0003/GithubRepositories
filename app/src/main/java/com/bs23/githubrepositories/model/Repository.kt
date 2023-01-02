package com.bs23.githubrepositories.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.io.Serializable

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
    val fullName: String,
    @Json(name = "description")
    val description: String?,
    @Json(name = "updated_at")
    val updatedAt: String,
    @Json(name = "stargazers_count")
    val stargazersCount: Int,
    @Json(name = "owner")
    val owner: Owner,
) : Serializable

@JsonClass(generateAdapter = true)
data class Owner(
    @Json(name = "avatar_url")
    val avatarUrl: String,
)

data class RepositoryPayload(
    val page: Int,
    val perPage: Int,
    val query: String
)
