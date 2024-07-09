package com.example.pwdvault.control.file_picker

import android.content.Context
import android.net.Uri
import java.io.BufferedReader
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStreamReader

object FilePickerUtils {

    fun readTextFromUri(ctx: Context, uri: Uri): String {
        val contentResolver = ctx.contentResolver
        val stringBuilder = StringBuilder()
        contentResolver.openInputStream(uri)?.use { inputStream ->
            BufferedReader(InputStreamReader(inputStream)).use { reader ->
                var line: String? = reader.readLine()
                while (line != null) {
                    stringBuilder.append(line)
                    line = reader.readLine()
                }
            }
        }
        return stringBuilder.toString()
    }


    fun alterDocument(ctx: Context, uri: Uri, data: String) {
        val contentResolver = ctx.contentResolver
        try {
            contentResolver.openFileDescriptor(uri, "w")?.use { fileDescriptor ->
                FileOutputStream(fileDescriptor.fileDescriptor).use {
                    it.write(
                        data.toByteArray()
                    )
                }
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }


}