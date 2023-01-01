package com.bs23.githubrepositories.db.dao

import androidx.room.Dao
import androidx.room.Query
import com.bs23.githubrepositories.db.entity.RepositoryOffline
import kotlinx.coroutines.flow.Flow

@Dao
interface RepositoryDao: BaseDao<RepositoryOffline> {
    @Query("SELECT * FROM repository_details;")
    fun getRepositoryList(): Flow<List<RepositoryOffline?>>
}
