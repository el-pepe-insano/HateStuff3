package com.example.hatestuff3.data.local.database.repository

import com.example.hatestuff3.data.local.database.user.UserDao
import com.example.hatestuff3.data.local.database.user.UserEntity

class UserRepository (
    private val userDao: UserDao
){
    //ejecutar el login o inicio sesion
    suspend fun login(email: String, pass: String): Result<UserEntity>{
        //verificar si el correo existe
        val user = userDao.getByEmail(email)
        return if (user != null && user.password == pass){
            Result.success(user)
        }else{
            Result.failure(IllegalStateException("Credenciales invalidas"))
        }
    }



    //ejecutar el registro de un usuario nuevo
    suspend fun register(name: String, email: String,  password: String):Result<Long>{
        //verificar si ya existe el usuario (por correo)
        val exists = userDao.getByEmail(email) != null
        if(exists){
            return Result.failure(IllegalArgumentException("el correo ya esta en uso"))

        } else{
            val id = userDao.insertar(
                UserEntity(
                    name = name,
                    email = email,
                    password = password

                )
            )
            return Result.success(id)
        }
    }


    //modificar un usuario o eliminar un usuario (investigar)


}