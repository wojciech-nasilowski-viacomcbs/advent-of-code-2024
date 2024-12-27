package day25

import utils.readInput
import java.util.*

fun main() {
    val input = readInput("Day25")
    part1(input)
    //part2(input)
}

fun part1(input: List<String>) {
    val (locks, keys, maxHeight) = input.toHeights()
    val matched =  mutableSetOf<Pair<List<Int>, List<Int>>>()
    locks.forEach { lock ->
        keys.forEach { key ->
            var fits = true
            for(i in lock.indices) {
                if(key[i] + lock[i] >= maxHeight - 1) {
                    fits = false
                    break
                }
            }
            if (fits) {
                println("matched $key to $lock $maxHeight")
                matched.add(key to lock)
            }
        }
    }
    println(matched)
    println("Result #1: ${matched.size}")
}

private fun List<String>.toHeights(): Triple<List<List<Int>>, List<List<Int>>, Int> {
    var keyHeight = 0
    val locks = mutableListOf<List<Int>>()
    val keys = mutableListOf<List<Int>>()
    while (this[keyHeight].isNotEmpty()) {
        keyHeight++
    }
    var lineCursor = 0
    while (lineCursor < size) {
        val isKey = this[lineCursor][0] == '.'
        val expectedChar = if (isKey) {
            '#'
        } else {
            '.'
        }
        val heights = mutableListOf<Int>()
        for (columnIndex in 0..<first().length) {
            var height = 0
            for (rowIndex in lineCursor..<lineCursor + keyHeight) {
                if (this[rowIndex][columnIndex] == expectedChar) {
                    height = rowIndex - lineCursor
                    break
                }
            }
            if(isKey) {
                heights.add(keyHeight - height - 1)
            } else {
                heights.add(height - 1)
            }
        }
        if (isKey) {
            keys.add(heights)
        } else {
            locks.add(heights)
        }
        lineCursor += keyHeight + 1
    }
    return Triple(locks,keys, keyHeight)
}