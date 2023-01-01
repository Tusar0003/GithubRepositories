package com.bs23.githubrepositories.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "repository_details")
data class RepositoryOffline(
    @PrimaryKey
    val fullName: String,
    val repoName: String,
    val totalCount: Int,
    val stargazersCount: Int,
    val avatarUrl: String
)
