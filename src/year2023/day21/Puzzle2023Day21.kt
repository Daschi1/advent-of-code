package year2023.day21

import Puzzle
import java.util.*

fun main() {
    val puzzle = Puzzle2023Day21()
    puzzle.testAndSolveAndPrint()
}

class Puzzle2023Day21 : Puzzle<Int, Int>("2023", "21", 16, -1) {
    override fun solvePart1(input: List<String>): Int {
        val grid = parseGridFromInput(input)
        // test input for part1 requires only 6 iterations
        val steps = if (input.size == 11) 6 else 64
        val tiles = grid.calculatePossibleTilesAfter(steps)
        return tiles.size
    }

    override fun solvePart2(input: List<String>): Int {
        return input.size
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
        if (y !in 0..<height) return false
        if (x !in 0..<width) return false
        return grid[y][x] == '.'
    }
}

private enum class Direction(val moveY: Int, val moveX: Int) {
    NORTH(-1, 0),
    SOUTH(1, 0),
    WEST(0, -1),
    EAST(0, 1)
}