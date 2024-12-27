package day21

import utils.readInput

fun main() {
    val input = readInput("Day21")
    //part1(input)
    //println("Part2")
    //part2(input)
    println("Part2New")
    part2WithMemo(input)
}

fun part1(input: List<String>) {
    var result = 0

    val kepPad = listOf(
        listOf('7', '8', '9'),
        listOf('4', '5', '6'),
        listOf('1', '2', '3'),
        listOf(' ', '0', 'A')
    )
    val keyPadGraph = Graph()

    padToGraph(keyPadGraph, kepPad)
    //println(keyPadGraph)

    val cursorPad = listOf(
        listOf(' ', '^', 'A'),
        listOf('<', 'v', '>'),
    )
    val cursorPadGraph = Graph()
    padToGraph(cursorPadGraph, cursorPad)
    //println(cursorPadGraph)

    input.forEach { sequence ->
        println(sequence)
        val cursorsSequences1 = generateSequences(sequence, keyPadGraph)
        var cursorsSequence3Length = Int.MAX_VALUE
        var optimalSequence2 = ""
        cursorsSequences1.forEach { cursorsSequence1 ->
            val cursorsSequences2 = generateSequences(cursorsSequence1, cursorPadGraph)
            cursorsSequences2.forEach { cursorsSequence2 ->
                val localLength = generateSequence(cursorsSequence2, cursorPadGraph).length
                if (localLength < cursorsSequence3Length) {
                    println(cursorsSequence1)
                    println(cursorsSequence2)
                    cursorsSequence3Length = localLength
                    optimalSequence2 = cursorsSequence2
                }
            }
        }
        println(optimalSequence2)
        val cursorsSequence3 = generateSequence(optimalSequence2, cursorPadGraph)
        println(cursorsSequence3)
        val length = cursorsSequence3.length
        val numericPartOfCode = sequence.filter { it.isDigit() }.toInt()
        println("length: $length numericPartOfCode: $numericPartOfCode")
        result += length * numericPartOfCode
    }

    println("Result #1: $result")
}

//val targetLevel = 26
fun part2(input: List<String>) {
    var result = 0

    val kepPad = listOf(
        listOf('7', '8', '9'),
        listOf('4', '5', '6'),
        listOf('1', '2', '3'),
        listOf(' ', '0', 'A')
    )
    val keyPadGraph = Graph()

    padToGraph(keyPadGraph, kepPad)
    //println(keyPadGraph)

    val cursorPad = listOf(
        listOf(' ', '^', 'A'),
        listOf('<', 'v', '>'),
    )
    val cursorPadGraph = Graph()
    padToGraph(cursorPadGraph, cursorPad)
    //println(cursorPadGraph)


    input.forEach { sequence ->
        println(sequence)
        //println(cursorPadGraph)

        val cursorsSequence = generateSequence(sequence, keyPadGraph)
        val optimalCursorsSequence = cursorsSequence.optimize(keyPadGraph)
        var currentSequence = optimalCursorsSequence
        println("Sequence: $currentSequence")
        repeat(targetLevelValue) {
            val newSequence = generateSequence(currentSequence, cursorPadGraph)
            currentSequence = newSequence.optimize(cursorPadGraph)
            println("Repetition: ${it + 1} Sequence: $currentSequence")
        }
        val length = currentSequence.length
        val numericPartOfCode = sequence.filter { it.isDigit() }.toInt()
        println("length: $length numericPartOfCode: $numericPartOfCode")
        result += length * numericPartOfCode
    }

    println("Result #2: $result")
}

fun part2WithMemo(input: List<String>) {
    var result = 0L
    val kepPad = listOf(
        listOf('7', '8', '9'),
        listOf('4', '5', '6'),
        listOf('1', '2', '3'),
        listOf(' ', '0', 'A')
    )
    val keyPadGraph = Graph()

    padToGraph(keyPadGraph, kepPad)
    //println(keyPadGraph)

    val cursorPad = listOf(
        listOf(' ', '^', 'A'),
        listOf('<', 'v', '>'),
    )
    val cursorPadGraph = Graph()
    padToGraph(cursorPadGraph, cursorPad)
    //println(cursorPadGraph)

    input.forEach { sequence ->
        println(sequence)
        //println(cursorPadGraph)

        val cursorsSequence = sequence.toNewSequence(keyPadGraph)
            .mapIndexed { i, item -> item.optimize(keyPadGraph, sequence.getOrNull(i - 1) ?: 'A') }
        //generateSequence(sequence, keyPadGraph)
        //val optimalCursorsSequence = sortGroups(cursorsSequence)
        // println("#1: $optimalCursorsSequence")
        //println("#2: ${cursorsSequence.optimize(cursorPadGraph)}")
        val length = cursorsSequence.map {
            dfs(it, 0, cursorPadGraph)
        }.sum()
        val numericPartOfCode = sequence.filter { it.isDigit() }.toLong()
        println("length: $length numericPartOfCode: $numericPartOfCode")
        result += length * numericPartOfCode
    }

    println("Result #2: $result")
}

private val targetLevelValue = 25

private fun dfs(sequence: String, level: Int, padGraph: Graph): Long {
    println("Level: $level")

    if (level > targetLevelValue - 1) {
        return sequence.toCharArray().size.toLong()
    }
    val newSequences = sequence.toNewSequence(padGraph)
    val prevItems = sequence.toCharArray()

    return newSequences.mapIndexed { i, seq ->
        val previous = prevItems.getOrNull(i - 1) ?: 'A'
        println("seq: $seq")
        generateSequencesCombinations(seq, padGraph)
            .filter {
                isValidPath(padGraph, it.dropLast(1), previous)
            }
            .map { sequenceCandidate ->
            println("sequenceCandidate: $sequenceCandidate")
            memoDfs.getOrPut(sequenceCandidate to level + 1) {
                dfs(sequenceCandidate, level + 1, padGraph)
            }
        }.min()
    }.sum()
}

private val memoDfs = mutableMapOf<Pair<String, Int>, Long>()

fun findAllAOccurrences(input: String): List<String> {
    val regex = Regex("A+")
    return regex.findAll(input).map { it.value }.toList()
}

private val memoPath = mutableMapOf<Pair<Char, Char>, String>()

private val memoSequences = mutableMapOf<String, List<String>>()

private fun List<String>.toNewSequence(
    padGraph: Graph
): List<String> {
    return map {
        memoSequences.getOrPut(it) {
            it.toNewSequence(padGraph)
        }
    }.flatten()
}

private fun String.toNewSequence(
    padGraph: Graph
): List<String> {
    var currentKeyPosition = 'A'
    val result = mutableListOf<String>()
    for (key in this) {
        val start = Node(currentKeyPosition)
        val end = Node(key)
        val item =
            //memoPath.getOrPut(start.key to end.key) {
                padGraph.findPath(start, end).joinToString("") + 'A'
            //}

        currentKeyPosition = key
        result.add(item)
    }
    return result
}

private fun generateSequence(
    desiredSequence: String,
    padGraph: Graph
): String {
    var currentKeyPosition = 'A'
    var cursorsSequence = ""
    for (key in desiredSequence) {
        val start = Node(currentKeyPosition)
        val end = Node(key)
        cursorsSequence +=
                // memoPath.getOrPut(start.key to end.key) {
            padGraph.findPath(start, end).joinToString("") + 'A'
        //}

        currentKeyPosition = key
    }
    return cursorsSequence
}


fun Char.toOrder() = when (this) {
    '<' -> 1
    'v' -> 2
    '^' -> 3
    '>' -> 3
    else -> throw IllegalArgumentException()
}

fun sortGroups(input: String, separator: Char = 'A'): String {
    val parts = input.split(separator)
    val sortedParts = parts.map { part ->
        if (part.isEmpty()) "" else part.toCharArray().sortedBy {
            it.toOrder()
        }.joinToString("")
    }
    return sortedParts.joinToString(separator.toString())
}

private fun String.optimize(padGraph: Graph, prev: Char = 'A'): String {
    if (length <3) {
        return this
    }
    val aOccurrences = findAllAOccurrences(this)
    //println("optimize: $this for $padGraph")
    val sequences = split(Regex("A+"))
    var line = ""
    sequences.forEachIndexed { i, sequence ->
        val candidateLines = listOf(
            line + sequence.toCharArray().sortedBy {
                it.toOrder()
            }.joinToString("") + aOccurrences.getOrNull(i).orEmpty(),
            line + sequence + aOccurrences.getOrNull(i).orEmpty()
        )
        //println("candidateLines: $candidateLines")
        val selected = candidateLines.first {
            //println("checking ${it.dropLast(1)}")
            isValidPath(padGraph, it.dropLast(1), prev)
        }
        line = selected
    }
    //println("Optimized: $line")
    return line
}

private fun generateSequencesCombinations(
    sourceSequence: String,
    padGraph: Graph
): List<String> {
    val combinations = generateCombinations(sourceSequence.split(Regex("A+"))).toSet()
    val aOcurrences = findAllAOccurrences(sourceSequence)
    val resultList = mutableListOf<String>()
    combinations.forEach { combination ->
        var line = ""
        combination.forEachIndexed { i, move ->
            line += move + aOcurrences.getOrNull(i).orEmpty()
        }

        resultList.add(line)
    }
    return resultList
}


private fun generateSequences(
    desiredSequence: String,
    padGraph: Graph
): List<String> {
    val shortestSequence = generateSequence(desiredSequence, padGraph)
    //println("Shortest initial: $shortestSequence")
    val combinations = generateCombinations(shortestSequence.split(Regex("A+"))).toSet()
    val aOcurrences = findAllAOccurrences(shortestSequence)
    val resultList = mutableListOf<String>()
    combinations.forEach { combination ->
        var line = ""
        combination.forEachIndexed { i, move ->
            line += move + aOcurrences.getOrNull(i).orEmpty()
        }
        if (isValidPath(padGraph, line))
            resultList.add(line)
    }
    return resultList
}

private fun isValidPath(graph: Graph, sequence: String, prev: Char = 'A'): Boolean {
    var currentNode = graph.nodes.find { it == Node(prev) }
    for (move in sequence) {
        //println("Move: $move")
        if (move == 'A') {
            // graph.nodes.find { it == Node('A') }
        } else {
            currentNode = currentNode?.neighbours?.find { it.type == move }?.to
        }
        //println("currentNode: $currentNode")

        if (currentNode == null) {
            return false
        }
    }
    return true
}

private fun padToGraph(
    cursorPadGraph: Graph,
    kepPad: List<List<Char>>,
) {
    with(cursorPadGraph) {
        kepPad.forEachIndexed { y, line ->
            line.forEachIndexed { x, key ->
                val current = Coordinates(x, y)
                VECTORS.forEach {
                    val candidate = current + it.first
                    if (candidate.x >= 0 && candidate.x < line.size &&
                        candidate.y >= 0 && candidate.y < kepPad.size && kepPad[candidate.y][candidate.x] != ' ' &&
                        key != ' '
                    ) {
                        addEdge(Node(key), Node(kepPad[candidate.y][candidate.x]), it.second)
                    }
                }
            }
        }
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

private val VECTORS =
    listOf(
        Coordinates(0, 1) to 'v',
        Coordinates(1, 0) to '>',
        Coordinates(0, -1) to '^',
        Coordinates(-1, 0) to '<',
    )

private data class Node(
    val key: Char,
    val neighbours: MutableSet<Edge> = mutableSetOf<Edge>()
) {
    override fun equals(other: Any?): Boolean {
        return key == (other as? Node)?.key
    }

    override fun hashCode(): Int {
        return key.hashCode()
    }
}

private data class Edge(
    val from: Node,
    val to: Node,
    val type: Char,
) {
    override fun toString() = "${from.key} $type ${to.key}"
}


private data class Graph(
    val nodes: MutableSet<Node> = mutableSetOf()
) {
    fun addEdge(start: Node, end: Node, type: Char) {
        val from = if (nodes.add(start)) {
            start
        } else {
            nodes.find { it == start }!!
        }
        val to = if (nodes.add(end)) {
            end
        } else {
            nodes.find { it == end }!!
        }

        from.neighbours.add(Edge(from, to, type))
    }
}

private fun Graph.findPath(inputStart: Node, inputEnd: Node): List<Char> {
    val start = nodes.first { it == inputStart }
    val end = nodes.first { it == inputEnd }
    val queue = mutableListOf(start)
    val parents = mutableMapOf<Node, Pair<Node, Char>>()
    val distancesFromStart = mutableMapOf<Node, Int>()
    distancesFromStart[start] = 0
    while (queue.isNotEmpty()) {
        val current = queue.removeFirst()
        if (current == end) {
            val path = getPathKeys(start, end, parents)
            return path
        }
        //println("checking $current")
        current.neighbours.forEach { edge ->
            val newNode = edge.to
            if (!parents.contains(newNode)) {
                queue.add(newNode)
                parents[newNode] = current to edge.type
            }
        }
    }
    return emptyList()
}

private fun getPathNodes(
    start: Node,
    end: Node,
    parents: Map<Node, Node>
): List<Node> {
    val path = mutableListOf<Node>()
    var i = 0L
    var current = end
    while (current != start) {
        i++
        path.add(current)
        current = parents[current]!!
    }
    return path.reversed()
}

private fun getPathKeys(
    start: Node,
    end: Node,
    parents: Map<Node, Pair<Node, Char>>
): List<Char> {
    val path = mutableListOf<Char>()
    var i = 0L
    var current = end
    while (current != start) {
        i++
        path.add(parents[current]!!.second)
        current = parents[current]!!.first
    }
    return path.reversed()
}

fun permuteString(str: String): List<String> {
    if (str.isEmpty()) return listOf("")
    val permutations = mutableListOf<String>()
    for (i in str.indices) {
        val char = str[i]
        val remaining = str.substring(0, i) + str.substring(i + 1)
        for (perm in permuteString(remaining)) {
            permutations.add(char + perm)
        }
    }
    return permutations
}

fun generateCombinations(strings: List<String>): List<List<String>> {
    if (strings.isEmpty()) return listOf(emptyList())
    val result = mutableListOf<List<String>>()
    val firstStringPermutations = permuteString(strings.first())
    val remainingCombinations = generateCombinations(strings.drop(1))
    for (perm in firstStringPermutations) {
        for (comb in remainingCombinations) {
            result.add(listOf(perm) + comb)
        }
    }
    return result
}
