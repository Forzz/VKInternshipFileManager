package com.forzz.android.vkinternshipfilemanager.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.forzz.android.vkinternshipfilemanager.R
import com.forzz.android.vkinternshipfilemanager.common.Constants
import com.forzz.android.vkinternshipfilemanager.data.model.UserFile
import com.forzz.android.vkinternshipfilemanager.databinding.FileOrDirItemBinding

class ItemsAdapter(private val clickListener: (UserFile) -> Unit) :
    RecyclerView.Adapter<ItemsAdapter.FilesViewHolder>() {

    private val files: MutableList<UserFile> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilesViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = FileOrDirItemBinding.inflate(inflater, parent, false)

        return FilesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FilesViewHolder, position: Int) {
        holder.bind(files[position])
    }

    override fun getItemCount(): Int = files.size

    fun changeData(list: List<UserFile>) {
        this.files.clear()
        this.files.addAll(list)
        notifyDataSetChanged()
    }

    inner class FilesViewHolder(
        private val binding: FileOrDirItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(file: UserFile) {
            binding.tvFileName.text = file.name
            binding.tvFileCreationDate.text = file.getCreationDate()
            if (!file.isDirectory) {
                binding.tvFileSize.text = file.getSize()
            } else {
                binding.tvFileSize.text = ""
            }

            binding.root.setOnClickListener {
                clickListener.invoke(file)
            }

            when {
                file.isDirectory -> binding.ivFileIcon.setImageResource(R.drawable.ic_baseline_folder_24)
                Constants.imageExtensions.contains(file.fileType) -> binding.ivFileIcon.setImageResource(
                    R.drawable.ic_baseline_image_24
                )
                else -> binding.ivFileIcon.setImageResource(R.drawable.ic_baseline_insert_drive_file_24)
            }
        }
    }
}