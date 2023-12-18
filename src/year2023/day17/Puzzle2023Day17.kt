package year2023.day17

import Puzzle
import java.util.*

fun main() {
    val puzzle = Puzzle2023Day17()
    puzzle.testAndSolveAndPrint()
}

class Puzzle2023Day17 : Puzzle<Int, Int>("2023", "17", 102, 94) {
    override fun solvePart1(input: List<String>): Int {
        val map = parseMapFromInput(input)
        return map.runDijkstra(
            startStates = listOf(State(Point2D(0, 0), Point2D.EAST, 0)),
            minBlocks = 0,
            maxBlocks = 3
        )
    }

    override fun solvePart2(input: List<String>): Int {
        val map = parseMapFromInput(input)
        return map.runDijkstra(
            startStates = listOf(State(Point2D(0, 0), Point2D.EAST, 0), State(Point2D(0, 0), Point2D.SOUTH, 0)),
            minBlocks = 4,
            maxBlocks = 10
        )
    }
}

private fun parseMapFromInput(input: List<String>): Map {
    val height = input.size
    val width = input[0].length
    val grid: Array<IntArray> = Array(height) { IntArray(width) }
    for ((y, s) in input.withIndex()) {
        for ((x, c) in s.toCharArray().withIndex()) {
            grid[y][x] = c.digitToInt()
        }
    }
    return Map(height, width, grid)
}

private data class Map(val height: Int, val width: Int, val grid: Array<IntArray>) {
    fun runDijkstra(startStates: List<State>, minBlocks: Int, maxBlocks: Int): Int {
        val end = Point2D(height - 1, width - 1)

        val costsFromStart = mutableMapOf<State, Int>().withDefault { Int.MAX_VALUE }
        val unvisitedNodes = PriorityQueue<StateWithCost>()

        for (state in startStates) {
            costsFromStart[state] = 0
            unvisitedNodes.add(StateWithCost(state, 0))
        }

        while (unvisitedNodes.isNotEmpty()) {
            val currentNode = unvisitedNodes.poll()

            if (currentNode.state.point == end) {
                return currentNode.cost
            }

            currentNode.state.next(minBlocks, maxBlocks)
                .filter { it.point.y in 0..<height && it.point.x in 0..<width }
                .forEach { next ->
                    val nextCost = currentNode.cost + grid[next.point.y][next.point.x]
                    if (nextCost < costsFromStart.getValue(next)) {
                        costsFromStart[next] = nextCost
                        unvisitedNodes.add(StateWithCost(next, nextCost))
                    }
                }
        }
        error("Could not find a path")
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Map) return false

        if (height != other.height) return false
        if (width != other.width) return false
        if (!grid.contentDeepEquals(other.grid)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = height
        result = 31 * result + width
        result = 31 * result + grid.contentDeepHashCode()
        return result
    }
}

private data class Point2D(val y: Int, val x: Int) {
    operator fun plus(other: Point2D): Point2D = Point2D(y + other.y, x + other.x)

    operator fun times(other: Int): Point2D = Point2D(y * other, x * other)

    companion object {
        val NORTH = Point2D(-1, 0)
        val EAST = Point2D(0, 1)
        val SOUTH = Point2D(1, 0)
        val WEST = Point2D(0, -1)
    }
}

private data class State(val point: Point2D, val dir: Point2D, val blocks: Int) {
    fun next(minBlocks: Int, maxBlocks: Int) = buildList {
        when {
            blocks < minBlocks -> add(copy(point = point + dir, dir = dir, blocks = blocks + 1))
            else -> {
                val left = Point2D(dir.x, dir.y)
                val right = Point2D(-dir.x, -dir.y)

                add(copy(point = point + left, dir = left, blocks = 1))
                add(copy(point = point + right, dir = right, blocks = 1))

                if (blocks < maxBlocks) {
                    add(copy(point = point + dir, dir = dir, blocks = blocks + 1))
                }
            }
        }
    }
}

private data class StateWithCost(val state: State, val cost: Int) : Comparable<StateWithCost> {
    override fun compareTo(other: StateWithCost): Int {
        return cost compareTo other.cost
    }
}