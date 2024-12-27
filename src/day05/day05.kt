package day14

import utils.readInput

fun main() {
    //part1()
    part2()
}

private val xSize = 101L
private val ySize = 103L


private fun part1() {
    val input = readInput("Day14")
    val robots = input.toRobots()
    val n = 100L
    robots.forEach { robot ->
        robot.move(n, xSize, ySize)
    }
    val map = mutableMapOf<Coordinates, Long>()
    robots.forEach { robot ->
        val middleX = xSize / 2
        val middleY = ySize / 2
        if (robot.position.x != middleX && robot.position.y != middleY) {
            map[robot.position] = map.getOrDefault(robot.position, 0) + 1
        }
    }
    val quadrants = mutableMapOf<Int, Long>()
    map.forEach { (position, count) ->
        val q = position.toQuadrant(xSize, ySize)
        quadrants[q] = quadrants.getOrDefault(q,0) + count
    }
    val result = quadrants.values.reduce(Long::times)
    println("Result (1): $result")
 }

private fun Coordinates.toQuadrant(xSize: Long, ySize: Long): Int {
    val middleX = xSize / 2
    val middleY = ySize / 2
    return when {
        x < middleX && y < middleY -> 1
        x > middleX && y < middleY -> 2
        x < middleX && y > middleY -> 3
        x > middleX && y > middleY -> 4
        else -> throw IllegalArgumentException("Shouldn't check this value")
    }
}

private fun part2() {
    val input = readInput("Day14")
    val robots = input.toRobots()
    var result = 0
    repeat(10000) { n ->
        robots.forEach { robot ->
            robot.move(1, xSize, ySize)
        }
        //println("N: $n")
        val map = mutableMapOf<Coordinates, Long>()
        robots.forEach { robot ->

            map[robot.position] = map.getOrDefault(robot.position, 0) + 1
        }
        if (map.values.none { it > 1 }) {
            println("Most dispersion for: ${n+1}")
            result = n+1
            map.print()
        }
    }

    println("Result (2): $result")

}

private fun Map<Coordinates, Long>.print() {
    for (x in 0..<xSize) {
        for (y in 0..<xSize) {
            if (getOrDefault(Coordinates(x,y),0) > 0) {
                print("x")
            } else {
                print(".")
            }
        }
        println()
    }
}


private data class Coordinates(
    val x: Long,
    val y: Long,
) {
    operator fun plus(other: Coordinates): Coordinates {
        return Coordinates(x + other.x, y + other.y)
    }
    operator fun times(scalar: Long): Coordinates {
        return Coordinates(scalar * x, scalar * y)
    }

    fun normalize(xSize: Long, ySize: Long): Coordinates {
        val newXCand = x % xSize
        val newYCand = y % ySize
        val newX = if(newXCand < 0) {
            xSize + newXCand
        } else {
            newXCand
        }
        val newY = if(newYCand < 0) {
            ySize + newYCand
        } else {
            newYCand
        }
        return Coordinates(newX, newY)
    }
}

private data class Robot(
    var position: Coordinates,
    val v: Coordinates,
) {
    fun move(n: Long, xSize: Long, ySize: Long) {
        position += v * n
        position = position.normalize(xSize, ySize)
    }
}

private fun extractNumbers(input: String): List<Long> {
    val regex = """p=(-?\d+),(-?\d+) v=(-?\d+),(-?\d+)""".toRegex()
    val matchResult = regex.matchEntire(input)

    return matchResult?.destructured?.let { (px, py, vx, vy) ->
        listOf(px.toLong(), py.toLong(), vx.toLong(), vy.toLong())
    } ?: emptyList()
}

private fun List<String>.toRobots() : List<Robot>{
    val robots = mutableListOf<Robot>()
    forEach {
        val (px, py, vx, vy) = extractNumbers(it)
        robots.add(Robot(Coordinates(px, py), Coordinates(vx, vy)))
    }
    return robots
}