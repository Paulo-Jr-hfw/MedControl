package com.app.medcontrol.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import androidx.core.graphics.scale

@Singleton
class ImageStorageManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val TAG = "ImageStorageManager"
    private val FOLDER_NAME = "med_images"
    private val MAX_SIZE = 800

    suspend fun saveImage(uri: Uri): String? = withContext(Dispatchers.IO) {
        var arquivoCriado: File? = null
        var originalBitmap: Bitmap? = null
        var resizedBitmap: Bitmap? = null

        try {
            val (origWidth, origHeight) = obterDimensoesImagem(uri)
            if (origWidth <= 0 || origHeight <= 0) {
                Log.e(TAG, "Falha crítica: Não foi possível decodificar as dimensões da imagem (imagem inválida/corrompida).")
                return@withContext null
            }


            val sampleSize = calcularSampleSize(origWidth, origHeight, MAX_SIZE)

            originalBitmap = carregarBitmapSeguro(uri, sampleSize) ?: return@withContext null

            resizedBitmap = redimensionarSeNecessario(originalBitmap, MAX_SIZE)

            val folder = File(context.filesDir, FOLDER_NAME)
            if (!folder.exists() && !folder.mkdirs()) {
                Log.e(TAG, "Falha crítica: Não foi possível criar o diretório de armazenamento: ${folder.absolutePath}")
                return@withContext null
            }

            val fileName = "med_${UUID.randomUUID()}.webp"
            arquivoCriado = File(folder, fileName)

            val sucessoCompressao = salvarEmDisco(resizedBitmap, arquivoCriado)

            if (!sucessoCompressao) {
                Log.e(TAG, "Falha crítica: compress() retornou falso ao gravar no disco.")

                if (arquivoCriado.exists()) arquivoCriado.delete()
                return@withContext null
            }

            Log.i(TAG, "Imagem processada e salva com sucesso: $fileName")
            fileName

        } catch (e: Exception) {
            Log.e(TAG, "Erro inesperado ao salvar imagem da URI: $uri", e)

            arquivoCriado?.let { if (it.exists()) it.delete() }
            null
        } finally {

            if (originalBitmap != resizedBitmap) {
                originalBitmap?.recycle()
            }
            resizedBitmap?.recycle()
        }
    }

    fun substituirImagemAntiga(nomeImagemAntiga: String?, novaUri: Uri?): suspend () -> String? {
        return {
            if (!nomeImagemAntiga.isNullOrBlank()) {
                Log.d(TAG, "Substituindo imagem antiga: $nomeImagemAntiga. Removendo arquivo do disco...")
                deleteImage(nomeImagemAntiga)
            }
            novaUri?.let { saveImage(it) }
        }
    }

    fun getFileFromName(fileName: String?): File? {
        if (fileName.isNullOrBlank()) return null
        val folder = File(context.filesDir, FOLDER_NAME)
        return File(folder, fileName)
    }

    fun deleteImage(fileName: String?) {
        if (fileName.isNullOrBlank()) return
        try {
            val file = getFileFromName(fileName)
            if (file?.exists() == true) {
                if (file.delete()) {
                    Log.d(TAG, "Arquivo deletado com sucesso: $fileName")
                } else {
                    Log.w(TAG, "Falha ao deletar arquivo existente: $fileName")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao tentar deletar imagem: $fileName", e)
        }
    }


    private fun obterDimensoesImagem(uri: Uri): Pair<Int, Int> {
        val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        context.contentResolver.openInputStream(uri)?.use { stream ->
            BitmapFactory.decodeStream(stream, null, options)
        }
        return Pair(options.outWidth, options.outHeight)
    }

    private fun calcularSampleSize(width: Int, height: Int, maxSize: Int): Int {
        var sampleSize = 1
        if (width > maxSize || height > maxSize) {
            val halfWidth = width / 2
            val halfHeight = height / 2
            while ((halfWidth / sampleSize) >= maxSize && (halfHeight / sampleSize) >= maxSize) {
                sampleSize *= 2
            }
        }
        return sampleSize
    }

    private fun carregarBitmapSeguro(uri: Uri, sampleSize: Int): Bitmap? {
        val bitmapOptions = BitmapFactory.Options().apply { inSampleSize = sampleSize }
        return context.contentResolver.openInputStream(uri)?.use { stream ->
            BitmapFactory.decodeStream(stream, null, bitmapOptions)
        }
    }

    private fun redimensionarSeNecessario(bitmap: Bitmap, maxSize: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height

        if (width <= maxSize && height <= maxSize) {
            return bitmap
        }

        val bitmapRatio = width.toFloat() / height.toFloat()
        val finalWidth: Int
        val finalHeight: Int

        if (bitmapRatio > 1) {
            finalWidth = maxSize
            finalHeight = (maxSize / bitmapRatio).toInt()
        } else {
            finalHeight = maxSize
            finalWidth = (maxSize * bitmapRatio).toInt()
        }
        return bitmap.scale(finalWidth, finalHeight)
    }

    private fun salvarEmDisco(bitmap: Bitmap, file: File): Boolean {
        return try {
            FileOutputStream(file).use { out ->
                val format = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    Bitmap.CompressFormat.WEBP_LOSSY
                } else {
                    @Suppress("DEPRECATION")
                    Bitmap.CompressFormat.WEBP
                }
                bitmap.compress(format, 75, out)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao gravar arquivo em disco", e)
            false
        }
    }
}
