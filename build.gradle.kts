// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.ksp) apply false

    // --- AGREGA ESTA LÍNEA EXACTA ---
    // Esto descarga la versión 2.51.1 de Hilt para que coincida con lo que pusimos en el otro archivo
    id("com.google.dagger.hilt.android") version "2.51.1" apply false
}