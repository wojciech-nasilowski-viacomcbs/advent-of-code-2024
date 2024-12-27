package day12

import utils.readInput
import kotlin.math.abs

fun main() {
    //part1()
    part2()
}

private fun part1() {
    val input = readInput("Day12")
    val grouped = mutableSetOf<Coordinates>()
    val groups = mutableListOf<Set<Coordinates>>()
    for(x in 0..<input.xSize()) {
        for(y in 0..<input.ySize()) {
            val current = Coordinates(x, y)
            if (grouped.contains(current)) {
                continue
            }
            val group = mutableSetOf<Coordinates>()
            group.add(current)
            findNeighbours(current, group, input)
            if (group.isNotEmpty()) {
                grouped.addAll(group)
                groups.add(group)
            }
        }
    }
    var result = 0
    groups.forEach { group ->
        result +=  group.area() * group.perimeter(input)
    }
    println("Result 1: $result")
 }

private fun Set<Coordinates>.area() = size
private fun Set<Coordinates>.perimeter(input: List<String>): Int {
    var result = 0
    forEach { current ->
        VECTORS.forEach { v ->
            val neighbour = current + v
            if (!input.inBounds(neighbour) || input.get(neighbour) != input.get(current)) {
                result++
            }
        }
    }
    return result
}

private data class WallElement(
    val coordinate: Coordinates,
    val direction: Coordinates,
)

private fun Set<Coordinates>.sides(input: List<String>): Int {
    val wallElements =  mutableSetOf<WallElement>()
    forEach { current ->
        VECTORS.forEach { v ->
            val neighbour = current + v
            if (!input.inBounds(neighbour) || input.get(neighbour) != input.get(current)) {
                wallElements.add(WallElement(current, v))
            }
        }
    }
    var result = 0
    wallElements.groupBy { it.direction }.forEach { (direction, items) ->
        if(direction.y == 0) {
            items.groupBy { it.coordinate.x }.forEach { (_, elements) ->
                val sortedItems = elements.map{it.coordinate.y}.distinct().sorted()
                result += countSequentialDifferences(sortedItems)
            }
        } else {
            items.groupBy { it.coordinate.y }.forEach { (_, elements) ->
                val sortedItems = elements.map{it.coordinate.x}.distinct().sorted()
                result += countSequentialDifferences(sortedItems)
            }
        }

    }
    println("result: $result")
    return result
}

fun countSequentialDifferences(list: List<Int>): Int {
    if (list.isEmpty()) return 0

    var count = 1

    for (i in 1 until list.size) {
        val current = list[i]
        val prev = list[i-1]
        if (abs(current - prev) != 1) {
            count++
        }
    }

    return count
}

private fun stringRepr(
    groups: MutableList<Set<Coordinates>>,
    input: List<String>
) = groups.map {
    println("it: $it")
    input.get(it.first()) + ": " + it.map { c -> "${c.x}x${c.y}" }
}

private fun part2() {
    val input = readInput("Day12")
    val grouped = mutableSetOf<Coordinates>()
    val groups = mutableListOf<Set<Coordinates>>()
    for(x in 0..<input.xSize()) {
        for(y in 0..<input.ySize()) {
            val current = Coordinates(x, y)
            if (grouped.contains(current)) {
                continue
            }
            val group = mutableSetOf<Coordinates>()
            group.add(current)
            findNeighbours(current, group, input)
            if (group.isNotEmpty()) {
                grouped.addAll(group)
                groups.add(group)
            }
        }
    }
    var result = 0
    groups.forEach { group ->
        val repr = input.get(group.first()) + ": " + group.map { c -> "${c.x}x${c.y}" }
        println(repr)
        result +=  group.area() * group.sides(input)
    }
    println("Result 1: $result")
}

private fun findNeighbours(start: Coordinates, path: MutableSet<Coordinates>, input: List<String>) {
    val elementType = input.get(start)
    VECTORS.forEach { vector ->
        val newCoordinate = start + vector
        if (input.inBounds(newCoordinate) && !path.contains(newCoordinate) && elementType == input.get(newCoordinate)) {
            path.add(newCoordinate)
            findNeighbours(newCoordinate, path, input)
        }
    }
}


private val VECTORS = listOf (
    Coordinates(-1, 0),
    Coordinates(0, -1),
    Coordinates(1, 0),
    Coordinates(0, 1)
)

private fun List<String>.get(x: Int, y: Int): Char = this[y][x]
private fun List<String>.get(coordinates: Coordinates): Char  = this[coordinates.y][coordinates.x]
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