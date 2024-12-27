package day19

import utils.readInput
import kotlin.math.pow

fun main() {
    //part1()
    part2()
}

private fun part1() {
    val input = readInput("Day19")
    val towels: List<String> = input[0].split(", ")
    val patterns = input.drop(2)
    println("Result #1: ")
    val possible = mutableSetOf<String>()
    var i = 0
    for (pattern in patterns) {
        println("Checking item ${i++} - $pattern")
         if (checkPattern(pattern, towels)) {
             possible.add(pattern)
         }
    }
    println("possible: $possible")
    println("result #1: ${possible.size}")
}

private fun part2() {
    val input = readInput("Day19")
    val towels: List<String> = input[0].split(", ")
    val patterns = input.drop(2)
    var possiblePatterns = 0L

    for (pattern in patterns) {
        //println("Checking item ${i++} - $pattern")
        possiblePatterns += countPatterns(pattern, towels)
    }
    println("result #2: $possiblePatterns")
}


private fun countPatterns(patternLeft: String, towels: List<String>): Long {
    if (patternLeft.isEmpty()) {
        return 1
    }
    val prefixes = prefixesMemo.getOrPut(patternLeft) {
        getPrefixes(patternLeft, towels)
    }
    //println("prefixes: $prefixes")
    if (prefixes.isEmpty()) {
        return 0
    }
    return prefixes.sumOf {
        memoCounter.getOrPut(patternLeft.substring(it.length)) {
            countPatterns(patternLeft.substring(it.length), towels)
        }
    }
}

val memoCounter = mutableMapOf<String, Long>()
val memo = mutableMapOf<String, Boolean>()

private fun checkPattern(patternLeft: String, towels: List<String>): Boolean {
    if (patternLeft.isEmpty()) {
        return true
    }
    val prefixes = prefixesMemo.getOrPut(patternLeft) {
        getPrefixes(patternLeft, towels)
    }
    //println("prefixes: $prefixes")
    if (prefixes.isEmpty()) {
        return false
    }
    return prefixes.map {
        memo.getOrPut(patternLeft.substring(it.length)) {
            checkPattern(patternLeft.substring(it.length), towels)
        }
    }.any { it }
}

private val prefixesMemo = mutableMapOf<String, Set<String>>()

private fun getPrefixes(patternLeft: String, towels: List<String>): Set<String> {
    val prefixes = mutableSetOf<String>()
    for (towel in towels) {
        if (patternLeft.startsWith(towel)) {
            prefixes.add(towel)
        }
    }
    return prefixes
}


