package day10

import utils.readInput

fun main() {
    val input = readInput("Day10")
    part1(input)
    part2(input)
}

private fun List<String>.get(x: Int, y: Int): Int  = this[y][x].code - '0'.code
private fun List<String>.get(coordinates: Coordinates): Int  = this[coordinates.y][coordinates.x].code - '0'.code
private fun List<String>.xSize(): Int  = first().length
private fun List<String>.ySize(): Int  = size
private fun List<String>.inBounds(coordinate: Coordinates): Boolean  =
    coordinate.x >= 0 && coordinate.x<xSize() && coordinate.y >= 0 && coordinate.y<ySize()


private data class Coordinates(
    val x: Int,
    val y: Int,
) {
    operator fun plus(other: Coordinates): Coordinates {
        return Coordinates(x + other.x, y + other.y)
    }
}

private fun List<String>.find(value: Int): List<Coordinates>  {
    val result = mutableListOf<Coordinates>()
    forEachIndexed { y, line ->
        line.forEachIndexed {x, element ->
            val elementValue = element.code - '0'.code
            if (elementValue == value) {
                result.add(Coordinates(x, y))
            }
        }
    }
    return result
}

private fun part1(input: List<String>): Int {
    score.clear()
    rating.clear()
    input.find(0).forEach {
        moveOn(input, it, it)
    }
    val result = score.values.sum()
    //println(reachedCounter)
    println("part1: $result")
    return result
}

private fun part2(input: List<String>): Int {
    score.clear()
    rating.clear()
    input.find(0).forEach {
        moveOn(input, it, it)
    }
    val result = rating.values.sum()
    //println(rating)
    println("part2: $result")
    return result
}

private val score = mutableMapOf<Coordinates, Int>()
private val rating = mutableMapOf<Pair<Coordinates, Coordinates>, Int>()

private val VECTORS = listOf (
    Coordinates(-1, 0),
    Coordinates(0, -1),
    Coordinates(1, 0),
    Coordinates(0, 1)
)
private val trackSaved = mutableSetOf<Pair<Coordinates, Coordinates>>()

private fun moveOn(input: List<String>, start: Coordinates, current: Coordinates, history: List<Coordinates> = emptyList()) {
    //println("visiting in $current")
    if (input.get(current) == 9) {
        if(trackSaved.add(start to current)) {
            score[start] = score.getOrDefault(start, 0) + 1
        }
        rating[start to current] = rating.getOrDefault(start to current, 0) + 1
        //println("history: ${history.map {"${it.x}x${it.y}"} }")
        //println("Finished $start -> $current")
        return
    }
    VECTORS.forEach { vector ->
        val newCoordinate = vector + current
        if (input.inBounds(newCoordinate) && input.get(newCoordinate) - input.get(current) == 1) {
            moveOn(input, start, newCoordinate, history+current)
        }
    }
}


