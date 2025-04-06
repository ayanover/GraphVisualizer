package com.service

import java.io.File

interface IDialogService {
    fun showOpenDialog(): File?
    fun showSaveDialog(extension: String): File?
    fun showConfirmDialog(message: String, title: String): Boolean
    fun showErrorMessage(message: String, title: String)
    fun showInfoMessage(message: String, title: String)
}