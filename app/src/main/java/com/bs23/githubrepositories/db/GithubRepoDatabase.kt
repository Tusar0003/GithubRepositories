package com.bs23.githubrepositories.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.bs23.githubrepositories.db.dao.RepositoryDao
import com.bs23.githubrepositories.db.entity.RepositoryOffline


@Database(
    entities = [
        RepositoryOffline::class,
    ],
    version = 1,
    exportSchema = false
)
abstract class GithubRepoDatabase : RoomDatabase() {

    abstract val repositoryDao: RepositoryDao

    companion object {
        private const val databaseName = "repository_app_db"

        fun buildDatabase(context: Context): GithubRepoDatabase {
            return Room.databaseBuilder(context, GithubRepoDatabase::class.java, databaseName)
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}