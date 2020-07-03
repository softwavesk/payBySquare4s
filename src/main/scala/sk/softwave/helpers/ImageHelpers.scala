package sk.softwave.helpers

import java.io.{ File, FileNotFoundException, IOException }

import javax.imageio.IIOImage
import javax.imageio.ImageIO
import javax.imageio.ImageTypeSpecifier
import javax.imageio.metadata.IIOInvalidTreeException
import javax.imageio.metadata.IIOMetadata
import javax.imageio.metadata.IIOMetadataNode
import java.awt.image.BufferedImage

object ImageHelpers {

  @throws[IOException]
  def saveAsPNG(bImage: BufferedImage, outputFile: File, dpi: Int): Unit = {
    val formatName = "png"
    outputFile.delete
    outputFile.isFile
    val iw = ImageIO.getImageWritersByFormatName(formatName)
    iw.hasNext
    val writer = iw.next
    val writeParam = writer.getDefaultWriteParam
    val typeSpecifier = ImageTypeSpecifier.createFromBufferedImageType(BufferedImage.TYPE_INT_RGB)
    val metadata = writer.getDefaultImageMetadata(typeSpecifier, writeParam)
    setDPI(metadata, dpi)
    //createImageOutputStream catches FileNotFoundException and returns null for some reason
    Option(ImageIO.createImageOutputStream(outputFile)) match {
      case Some(stream) =>
        try {
          writer.setOutput(stream)
          writer.write(metadata, new IIOImage(bImage, null, metadata), writeParam)
        } finally {
          writer.dispose()
          stream.flush()
          stream.close()
        }
      case None => throw new FileNotFoundException("No such file or directory, or access denied")
    }
  }

  @throws[IIOInvalidTreeException]
  private def setDPI(metadata: IIOMetadata, dpi: Int): Unit = {
    val inch_2_cm = 2.54
    val dotsPerMilli = 1.0 * dpi / 10 / inch_2_cm // for PNG, it's dots per millimeter
    val horiz = new IIOMetadataNode("HorizontalPixelSize")
    horiz.setAttribute("value", dotsPerMilli.toString)
    val vert = new IIOMetadataNode("VerticalPixelSize")
    vert.setAttribute("value", dotsPerMilli.toString)
    val dim = new IIOMetadataNode("Dimension")
    dim.appendChild(horiz)
    dim.appendChild(vert)
    val root = new IIOMetadataNode("javax_imageio_1.0")
    root.appendChild(dim)
    metadata.mergeTree("javax_imageio_1.0", root)
  }
}
