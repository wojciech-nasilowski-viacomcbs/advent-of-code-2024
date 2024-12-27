package day03

import utils.readInput

fun main() {
    //part1()
    part2()
}

private fun part1() {
    val input = readInput("Day03")
    val pairs = input.toPairs()
    println(pairs)
    val result = pairs.sumOf { it.first * it.second }
    println("res #1: $result")
 }

private fun part2() {
    val input = readInput("Day03")
    val pairs = input.toPairs2()
    println(pairs)
    val result = pairs.sumOf { it.first * it.second }
    println("res #2: $result")
}


private fun List<String>.toPairs(): List<Pair<Long, Long>> {
    val pairs = mutableListOf<Pair<Long, Long>>()
    forEach {
        val regex = Regex("""mul\(([0-9]+),([0-9]+)\)""")
        val matches = regex.findAll(it)
        matches.forEach { item ->
            val elements = item.destructured.toList()
            pairs.add(elements[0].toLong() to elements[1].toLong())
        }
    }
    return pairs
}

private fun List<String>.toPairs2(): List<Pair<Long, Long>> {
    val pairs = mutableListOf<Pair<Long, Long>>()
    var enabled = true
    forEach {
        val regex = Regex("""mul\(([0-9]+),([0-9]+)\)|do\(\)|don't\(\)""")
        val matches = regex.findAll(it)
        matches.forEach { item ->
            when {
                item.value == "do()" -> enabled = true
                item.value == "don't()" -> enabled = false
                item.value.startsWith("mul(") && enabled -> {
                    val elements = item.destructured.toList()
                    pairs.add(elements[0].toLong() to elements[1].toLong())
                }
            }
        }
    }
    return pairs
}
