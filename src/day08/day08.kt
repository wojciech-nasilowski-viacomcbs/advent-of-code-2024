package day08

import utils.readInput

fun main() {
    //part1()
    part2()
}

private fun part1() {
    val input = readInput("Day08")
    val groups = getGroups(input)
    val antinodes = mutableSetOf<Coordinates>()
    //println("Groups $groups")
    groups.forEach { (type, group) ->
        group.combinations().forEach { pair ->
            //println("Pair $pair")
            val vector = pair.first - pair.second
            //println("vector $vector")
            val candidate = pair.second - vector
            if (input.inBounds(candidate)) {
                antinodes.add(candidate)
            }
        }
    }
    println("" +antinodes.map {
        it.x.toString() +"x"+it.y.toString()
    })
    println("Result #1 ${antinodes.size}")
}

private fun part2() {
    val input = readInput("Day08")
    val groups = getGroups(input)
    val antinodes = mutableSetOf<Coordinates>()
    //println("Groups $groups")
    groups.forEach { (type, group) ->
        println("group: $group")
        group.combinations().forEach { pair ->
            println("Pair $pair")
            val vector = pair.first - pair.second
            println("Vector $vector")
            var candidate = pair.second + vector
            println("Candidate $candidate")
            while(input.inBounds(candidate)) {
                antinodes.add(candidate)
                candidate += vector
            }

        }
    }
    println("" +antinodes.map {
        it.x.toString() +"x"+it.y.toString()
    })
    println("Result #1 ${antinodes.size}")
}

private fun <T> Set<T>.combinations(): Set<Pair<T, T>>  {
    val result = mutableSetOf<Pair<T, T>>()
    forEach { a->
        forEach { b->
            if(a != b) {
                result.add(a to b)
            }
        }
    }
    return result
}

private fun getGroups(input: List<String>): Map<Char, Set<Coordinates>> {
    val groups = mutableMapOf<Char, Set<Coordinates>>()
    for(x in 0..<input.xSize()) {
        for(y in 0..<input.ySize()) {
            val element = input.get(x,y)
            if (element != '.') {
                groups[element] = groups.getOrDefault(element, emptySet()) + Coordinates(x, y)
            }
        }
    }
    return groups
}

private data class Coordinates(
    val x: Int,
    val y: Int,
) {
    operator fun plus(other: Coordinates): Coordinates {
        return Coordinates(x + other.x, y + other.y)
    }
    operator fun minus(other: Coordinates): Coordinates {
        return Coordinates(x - other.x, y - other.y)
    }
}
private fun List<String>.get(x: Int, y: Int): Char = this[y][x]
private fun List<String>.get(coordinates: Coordinates): Char  = this[coordinates.y][coordinates.x]
private fun List<String>.xSize(): Int  = first().length
private fun List<String>.ySize(): Int  = size
private fun List<String>.inBounds(coordinate: Coordinates): Boolean  =
    coordinate.x >= 0 && coordinate.x<xSize() && coordinate.y >= 0 && coordinate.y<ySize()

