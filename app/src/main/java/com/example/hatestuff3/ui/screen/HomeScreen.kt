package com.example.hatestuff3.ui.screen

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import coil.request.ImageRequest

import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private fun createTempImageFile(context: Context): File {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val storageDir = File(context.cacheDir, "images").apply {
        if(!exists()) mkdirs() //creo la carpeta si no existe
    }
    return File(storageDir,"IMG_$timeStamp.jpg") //archivo temporal de la foto
}

//convertir un archivo en una uri segura mediante el FileProvider
private fun getImageUriForFile(context: Context, file: File): Uri {
    val authority = "${context.packageName}.fileprovider"
    return FileProvider.getUriForFile(context,authority,file)
}
@Composable
fun HomeScreen(
    onGoLogin: () -> Unit,
    onGoRegister: () -> Unit
){
    //contexto para la manipulacion de archivos
    val context = LocalContext.current
    //guardemos la ultima foto tomada
    var photoUriString by rememberSaveable { mutableStateOf<String?>(null) }
    //guardar la Uri actual para que la use la camara
    var pendingCaptureUri by remember { mutableStateOf<Uri?>(null) }
    //launcher para que abra la camara y guarde el resultado
    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if(success){
            //si la camara devuelve exito, guardamos la Uri para poder mostrar la foto
            photoUriString = pendingCaptureUri?.toString()
            //mensaje no intrusivo
            Toast.makeText(context, "Foto guardada correctamente", Toast.LENGTH_SHORT).show()
        } else {
            //limpiar la variable de Uri
            pendingCaptureUri = null
            Toast.makeText(context, "No se tomó ninguna foto", Toast.LENGTH_SHORT).show()
        }
    }
    val bg = MaterialTheme.colorScheme.surfaceVariant

    //contenedor es a pantalla completa
    Box(
        modifier = Modifier
            .fillMaxSize() //ocupa todo
            .background(bg) //color de fondo de la caja
            .padding(16.dp), //margenes interiores a la caja
        contentAlignment = Alignment.Center //centra todos sus elementos internos

    ){
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Página Principal",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold
                )
                //un espacio en blanco de tamaño determinado por mi
                Spacer(Modifier.width(8.dp))
                AssistChip(
                    onClick = {},
                    label = { Text("Botón de navegación")}
                )
            } //fin de la fila (Row)
            Spacer(Modifier.width(16.dp))

            //CARD elevado (con sombra)
            ElevatedCard(
                modifier = Modifier.fillMaxWidth() //ocupe el tamaño completo de la columna
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Soy un texto de la página principal",
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text = "Soy otro texto con otro formato",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            //espacio
            Spacer(Modifier.width(24.dp))
            ElevatedCard(
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Captura de foto con cámara",
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(12.dp))
                    //si ya ha tomado una foto, la muestra sino muestra un texto
                    if(photoUriString.isNullOrEmpty()){
                        Text("No ha tomado fotos",
                            style = MaterialTheme.typography.bodyMedium )
                        Spacer(Modifier.height(12.dp))
                    } else {
                        //si existe una foto
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(Uri.parse(photoUriString))
                                .crossfade(true)
                                .build(),
                            contentDescription = "Foto tomada",
                            modifier = Modifier.fillMaxWidth()
                                .height(250.dp),
                            contentScale = ContentScale.Fit
                        )
                        Spacer(Modifier.height(12.dp))
                    }

                    //estado para mostrar un dialog alert
                    var showDialog by remember { mutableStateOf(false) }
                    //boton para abrir la camara
                    Button(
                        onClick = {
                            val file =
                                com.example.hatestuff3.ui.screen.createTempImageFile(context)
                            val uri = getImageUriForFile(context, file)
                            pendingCaptureUri = uri
                            takePictureLauncher.launch(uri)
                        }
                    ) {
                        Text(
                            if(photoUriString.isNullOrEmpty()) "Abrir Cámara"
                            else "Volver a tomar foto"
                        )
                    }

                    //boton adicional para eliminar la foto
                    if(!photoUriString.isNullOrEmpty()){
                        Spacer(Modifier.height(12.dp))
                        OutlinedButton(onClick = { showDialog = true }) {
                            Text("Eliminar Foto")
                        }
                    }

                    if(showDialog){
                        AlertDialog(
                            onDismissRequest = { showDialog = false },
                            title = { Text(text = "Confirmación")},
                            text = { Text("¿Desea eliminar la foto?")},
                            confirmButton = {
                                TextButton(onClick = {
                                    photoUriString = null
                                    showDialog = false
                                    Toast.makeText(context, "Foto eliminada", Toast.LENGTH_SHORT).show()
                                }) {
                                    Text("Aceptar")
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { showDialog = false }) {
                                    Text("Cancelar")
                                }
                            }

                        )
                    }


                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ){
                Button(onClick = onGoLogin) { Text("Ir al inicio de sesión") }
                OutlinedButton(onClick = onGoRegister) { Text("Ir al Registro") }
            }

        }
    }

}