package com.example.hatestuff3.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.hatestuff3.data.local.database.post.PostDao
import com.example.hatestuff3.data.local.database.user.UserDao
import com.example.hatestuff3.data.local.database.user.UserEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.hatestuff3.data.local.database.post.PostEntity
import com.example.hatestuff3.data.local.database.post.CommentEntity

@Database(
    entities = [UserEntity::class, PostEntity::class, CommentEntity::class],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase: RoomDatabase(){

    abstract fun userDao(): UserDao
    abstract fun postDao(): PostDao
    // abstract fun commentDao(): CommentDao // Descomenta esto cuando crees el CommentDao

    companion object{
        @Volatile
        private var INSTANCE: AppDatabase? = null
        private const val DB_NAME = "hatestuff.db"

        fun getInstance(context: Context): AppDatabase{
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DB_NAME
                )
                    .addCallback(object : RoomDatabase.Callback(){
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            CoroutineScope(Dispatchers.IO).launch {
                                // Usamos el contexto para obtener la instancia y el DAO
                                val dao = getInstance(context).userDao()

                                // DATOS DE PRUEBA (SEED) - SIN TELÉFONO
                                val seed = listOf(
                                    UserEntity(
                                        name = "admin",
                                        email = "a@a.cl",
                                        password = "Admin123!"
                                    ),
                                    UserEntity(
                                        name = "Cliente",
                                        email = "c@c.cl",
                                        password = "Cliente123!"
                                    )
                                )

                                if (dao.count() == 0){
                                    seed.forEach { dao.insertar(it) }
                                }
                            }
                        }
                    })
                    // Esto permite borrar la BD vieja y crear la nueva sin crashear al cambiar versiones
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
