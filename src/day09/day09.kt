package day09

import utils.readInput
import java.util.*
import kotlin.collections.ArrayList

fun main() {
    //part1()
    part2()
}

private fun part1() {
    val input = readInput("Day09").first()
    val diskMap = parseInput(input)
    println("Disk map: $diskMap")
    println("input $input")
    reorderEmpty(diskMap)
    println("reordered $diskMap")
    val result = calculateChecksum(diskMap)
    println("Result: $result")
}

private fun part2() {
    val input = readInput("Day09").first()
    val diskMap = parseInput(input)
    //println("Disk map: $diskMap")
    val blocksMap = diskMap.toBlocksMap()
    //println("Blocks map: $blocksMap")
    reorderFiles(diskMap)
    var stringRepresentation = ""
    diskMap.forEach {
        val sign = if(it<0) {
            "."
        } else {
            "$it"
        }
        stringRepresentation += sign
    }
    println("output: $stringRepresentation")
    println("Result: ${calculateChecksum(diskMap)}")
}

private const val DOT = -1L
private fun parseInput(input: String): MutableList<Long> {
    val result = LinkedList<Long>()
    var id = 0L
    input.forEachIndexed { strIndex, number ->
        val times = number.code - '0'.code
        if (strIndex % 2 == 0) {
            repeat(times) {
                result.add(id)
            }
            id++
        } else {
            repeat(times) {
                result.add(DOT)
            }
        }
    }
    return ArrayList(result)
}

private data class Block (
    val size: Int,
    val id: Long,
    val startIndex: Int,
): Comparable<Block> {
    override fun compareTo(other: Block): Int {
        // Compare by id first
        val idComparison = id.compareTo(other.id)
        if (idComparison != 0) {
            return idComparison
        }

        // If ids are equal, compare by startIndex
        return startIndex.compareTo(other.startIndex)
    }
}

private fun getBlocksMap(input: String): LinkedList<Block> {
    val result = LinkedList<Block>()
    var id = 0L
    input.forEachIndexed { strIndex, number ->
        val size = number.code - '0'.code
        if (strIndex % 2 == 0) {
            result.add(Block(size, id, strIndex))
            id++
        } else {
            result.add(Block(size, -1, strIndex))
        }
    }
    return result
}

private fun List<Long>.toBlocksMap(): LinkedList<Block> {
    val blocks = LinkedList<Block>()
    var i = 0
    while(i < size) {
        val blockElement = this[i]
        val blockStart = i
        var blockSize = 0
        while(i < size && this[i] == blockElement) {
            blockSize++
            i++
        }
        blocks.add(Block(blockSize, blockElement, blockStart))
    }
    return blocks
}

private fun reorderEmpty(diskMap: MutableList<Long>) {
    var leftIndex = 0
    var rightIndex = diskMap.size - 1
    while (leftIndex < rightIndex) {
        while(diskMap[leftIndex] != DOT && leftIndex < rightIndex) {
            leftIndex++
        }
        while(diskMap[rightIndex] == DOT && leftIndex < rightIndex) {
            rightIndex--
        }
        if (leftIndex < rightIndex) {
            val buffer = diskMap[leftIndex]
            diskMap[leftIndex] = diskMap[rightIndex]
            diskMap[rightIndex] = buffer
        }
    }
}

private fun reorderFiles(diskMap: MutableList<Long>) {
    val originalBocks = diskMap.toBlocksMap()
    val initialBlocks = originalBocks.reversed()
        .filter {
            it.id >= 0
        }
    var count = 0
    println("Processing...")
    initialBlocks.forEach { initialBlock ->
        count++
        if (count % 10 == 0) {
            println("Processing [$count] id: ${initialBlock.id}")
        }
        val blocks = diskMap.toBlocksMap()
        val file = blocks.first { it.id == initialBlock.id }
        blocks.firstOrNull { it.id == DOT && it.size >= file.size && it.startIndex < initialBlock.startIndex }?.let { emptyBlock ->
            for (i in 0..<file.size) {
                val buffer = diskMap[emptyBlock.startIndex + i]
                diskMap[emptyBlock.startIndex + i] = diskMap[file.startIndex + i]
                diskMap[file.startIndex + i] = buffer
            }
        }
    }
}

private fun calculateChecksum(diskMap: List<Long>): Long {
    var result = 0L
    diskMap
        .forEachIndexed { i, id ->
            val multiplier = if(id < 0) {
                0
            } else {
                id
            }
            result += i * multiplier
    }
    return result
}