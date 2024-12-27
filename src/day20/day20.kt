package day18

import utils.readInput
import java.util.*
import kotlin.math.abs

fun main() {
    val input = readInput("Day20")
    //part1(input)
    part2(input)
}
private val distances = mutableMapOf<Pair<Coordinates, Coordinates>, Int>()

fun part1(input: List<String>) {
    val minSaving = 100
    val start = find('S', input)
    val end = find('E', input)
    println("start: $start")
    println("end: $end")
    val fairPath = findPath(input, start, end)
    val fairPathLength = fairPath.size
    /*input.forEachIndexed{ y, line ->
        line.forEachIndexed { x, point ->
            if (point != '#') {
                val current = Coordinates(x,y)
                distancesFromStart[current] = findPath(input, start, current).size
            }
        }
    }*/
    val cheats = (fairPath + start).map { node ->
        input.getCheats(node)
    }.flatten()
    val cheatsSavings = mutableMapOf<Int ,Int>()
    for (cheat in cheats) {
        println("cheat: $cheat")
        //println("cheat: ${findPath(input, start, cheat.first())}")
        val costBeforeCheat = distance(input, start, cheat.first())
        //println("costBeforeCheat: $costBeforeCheat")
        val costAfterCheat = distance(input, cheat[1], end)
        //println("costAfterCheat: $costAfterCheat")
        val fullCost = costBeforeCheat + 2 + costAfterCheat
        //println("fairPathLength: $fairPathLength")
        //println("fullCost: $fullCost")

        val saving = fairPathLength - fullCost
        //println("saving: $saving")

        if (saving >= minSaving) {
            cheatsSavings[saving] = cheatsSavings.getOrDefault(saving, 0) + 1
        }
    }
    val result = cheatsSavings.filterKeys { it >= minSaving }.values.sum()
    println("Result #1: $result")
}


fun part2(input: List<String>) {
    val minSaving = 100
    val cheatDuration = 20
    val start = find('S', input)
    val end = find('E', input)
    println("start: $start")
    println("end: $end")
    val fairPath = findPath(input, start, end)
    val fairLength = fairPath.size
    println("fairLength: $fairLength")
    val itemsToCheck = listOf(start) + fairPath
    val distances = mutableMapOf<Pair<Coordinates, Coordinates>, Int>()
    itemsToCheck.forEachIndexed { i, point ->
        distances[start to point] = i
        distances[point to end] = itemsToCheck.size - i - 1
    }
    //println(distances)
    var result = 0
    val cheatsSavings = mutableMapOf<Int ,Int>()
    for (startCheat in itemsToCheck) {
        for (endCheat in itemsToCheck) {
            val distance = abs(startCheat.x-endCheat.x) + abs(startCheat.y-endCheat.y)
            if (distance <= cheatDuration) {
                val newLength = distances[start to startCheat]!! + distance + distances[endCheat to end]!!
                val saving = fairLength - newLength
                if (saving >= minSaving) {
                    cheatsSavings[saving] = cheatsSavings.getOrDefault(saving, 0) + 1
                    //println("Saving: $saving for ${startCheat.x}-${startCheat.y} to ${endCheat.x}-${endCheat.y}.")
                        result++
                }
            }
        }
    }
    println("Savings: $cheatsSavings")
    println("Result #2: $result")
}


private fun distance(input: List<String>, start: Coordinates, end: Coordinates): Int {
    return distances.getOrPut(start to end) {
        findPath(input, start, end).size
    }
}

private fun findPath(input: List<String>, start: Coordinates, end: Coordinates): List<Coordinates> {
    val queue = mutableListOf(start)
    val parents = mutableMapOf<Coordinates, Coordinates>()
    val distancesFromStart = mutableMapOf<Coordinates, Int>()
    distancesFromStart[start] = 0
    while (queue.isNotEmpty()) {
        val current = queue.removeFirst()
        if (current == end) {
            val path = getPath(start, end, parents)
            return path
        }
        //println("checking $current")
        current.neighbors(input).forEach { newPosition ->
            if(input.get(newPosition) != '#' && !parents.contains(newPosition)) {
                distancesFromStart[newPosition] = distancesFromStart[current]!! + 1
                queue.add(newPosition)
                parents[newPosition] = current
            }
        }
    }
    return emptyList()
}

private fun Coordinates.neighbors(input: List<String>): List<Coordinates> {
    val neighbors = mutableListOf<Coordinates>()
    VECTORS.forEach { vector ->
        val newPosition = this + vector
        if (input.inBounds(newPosition)) {
            neighbors.add(newPosition)
        }
    }
    return neighbors
}

private fun getPath(
    start: Coordinates,
    end: Coordinates,
    parents: Map<Coordinates, Coordinates>
): List<Coordinates> {
    val path = mutableListOf<Coordinates>()
    var i = 0L
    var current = end
    while (current != start) {
        i++
        path.add(current)
        current = parents[current]!!
    }
    return path.reversed()
}

private data class Coordinates(
    val x: Int,
    val y: Int,
) {
    operator fun plus(other: Coordinates): Coordinates {
        return Coordinates(x + other.x, y + other.y)
    }
}

private fun List<String>.inBounds(coordinate: Coordinates): Boolean =
    coordinate.x >= 0 && coordinate.x < xSize() && coordinate.y >= 0 && coordinate.y < ySize()

private fun List<String>.get(x: Int, y: Int): Char = this[y][x]
private fun List<String>.get(coordinates: Coordinates): Char = this[coordinates.y][coordinates.x]
private fun List<String>.xSize(): Int = first().length
private fun List<String>.ySize(): Int = size

private fun List<String>.getCheats(lastPointCoordinates: Coordinates): Set<List<Coordinates>> {
    val cheats = mutableSetOf<List<Coordinates>>()
    VECTORS.forEach { firstMove ->
        val wallToRemoveCandidate =  lastPointCoordinates + firstMove
        if (get(wallToRemoveCandidate) == '#' && inBounds(wallToRemoveCandidate)) {
            VECTORS.forEach { secondMove ->
                val backOnTrackCandidate = wallToRemoveCandidate + secondMove
                if (lastPointCoordinates != backOnTrackCandidate && inBounds(backOnTrackCandidate) && get(backOnTrackCandidate) != '#') {
                    cheats.add(listOf(lastPointCoordinates, backOnTrackCandidate))
                }
            }
        }
    }
    return cheats
}

private fun find(el: Char, input: List<String>): Coordinates {
    var y = 0
    while (y < input.size) {
        val line = input[y]
        var x = 0
        while (x < line.length) {
            if (el == line[x]) {
                return Coordinates(x, y)
            }
            x++
        }
        y++
    }
    throw IllegalArgumentException("Not found")
}


private val VECTORS =
    listOf(
        Coordinates(0, 1),
        Coordinates(1, 0),
        Coordinates(0, -1),
        Coordinates(-1, 0),
    )
//977665