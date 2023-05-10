package com.forzz.android.vkinternshipfilemanager.data.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface FileDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFile(fileEntity: FileEntity)

    @Query("SELECT * FROM files")
    suspend fun getAllFiles(): List<FileEntity>
}
