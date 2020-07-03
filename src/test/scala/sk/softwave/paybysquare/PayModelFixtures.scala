package sk.softwave.paybysquare

trait PayModelFixtures {

  val simplePay = SimplePay(
    33.00,
    "EUR",
    Some("0037748920"),
    Some("1120913687"),
    Some("0308"),
    None,
    Some("esluzby orsr - 377489/2020"),
    "SK7281800000007000145308",
    Some("SPSRSKBAXXX")
  )

  val complexPay1: Pay = new Pay {
    override val invoiceId = Some("0000010345")
    override val payments = Seq(
      Payment(
        payType = 1,
        amount = 52.54,
        currency = "EUR",
        dueDate = None,
        vs = Some("0221668130"),
        ss = None,
        ks = Some("0308"),
        reference = None,
        paymentNote = Some("QR platba Orange"),
        bankAccounts = Seq(
          BankAccount("SK2911000000002628005850", Some("TATRSKBX")),
          BankAccount("SK0209000000000176084455", Some("GIBASKBX"))
        )
      )
    )
  }

  val complexPay2: Pay = new Pay {
    override val invoiceId = None
    override val payments = Seq(
      Payment(
        payType = 1,
        amount = 168.18,
        currency = "EUR",
        dueDate = None,
        vs = Some("6598280752"),
        ss = None,
        ks = Some("7777"),
        reference = None,
        paymentNote = Some("poistne za obdobie 01.06.2020 - 01.06.2021"),
        bankAccounts = Seq(
          BankAccount("SK2509000000000175126457", Some("GIBASKBX")),
          BankAccount("SK2911110000001029706001", Some("UNCRSKBX")),
          BankAccount("SK1102000000000090004012", Some("SUBASKBX")),
          BankAccount("SK3465000000000202120000", Some("POBNSKBA")),
          BankAccount("SK2056000000004804915001", Some("KOMASK2X"))
        )
      )
    )
  }

}
