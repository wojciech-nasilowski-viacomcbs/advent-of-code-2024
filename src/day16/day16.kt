package day16

import utils.readInput
import java.util.*

fun main() {
    //part1()
    part2()
}

private fun part1() {
    val input = readInput("Day16")
    val start = find('S', input)
    val end = find('E', input)
    println(start)
    println(end)
    val graph = input.parse()
    val startNode = Node(start, '>')
    val map = dijkstra(graph.edges, startNode)
    //println("dijkstra: $map")
    val res = DIRECTIONS.map {
        val endNode = Node(end, it)
        map[endNode]
    }.minOfOrNull { it ?: Int.MAX_VALUE }

    println("Result #1: $res")
}

private fun part2() {
    val input = readInput("Day16")
    val start = find('S', input)
    val end = find('E', input)
    println(start)
    println(end)
    val graph = input.parse()
    val startNode = Node(start, '>')
    val seats = mutableSetOf(start, end)
    val map = dijkstra(graph.edges, startNode)
    val res = DIRECTIONS.map {
        val endNode = Node(end, it)
        map[endNode]
    }.minOfOrNull { it ?: Int.MAX_VALUE }!!
    println("res: $res")
    val paths = findAllPathsWithMinimumWeight(graph.edges, startNode, end)
    paths.forEach { (path, score) ->
        println("score: $score")
        if (res == score) {
            seats.addAll(path.map { it.position })
        }
    }

    println("Result #2: ${seats.size}")

    input.forEachIndexed { y, line ->
        line.forEachIndexed { x, type ->
            if (seats.contains(Coordinates(x,y))) {
                print("O")
            } else {
                print(type)
            }
        }
        println()
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

private fun List<String>.get(x: Int, y: Int): Char = this[y][x]
private fun List<String>.get(coordinates: Coordinates): Char = this[coordinates.y][coordinates.x]
private fun List<String>.xSize(): Int = first().length
private fun List<String>.ySize(): Int = size
private fun List<String>.inBounds(coordinate: Coordinates): Boolean =
    coordinate.x >= 0 && coordinate.x < xSize() && coordinate.y >= 0 && coordinate.y < ySize()

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

private val DIRECTIONS = listOf('<', 'v', '>', '^')

private fun Char.rotateRight() = when (this) {
    '^' -> '>'
    '>' -> 'v'
    'v' -> '<'
    '<' -> '^'
    else -> throw IllegalArgumentException("No such direction")
}

private fun Char.rotateLeft() = when (this) {
    '^' -> '<'
    '>' -> '^'
    'v' -> '>'
    '<' -> 'v'
    else -> throw IllegalArgumentException("No such direction")
}

private fun Char.toVector() = when (this) {
    '^' -> Coordinates(0, -1)
    'v' -> Coordinates(0, 1)
    '<' -> Coordinates(-1, 0)
    '>' -> Coordinates(1, 0)
    else -> throw IllegalArgumentException("No such direction")
}


fun Set<Char>.countRotations(): Int {
    if (size == 1) {
        return 0
    }
    return when {
        this == setOf('^', 'v') || this == setOf('<', '>') -> 2
        else -> 1
    }
}

private data class Node(
    val position: Coordinates,
    val direction: Char,
)

private fun List<String>.parse(): Graph<Node> {
    val graph = Graph<Node>()
    forEachIndexed { y, line ->
        line.forEachIndexed { x, type ->
            if (type != '#') {
                DIRECTIONS.forEach { startDirection ->
                    val startNode = Node(Coordinates(x, y), startDirection)
                    DIRECTIONS.forEach { moveDirection ->
                        val endPosition = moveDirection.toVector() + startNode.position
                        if (get(endPosition) != '#') {
                            val endNode = Node(endPosition, moveDirection)
                            val score = setOf(startDirection, moveDirection).countRotations() * 1000 + 1
                            graph.addEdge(startNode, endNode, score)
                        }
                    }
                }
            }
        }
    }

    return graph
}

class Graph<T> {
    val edges = mutableMapOf<T, List<Pair<T, Int>>>()
    fun addEdge(start: T, end: T, weight: Int) {
        edges[start] = edges.getOrDefault(start, emptyList()) + (end to weight)
    }
}

private fun dijkstra(graph: Map<Node, List<Pair<Node, Int>>>, start: Node): Map<Node, Int> {
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

private fun findPathsWithWeightSum(
    graph: Map<Node, List<Pair<Node, Int>>>,
    node1: Node,
    node2: Node,
    targetWeight: Int
): List<List<Node>> {
    val paths = mutableListOf<List<Node>>()
    val visited = mutableSetOf<Node>()
    fun dfs(currentNode: Node, currentWeight: Int, path: List<Node>): List<Node> {
        if (currentNode == node2 && currentWeight == targetWeight) {
            paths.add(path)
            return emptyList()
        }

        if (currentWeight > targetWeight) {
            return emptyList()
        }

        visited.add(currentNode)

        for ((neighbor, weight) in graph[currentNode] ?: emptyList()) {
            if (neighbor !in visited) {
                dfs(neighbor, currentWeight + weight, path + currentNode)
            }
        }

        visited.remove(currentNode)
        return emptyList()
    }

    dfs(node1, 0, emptyList())
    return paths
}

private data class Path(val nodes: List<Node>, val weight: Int)

private fun findAllPathsWithMinimumWeight(
    graph: Map<Node, List<Pair<Node, Int>>>,
    startNode: Node,
    endNodePosition: Coordinates
): List<Pair<List<Node>, Int>> {
    val paths = mutableListOf<Pair<List<Node>, Int>>()
    val queue = PriorityQueue(compareBy<Path> { it.weight })
    val minWeightMap = mutableMapOf<Node, Int>()
    var minWeight = Int.MAX_VALUE

    queue.add(Path(listOf(startNode), 0))
    minWeightMap[startNode] = 0

    while (queue.isNotEmpty()) {
        val (currentPath, currentWeight) = queue.poll()
        val currentNode = currentPath.last()

        if (currentNode.position == endNodePosition) {
            if (currentWeight < minWeight) {
                minWeight = currentWeight
                paths.clear()
            }
            if (currentWeight == minWeight) {
                paths.add(currentPath to currentWeight)
            }
            continue
        }

        for ((neighbor, weight) in graph[currentNode] ?: emptyList()) {
            val newWeight = currentWeight + weight
            if (newWeight <= minWeightMap.getOrDefault(neighbor, Int.MAX_VALUE)) {
                minWeightMap[neighbor] = newWeight
                queue.add(Path(currentPath + neighbor, newWeight))
            }
        }
    }

    return paths
}