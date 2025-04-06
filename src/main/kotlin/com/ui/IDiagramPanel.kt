package com.ui

import java.awt.image.BufferedImage

interface IDiagramPanel {
    var diagram: BufferedImage?
    fun updateDiagram(newDiagram: BufferedImage?)
}