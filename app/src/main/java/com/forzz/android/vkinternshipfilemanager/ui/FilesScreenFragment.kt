package com.forzz.android.vkinternshipfilemanager.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.forzz.android.vkinternshipfilemanager.R
import com.forzz.android.vkinternshipfilemanager.data.model.UserFile
import com.forzz.android.vkinternshipfilemanager.databinding.FragmentFilesScreenBinding
import java.io.File
import java.util.*

class FilesScreenFragment : Fragment() {

    private lateinit var binding: FragmentFilesScreenBinding
    private lateinit var adapter: ItemsAdapter
    private val viewModel: FilesScreenViewModel by viewModels()

    private var currentSortingOption = SortingOption.NAME_INC

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.initDirectory()
        adapter = ItemsAdapter(::onFileClicked)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_files_screen, container, false)
        binding.rvFiles.layoutManager = LinearLayoutManager(context)
        binding.rvFiles.adapter = adapter

        viewModel.files.observe(viewLifecycleOwner) { files ->
            if (files != null) {
                val sortedFiles = sortFilesByOption(files)
                adapter.changeData(sortedFiles)
            }
        }

        binding.spinnerSorting.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                currentSortingOption = when (position) {
                    0 -> SortingOption.NAME_INC
                    1 -> SortingOption.NAME_DEC
                    2 -> SortingOption.SIZE_INC
                    3 -> SortingOption.SIZE_DEC
                    4 -> SortingOption.CREATION_DATE_INC
                    5 -> SortingOption.CREATION_DATE_DEC
                    6 -> SortingOption.EXTENSION_INC
                    7 -> SortingOption.EXTENSION_DEC
                    else -> SortingOption.NAME_INC
                }

                viewModel.files.value?.let { files ->
                    val sortedFiles = sortFilesByOption(files)
                    adapter.changeData(sortedFiles)
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                // Nothing
            }
        }

        binding.ivBack.setOnClickListener {
            viewModel.parentDirectory()
        }

        fun saveFiles(file: File) {
            if (file.isDirectory) {
                file.listFiles()?.forEach {
                    viewModel.saveFileToDatabase(it, file.hashCode())
                    saveFiles(it)
                }
            }
        }

        saveFiles(File("/storage/emulated/0/"))

        return binding.root
    }


    private fun onFileClicked(file: UserFile) {
        if (file.isDirectory) {
            viewModel.setFiles(File(file.path))
        }
    }

    private fun sortFilesByOption(files: List<UserFile>): List<UserFile> {
        return when (currentSortingOption) {
            SortingOption.NAME_INC -> files.sortedWith(nameComparator)
            SortingOption.NAME_DEC -> files.sortedWith(nameComparator.reversed())
            SortingOption.SIZE_INC -> files.sortedWith(sizeComparator)
            SortingOption.SIZE_DEC -> files.sortedWith(sizeComparator.reversed())
            SortingOption.CREATION_DATE_INC -> files.sortedWith(creationDateComparator)
            SortingOption.CREATION_DATE_DEC -> files.sortedWith(creationDateComparator.reversed())
            SortingOption.EXTENSION_INC -> files.sortedWith(extensionComparator)
            SortingOption.EXTENSION_DEC -> files.sortedWith(extensionComparator.reversed())
        }
    }

    companion object {
        private val nameComparator = compareBy<UserFile> { !it.isDirectory }
            .thenBy { it.name.lowercase(Locale.getDefault()) }
        private val sizeComparator = compareBy<UserFile> { !it.isDirectory }
            .thenBy { it.size }
        private val creationDateComparator = compareBy<UserFile> { !it.isDirectory }
            .thenBy { it.creationDateMillis }
        private val extensionComparator = compareBy<UserFile> { !it.isDirectory }
            .thenBy { it.fileType.lowercase(Locale.getDefault()) }
    }
}

enum class SortingOption {
    NAME_INC,
    NAME_DEC,
    SIZE_INC,
    SIZE_DEC,
    CREATION_DATE_INC,
    CREATION_DATE_DEC,
    EXTENSION_INC,
    EXTENSION_DEC
}