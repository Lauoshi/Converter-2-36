package converter

import java.math.BigDecimal
import java.math.BigInteger
import java.math.RoundingMode

fun fromDec(num: String, tgtBase: Int):String {
    var numDec = BigInteger(num)
    var numTgt = ""
    val numberToSymbolStep = 87                   // In Unicode 'a' code equal 97, so to get 'a' from number 10 you should add this step == 87. This is also true for subsequent characters
    if (numDec.toInt() == 0) numTgt = "0"
    while (numDec != BigInteger.ZERO) {
        if (numDec % tgtBase.toBigInteger() >= BigInteger.TEN) {
            numTgt = ((numDec % tgtBase.toBigInteger()).toInt() + numberToSymbolStep).toChar().uppercaseChar().toString() + numTgt
            numDec /= tgtBase.toBigInteger()
        } else {
            numTgt = (numDec % tgtBase.toBigInteger()).toInt().digitToChar().toString() + numTgt
            numDec /= tgtBase.toBigInteger()
        }
    }
    return numTgt
}

fun toDec(numSrs: String, srsBase: Int):String {
    val numRadix = numSrs.reversed().toMutableList()
    var numDec = BigInteger.ZERO
    var pow: BigInteger
    for (i in numRadix.indices) {
        pow = BigInteger.ONE
        repeat(i) {pow *= srsBase.toBigInteger()}
        numDec += numRadix[i].digitToInt(srsBase).toBigInteger() * pow
    }
    return numDec.toString()
}

fun fractionalToTgt (numFractional: String, tgtBase: Int, srsBase: Int): String {
    var fractionalDec = BigDecimal.ZERO
    var pow: BigDecimal
    for (i in 1..numFractional.length) {          // Converting from Sourse base to decimal
        pow = BigDecimal.ONE.setScale(6)
        repeat(i) {pow /= srsBase.toBigDecimal()}
        fractionalDec += numFractional[i-1].digitToInt(srsBase).toBigDecimal() * pow
    }
    var num = fractionalDec.setScale(5, RoundingMode.CEILING)
    var fractionalTgt = ""
    val numberToSymbolStep = 87    // Using the same step for the fractional part
    while (num - num.toBigInteger().toBigDecimal() != BigDecimal.ZERO) {      // Converting from decimal to Target base
        num *= tgtBase.toBigDecimal()
        fractionalTgt +=  if (num.toBigInteger() < BigInteger.TEN) {
            num.toBigInteger()
        } else {
            (num.toBigInteger().toInt() + numberToSymbolStep).toChar().uppercaseChar().toString()
        }
        if (fractionalTgt.length == 5) break
        num -= num.toBigInteger().toBigDecimal()
    }
    while (fractionalTgt.last() == '0') {
        fractionalTgt = fractionalTgt.substring(0, fractionalTgt.length - 1)  // Cut '0' at the end of the number
    }
    return  fractionalTgt
}

fun main() {
    var numTgtInt:String
    var numTgtFractional: String
    var numInt: String
    var numFractional: String
    var input: String
    do {
        print("Enter two numbers in format: {source base} {target base} (To quit type /exit) > ")
        input = readLine()!!
        if (input == "/exit") break
        val (srsBase, tgtBase) = input.split(" ").map { it.toInt() }
        do {
            print("Enter number in base $srsBase to convert to base $tgtBase (To go back type /back) > ")
            input = readLine()!!
            if (input == "/back") break
            if ("." in input) {                   // If number has fractional part
                numInt = input.substringBefore(".")
                numFractional = input.substringAfter(".")
                numTgtInt = fromDec(toDec(numInt, srsBase), tgtBase)
                numTgtFractional =  fractionalToTgt(numFractional, tgtBase, srsBase)
                println("Conversion result: $numTgtInt.$numTgtFractional\n")
            } else {
                numInt = input                   // If number is Integer
                numTgtInt = fromDec(toDec(numInt, srsBase), tgtBase)
                println("Conversion result: $numTgtInt\n")
            }
        } while (input != "/back")
    } while (input != "/exit")
}