package sk.softwave.paybysquare

import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.io.{ ByteArrayOutputStream, File, InputStream }
import java.nio.{ ByteBuffer, ByteOrder }
import java.util.zip.CRC32

import com.google.zxing.BarcodeFormat
import com.google.zxing.client.j2se.MatrixToImageWriter
import com.google.zxing.qrcode.QRCodeWriter
import javax.imageio.ImageIO
import org.apache.commons.codec.binary.Base32
import org.tukaani.xz.{ LZMA2Options, LZMAOutputStream }
import sk.softwave.helpers.ImageHelpers

trait PayBySquare {

  private val dictSizeKb = 128
  private val dictSizeB = dictSizeKb * 1024

  private val lc = 3
  private val lp = 0
  private val pb = 2
  private val lzmaProp = (lc + lp * 9 + pb * 9 * 5).toByte

  private lazy val lzmaOptions = {
    val lzmaOpts = new LZMA2Options()
    lzmaOpts.setDictSize(dictSizeB)
    lzmaOpts.setLcLp(lc, lp)
    lzmaOpts.setPb(pb)
    lzmaOpts
  }

  private lazy val base32 = new Base32(true)

  def encode(pay: Pay): String = {
    val payString = pay.serialize
    val payBytes = payString.getBytes("UTF-8")
    val crc32 = new CRC32()
    crc32.update(payBytes)
    val checksum = crc32.getValue.toInt
    val checksumBytes = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(checksum).array
    val uncompBytes = checksumBytes ++ payBytes
    val lzmaHeader = ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort(uncompBytes.size.toShort).array
    val out = new ByteArrayOutputStream()
    val lzmaOut = new LZMAOutputStream(out, lzmaOptions, false)

    try {
      lzmaOut.write(uncompBytes)
      lzmaOut.finish()
      val lzmaCompressed = lzmaHeader ++ out.toByteArray
      val toEncode = Array(0x00.toByte, 0x00.toByte) ++ lzmaCompressed
      base32.encodeToString(toEncode).replace("=", "")
    } finally {
      lzmaOut.close()
      out.close()
    }
  }

  def encodePlainQR(pay: Pay, filePath: String, size: Int = 300): Unit = {
    val qrImg = generateQR(encode(pay), size)
    val imgFile = new File(sanitizePath(filePath))
    ImageHelpers.saveAsPNG(qrImg, imgFile, 144)
  }

  def encodeFrameQR(pay: Pay, filePath: String): Unit = {
    val qrImg = generateQR(encode(pay), 300)
    val imgFile = new File(sanitizePath(filePath))

    val frameIs = getResourceAsStream("payBySquareFrame-grey.png")
    val frame = ImageIO.read(frameIs)
    val combined = new BufferedImage(320, 374, BufferedImage.TYPE_INT_ARGB)
    val g = combined.getGraphics().asInstanceOf[Graphics2D]
    g.drawImage(frame, 0, 0, null)
    g.drawImage(qrImg, 10, 10, null)
    ImageHelpers.saveAsPNG(combined, imgFile, 144)
    g.dispose()

  }

  private def getResourceAsStream(resourceName: String): InputStream = {
    val classloader = Thread.currentThread().getContextClassLoader
    classloader.getResourceAsStream(resourceName)
  }

  private def generateQR(toEncode: String, size: Int): BufferedImage = {
    val qrCodeWriter = new QRCodeWriter()
    val bitMatrix = qrCodeWriter.encode(toEncode, BarcodeFormat.QR_CODE, size, size)
    MatrixToImageWriter.toBufferedImage(bitMatrix)
  }

  private def sanitizePath(path: String): String = {
    val homeDir = Option(System.getProperty("user.home")).getOrElse("")
    if (path.startsWith("~/") && !homeDir.isEmpty)
      s"$homeDir/${path.substring(2)}"
    else
      path
  }

}

object PayBySquare extends PayBySquare
