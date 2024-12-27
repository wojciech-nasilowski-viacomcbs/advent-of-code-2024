package day22

import utils.readInput
import java.util.*

fun main() {
    val input = readInput("Day22")
    //part1(input)
    part2(input)
}

fun part1(input: List<String>) {
    val secretNumbers = input.map {it.toLong()}
    var resultSecretNumbers = secretNumbers
    val n = 2000
    repeat(n) {
        resultSecretNumbers = resultSecretNumbers.map { secretNumber ->
            calculate(secretNumber)
        }
        println("Secrets after ${it+1}: $resultSecretNumbers")
    }

    val result = resultSecretNumbers.sum()
    println("Result #1: $result")
}

val memoCalculation = mutableMapOf<Long, Long>()

private fun calculate(secretNumber: Long): Long {
    return memoCalculation.getOrPut(secretNumber) {
        val multiplied = secretNumber * 64
        val secretNumber2 = prune(mix(multiplied, secretNumber))
        val divided = secretNumber2 / 32
        val secretNumber3 = prune(mix(divided, secretNumber2))
        val multiplied2 = 2048 * secretNumber3
        val secretNumber4 = prune(mix(multiplied2, secretNumber3))
        secretNumber4
    }
}

private fun mix(input: Long, secretNumber: Long): Long {
    return input xor secretNumber
}


private fun prune(input: Long): Long {
    return input % 16777216
}

fun part2(input: List<String>) {
    val secretNumbers = input.map {it.toLong()}

    var resultSecretNumbers = secretNumbers
    var prev: List<Long>
    val n = 2000
    val currentPrices = resultSecretNumbers.map {
        mutableListOf(it % 10)
    }.toMutableList()
    val diffs = resultSecretNumbers.map {
        mutableListOf<Long>()
    }.toMutableList()
    repeat(n) {
        prev = resultSecretNumbers
        resultSecretNumbers = resultSecretNumbers.mapIndexed {i,  secretNumber ->
            val newSecretNumber = calculate(secretNumber)
            val lastDigit = (newSecretNumber % 10)
            val prevLastDigit = prev[i] % 10
            diffs[i].add(lastDigit - prevLastDigit)
            currentPrices[i].add(lastDigit)
            println(newSecretNumber)
            newSecretNumber
        }
        //println("Secrets after ${it+1}: $resultSecretNumbers")
    }
    println("currentPrices: $currentPrices")
    println("diffs: $diffs")
    val sequencePrices = mutableMapOf<List<Long>, Long>()

    for (i in currentPrices.indices) {
        val visitedSequences = mutableSetOf<List<Long>>()
        diffs[i].windowed(4).forEachIndexed { j, sequence ->
            //println("sequence: $sequence")
            if (visitedSequences.add(sequence) && j+4 < currentPrices[i].size) {
                sequencePrices[sequence] = sequencePrices.getOrZero(sequence) + (currentPrices[i][j+4])
            }
        }
    }

    println("sequencePrices: $sequencePrices")
    val result = sequencePrices.values.max()
    val key = sequencePrices.maxByOrNull { it.value }?.key
    println("Result #2: $result for $key")
}

fun Map<List<Long>, Long>.getOrZero(key: List<Long>,) = getOrDefault(key, 0)