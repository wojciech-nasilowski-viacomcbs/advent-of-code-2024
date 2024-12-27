package day11

import kotlinx.coroutines.*
import utils.readInput
import java.util.*

fun main() {
    val input = readInput("Day11Test").first()
    part1(input)
    part2(input)
}

private data class Item(
    var value: Long,
    var next: Item? = null,
)

private class MyLinkedList() {
    var start: Item? = null
    fun add(value: Long) {
        val newItem = Item(value)
        if (start == null) {
            start = newItem
            return
        }
        var current = start
        while (current?.next != null) {
            current = current.next
        }
        current?.next = newItem
    }

    fun size(): Int {
        if (start == null) {
            return 0
        }
        var current = start
        var count = 0

        while (current != null) {
            count++
            current = current.next
        }
        return count
    }

    fun toList(): List<Long> {
        if (start == null) {
            return emptyList()
        }
        var current = start
        val list = LinkedList<Item>()
        while (current != null) {
            list.add(current)
            current = current.next
        }
        return list.map {
            it.value
        }
    }
}

private fun part1(input: String): Int {
    var stones = LinkedList(input.split(" ").map { it.toLong() })
    val n = 25
    var items = 0
    repeat(n) {
        println("Blink: ${it + 1}")
        val newStones = LinkedList<Long>()
        stones.forEach { stone ->
            val stringStone = stone.toString()
            when {
                stone == 0L -> newStones.add(1)
                stringStone.length % 2 == 0 -> {
                    val halfLength = stringStone.length / 2
                    newStones.add(stringStone.substring(0, halfLength).toLong())
                    newStones.add(stringStone.substring(halfLength).toLong())
                }

                else -> newStones.add(stone * 2024)
            }
        }
        stones = newStones
        items = newStones.size
    }
    println("Items 1: $items")
    return items
}

private fun part2(input: String): Int {
    val stones = LinkedList(input.split(" ").map { it.toLong() })
    val n = 75
    var stonesMap = HashMap<Long, Long>()
    val memo = HashMap<Long, List<Long>>()
    stones.forEach {
        stonesMap[it] = stonesMap.getOrDefault(it, 0) + 1
    }

    repeat(n) {
        println("Blink: ${it + 1}")
        val newStonesMap = HashMap<Long, Long>()
        stonesMap.keys.forEach { stone ->
            val numberOfEl = stonesMap.getOrDefault(stone, 0)
            val stringStone = stone.toString()
            val itemsToAdd = when {
                stone == 0L -> listOf(1L)
                stringStone.length % 2 == 0 -> {
                    val halfLength = stringStone.length / 2
                    listOf(stringStone.substring(0, halfLength).toLong(), stringStone.substring(halfLength).toLong())
                }
                else -> listOf(stone * 2024)
            }
            itemsToAdd.forEach { itemToAdd ->
                newStonesMap[itemToAdd] = newStonesMap.getOrDefault(itemToAdd, 0) + numberOfEl
            }
        }
        stonesMap = newStonesMap
    }
    val items = stonesMap.values.sum()
    println("Items 1: $items")
    return items.toInt()
}
