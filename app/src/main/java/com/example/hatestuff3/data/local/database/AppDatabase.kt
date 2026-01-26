package com.example.hatestuff3.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.hatestuff3.data.local.database.post.CommentDao
import com.example.hatestuff3.data.local.database.post.PostDao
import com.example.hatestuff3.data.local.database.post.PostEntity
import com.example.hatestuff3.data.local.database.user.UserDao
import com.example.hatestuff3.data.local.database.post.CommentEntity
import com.example.hatestuff3.data.local.database.user.UserEntity


@Database(
    entities = [UserEntity::class, PostEntity::class, CommentEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun postDao(): PostDao
    abstract fun commentDao(): CommentDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "hatestuff_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}