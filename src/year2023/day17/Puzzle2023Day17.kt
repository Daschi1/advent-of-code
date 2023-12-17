package year2023.day17

import Puzzle
import println
import java.util.*

fun main() {
    val puzzle = Puzzle2023Day17()
    puzzle.testAndSolveAndPrint()
}

class Puzzle2023Day17 : Puzzle<Int, Int>("2023", "17", 102, -1) {
    override fun solvePart1(input: List<String>): Int {
        val map = parseMapFromInput(input)
        val cost = map.findMostEfficientRouteFromTo(0, 0, map.height - 1, map.width - 1)
        cost.println()
        println("---")
        for (y in 0..<map.height) {
            for (x in 0..<map.width) {
                if (Pair(y, x) in cost) print("#")
                else print(".")
            }
            print("\n")
        }
        return input.size
    }

    override fun solvePart2(input: List<String>): Int {
        return input.size
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

private data class Node(val y: Int, val x: Int, val distance: Int)

private enum class Direction(val stepY: Int, val stepX: Int) {
    NORTH(-1, 0),
    SOUTH(1, 0),
    WEST(0, -1),
    EAST(0, 1)
}

private data class Map(val height: Int, val width: Int, val grid: Array<IntArray>) {
    fun findMostEfficientRouteFromTo(startY: Int, startX: Int, endY: Int, endX: Int): List<Pair<Int, Int>> {
        // initial distances all "infinity"
        val distancesFromStart = Array(height) { IntArray(width) { Int.MAX_VALUE } }
        val visitedNodes = Array(height) { BooleanArray(width) { false } }
        // store previousNodes (y, x) of node [y][x] in the path
        val previousNodes = Array(height) { Array<Pair<Int, Int>?>(width) { null } }

        // define unvisitedNodes to choose lowest distance first
        val unvisitedNodes = PriorityQueue<Node>(compareBy { it.distance })
        // setup and add startNode
        distancesFromStart[startY][startX] = 0
        val startNode = Node(startY, startX, 0)
        unvisitedNodes.add(startNode)

        // while there are nodes to visit
        while (unvisitedNodes.isNotEmpty()) {
            val currentNode = unvisitedNodes.poll()

            // exit if endNode
            if (currentNode.y == endY && currentNode.x == endX) break

            // skip currentNode if already been visited, else mark as visited
            if (visitedNodes[currentNode.y][currentNode.x]) continue
            else visitedNodes[currentNode.y][currentNode.x] = true

            // for each possible next node
            for (nextDirection in Direction.entries) {
                val nextY = currentNode.y + nextDirection.stepY
                val nextX = currentNode.x + nextDirection.stepX
                // if nextNode is not inside grid bounds or has been visited, skip
                if (nextY !in 0..<height || nextX !in 0..<width || visitedNodes[nextY][nextX]) continue
                // calculate nextDistance by using distancesFromStart of currentNode + the cost of travelling to nextNode
                val nextDistance = distancesFromStart[currentNode.y][currentNode.x] + grid[nextY][nextX]
                // if nextDistance is better than previous found distance to nextNode
                if (nextDistance < distancesFromStart[nextY][nextX]) {
                    // update best distance, set previousNode on best path and add nexNode to unvisitedNodes
                    distancesFromStart[nextY][nextX] = nextDistance
                    previousNodes[nextY][nextX] = Pair(currentNode.y, currentNode.x)
                    val nextNode = Node(nextY, nextX, nextDistance)
                    unvisitedNodes.add(nextNode)
                }
            }
        }
        return reconstructPath(previousNodes, startY, startX, endY, endX)
    }

    private fun reconstructPath(
        previousNodes: Array<Array<Pair<Int, Int>?>>,
        startY: Int,
        startX: Int,
        endY: Int,
        endX: Int
    ): List<Pair<Int, Int>> {
        val path = mutableListOf<Pair<Int, Int>>()
        // start with endNode as currentNode
        var currentY = endY
        var currentX = endX

        // as long as endNode != startNode, add currentNode to path and look again for currentNode
        while (currentY != startY || currentX != startX) {
            path.add(Pair(currentY, currentX))
            val previous = previousNodes[currentY][currentX] ?: break
            currentY = previous.first
            currentX = previous.second
        }
        path.add(Pair(startY, startX))

        return path.reversed()
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