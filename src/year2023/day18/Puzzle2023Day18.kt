package year2023.day18

import Puzzle

fun main() {
    val puzzle = Puzzle2023Day18()
    puzzle.testAndSolveAndPrint()
}

class Puzzle2023Day18 : Puzzle<Int, Int>("2023", "18", 62, -1) {
    override fun solvePart1(input: List<String>): Int {
        val digPlan = parseDigPlanFromInput(input)
        val ground = digPlan.digTrenches().ground
        return digPlan.countEnclosedChars(ground)
    }

    override fun solvePart2(input: List<String>): Int {
        return input.size
    }
}

private fun parseDigPlanFromInput(input: List<String>): DigPlan {
    val digInstructions = mutableListOf<DigInstruction>()
    for (s in input) {
        val split = s.split(" ")
        val direction = when (split[0]) {
            "U" -> Direction.NORTH
            "D" -> Direction.SOUTH
            "L" -> Direction.WEST
            "R" -> Direction.EAST
            else -> error("Could not match '${split[0]}' to any Direction")
        }
        val amount = split[1].toInt()
        val color = split[2].removePrefix("(").removeSuffix(")")
        val digInstruction = DigInstruction(direction, amount, color)
        digInstructions.add(digInstruction)
    }
    return DigPlan(digInstructions)
}

private data class DigPlan(val digInstructions: List<DigInstruction>) {
    fun digTrenches(): Ground {
        val ground = Ground()
        // just use a big array as a starting base, don't care about expansion!
        var currentTile = Pair(500, 500)

        for (digInstruction in digInstructions) {
            val nextY = digInstruction.direction.moveY * digInstruction.amount
            val nextX = digInstruction.direction.moveX * digInstruction.amount
            val nextTile = Pair(currentTile.first + nextY, currentTile.second + nextX)

            ground.digLine(currentTile, nextTile, digInstruction.direction)
            currentTile = nextTile
        }
        return ground
    }

    fun countEnclosedChars(grid: Array<CharArray>): Int {
        val rows = grid.size
        val cols = grid[0].size
        var countHash = 0

        // Count all '#' characters
        for (i in 0 until rows) {
            for (j in 0 until cols) {
                if (grid[i][j] == '#') {
                    countHash++
                }
            }
        }

        // Iterative flood fill using a queue
        fun floodFill(startX: Int, startY: Int) {
            val queue: ArrayDeque<Pair<Int, Int>> = ArrayDeque()
            queue.add(Pair(startX, startY))

            while (queue.isNotEmpty()) {
                val (x, y) = queue.removeFirst()

                if (x !in 0 until rows || y !in 0 until cols || grid[x][y] != '.') {
                    continue
                }

                grid[x][y] = 'o' // Mark as visited

                // Add adjacent cells to queue
                queue.add(Pair(x + 1, y))
                queue.add(Pair(x - 1, y))
                queue.add(Pair(x, y + 1))
                queue.add(Pair(x, y - 1))
            }
        }

        // Apply flood fill from the edges
        for (i in 0 until rows) {
            floodFill(i, 0)
            floodFill(i, cols - 1)
        }
        for (j in 0 until cols) {
            floodFill(0, j)
            floodFill(rows - 1, j)
        }

        // Count enclosed '.' characters
        var countEnclosedDots = 0
        for (i in 0 until rows) {
            for (j in 0 until cols) {
                if (grid[i][j] == '.') {
                    countEnclosedDots++
                }
            }
        }

        return countHash + countEnclosedDots
    }
}

private data class DigInstruction(val direction: Direction, val amount: Int, val hexCode: String)

private enum class Direction(val moveY: Int, val moveX: Int) {
    NORTH(-1, 0),
    SOUTH(1, 0),
    WEST(0, -1),
    EAST(0, 1)
}

private class Ground {
    // just use a big array as a starting base, don't care about expansion!
    var ground = Array(1000) { CharArray(1000) { '.' } }
        private set
    private var xOffset = 0
    private var yOffset = 0

    fun digTile(tile: Pair<Int, Int>) {
        val internalY = tile.first + yOffset
        val internalX = tile.second + xOffset
        ensureCapacity(internalY, internalX)
        ground[internalY][internalX] = '#'
    }

    fun digLine(startTile: Pair<Int, Int>, endTile: Pair<Int, Int>, direction: Direction) {
        val internalEndY = endTile.first + yOffset
        val internalEndX = endTile.second + xOffset
        ensureCapacity(internalEndY, internalEndX)

        when (direction) {
            Direction.NORTH, Direction.SOUTH -> {
                val startX = startTile.second + xOffset
                for (y in minOf(startTile.first + yOffset, internalEndY)..maxOf(
                    startTile.first + yOffset,
                    internalEndY
                )) {
                    digTile(Pair(y, startX))
                }
            }

            Direction.WEST, Direction.EAST -> {
                val startY = startTile.first + yOffset
                for (x in minOf(startTile.second + xOffset, internalEndX)..maxOf(
                    startTile.second + xOffset,
                    internalEndX
                )) {
                    digTile(Pair(startY, x))
                }
            }
        }
    }

    private fun ensureCapacity(y: Int, x: Int) {
        var newGround = ground
        if (y < 0) {
            val additionalRows = Array(-y) { CharArray(ground[0].size) { '.' } }
            newGround = additionalRows + ground
            yOffset += -y
        }

        if (x < 0) {
            newGround = newGround.map { row ->
                CharArray(-x) { '.' } + row
            }.toTypedArray()
            xOffset += -x
        }

        val maxY = maxOf(y, newGround.size - 1)
        val maxX = maxOf(x, newGround[0].size - 1)
        if (maxY >= newGround.size || maxX >= newGround[0].size) {
            val expandedGround = Array(maxY + 1) { CharArray(maxX + 1) { '.' } }
            for (i in newGround.indices) {
                for (j in newGround[i].indices) {
                    expandedGround[i][j] = newGround[i][j]
                }
            }
            newGround = expandedGround
        }

        ground = newGround
    }
}