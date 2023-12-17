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

private data class Map(val height: Int, val width: Int, val grid: Array<IntArray>) {
    fun findMostEfficientRouteFromTo(startY: Int, startX: Int, endY: Int, endX: Int): List<Pair<Int, Int>> {
        val visited = Array(height) { Array(width) { Array(4) { BooleanArray(4) } } }
        val distance = Array(height) { Array(width) { IntArray(4) { Int.MAX_VALUE } } }
        val path = Array(height) { Array(width) { Array<Pair<Int, Int>?>(4) { null } } }
        val directionPath = Array(height) { Array(width) { IntArray(4) { -1 } } }
        val directions = listOf(Pair(0, 1), Pair(1, 0), Pair(0, -1), Pair(-1, 0))

        val queue =
            PriorityQueue(compareBy<Triple<Pair<Int, Int>, Int, Int>> { distance[it.first.first][it.first.second][it.second] })
        for (i in 0 until 4) {
            distance[startY][startX][i] = 0
            queue.add(Triple(Pair(startY, startX), i, 1))
        }

        while (queue.isNotEmpty()) {
            val (current, dir, steps) = queue.poll()
            val (y, x) = current
            if (visited[y][x][dir][steps]) continue
            visited[y][x][dir][steps] = true

            if (y == endY && x == endX) break

            for ((i, direction) in directions.withIndex()) {
                val newY = y + direction.first
                val newX = x + direction.second
                val newSteps = if (dir == i) steps + 1 else 1

                if (newY in 0 until height && newX in 0 until width && newSteps <= 3 && !visited[newY][newX][i][newSteps]) {
                    val newDist = distance[y][x][dir] + grid[newY][newX]
                    if (newDist < distance[newY][newX][i]) {
                        distance[newY][newX][i] = newDist
                        path[newY][newX][i] = current
                        directionPath[newY][newX][i] = dir
                        queue.add(Triple(Pair(newY, newX), i, newSteps))
                    }
                }
            }
        }

        // Reconstruct path
        val route = mutableListOf<Pair<Int, Int>>()
        var current: Pair<Int, Int>? = Pair(endY, endX)
        var currentDir = directions.indices.minByOrNull { distance[endY][endX][it] } ?: return emptyList()

        while (current != null && current != Pair(startY, startX)) {
            route.add(0, current)
            val prevDir = directionPath[current.first][current.second][currentDir]
            current = if (prevDir != -1) path[current.first][current.second][prevDir] else null
            currentDir = prevDir
        }
        route.add(0, Pair(startY, startX)) // Add starting point at beginning
        return route
    }
}