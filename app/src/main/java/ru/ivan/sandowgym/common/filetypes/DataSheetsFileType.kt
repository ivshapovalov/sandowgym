package ru.ivan.sandowgym.common.filetypes

import me.rosuh.filepicker.filetype.FileType
import ru.ivan.sandowgym.R

class DataSheetsFileType(
        override val fileType: String = "",
        override val fileIconResId: Int = R.drawable.ic_unknown_file_picker
) : FileType {
    override fun verify(fileName: String): Boolean {
        return fileName.endsWith("xlsx") || fileName.endsWith("xls")
    }
}