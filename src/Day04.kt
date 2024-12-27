import utils.readInput

fun main() {
    part2()
}

fun part1() {
    val word = "XMAS"
    var count = 0
    //val input = readInput("Day04_test")
    val input = readInput("Day04")
    fun traverse(x: Int, y: Int, dx: Int, dy: Int) {
        var i = x
        var j = y
        repeat(word.length) { index ->
            val letter = try {
                input[j][i]
            } catch (e: IndexOutOfBoundsException) {
                return
            }
            if (word[index] != letter) {
                return
            } else if(index == word.length-1) {
                count++
            }
            i += dx
            j += dy
        }
    }
    input.forEachIndexed {y, line->
        line.forEachIndexed { x, letter ->
            if (letter == word.first()) {
                val directions = listOf (-1,0,1)
                directions.flatMap { first ->
                    directions.map { second ->
                        Pair(first, second)
                    }
                }.forEach {(dx, dy) ->
                    traverse(x, y, dx, dy)
                }
            }
        }
    }
    println("Result: $count")
}

fun part2() {
    var a: String? = null
    val contains = listOf("a", "b").contains(a)
    println("WNASILOWSKILOG")
    val word = "MAS"
    var count = 0
    //val input = readInput("Day04_test")
    val input = readInput("Day04")
    fun getFromInput(x: Int, y: Int) = input[y][x]
    fun visit(x: Int, y: Int){
        try {
            val vectorAIs = "${getFromInput(x-1,y-1)}${getFromInput(x+1,y+1)}".toSortedSet() == "MS".toSortedSet()
            val vectorBIs = "${getFromInput(x-1,y+1)}${getFromInput(x+1,y-1)}".toSortedSet() == "MS".toSortedSet()
            if (vectorAIs && vectorBIs) {
               count++
            }
        } catch (e: IndexOutOfBoundsException) {
            return
        }
    }
    input.forEachIndexed {y, line->
        line.forEachIndexed { x, letter ->
            if (letter == 'A') {
                visit(x, y)
            }
        }
    }
    println("Result: $count")

}