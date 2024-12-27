package day17

import utils.readInput
import kotlin.math.pow

fun main() {
    //part1()
    part2()
}

private fun part1() {
    val input = readInput("Day17")
    val computer = input.parse()
    computer.execute()
    print("Result #1: ")
    computer.print()
}

private fun part2() {
    val input = readInput("Day17")
    val computer = input.parse()
    var newA = 0L
    var i = computer.program.size-1
    while (i>=0) {
        println("i: $i")
        val expectedResult = computer.program.subList(i, computer.program.lastIndex+1)
        println("expectedResult: $expectedResult")
        val target = expectedResult.joinToString(",")
        while(true) {
            val newComputer = with(computer) { Computer(newA, b, c, program) }
            newComputer.executeWithTarget(target)
            if (target == newComputer.result()) {
                break
            }
            newA++
        }
        i--
        if (i>=0) {
            newA = newA shl 3
        }
    }
    println("NewA: $newA")

        //bruteForce(computer, expected)

}

/*private fun bruteForce(computer: Computer, expected: String): Int {
    var newA = Int.MIN_VALUE
    var currentResult = ""
    while ( currentResult != expected) {
        if(newA % 1000 == 0) {
            println("checking $newA which is ${abs((newA-Int.MIN_VALUE.toDouble()).toDouble() / (Int.MAX_VALUE.toDouble()-Int.MIN_VALUE.toDouble())) * 100.0} percent")
        }
        val newComputer = with(computer) { Computer(newA, b, c, program) }
        newComputer.executeWithTarget(expected)
        newA++
        currentResult = newComputer.result()
    }
    println("Result is ${newA -1}")
    return newA - 1
}
*/
private data class Computer(
    var a: Long,
    var b: Long,
    var c: Long,
    val program: List<Int>,
) {
    private var output: String = ""

    private fun Int.toComboOperand(): Long = when(this) {
        0,1,2,3 -> this.toLong()
        4 -> a
        5 -> b
        6 -> c
        else -> throw IllegalArgumentException("Unexpected operand on input!")
    }

    private var pointer = 0
    private fun adv(operandInput: Int) {
        a /= (2.toDouble().pow(operandInput.toComboOperand().toDouble())).toInt()
        pointer+=2
    }
    private fun bxl(operandInput: Int) {
        b = b xor operandInput.toLong()
        pointer+=2
    }
    private fun bst(operandInput: Int) {
        val mask = 0b111.toLong()
        b = (operandInput.toComboOperand() % 8) and mask
        pointer+=2
    }
    private fun jnz(operandInput: Int) {
        if (a != 0L) {
            pointer = operandInput
            return
        }
        pointer+=2
    }
    private fun bxc(operandInput: Int) {
        b = b xor c
        pointer+=2
    }

    private fun out(operandInput: Int) {
        val res = operandInput.toComboOperand() % 8
        output += "$res,"
        pointer+=2
    }
    private fun bdv(operandInput: Int) {
        b = a / (2.toDouble().pow(operandInput.toComboOperand().toDouble())).toInt()
        pointer+=2
    }
    private fun cdv(operandInput: Int) {
        c = a / (2.toDouble().pow(operandInput.toComboOperand().toDouble())).toInt()
        pointer+=2
    }

    fun print() {
        val res = if (output.endsWith(",")) {
            output.dropLast(1)
        } else {
            output
        }
        println(res)
    }

    fun result(): String {
        val res = if (output.endsWith(",")) {
            output.dropLast(1)
        } else {
            output
        }
        return res
    }

    fun executeWithTarget(target: String) {
        while (pointer < program.size-1) {
            //println("state is $this")
            //println("pointer is $pointer")
            val instruction = program[pointer]
            val operandInput = program[pointer+1]
            //println("instruction is $instruction")
            //println("operandInput is $operandInput")

            when(instruction) {
                0 -> adv(operandInput)
                1 -> bxl(operandInput)
                2 -> bst(operandInput)
                3 -> jnz(operandInput)
                4 -> bxc(operandInput)
                5 -> out(operandInput)
                6 -> bdv(operandInput)
                7 -> cdv(operandInput)
            }
            if (!target.startsWith(result())) {
                return
            }
        }
    }

    fun execute() {
        while (pointer < program.size-1) {
            //println("state is $this")
            //println("pointer is $pointer")
            val instruction = program[pointer]
            val operandInput = program[pointer+1]
            //println("instruction is $instruction")
            //println("operandInput is $operandInput")

            when(instruction) {
                0 -> adv(operandInput)
                1 -> bxl(operandInput)
                2 -> bst(operandInput)
                3 -> jnz(operandInput)
                4 -> bxc(operandInput)
                5 -> out(operandInput)
                6 -> bdv(operandInput)
                7 -> cdv(operandInput)
            }
        }
    }
}

private fun List<String>.parse(): Computer {
    var a = -1L
    var b = -1L
    var c = -1L
    var program: List<Int>? = null
    forEach { line ->
        when {
            line.startsWith("Register A") -> a = line.split(": ")[1].toLong()
            line.startsWith("Register B") -> b = line.split(": ")[1].toLong()
            line.startsWith("Register C") -> c = line.split(": ")[1].toLong()
            line.startsWith("Program") -> program = line.split(": ")[1].split(",").map { it.toInt() }
        }
    }
    return Computer(a, b, c, program!!)
}

/*
;; bst A {store A mod 8 in B}
;; bxl 1 {store A xor 1 in A}
;; cdv B {store A / (2 ^ (A mod 8 xor 1)) in C}
;; bxl 5 {store A xor 5 in A}
;; bxc   {store B xor C in b}
;; out B {print B mod 8}
;; adv 3 {store A / 8 in A}
;; jnz 0 {GOTO start}
 */
