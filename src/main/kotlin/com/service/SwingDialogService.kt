package com.service

import java.io.File
import javax.inject.Inject
import javax.swing.JFileChooser
import javax.swing.JFrame
import javax.swing.JOptionPane
import javax.swing.filechooser.FileNameExtensionFilter

class SwingDialogService @Inject constructor(
    private val parent: JFrame
) : IDialogService {
    override fun showOpenDialog(): File? {
        val fileChooser = JFileChooser()
        fileChooser.fileFilter = FileNameExtensionFilter("Text Files", "txt")

        return if (fileChooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
            fileChooser.selectedFile
        } else null
    }

    override fun showSaveDialog(extension: String): File? {
        val fileChooser = JFileChooser()
        fileChooser.fileFilter = FileNameExtensionFilter(
            "${extension.uppercase()} Files",
            extension
        )

        return if (fileChooser.showSaveDialog(parent) == JFileChooser.APPROVE_OPTION) {
            var file = fileChooser.selectedFile
            if (!file.name.lowercase().endsWith(".$extension")) {
                file = File(file.absolutePath + ".$extension")
            }
            file
        } else null
    }

    override fun showConfirmDialog(message: String, title: String): Boolean {
        val result = JOptionPane.showConfirmDialog(
            parent,
            message,
            title,
            JOptionPane.YES_NO_OPTION
        )
        return result == JOptionPane.YES_OPTION
    }

    override fun showErrorMessage(message: String, title: String) {
        JOptionPane.showMessageDialog(
            parent,
            message,
            title,
            JOptionPane.ERROR_MESSAGE
        )
    }

    override fun showInfoMessage(message: String, title: String) {
        JOptionPane.showMessageDialog(
            parent,
            message,
            title,
            JOptionPane.INFORMATION_MESSAGE
        )
    }
}