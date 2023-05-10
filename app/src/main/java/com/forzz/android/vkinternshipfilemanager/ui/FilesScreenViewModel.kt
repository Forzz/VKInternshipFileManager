package com.forzz.android.vkinternshipfilemanager.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.forzz.android.vkinternshipfilemanager.data.model.FileDao
import com.forzz.android.vkinternshipfilemanager.data.model.FileEntity
import com.forzz.android.vkinternshipfilemanager.data.model.UserFile
import com.forzz.android.vkinternshipfilemanager.data.model.toUserFile
import kotlinx.coroutines.launch
import java.io.File

class FilesScreenViewModel : ViewModel() {

    private val _files = MutableLiveData<List<UserFile>?>()
    val files: LiveData<List<UserFile>?> = _files
    private val path = MutableLiveData<String>()

    fun setFiles(file: File) {
        if (file.isDirectory) {
            val userFiles = file.listFiles()?.map { it.toUserFile() }
            _files.postValue(userFiles)
            path.value = file.path
            Log.d("PATH_VAL", "${path.value}")
        }
    }

    fun parentDirectory() {
        val currentPath = path.value
        if (currentPath != "/storage/emulated/0") {
            val parentDirectory = File(currentPath ?: "").parentFile
            if (parentDirectory != null) {
                setFiles(parentDirectory)
                path.value = parentDirectory.path
            }
        }
    }

    fun initDirectory() {
        path.value = "/storage/emulated/0/"
        setFiles(File(path.value ?: ""))
    }

    fun saveFileToDatabase(file: File, hashCode: Int) {
        val fileEntity = FileEntity(
            filePath = file.path,
            fileName = file.name,
            hashCode = hashCode
        )
        viewModelScope.launch {
//            FileDao.insertFile(fileEntity)
        }
    }

    fun getHashCode(file: File): Int = file.hashCode()
}