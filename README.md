# payBySquare4s

Independent Scala library for Slovak PayBySquare QR payment standard. 

This standard was (probably on purpose) made overly complex, with no apparent benefits, and as a result most of users had to rely on not-so-cheap proprietery solutions.
This library aims to solve this problem for Scala based projects (and, with some effort, possibly for Java, Kotlin and other JVM-based languages too)

Supports both Scala 2.12 and 2.13

## Getting started

In `build.sbt`, add the JitPack resolver and the [latest library dependency](https://jitpack.io/#sk.softwave/paybysquare4s):

```scala
resolvers += "jitpack" at "https://jitpack.io"
libraryDependencies += "sk.softwave" %% "paybysquare4s" % "1.0.1"
```

## Usage

PayBySquare QR code with the official frame:

```scala
val pay = SimplePay(
          amount = 33.00,
          currency = "EUR",
          vs = Some("0037748920"),
          ss = Some("1120913687"),
          ks = Some("0308"),
          reference = None,
          paymentNote = Some("esluzby orsr - 377489/2020"),
          iban = "SK7281800000007000145308",
          bic = Some("SPSRSKBAXXX")
        )
        
PayBySquare.encodeFrameQR(pay, "/path/to/store/qr-file.png")        
```

Plain QR code without frame:

```scala
PayBySquare.encodePlainQR(pay, "/path/to/store/qr-file.png") 

//or with explicit size param (default == 300)
PayBySquare.encodePlainQR(pay, "/path/to/store/qr-file.png", size = 250) 
```

Plain PayBySquare string (to be used in your own QR generator for example):

```scala
val pbs = PayBySquare.encode(pay) 
```

`SimplePay` allows generating single payment with single bank account. Mandatory params are `amount`, `currency` and `iban`. 
It is also advised that either `vs`+`ss`+`ks` combination or `reference` is used and not both at the same time, but it is not enforced. If both are used, it is 
in general undefined what takes preference (up to a bank that will scan the QR code)

For more complex payments (eg. if choice of mulitple bank accounts is to be offered), `Pay` trait needs to be extended *(note: I might provide some more straight forward case class for this purpose if there will be an interest)*
