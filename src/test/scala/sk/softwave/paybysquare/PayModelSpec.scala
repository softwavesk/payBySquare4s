package sk.softwave.paybysquare

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class PayModelSpec extends AnyFlatSpec with Matchers with PayModelFixtures {

  "SimplePay" should "create proper Pay data structure" in {
    val testPay = simplePay

    testPay.invoiceId shouldBe None
    testPay.payments.size shouldBe 1
    testPay.payments(0).bankAccounts.size shouldBe 1
    testPay.payments(0).dueDate shouldBe None
    testPay.payments(0).payType shouldBe 1
    val expectedSerialized =
      "|1|1|33.00|EUR||0037748920|0308|1120913687||esluzby orsr - 377489/2020|1|SK7281800000007000145308|SPSRSKBAXXX|0|0".replace("|", "\t")
    testPay.serialize shouldEqual expectedSerialized
  }

  "Complex Pay" should "create proper serialization string" in {
    val testPay1 = complexPay1
    val testPay2 = complexPay2

    val expectedSerialized1 =
      "0000010345|1|1|52.54|EUR||0221668130|0308|||QR platba Orange|2|SK2911000000002628005850|TATRSKBX|SK0209000000000176084455|GIBASKBX|0|0"
        .replace("|", "\t")
    val expectedSerialized2 =
      "|1|1|168.18|EUR||6598280752|7777|||poistne za obdobie 01.06.2020 - 01.06.2021|5|SK2509000000000175126457|GIBASKBX|SK2911110000001029706001|UNCRSKBX|SK1102000000000090004012|SUBASKBX|SK3465000000000202120000|POBNSKBA|SK2056000000004804915001|KOMASK2X|0|0"
        .replace("|", "\t")

    testPay1.serialize shouldEqual expectedSerialized1
    testPay2.serialize shouldEqual expectedSerialized2
  }
}
