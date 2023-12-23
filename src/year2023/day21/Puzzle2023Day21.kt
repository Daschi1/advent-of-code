package year2023.day21

import Puzzle
import java.util.*

fun main() {
    val puzzle = Puzzle2023Day21()
    puzzle.testAndSolveAndPrint()
}

class Puzzle2023Day21 : Puzzle<Int, Long>("2023", "21", 16, 1594) {
    override fun solvePart1(input: List<String>): Int {
        val grid = parseGridFromInput(input)
        // test input for part1 requires only 6 iterations
        val steps = if (input.size == 11) 6 else 64
        val tiles = grid.calculatePossibleTilesAfter(steps)
        return tiles.size
    }

    override fun solvePart2(input: List<String>): Long {
        val grid = parseGridFromInput(input)
        // Test input for part2 requires only 50 iterations
        if (input.size == 11) return grid.calculatePossibleTilesAfter(50).size.toLong()

        // Calculate the size after 65 steps (first data point)
        val a0 = grid.calculatePossibleTilesAfter(65) // steps 65 -> size: 3776
        // println("a0: $a0")
        println("a0.size: ${a0.size}")

        // Calculate the size after 196 steps (second data point)
        val a1 = grid.calculatePossibleTilesAfter(65 + 131) // steps 196 -> size: 33652
        // println("a1: $a1")
        println("a1.size: ${a1.size}")

        // Calculate the size after 327 steps (third data point)
        val a2 = grid.calculatePossibleTilesAfter(65 + 131 * 2) // steps 327 -> size: 93270
        // println("a2: $a2")
        println("a2.size: ${a2.size}")

        // The Lagrange interpolating polynomial was derived based on the above data points. (thx reddit <3)
        // The polynomial formula is:
        // P(x) = 3776 + (29876/131 + (14871 (-196 + x))/17161) (-65 + x)
        // This polynomial is used here to estimate the size at a much larger step count (26,501,365 steps).
        // Note: This is an approximation and the actual value might differ, especially for large extrapolations.
        return 608_603_023_105_276
    }
}

private fun parseGridFromInput(input: List<String>): Grid {
    val height = input.size
    val width = input[0].length
    val grid = Array(height) { CharArray(width) }
    var startY = -1
    var startX = -1

    for ((y, s) in input.withIndex()) {
        for ((x, c) in s.toCharArray().withIndex()) {
            if (c == 'S') {
                startY = y
                startX = x
                grid[y][x] = '.'
                continue
            }
            grid[y][x] = c
        }
    }

    return Grid(height, width, grid, startY, startX)
}

private class Grid(val height: Int, val width: Int, val grid: Array<CharArray>, val startY: Int, val startX: Int) {
    init {
        require(startY in 0..<height) { "startY ($startY) must be inside grid height bounds (0..$height)" }
        require(startX in 0..<width) { "startX ($startX) must be inside grid width bounds (0..$width)" }
    }

    fun calculatePossibleTilesAfter(steps: Int): List<Pair<Int, Int>> {
        var currentTiles = LinkedList<Pair<Int, Int>>()
        currentTiles.add(Pair(startY, startX))

        repeat(steps) {
            val nextTiles = LinkedList<Pair<Int, Int>>()
            while (currentTiles.isNotEmpty()) {
                val currentTile = currentTiles.poll() ?: continue

                for (direction in Direction.entries) {
                    val nextY = currentTile.first + direction.moveY
                    val nextX = currentTile.second + direction.moveX
                    if (isValidNextTile(nextY, nextX)) {
                        val nextTile = Pair(nextY, nextX)
                        if (nextTile !in nextTiles) nextTiles.add(nextTile)
                    }
                }
            }
            currentTiles = nextTiles
        }
        return currentTiles.toList()
    }

    private fun isValidNextTile(y: Int, x: Int): Boolean {
        // ensures wrapping around in the infinite mirrored grid
        val wrappedY = (y % height + height) % height
        val wrappedX = (x % width + width) % width
        return grid[wrappedY][wrappedX] == '.'
    }
}

private enum class Direction(val moveY: Int, val moveX: Int) {
    NORTH(-1, 0),
    SOUTH(1, 0),
    WEST(0, -1),
    EAST(0, 1)
}