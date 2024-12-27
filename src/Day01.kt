import utils.println
import utils.readInput
import kotlin.math.abs

fun main() {
    fun part1() {
        val input = readInput("Day01")
        val left = mutableListOf<Int>()
        val right = mutableListOf<Int>()
        input.forEach {line ->
            val (leftItem, rightItem) = line.split("   ").map { it.toInt() }
            left.add(leftItem)
            right.add(rightItem)
        }
        println("left: $left")
        println("right: $right")
        left.sort()
        right.sort()
        var result = 0
        for (i in 0..<left.size) {
            result += abs(left[i] - right[i])
        }
        println("result #1: $result")
    }

    fun part2() {
        val input = readInput("Day01")
        val left = mutableListOf<Int>()
        val right = mutableListOf<Int>()
        input.forEach {line ->
            val (leftItem, rightItem) = line.split("   ").map { it.toInt() }
            left.add(leftItem)
            right.add(rightItem)
        }
        var result = 0
        left.forEach { leftValue ->
            val count = right.count { it == leftValue }
            val score = count * leftValue
            result += score
        }
        println("result #2: $result")
    }

    //part1()
    part2()
}
