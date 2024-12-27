package day07

import utils.readInput

private val resultsPart1 = mutableSetOf<Calibration>()

private data class Calibration (
    val result: Long,
    val numbers: List<Long>,
)

fun main() {
    part1()
    part2()
}


private fun part1() {
    val input = readInput("Day07")
    val calibrationData = input.map { line ->
        val splitLine = line.split(": ")
        val result = splitLine[0].toLong()
        val numbers = splitLine[1].split(" ").map { it.toLong() }
        Calibration(result, numbers)
    }
    calibrationData.forEach { item ->
        dive(0, 0,  item)
    }
    //println(calibrationData)
    println("resultsPart1: ${resultsPart1.toList()}")
    println("part1 sum: ${resultsPart1.sumOf { it.result }}")
}

private fun dive(currentResult: Long,  i: Int, calibrationItem: Calibration) {
    if (currentResult > calibrationItem.result) {
        return
    }
    if (i < calibrationItem.numbers.lastIndex) {
        OPERATORS.forEach {
            when(it) {
                '+' -> {
                    dive(currentResult + calibrationItem.numbers[i],  i+1, calibrationItem)
                }
                '*' -> {
                    dive(currentResult * calibrationItem.numbers[i], i+1, calibrationItem)
                }
                '|' -> {
                    val newResult = concatenate(currentResult, calibrationItem.numbers[i])
                    dive(newResult, i+1, calibrationItem)
                }
                else -> throw IllegalArgumentException("Unknown Operator")
            }
        }
        return
    }
    if (i == calibrationItem.numbers.lastIndex && (currentResult+calibrationItem.numbers[i] == calibrationItem.result
                || currentResult*calibrationItem.numbers[i] == calibrationItem.result)
        || concatenate(currentResult, calibrationItem.numbers[i]) == calibrationItem.result
        ) {
        resultsPart1.add(calibrationItem)
    }
}

private fun concatenate(a: Long, b: Long): Long {
    return (a.toString() + b.toString()).toLong()
}

val OPERATORS = listOf('+', '*', '|')

private fun part2() {

}
