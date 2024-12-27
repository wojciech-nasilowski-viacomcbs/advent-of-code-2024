package day15

import utils.readInput

fun main() {
    //part1()
    part2()
}

private fun part1() {
    val input = readInput("Day15")
    var current = findRobot(input)
    val(map, moves) = input.parse()
    val mapToModify = map.map { it.toMutableList() }.toMutableList()
    println("map: $map")
    println("moves: $moves")
    moves.forEach { move ->
        println("move: $move")
        val moveVector = move.toVector()
        val nextCandidate = current + moveVector
        val candidateContent = mapToModify.get(nextCandidate)
        when(candidateContent) {
            '.' -> current = nextCandidate
            '#' -> Unit
            'O' -> {
                var tracker: Coordinates = nextCandidate
                val boxes = mutableSetOf<Coordinates>()
                while(true) {
                    val trackerContent = mapToModify.get(tracker)
                    if (trackerContent == '.') {
                        mapToModify.set(boxes.first(), '.')
                        current = boxes.first()
                        mapToModify.set(tracker, 'O')
                        break
                    }
                    if (trackerContent == '#') {
                        break
                    }
                    if (trackerContent == 'O') {
                        boxes.add(tracker)
                        tracker += moveVector
                        continue
                    }
                    throw IllegalStateException("Shouldn't get here")
                }
            }
        }

    }
    mapToModify.print(current)
    val result = mapToModify.calculateGps()

    println("Result #1: $result")
}

private fun part2() {
    val input = readInput("Day15")
    val(map, moves, curr) = input.parse2()
    var current = curr
    val mapToModify = map.map { it.toMutableList() }.toMutableList()
    mapToModify.print(current)

    moves.forEach { move ->
        println("move: $move")
        val moveVector = move.toVector()
        val nextCandidate = current + moveVector
        val candidateContent = mapToModify.get(nextCandidate)
        val allBoxes = mapToModify.toBoxes()
        //println("all $allBoxes")
        when(candidateContent) {
            '.' -> current = nextCandidate
            '#' -> Unit
            '[', ']' -> {
                val boxesToMove = mutableSetOf<Pair<Coordinates, Coordinates>>()
                val boxes = mutableSetOf<Pair<Coordinates, Coordinates>>()
                if (candidateContent == '[') {
                    boxes.add(nextCandidate to nextCandidate + Coordinates(1, 0))
                } else {
                    boxes.add(nextCandidate + Coordinates(-1, 0) to nextCandidate)
                }
                println("Boxes: $boxes")
                while(boxes.isNotEmpty()) {
                    val currentBox = boxes.first()
                    boxesToMove.add(currentBox)
                    //println("Check box $currentBox")
                    val movedBox = currentBox.first + moveVector to currentBox.second + moveVector
                    //println("Moved box $movedBox")

                    val adjacentBoxes = allBoxes.filter {
                        it.contains(movedBox.first) || it.contains(movedBox.second)
                    }
                    boxes.addAll(adjacentBoxes.map {it[0] to it[1]})
                    boxes.remove(currentBox)
                }
                println("boxes to move $boxesToMove")
                if (canMoveBoxes(boxesToMove, moveVector, mapToModify)) {
                    boxesToMove.forEach { box ->
                        mapToModify.set(box.first, '.')
                        mapToModify.set(box.second, '.')
                    }
                    boxesToMove.forEach { box ->
                        println("Moving box $box")
                        mapToModify.set(box.first + moveVector, '[')
                        mapToModify.set(box.second + moveVector, ']')
                    }
                    current = nextCandidate
                }

            }
        }
        //mapToModify.print(current)
    }


    val result = mapToModify.calculateGps2()
    println("Result #2: $result")
}

private fun List<List<Char>>.calculateGps2(): Int {
    var result = 0
    forEachIndexed { y, row, ->
        row.forEachIndexed { x, item,  ->
            if (item == '[') {
                result += y * 100 + x
            }
        }
    }
    return result
}

private fun canMoveBoxes(boxes: Set<Pair<Coordinates, Coordinates>>, moveVector: Coordinates, map: List<List<Char>>) : Boolean {
    boxes.forEach { box ->
        val newPosition = box.first + moveVector to box.second + moveVector
        if (map.get(newPosition.first) == '#' || map.get(newPosition.second) == '#') {
            return false
        }
    }
    return true
}

private fun List<List<Char>>.toBoxes(): Set<List<Coordinates>> {
    val result = mutableSetOf<List<Coordinates>>()
    forEachIndexed { y, row ->
        row.forEachIndexed { x, el ->
            if (el == '[') {
                result.add(listOf(Coordinates(x,y), Coordinates(x + 1,y)))
            }
        }
    }
    return result
}

private fun Char.toVector() = when(this) {
    '^' -> Coordinates(0, -1)
    'v' -> Coordinates(0, 1)
    '<' -> Coordinates(-1, 0)
    '>' -> Coordinates(1, 0)
    else -> throw IllegalArgumentException("No such direction")
}

private fun List<List<Char>>.get(x: Int, y: Int): Char = this[y][x]
private fun List<List<Char>>.get(coordinates: Coordinates): Char  = this[coordinates.y][coordinates.x]
private fun List<MutableList<Char>>.set(coordinates: Coordinates, item: Char)  { this[coordinates.y][coordinates.x] = item }
private fun List<List<Char>>.xSize(): Int  = first().size
private fun List<List<Char>>.ySize(): Int  = size
private fun List<List<Char>>.inBounds(coordinate: Coordinates): Boolean  =
    coordinate.x >= 0 && coordinate.x<xSize() && coordinate.y >= 0 && coordinate.y<ySize()

private fun List<List<Char>>.calculateGps(): Int {
    var result = 0
    forEachIndexed { y, row, ->
        row.forEachIndexed { x, item,  ->
            if (item == 'O') {
                result += y * 100 + x
            }
        }
    }
    return result
}

private fun List<List<Char>>.print(current: Coordinates) {
    forEachIndexed { j, line ->
        line.forEachIndexed { i, el ->
            if (current.x == i && current.y == j) {
                print('@')
            } else {
                print(el)
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

private fun findRobot(input: List<String>): Coordinates {
    var y = 0
    while (y < input.size) {
        val line = input[y]
        var x = 0
        while(x < line.length) {
            if (listOf('@').contains(line[x])) {
                return Coordinates(x, y)
            }
            x++
        }
        y++
    }
    throw IllegalArgumentException("Not found")
}

fun List<String>.parse(): Pair<List<List<Char>>, List<Char>> {
    val rows = mutableListOf<List<Char>>()
    val moves = mutableListOf<Char>()
    forEach { line ->
        val columns = mutableListOf<Char>()
        line.forEach { item ->
            if (line.startsWith("#")) {
                val itemToAdd = if(item == '@') {
                    '.'
                } else {
                    item
                }
                columns.add(itemToAdd)
            } else {
                moves.add(item)
            }
        }
        if (columns.isNotEmpty()) {
            rows.add(columns)
        }
    }

    return rows to moves
}



private fun List<String>.parse2(): Triple<List<List<Char>>, List<Char>, Coordinates> {
    val rows = mutableListOf<List<Char>>()
    val moves = mutableListOf<Char>()
    var current: Coordinates? = null
    var y = 0
    forEach { line ->
        var x = 0
        val columns = mutableListOf<Char>()
        line.forEach { item ->
            if (line.startsWith("#")) {
                if(item == '@') {
                    current = Coordinates(x, y)
                    repeat(2) {columns.add('.')}
                } else if(item == 'O') {
                    columns.add('[')
                    columns.add(']')
                } else {
                    repeat(2) {columns.add(item)}
                }

                x+=2
            } else {
                moves.add(item)
            }
        }
        if (columns.isNotEmpty()) {
            y++
            rows.add(columns)
        }

    }

    return Triple(rows, moves, current!!)
}