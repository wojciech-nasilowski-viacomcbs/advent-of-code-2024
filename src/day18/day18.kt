package day

import utils.readInput
import java.util.*

fun main() {
    val input = readInput("day18/Day18")
    // part1(input)
    // part2(input)
    // part11(input)
    part22(input)
}

fun part1(input: List<String>) {
    val size = 71
    val bytes = 1024
    val obstacles =
            input
                    .map { line ->
                        val lineElements = line.split(",")
                        val x = lineElements[0].toInt()
                        val y = lineElements[1].toInt()
                        Coordinates(x, y)
                    }
                    .take(bytes)
    val graph = Graph<Coordinates>()
    for (x in 0 ..< size) {
        for (y in 0 ..< size) {
            val startNode = Coordinates(x, y)
            if (!obstacles.contains(startNode) && startNode.inBounds(size)) {
                VECTORS.forEach { moveDirection ->
                    val endNode = startNode + moveDirection
                    if (!obstacles.contains(endNode) && endNode.inBounds(size)) {
                        graph.addEdge(startNode, endNode, 1)
                    }
                }
            }
        }
    }
    val start = Coordinates(0, 0)
    val end = Coordinates(size - 1, size - 1)

    val distances = dijkstra(graph.edges, start)
    println("res: ${distances[end]}")
}

fun part2(input: List<String>) {

    val size = 71
    val bytes = 1024
    val obstacles =
            input.map { line ->
                val lineElements = line.split(",")
                val x = lineElements[0].toInt()
                val y = lineElements[1].toInt()
                Coordinates(x, y)
            }
    for (i in 1024 ..< obstacles.size) {
        val slicedObstacles = obstacles.take(i)
        val graph = Graph<Coordinates>()
        println("checking for $i on ${obstacles.size}")
        val percent = 100.0 * i.toDouble() / (obstacles.size).toDouble()
        println("$percent%")
        for (x in 0 ..< size) {
            for (y in 0 ..< size) {
                val startNode = Coordinates(x, y)
                if (!slicedObstacles.contains(startNode) && startNode.inBounds(size)) {
                    VECTORS.forEach { moveDirection ->
                        val endNode = startNode + moveDirection
                        if (!slicedObstacles.contains(endNode) && endNode.inBounds(size)) {
                            graph.addEdge(startNode, endNode, 1)
                        }
                    }
                }
            }
        }
        val start = Coordinates(0, 0)
        val end = Coordinates(size - 1, size - 1)
        val distances = dijkstra(graph.edges, start)
        if (distances[end] == null) {
            val coordinates = slicedObstacles.last()
            println("Result #2: ${coordinates.x},${coordinates.y}")
            break
        }
    }
    //    println("res: ${distances[end]}")
}

class Graph<T> {
    val edges = mutableMapOf<T, List<Pair<T, Int>>>()
    fun addEdge(start: T, end: T, weight: Int) {
        edges[start] = edges.getOrDefault(start, emptyList()) + (end to weight)
    }
}

private data class Coordinates(
        val x: Int,
        val y: Int,
) {
    operator fun plus(other: Coordinates): Coordinates {
        return Coordinates(x + other.x, y + other.y)
    }
}

private fun Coordinates.inBounds(size: Int) = x >= 0 && x < size && y >= 0 && y < size

private val VECTORS =
        listOf(
                Coordinates(0, 1),
                Coordinates(1, 0),
                Coordinates(0, -1),
                Coordinates(-1, 0),
        )

private fun <Node> dijkstra(graph: Map<Node, List<Pair<Node, Int>>>, start: Node): Map<Node, Int> {
    val distances = mutableMapOf<Node, Int>().withDefault { Int.MAX_VALUE }
    val priorityQueue = PriorityQueue<Pair<Node, Int>>(compareBy { it.second })
    val visited = mutableSetOf<Node>()

    priorityQueue.add(start to 0)
    distances[start] = 0

    while (priorityQueue.isNotEmpty()) {
        val (node, currentDist) = priorityQueue.poll()
        if (visited.add(node)) {
            graph[node]?.forEach { (adjacent, weight) ->
                val totalDist = currentDist + weight
                if (totalDist < distances.getValue(adjacent)) {
                    distances[adjacent] = totalDist
                    priorityQueue.add(adjacent to totalDist)
                }
            }
        }
    }
    return distances
}

fun part11(input: List<String>) {
    val size = 71
    val bytes = 1024
    val obstacles =
            input
                    .map { line ->
                        val lineElements = line.split(",")
                        val x = lineElements[0].toInt()
                        val y = lineElements[1].toInt()
                        Coordinates(x, y)
                    }
                    .take(bytes)

    val start = Coordinates(0, 0)
    val end = Coordinates(size - 1, size - 1)
    val queue = mutableListOf(start)
    val parents = mutableMapOf<Coordinates, Coordinates>()
    while (queue.isNotEmpty()) {
        // println("queue: $queue")
        val current = queue.removeFirst()

        // println("Visiting: $current")
        if (current == end) {
            val path = getPath(start, end, parents)
            // println("Found path: $path")
            println("Finished after visiting ${path.size}")
            break
        }
        VECTORS.forEach { vector ->
            val newPosition = current + vector
            // println("new position proposal: $newPosition")
            if (!obstacles.contains(newPosition) &&
                            newPosition.inBounds(size) &&
                            !parents.contains(newPosition)
            ) {
                // println("adding $newPosition")
                queue.add(newPosition)
                parents[newPosition] = current
            }
        }
    }
    println("Finished way")
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

fun part22(input: List<String>) {
    val size = 71
    val bytes = 1024
    val obstaclesOrig =
            input.map { line ->
                val lineElements = line.split(",")
                val x = lineElements[0].toInt()
                val y = lineElements[1].toInt()
                Coordinates(x, y)
            }
    for (i in 1024 ..< obstaclesOrig.size) {
        val obstacles = obstaclesOrig.take(i)
        println(i)
        val start = Coordinates(0, 0)
        val end = Coordinates(size - 1, size - 1)
        val stack = mutableListOf(start)
        val visited = mutableSetOf<Coordinates>()
        var found = false
        while (stack.isNotEmpty()) {
            val current = stack.removeLast()
            visited.add(current)
            if (current == end) {
                found = true
                break
            }
            VECTORS.forEach { vector ->
                val newPosition = current + vector
                // println("new position proposal: $newPosition")
                if (!obstacles.contains(newPosition) &&
                                newPosition.inBounds(size) &&
                                !visited.contains(newPosition)
                ) {
                    // println("adding $newPosition")
                    stack.add(newPosition)
                }
            }
        }
        if (!found) {
            println(obstacles.last())
            break
        }
    }
    println("Finished")
}