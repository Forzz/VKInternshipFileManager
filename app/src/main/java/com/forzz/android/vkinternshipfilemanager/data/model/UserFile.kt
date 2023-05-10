package com.forzz.android.vkinternshipfilemanager.data.model

import java.io.File
import java.nio.file.Files
import java.nio.file.attribute.BasicFileAttributes
import java.text.SimpleDateFormat
import java.util.*

data class UserFile(
    val path: String,
    val parentPath: String?,
    val name: String,
    val fileType: String,
    val isDirectory: Boolean,
    val size: Long,
    val creationDateMillis: Long
) {
    fun getCreationDate(): String = File(path).getFormattedCreationDate()

    fun getSize(): String {
        val fileSize: Long = File(path).length()

        return when {
            fileSize < 1024 -> "$fileSize bytes"
            fileSize < 1024 * 1024 -> "${String.format("%.2f", fileSize.toDouble() / 1024)} Kb"
            fileSize < 1024 * 1024 * 1024 -> "${
                String.format(
                    "%.2f",
                    fileSize.toDouble() / (1024 * 1024)
                )
            } Mb"
            else -> "${String.format("%.2f", fileSize.toDouble() / (1024 * 1024 * 1024))} Gb"
        }
    }
}

fun File.toUserFile() = UserFile(
    path = this.path,
    parentPath = this.parent,
    name = this.name,
    fileType = this.extension,
    isDirectory = this.isDirectory,
    size = this.length(),
    creationDateMillis = this.getCreationDate()
)

fun File.getCreationDate(): Long = Files.readAttributes(
    this.toPath(),
    BasicFileAttributes::class.java
).creationTime().toMillis()

fun File.getFormattedCreationDate() =
    SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(Date(this.getCreationDate()))
