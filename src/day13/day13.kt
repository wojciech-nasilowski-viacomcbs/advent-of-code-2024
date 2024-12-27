package day13

import utils.readInput
import java.math.BigDecimal

fun main() {
    //part1()
    part2()
}

private fun part1() {
    val input = readInput("Day13")
    val machines = input.toMachines()
    var cost = 0L
    machines.forEach { machine ->
        val presses  = solveSimultaneousEquations(
            machine.buttonA.coordinates.x.toDouble(),
            machine.buttonB.coordinates.x.toDouble(),
            machine.prize.x.toDouble(),
            machine.buttonA.coordinates.y.toDouble(),
            machine.buttonB.coordinates.y.toDouble(),
            machine.prize.y.toDouble(),
        )
        println("Machine: $machine")
        presses?.let {
            val (pressesA, pressesB) = it
            if (pressesA.toLong().toDouble() == pressesA && pressesB.toLong().toDouble() == pressesB &&
                pressesA<=100 && pressesB<=100) {
                println("Possible A: $pressesA B: $pressesB")
                cost += 3 * pressesA.toLong() + pressesB.toLong()
            }
        }
    }
    println("Cost (1): $cost")
 }


private fun part2() {
    val input = readInput("Day13")
    val machines = input.toMachines(10000000000000)
    var cost = 0L
    machines.forEach { machine ->
        val presses  = solveSimultaneousEquations(
            machine.buttonA.coordinates.x.toDouble(),
            machine.buttonB.coordinates.x.toDouble(),
            machine.prize.x.toDouble(),
            machine.buttonA.coordinates.y.toDouble(),
            machine.buttonB.coordinates.y.toDouble(),
            machine.prize.y.toDouble(),
        )
        println("Machine: $machine")
        presses?.let {
            val (pressesA, pressesB) = it
            if (pressesA.toLong().toDouble() == pressesA && pressesB.toLong().toDouble() == pressesB) {
                println("A: ${pressesA.toLong()} B: ${pressesB.toLong()}")
                cost += 3 * pressesA.toLong() + pressesB.toLong()
            }
        }
    }
    println("Cost (2): $cost")

}

private const val MAX = 100

private data class Machine(
    val buttonA: Button,
    val buttonB: Button,
    val prize: Coordinates,
)

private data class Button(
        val coordinates: Coordinates,
        val xOperator: Char ='+',
        val yOperator: Char ='+',
    )


private val VECTORS = listOf (
    Coordinates(-1, 0),
    Coordinates(0, -1),
    Coordinates(1, 0),
    Coordinates(0, 1)
)

private fun List<String>.toMachines(prizePrefix: Long = 0): List<Machine> {
    val result = mutableListOf<Machine>()
    var i = 0
    while(i<size) {
        val (buttonAx, buttonAy) = extractNumbers(get(i))
        val buttonA = Button (Coordinates(buttonAx, buttonAy))
        i++
        val (buttonBx, buttonBy) = extractNumbers(get(i))
        val buttonB = Button (Coordinates(buttonBx, buttonBy))
        i++
        val (prizeX, prizeY) = extractNumbers(get(i))
        val prize = Coordinates(prizePrefix+prizeX, prizePrefix+prizeY)
        i+=2
        result.add(
            Machine(
                buttonA,
                buttonB,
                prize,
            )
        )
    }
    return result
}

private fun extractNumbers(input: String): List<Long> {
    val regex = """.*X.(\d+), Y.(\d+)""".toRegex()
    val matchResult = regex.matchEntire(input)

    return matchResult?.destructured?.let { (x, y) ->
        listOf(x.toLong(), y.toLong())
    } ?: emptyList()
}

private data class Coordinates(
    val x: Long,
    val y: Long,
) {
    operator fun plus(other: Coordinates): Coordinates {
        return Coordinates(x + other.x, y + other.y)
    }
}

    private fun solveSimultaneousEquations(a1: Double, b1: Double, c1: Double, a2: Double, b2: Double, c2: Double): Pair<Double, Double>? {
        val determinant = a1 * b2 - a2 * b1

        if (determinant == 0.0) {
            // Check for consistent or inconsistent system
            if ((c1 * b2 - c2 * b1) == 0.0) {
                println("Infinite solutions")
            } else {
                println("No solution")
            }
            return null
        } else {
            val x = (c1 * b2 - c2 * b1) / determinant
            val y = (a1 * c2 - a2 * c1) / determinant
            return Pair(x, y)
        }
    }
private fun solveSimultaneousEquationsD(a1: BigDecimal, b1: BigDecimal, c1: BigDecimal, a2: BigDecimal, b2: BigDecimal, c2: BigDecimal): Pair<BigDecimal, BigDecimal>? {
    val determinant = a1 * b2 - a2 * b1

    if (determinant == BigDecimal.ZERO) {
        // Check for consistent or inconsistent system
        if ((c1 * b2 - c2 * b1) == BigDecimal.ZERO) {
            println("Infinite solutions")
        } else {
            println("No solution")
        }
        return null
    } else {
        val x = (c1 * b2 - c2 * b1) / determinant
        val y = (a1 * c2 - a2 * c1) / determinant
        return Pair(x, y)
    }
}