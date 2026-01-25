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

// Definimos las entidades (tablas) y la versión de la BD
@Database(
    entities = [UserEntity::class, PostEntity::class, CommentEntity::class],
    version = 2, // Subimos la versión por si acaso
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    // Exponemos los DAOs
    abstract fun userDao(): UserDao
    abstract fun postDao(): PostDao
    abstract fun commentDao(): CommentDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Función Singleton para obtener la base de datos
        // NOTA: Eliminamos el 'scope' para simplificar la llamada desde MainActivity
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "hatestuff_database"
                )
                    // Si cambias la estructura de las tablas, esto evita que la app crashee (borra los datos viejos)
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}