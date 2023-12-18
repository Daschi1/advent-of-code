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
        return map.runDijkstra(true)
    }

    override fun solvePart2(input: List<String>): Int {
        val map = parseMapFromInput(input)
        return map.runDijkstra(false)
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
    fun runDijkstra(partOne: Boolean): Int {
        val visitedNodes = HashSet<Node>()
        val queue = PriorityQueue<State>()
        queue.add(State(Node(0, 1, Direction.EAST, 1), grid[0][1]))
        queue.add(State(Node(1, 0, Direction.SOUTH, 1), grid[1][0]))

        while (queue.isNotEmpty()) {
            val currentNode = queue.remove()
            if (currentNode.node in visitedNodes) continue
            visitedNodes.add(currentNode.node)

            if (currentNode.node.y == height - 1 && currentNode.node.x == width - 1 &&
                (partOne || currentNode.node.steps > 2)
            ) {
                return currentNode.cost
            }
            if (partOne) {
                currentNode.addNextPartOne(queue, grid)
            } else {
                currentNode.addNextPartTwo(queue, grid)
            }
        }
        return -1
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

private enum class Direction(val moveY: Int, val moveX: Int) {
    NORTH(-1, 0), EAST(0, 1), SOUTH(1, 0), WEST(0, -1);

    fun turnLeft(): Direction = when (this) {
        NORTH -> WEST
        WEST -> SOUTH
        SOUTH -> EAST
        EAST -> NORTH
    }

    fun turnRight(): Direction = when (this) {
        NORTH -> EAST
        EAST -> SOUTH
        SOUTH -> WEST
        WEST -> NORTH
    }
}

private data class Node(val y: Int, val x: Int, val direction: Direction, val steps: Int)

private class State(val node: Node, val cost: Int) : Comparable<State> {
    override fun compareTo(other: State): Int {
        var comparison = cost - other.cost
        if (comparison == 0 && node.direction == other.node.direction) {
            comparison = node.steps - other.node.steps
        }
        if (comparison == 0) {
            comparison = other.node.y + other.node.x - node.y - node.x
        }
        return comparison
    }

    private fun addNextWhenInBounds(states: MutableCollection<State>, direction: Direction, costs: Array<IntArray>) {
        val nextY = node.y + direction.moveY
        val nextX = node.x + direction.moveX
        if (nextY in costs.indices && nextX in costs[0].indices) {
            val nextSteps = if (node.direction == direction) node.steps + 1 else 0
            val nextState = State(Node(nextY, nextX, direction, nextSteps), cost + costs[nextY][nextX])
            states.add(nextState)
        }
    }

    fun addNextPartOne(states: MutableCollection<State>, costs: Array<IntArray>) {
        if (node.steps < 2) {
            addNextWhenInBounds(states, node.direction, costs)
        }
        addNextWhenInBounds(states, node.direction.turnLeft(), costs)
        addNextWhenInBounds(states, node.direction.turnRight(), costs)
    }

    fun addNextPartTwo(states: MutableCollection<State>, costs: Array<IntArray>) {
        if (node.steps < 9) {
            addNextWhenInBounds(states, node.direction, costs)
        }
        if (node.steps > 2) {
            addNextWhenInBounds(states, node.direction.turnLeft(), costs)
            addNextWhenInBounds(states, node.direction.turnRight(), costs)
        }
    }
}