package day02

import utils.readInput
import kotlin.math.abs
import kotlin.math.sign

fun main() {
    //part1()
    part2()
}

private fun part1() {
    val input = readInput("Day02")
    val numbers = input.toNumbers()
    println(numbers)
    var count = 0
    for(row in numbers) {
        var safe = true
        if (row.size > 2) {
            val firstDiff = row[1]-row[0]
            //println("firstDiff: $firstDiff")
            for (i in 0..row.size-2) {
                val diff = row[i+1]-row[i]
                //println("diff: $diff")
                if (diff.sign != firstDiff.sign || abs(diff) < 1 || abs(diff)> 3) {
                    safe = false
                    break
                }
            }
        }
        if (safe) {
            count++
        }
    }
    println("Result #1: $count")
 }

private fun part2() {
    val input = readInput("Day02")
    val numbers = input.toNumbers()
    println(numbers)
    var count = 0
    for(row in numbers) {
        val safe = isSafe(row)
        if (safe) {
            count++
        } else {
            //println("row: $row")
            for (i in row.indices) {
                val candidate = row.toMutableList()
                candidate.removeAt(i)
                //println("candidate: $candidate, because removed $removeCandidate")
                if (isSafe(candidate)) {
                    count++
                    break
                }
            }
        }
    }
    println("Result #2: $count")}

private fun isSafe(row: List<Int>): Boolean {
    var safe = true
    if (row.size > 2) {
        val firstDiff = row[1] - row[0]
        for (i in 0..row.size - 2) {
            val diff = row[i + 1] - row[i]
            if (diff.sign != firstDiff.sign || abs(diff) < 1 || abs(diff) > 3) {
                safe = false
                break
            }
        }
    }
    return safe
}

fun List<String>.toNumbers(): List<List<Int>> {
    val rows = mutableListOf<List<Int>>()
    forEach { line ->
        val columns = mutableListOf<Int>()
        line.split(" ").forEach {
            columns.add(it.toInt())
        }
        rows.add(columns)
    }
    return rows
}
