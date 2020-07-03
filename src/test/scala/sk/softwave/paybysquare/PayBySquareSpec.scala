package sk.softwave.paybysquare

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class PayBySquareSpec extends AnyFlatSpec with Matchers with PayModelFixtures {

  "PayBySquare" should "properly encode SimplePay to bysquare base-32 string" in {
    val encoded = PayBySquare.encode(simplePay)
    encoded shouldEqual "0007A0006454I05LJLA49VHIM9BV860668BM348JUAL1MGQ5JCFA84UF5ALICFVT6MKN482D6MR9T47JV0BED12UFO3HQH76VPA46KC7CU8KSU00FMLESL0MSADPMS13595OMP30PKJ5E6BO7GKRL917N2ADDHOGJQ4S90QJBLUD6"
  }

  "PayBySquare" should "properly encode more complex Pay structures to bysquare base-32 string" in {
    val encoded1 = PayBySquare.encode(complexPay1)
    encoded1 shouldEqual "0008K000FQV8M48LGOT9C30S0K5MB7AKNM30OG42TO4RF5UMS9MSH9E2CLNMAS6II5AL4B843GNMBUCE388RQRET8AIJRT6H2N8F171IL31DPG16CTIN49U745SPLOEAUI0O7SOJ00D8G7Q986HT5TMGLP442QPU3IT6QQ4CSDFC81612MUF0NB9HDS1T10"

    val encoded2 = PayBySquare.encode(complexPay2)
    encoded2 shouldEqual "000020809O2RQEUI6Q6ASLUOABMNO61CSH2PPNGFS0HSQUAKIMAPUC67AIEPACHGNFDHELE1S12BR1AVPRGLSR9EKPO2LM3IOI3G1SJK4B6UHBUKJ8MK7N2SMQMLUIBG993TP0C3EHO2GR4GLFF7NV8MCJQK3P4O2B179E7A221AG4FUIMT22GCLU8AOH6M0NU1QK94OA9546EQSVK67U6DF1056QN1IOBBJ2BTAU061AEDVNBTPJ1NCP6NK7M73CMSSJVVBUSPBM6B94PH4LA80"
  }

  "Encode->Decode rountrip" should "result in same serialized string as was encoded" in {
    import PayBySquareParser._

    parse(PayBySquare.encode(simplePay)) shouldEqual simplePay.serialize
    parse(PayBySquare.encode(complexPay1)) shouldEqual complexPay1.serialize
    parse(PayBySquare.encode(complexPay2)) shouldEqual complexPay2.serialize
  }

}

object PayBySquareParser {

  import java.io._
  import java.nio.{ ByteBuffer, ByteOrder }
  import org.tukaani.xz._
  import org.apache.commons.codec.binary.Base32

  private val lc = 3
  private val lp = 0
  private val pb = 2
  private val lzmaProp = (lc + lp * 9 + pb * 9 * 5).toByte
  private val dictSizeKb = 128
  private val dictSizeB = dictSizeKb * 1024

  val base32 = new Base32(true)

  def parse(encoded: String) = {

    val decoded = base32.decode(encoded)
    val (_, lzmaCompressed) = decoded.splitAt(2)
    val (lzmaHeader, lzmaData) = lzmaCompressed.splitAt(2)

    val uncompSize = ByteBuffer.wrap(lzmaHeader).order(ByteOrder.LITTLE_ENDIAN).getShort
    val in = new ByteArrayInputStream(lzmaData)
    val lzmaIn = new LZMAInputStream(in, uncompSize, lzmaProp, dictSizeB)

    try {
      val uncompBytes = Iterator.continually(lzmaIn.read).takeWhile(_ != -1).map(_.toByte).toArray
      //parsing only for testing purposes, so we ignore checksum as we used parsed data for direct comparison with the original input
      val (_, data) = uncompBytes.splitAt(4)
      new String(data, "UTF-8")
    } finally {
      lzmaIn.close()
      in.close()
    }
  }
}
