package com.service

import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import javax.inject.Inject

class FileService @Inject constructor() : IFileService {
    override fun readFile(file: File): String = file.readText()

    override fun writeFile(file: File, content: String) {
        file.writeText(content)
    }

    override fun exportImage(file: File, image: BufferedImage) {
        val extension = file.extension.lowercase()
        ImageIO.write(image, extension, file)
    }
}