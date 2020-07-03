package sk.softwave.paybysquare

import java.io.FileNotFoundException

import scala.util.{ Failure, Success, Try }

object Main extends App {
  val toEncode = if (args.length > 0) args(0) else ""
  val fileName = if (args.length > 1) args(1) else ""

  val usageStr = "Usage: paybysquare <payload> <output-file>"
  val payloadUsageStr = "payload - <amount>;<currency>;<vs>;<ss>;<ks>;<reference>;<paymentNote>;<iban>;<bic>"
  val outputUsageStr = "output-file - path where output PNG should be saved"

  private def strToOpt(str: String): Option[String] = if (str.isEmpty) None else Some(str)

  if (args.length == 1 && args(0) == "--help") {
    println(usageStr)
    println(payloadUsageStr)
    println(outputUsageStr)
  } else if (toEncode.isEmpty || fileName.isEmpty) {
    println(usageStr)
    sys.exit(1)
  } else {
    val data = toEncode.replace("\\;", 0.toChar.toString).split(";").map(_.replace(0.toChar, ';'))
    if (data.size != 9) {
      println(payloadUsageStr)
      println(data.size)
      sys.exit(2)
    } else {
      Try {
        val pay = SimplePay(
          amount = BigDecimal(data(0)),
          currency = data(1),
          vs = strToOpt(data(2)),
          ss = strToOpt(data(3)),
          ks = strToOpt(data(4)),
          reference = strToOpt(data(5)),
          paymentNote = strToOpt(data(6)),
          iban = data(7),
          bic = strToOpt(data(8))
        )
        PayBySquare.encodeFrameQR(pay, fileName)
      } match {
        case Success(_) =>
        case Failure(_: FileNotFoundException) =>
          println(s"'$fileName' is an invalid path for <output-file>")
          println(payloadUsageStr)
          sys.exit(3)
        case Failure(e) =>
          println(e)
          println(payloadUsageStr)
          sys.exit(3)
      }
    }
  }
}
