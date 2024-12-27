package day6

import utils.readInput


fun main() {

    val input = readInput("Day06")
    val part1Result = part1(input).size
    println("Part1 result: $part1Result")
    val part2Result = part2(input)
    println("Part2 result: $part2Result")
}


private fun part1(input: List<String>, block: Coordinates? = null): Set<Coordinates> {
    var count = 0
    var current = findDevice(input)
    var direction: Char = input.get(current)
    val visited = mutableSetOf<Coordinates>()
    while(true) {
        //println("position: $x - $y")
        visited.add(current)
        val next = current + direction.toVector()

        if (!input.inBounds(next)) {
            return visited
        }
        if (input.get(next) == '#' || block == next) {
            direction = direction.turn()
            //println("turning to $direction")
        } else {
            current = next
        }
        count++
        if (count> 10*(input.size) * input.first().length) {
            return emptySet()
        }
    }
}

fun part2(input: List<String>): Int {
    var current = findDevice(input)
    val visitedItems = part1(input) - current

    var count = 0
    visitedItems.forEach { block ->
        if (part1(input, block).isEmpty() ) {
            count++
        }
    }
    return count
}

private fun findDevice(input: List<String>):Coordinates {
    var y = 0
    while (y < input.size) {
        val line = input[y]
        var x = 0
        while(x < line.length) {
            if (listOf('<', '>', '^', 'v').contains(line[x])) {
                return Coordinates(x,  y)
            }
            x++
        }
        y++
    }
    throw IllegalArgumentException("Not found")
}


private fun Char.toVector() = when(this) {
    '^' -> Coordinates(0, -1)
    'v' -> Coordinates(0, 1)
    '<' -> Coordinates(-1, 0)
    '>' -> Coordinates(1, 0)
    else -> throw IllegalArgumentException("No such direction")
}

private data class Coordinates(
    val x: Int,
    val y: Int,
) {
    operator fun plus(other: Coordinates): Coordinates {
        return Coordinates(x + other.x, y + other.y)
    }
}

private fun Char.turn() = when(this) {
    '^' -> '>'
    '>' -> 'v'
    'v' -> '<'
    '<' -> '^'
    else -> throw IllegalArgumentException("No such direction")
}

private fun List<String>.get(x: Int, y: Int): Char = this[y][x]
private fun List<String>.get(coordinates: Coordinates): Char  = this[coordinates.y][coordinates.x]
private fun List<String>.xSize(): Int  = first().length
private fun List<String>.ySize(): Int  = size
private fun List<String>.inBounds(coordinate: Coordinates): Boolean  =
    coordinate.x >= 0 && coordinate.x<xSize() && coordinate.y >= 0 && coordinate.y<ySize()
