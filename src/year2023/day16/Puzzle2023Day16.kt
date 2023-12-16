package year2023.day16

import Puzzle

fun main() {
    val puzzle = Puzzle2023Day16()
    puzzle.testAndSolveAndPrint()
}

class Puzzle2023Day16 : Puzzle<Int, Int>("2023", "16", 46, 51) {
    override fun solvePart1(input: List<String>): Int {
        val grid = parseGridFromInput(input)
        val energizedTiles = grid.sendLightFrom(0, 0, Direction.EAST)
        return energizedTiles.size
    }

    override fun solvePart2(input: List<String>): Int {
        return input.size
    }
}

private fun parseGridFromInput(input: List<String>): Grid {
    val height = input.size
    val width = input[0].length
    val grid: Array<CharArray> = Array(height) { CharArray(width) }
    for ((y, s) in input.withIndex()) {
        for ((x, c) in s.toCharArray().withIndex()) {
            grid[y][x] = c
        }
    }
    return Grid(height, width, grid)
}

private enum class Direction(val moveY: Int, val moveX: Int) {
    NORTH(1, 0),
    SOUTH(-1, 0),
    WEST(0, -1),
    EAST(0, 1);
}

private data class Grid(val height: Int, val width: Int, val grid: Array<CharArray>) {
    fun sendLightFrom(y: Int, x: Int, direction: Direction): Set<Pair<Int, Int>> {
        val visitedStates = HashSet<Triple<Int, Int, Direction>>()
        val energizedTiles = mutableSetOf<Pair<Int, Int>>()

        val queue = ArrayDeque<Triple<Int, Int, Direction>>()
        queue.add(Triple(y, x, direction))
        while (queue.isNotEmpty()) {
            val (currentY, currentX, currentDirection) = queue.removeFirst()

            // Add to light path
            energizedTiles.add(Pair(currentY, currentX))

            // Calculate next tiles
            val nextTiles = calculateNextLightTiles(currentY, currentX, currentDirection)
            for ((nextY, nextX, nextDirection) in nextTiles) {
                val nextState = Triple(nextY, nextX, nextDirection)

                // Check if the state has been visited before
                if (visitedStates.add(nextState)) {
                    queue.add(nextState)
                }
            }
        }

        return energizedTiles
    }


    fun calculateNextLightTiles(y: Int, x: Int, direction: Direction): List<Triple<Int, Int, Direction>> {
        if (y < 0 || y >= height || x < 0 || x >= width) return emptyList()
        val nextDirections = when (val currentTile = grid[y][x]) {
            '.' -> listOf(direction)
            '/' -> {
                when (direction) {
                    Direction.NORTH -> listOf(Direction.WEST)
                    Direction.SOUTH -> listOf(Direction.EAST)
                    Direction.WEST -> listOf(Direction.NORTH)
                    Direction.EAST -> listOf(Direction.SOUTH)
                }
            }

            '\\' -> {
                when (direction) {
                    Direction.NORTH -> listOf(Direction.EAST)
                    Direction.SOUTH -> listOf(Direction.WEST)
                    Direction.WEST -> listOf(Direction.SOUTH)
                    Direction.EAST -> listOf(Direction.NORTH)
                }
            }

            '|' -> {
                when (direction) {
                    Direction.NORTH -> listOf(Direction.NORTH)
                    Direction.SOUTH -> listOf(Direction.SOUTH)
                    Direction.WEST,
                    Direction.EAST -> listOf(Direction.NORTH, Direction.SOUTH)
                }
            }

            '-' -> {
                when (direction) {
                    Direction.WEST -> listOf(Direction.WEST)
                    Direction.EAST -> listOf(Direction.EAST)
                    Direction.NORTH,
                    Direction.SOUTH -> listOf(Direction.WEST, Direction.EAST)
                }
            }

            else -> throw IllegalStateException("Tile '$currentTile' is not allowed")
        }
        return nextDirections.mapNotNull {
            val next = Triple(y + it.moveY, x + it.moveX, it)
            if (next.first < 0 || next.first >= height || next.second < 0 || next.second >= width) null
            else next
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Grid) return false

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