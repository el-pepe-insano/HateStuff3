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
    entities = [UserEntity::class, PostEntity::class, CommentEntity::class], // <--- AGREGAR AQUÍ
    version = 2, // <--- SUBIR VERSIÓN
    exportSchema = false
)
abstract class AppDatabase: RoomDatabase(){
    //exponer/importar todos los DAO  de mis entidades
    abstract fun userDao(): UserDao
    abstract fun postDao(): PostDao

    companion object{
        //variable para las instancia de la BD
        @Volatile
        private var INSTANCE: AppDatabase? = null
        //variable para indicar el nombre de la base de datos
        private const val DB_NAME = "hatestuff.db"

        //obteniendo la instancia de conexion a la BD
        fun getInstance(context: Context): AppDatabase{
            return INSTANCE ?: synchronized(this){
                //construimos la base de datos
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DB_NAME
                )
                    //ejecute la creacion en caso de que sea la primera vez
                    .addCallback(object : RoomDatabase.Callback(){
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            //corrutina insertar datos iniciales en mis tablas
                            CoroutineScope (Dispatchers.IO).launch {
                                val dao = getInstance(context).userDao()

                                // CORREGÍ ESTA LISTA (te faltaba una coma y cerrar bien el paréntesis)
                                val seed = listOf(
                                    UserEntity(
                                        name = "admin",
                                        email = "a@a.cl",
                                        phone = "12345678",
                                        password = "Admin123!"
                                    ), // <--- Coma aquí
                                    UserEntity(
                                        name = "Cliente",
                                        email = "c@c.cl",
                                        phone = "12345678",
                                        password = "Cliente123!"
                                    )
                                ) // <--- Cierre de lista aquí

                                //validar que solo se inserte la primera vez
                                if (dao.count() == 0){
                                    seed.forEach { dao.insertar(it) }
                                }
                            }
                        }
                    })
                    .fallbackToDestructiveMigration() // Esto está perfecto
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

