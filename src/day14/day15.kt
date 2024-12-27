package day05

import utils.readInput
import java.math.BigDecimal

fun main() {
    //part1()
    part2()
}

private fun part1() {
    val input = readInput("Day05")
    val (orders, updates) = input.parseData()

    val result = updates.filter { update ->
        var keep = true
        for(order in orders) {
            val lowerIndex = update.items.indexOf(order.before)
            val higherIndex = update.items.indexOf(order.after)
            if (lowerIndex >= 0 && higherIndex >= 0) {
                if (lowerIndex > higherIndex) {
                    keep = false
                    break
                }
            }
        }
        keep
    }.sumOf { update ->
        println("update: $update")
        update.items[update.items.size / 2]
    }
    //println("orders: $orders ")
    //println("updates: $updates ")

    println("Result #1: $result")
}

private fun part2() {
    val input = readInput("Day05")
    val (orders, updates) = input.parseData()

    val misaligned = updates.filter { update ->
        breaksRule(orders, update.items)
    }
    val sortedUpdates = mutableListOf<List<Int>>()
    misaligned.forEach { update ->
        val ordersThatApply = orders.filter {
            update.items.contains(it.before) && update.items.contains(it.after)
        }
        val sortedUpdate = sortItemsByPrecedence(update.items, ordersThatApply)
        sortedUpdates.add(sortedUpdate)
    }
    println("Sorted: $sortedUpdates")
    val result = sortedUpdates.sumOf { update ->
        println("update: $update")
        update[update.size / 2]
    }
    println("Result #2: $result")
}

private fun sortItemsByPrecedence(items: List<Int>, orders: List<Order>): List<Int> {
    // Create a directed graph to represent the precedence rules
    val graph = mutableMapOf<Int, MutableSet<Int>>()
    orders.forEach { order ->
        graph.getOrPut(order.before) { mutableSetOf() }.add(order.after)
    }

    // Perform topological sort to determine the correct order
    val sortedItems = mutableListOf<Int>()
    val visited = mutableSetOf<Int>()
    fun dfs(item: Int) {
        if (item in visited) return
        visited.add(item)

        graph[item]?.forEach { neighbor ->
            dfs(neighbor)
        }

        sortedItems.add(0, item) // Add to the beginning of the list
    }

    items.forEach { item ->
        dfs(item)
    }

    return sortedItems
}

private fun breaksRule(
    orders: List<Order>,
    updateItems: List<Int>,
): Boolean {
    var keep = true
    for (order in orders) {
        val lowerIndex = updateItems.indexOf(order.before)
        val higherIndex = updateItems.indexOf(order.after)
        if (lowerIndex >= 0 && higherIndex >= 0) {
            if (lowerIndex > higherIndex) {
                keep = false
                break
            }
        }
    }
    return !keep
}


private data class Order(
    val before: Int,
    val after: Int,
)

private data class Update(
    val items: List<Int>,
)

private fun List<String>.parseData(): Pair<List<Order>, List<Update>> {
    val orders = mutableListOf<Order>()
    val updates = mutableListOf<Update>()
    forEach {
        if (it.contains("|")) {
            orders.add(Order(it.split("|")[0].toInt(), it.split("|")[1].toInt()))
        }
        if (it.contains(",")) {
            updates.add(Update(it.split(",").toList().map { it.toInt() }))
        }
    }
   return orders to updates
}