package com.example.hatestuff3.domain.validation

import android.util.Patterns
import java.util.regex.Pattern


fun validateNameLettersOnly(nombre: String): String?{
    //validar si el nombre esta vacio
    if(nombre.isBlank()) return "El nombre es obligatorio"
    //validar que sean solo letras
    val regex = Regex("^[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ ]+$")
    return if(!regex.matches(nombre)) "Solo se aceptan letras y espacios"
    else null
}

fun validateEmail(email: String): String? {
    if(email.isBlank()) return "El correo es obligatorio"
    val ok = Patterns.EMAIL_ADDRESS.matcher(email).matches()
    return if(!ok) "Formato de correo inválido" else null
}

fun validatePhoneDigitsOnly(telefono: String): String?{
    if(telefono.isBlank()) return "El teléfono es obligatorio"
    if(!telefono.all { it.isDigit() }) return "Solo deben ser números"
    if(telefono.length !in 8 .. 15) return "Debe tener entre 8 y 15 numeros"
    return null
}

fun validateStringPassword(pass: String): String?{
    if(pass.isBlank()) return "Debe escribir una contraseña"
    if(pass.length < 8) return "La contraseña debe tener más de 8 carácteres"
    if(!pass.any { it.isUpperCase() }) return "Debe tener al menos 1 mayúscula"
    if(!pass.any { it.isLowerCase() }) return "Debe tener al menos 1 minúscula"
    if(!pass.any { it.isDigit() }) return "Debe tener al menos 1 número"
    if(!pass.any { it.isLetterOrDigit() }) return "Debe tener al menos 1 símbolo"
    if(pass.contains(' ')) return "No debe contener espacios en blanco"
    return null
}

fun validateConfirm(pass:String, confirm: String): String? {
    if(confirm.isBlank()) return "Debe confirmar la contraseña"
    return if(pass != confirm) "Las contraseñas deben ser iguales" else null
}
// Validar el contenido de un Post o Queja
fun validatePostContent(content: String): String? {
    if (content.isBlank()) return "¡No puedes publicar un silencio! Escribe tu odio."
    if (content.length < 5) return "Tu queja es muy corta, desahógate más."
    if (content.length > 500) return "Demasiado odio para un solo post (máx. 500 caracteres)."
    return null
}

// Validar Comentarios
fun validateComment(text: String): String? {
    if (text.isBlank()) return "Escribe algo para responder."
    if (text.length > 200) return "Comentario demasiado largo (máx. 200 caracteres)."
    return null
}
fun validateBio(bio: String): String? {
    if (bio.length > 150) return "La biografía no puede superar los 150 caracteres."
    return null
}