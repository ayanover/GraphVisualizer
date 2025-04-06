package com.service

import java.awt.image.BufferedImage
import java.io.File


interface IFileService {
    fun readFile(file: File): String
    fun writeFile(file: File, content: String)
    fun exportImage(file: File, image: BufferedImage)
}