package converter

import java.math.BigDecimal
import java.math.BigInteger
import java.math.RoundingMode

fun fromDec(numDec: String, tgtBase: Int):String {
    var buffer = BigInteger(numDec)
    var numTgt = ""
    val numberToSymbolStep = 87
    if (buffer.toInt() == 0) numTgt = "0"
    while (buffer != BigInteger.ZERO) {
        if (buffer % tgtBase.toBigInteger() >= BigInteger.TEN) {
            numTgt = ((buffer % tgtBase.toBigInteger()).toInt() + numberToSymbolStep).toChar().uppercaseChar().toString() + numTgt
            buffer /= tgtBase.toBigInteger()
        } else {
            numTgt = (buffer % tgtBase.toBigInteger()).toInt().digitToChar().toString() + numTgt  // Считает остаток от деления переменной на базис системы счисления и записывает его первым символом в строке
            buffer /= tgtBase.toBigInteger() // Результат целочисленного деления переменной на базис системы счисления используется для дальнейших вычислений
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
    for (i in 1..numFractional.length) {
        pow = BigDecimal.ONE.setScale(6)
        repeat(i) {pow /= srsBase.toBigDecimal()}
        fractionalDec += numFractional[i-1].digitToInt(srsBase).toBigDecimal() * pow
    }
    var num = fractionalDec.setScale(5, RoundingMode.CEILING)
    var fractionalTgt = ""
    while (num - num.toBigInteger().toBigDecimal() != BigDecimal.ZERO) {
        num *= tgtBase.toBigDecimal()
        fractionalTgt +=  if (num.toBigInteger() < BigInteger.TEN) {
            num.toBigInteger()
        } else {
            (num.toBigInteger().toInt() + 87).toChar().uppercaseChar().toString()
        }
        if (fractionalTgt.length == 5) break
        num -= num.toBigInteger().toBigDecimal()
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
            if ("." in input) {
                numInt = input.substringBefore(".")
                numFractional = input.substringAfter(".")
                numTgtInt = fromDec(toDec(numInt, srsBase), tgtBase)
                numTgtFractional =  fractionalToTgt(numFractional, tgtBase, srsBase)
                println("Conversion result: $numTgtInt.$numTgtFractional\n")
            } else {
                numInt = input
                numTgtInt = fromDec(toDec(numInt, srsBase), tgtBase)
                println("Conversion result: $numTgtInt\n")
            }
        } while (input != "/back")
    } while (input != "/exit")
}