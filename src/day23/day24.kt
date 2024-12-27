package day23

import utils.readInput
import java.util.*

fun main() {
    val input = readInput("Day23")
    //part1(input)
    part2(input)

}

fun part1(input: List<String>) {
    //var result = 0
    val graph = Graph<String>()
    input.forEach {
        val items = it.split("-")
        graph.addEdge(items[0], items[1])
        graph.addEdge(items[1], items[0])
    }
    println(graph)
    val triples = graph.findConnectedTriples()
    println("Triples: ${triples.map {it.joinToString(",") } }")
    println("Triples: ${triples.size}")

    val selected = triples.filter { it.any { it.startsWith("t") } }
    println("Selected: $selected")
    val result = selected.size
    println("Result #1: $result")
}

fun part2(input: List<String>) {
    val graph = Graph<String>()
    input.forEach {
        val items = it.split("-")
        graph.addEdge(items[0], items[1])
        graph.addEdge(items[1], items[0])
    }
    println(graph)

    val maxGroup = graph.findSubsets().maxByOrNull { it.size }?.sorted()?.joinToString(",")
    println("Result #2: $maxGroup")
}


class Graph<T> {
    val edges = mutableMapOf<T, List<T>>()
    fun addEdge(start: T, end: T) {
        edges[start] = edges.getOrDefault(start, emptyList()) + end
    }
}

private fun Graph<String>.findConnectedTriples(): Set<Set<String>> {
    val triples = mutableSetOf<Set<String>>()
    for (node1 in edges.keys) {
        for (edge1 in edges[node1]!!) {
            val node2 = edge1
            for (edge2 in edges[node2]!!) {
                val node3 = edge2
                if (edges[node1]!!.any { it == node3 }) {
                    triples.add(setOf(node1, node2, node3))
                }
            }
        }
    }
    return triples
}

private fun Graph<String>.findSubsets(): List<Set<String>> {
    val subsets = mutableListOf<Set<String>>()
    val visited = mutableSetOf<String>()
    val keys = edges.keys
    println("keys: $keys")
    keys.forEach { startNode ->
        val subset = mutableSetOf<String>()
        val queue = mutableListOf<String>()
        queue.add(startNode)
        while (queue.isNotEmpty()) {
            val node = queue.removeFirst()
            if (!visited.contains(node) && edges[node]!!.containsAll(subset)) {
                subset.add(node)
                visited.add(node)
                for (next in edges[node]!!) {
                    if (!visited.contains(next)) {
                        queue.add(next)
                    }
                }
            }
        }
        subsets.add(subset)
    }
    return subsets
}
