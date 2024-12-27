package day24

import utils.readInput
import java.util.*

fun main() {
    val input = readInput("Day24")
    //part1(input)
    part2(input)

}

fun part1(input: List<String>) {
    val device = input.parseInput()
    // println(device)
    with(device) {
        var leftConnections: List<Connection> = emptyList()
        do {
            leftConnections = processConnections(device.connections)
            leftConnections = topologicalSort(leftConnections).reversed()
        } while (leftConnections.isNotEmpty())
    }
    val zKeys = device.numbers.keys.filter { it.matchesZdd() }.sorted().reversed()
    val zResult = zKeys.map { device.numbers[it] }.joinToString("")
    val result = zResult.toLong(2)
    // println("Result #1: $result")
}

private fun Device.processConnections(
    connections: List<Connection>,
): List<Connection> {
    val unprocessed = mutableListOf<Connection>()

    for (connection in connections) {
        //// println("Checking connection $connection")
        if (!connection.inputs.all { numbers.containsKey(it) }) {
            //// println("before: $leftIndexes")
            //// println("after: $leftIndexes")
            unprocessed.add(connection)
            continue
        }
        numbers[connection.output] = calculate(
            numbers[connection.inputs[0]]!! to numbers[connection.inputs[1]]!!,
            connection.gateType,
        )
    }
    return unprocessed
}

fun String.matchesZdd(): Boolean {
    val regex = Regex("^z\\d{2}$")
    return regex.matches(this)
}

private data class Connection(
    val inputs: List<String>,
    val output: String,
    val gateType: String
)

private data class Device(
    val numbers: MutableMap<String, Int> = mutableMapOf(),
    val connections: List<Connection>
)

private fun List<String>.parseInput(): Device {
    val values: MutableMap<String, Int> = mutableMapOf()
    val connections = mutableListOf<Connection>()
    var valuesFetched = false
    for (line in this) {

        if (line.isEmpty()) {
            valuesFetched = true
            continue
        }
        if (!valuesFetched) {
            val splitLine = line.split(": ")
            values[splitLine[0]] = splitLine[1].toInt()
        } else {
            val splitLine = line.split(" ")
            val connection = Connection(
                listOf(splitLine[0], splitLine[2]),
                splitLine[4],
                splitLine[1]
            )
            connections.add(connection)
        }
    }

    return Device(values, connections)
}

private fun calculate(inputs: Pair<Int, Int>, type: String): Int {
    require(inputs.first == 0 || inputs.first == 1)
    require(inputs.second == 0 || inputs.second == 1)
    return when (type) {
        "OR" -> inputs.first or inputs.second
        "AND" -> inputs.first and inputs.second
        "XOR" -> inputs.first xor inputs.second
        else -> throw IllegalArgumentException("Unexpected gate type!")
    }
}

private fun topologicalSort(connections: List<Connection>): List<Connection> {
    val indegree = mutableMapOf<String, Int>()
    val queue = LinkedList<Connection>()
    val result = mutableListOf<Connection>()

    // Initialize in-degree and queue
    connections.forEach { connection ->
        connection.inputs.forEach { neighbour ->
            indegree[neighbour] = (indegree[neighbour] ?: 0) + 1
        }
        if (indegree[connection.output] == null) {
            indegree[connection.output] = 0
            queue.offer(connection)
        }
    }

    // Topological Sort (ignoring cycles)
    while (queue.isNotEmpty()) {
        val node = queue.poll()!!
        result.add(node)

        node.inputs.forEach { neighbour ->
            indegree[neighbour] = indegree[neighbour]!! - 1
            val neighbourConnection = connections.find { it.output == neighbour }
            if (neighbourConnection != null && indegree[neighbour] == 0) {
                queue.offer(neighbourConnection)
            }
        }
    }

    return result
}

fun part2(input: List<String>) {
    // https://www.instructables.com/4-Bit-Binary-Adder-1/ - schema from here for the rules
    val adder = input.parseInput()
    val anomalies = mutableSetOf<String>()
    with(adder) {
        val lastZ = connections.map { it.output }.filter { it.startsWith('z') }.maxOf { it }
        // println("lastZ $lastZ")
        connections.forEach { connection ->
            when {
                connection.output == "z00" ->
                    if (connection.gateType != "XOR" || connection.inputs.toSet() != setOf("x00", "y00")) {
                        // println("1 ${connection.output}")
                        anomalies.add(connection.output)
                    }

                connection.output == lastZ ->
                    if (connection.gateType != "OR" || connection.inputs.any { it.startsWith("x") || it.startsWith("y") }) {
                        // println("2 ${connection.output}")
                        anomalies.add(connection.output)

                    }
                connection.inputs.any { it == "x00" || it == "y00" } -> {
                    println("connection $connection")
                    println("connection set  ${connection.inputs.toSet()}")

                    if (connection.inputs.toSet() != setOf("x00", "y00") || !setOf("XOR", "AND").contains(connection.gateType) ) {
                        println("03 ${connection.output}")
                        anomalies.add(connection.output)
                    }

                }
                connection.gateType == "AND" -> {
                    if (!((connection.inputs.map { it.first() }.toSet() == setOf('x', 'y')) ||
                                connection.inputs.none { it.startsWith('x') || it.startsWith('y') }
                                )
                    ) {
                        // println("3 ${connection.output}")
                        anomalies.add(connection.output)
                    }
                    val outputs = connections.filter {
                        it.inputs.contains(connection.output)
                    }.map { it.gateType }.toSet()
                    if (!outputs.contains("OR")) {
                        // println("4 ${connection.output}")
                        anomalies.add(connection.output)
                    }
                }

                connection.gateType == "OR" -> {
                    if (connection.inputs.any { it.startsWith('x') || it.startsWith('y') }) {
                        // println("05 ${connection.output}")
                        anomalies.add(connection.output)
                    }
                    val outputs = connections.filter {
                        it.inputs.contains(connection.output)
                    }.map { it.gateType }.toSet()
                    if (
                        !outputs.contains("AND") || !outputs.contains("XOR")
                    ) {
                        // println("5 ${connection.output}")
                        anomalies.add(connection.output)
                    }
                }
                connection.gateType == "XOR" -> {
                    if(!(
                                (connection.inputs.none{it.startsWith('x') || it.startsWith('y')} && connection.output.startsWith('z'))
                                    || (
                                        connection.inputs.map{it.first()}.toSet() == setOf('x', 'y') &&
                                                connections.filter {
                                                    it.inputs.contains(connection.output)
                                                }.map { it.gateType }.all { it == "AND"  || it == "XOR"}
                                            )
                            )
                    ) {
                        // println("6 ${connection.output}")
                        anomalies.add(connection.output)
                    }
                }
            }
        }
        val result = anomalies.sorted().joinToString(",")
        println("Result2: $result (${anomalies.size} items)")
    }
}
