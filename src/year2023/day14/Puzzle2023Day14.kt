package year2023.day14

import Puzzle

fun main() {
    val puzzle = Puzzle2023Day14()
    puzzle.testAndSolveAndPrint()
}

class Puzzle2023Day14 : Puzzle<Int, Int>("2023", "14", 136, 64) {
    override fun solvePart1(input: List<String>): Int {
        val plattform = parsePlattformFromInput(input)
        plattform.tiltNorth()

        var sum = 0
        for (y in 0..<plattform.height) {
            for (x in 0..<plattform.width) {
                if (plattform.plattform[y][x] == 'O') sum += plattform.height - y
            }
        }
        return sum
    }

    override fun solvePart2(input: List<String>): Int {
        val plattform = parsePlattformFromInput(input)
        plattform.spinCycle(1_000_000_000)

        var sum = 0
        for (y in 0..<plattform.height) {
            for (x in 0..<plattform.width) {
                if (plattform.plattform[y][x] == 'O') sum += plattform.height - y
            }
        }
        return sum
    }
}

private fun parsePlattformFromInput(input: List<String>): Plattform {
    val height = input.size
    val width = input[0].length
    val plattform: Array<CharArray> = Array(height) { CharArray(width) }
    for ((y, s) in input.withIndex()) {
        for ((x, c) in s.toCharArray().withIndex()) {
            plattform[y][x] = c
        }
    }
    return Plattform(height, width, plattform)
}

private data class Plattform(val height: Int, val width: Int, val plattform: Array<CharArray>) {
    fun spinCycle(times: Int) {
        val cachedStates =
            hashMapOf<String, Int>()  // HashMap to store the unique states and their corresponding step numbers.
        var skip = true  // Flag to control skipping of cycles.

        var step = 0
        while (step < times) {
            // Execute tilting in all four directions.
            tiltNorth()
            tiltWest()
            tiltSouth()
            tiltEast()

            // Check for repeating states to optimize processing. We should only skip once, after that we are close to end and the rest can be calculated easily
            if (skip) {
                val currentState = joinPlattformToString()  // Convert the current state of the grid to a string.

                if (currentState in cachedStates) {
                    // If the current state is a repeat, calculate the cycle length.
                    val cycle = step - cachedStates.getValue(currentState)
                    // Calculate how many entire cycles can be skipped.
                    val cyclesLeft = (times - step) / cycle
                    step += cycle * cyclesLeft  // Skip the calculated number of cycles.
                    skip = false  // Disable further skipping.
                } else {
                    // If the state is new, add it to the cache.
                    cachedStates[currentState] = step
                }
            }
            step++  // Increment the step count.
        }
    }

    private fun joinPlattformToString() = plattform.joinToString("") { it.joinToString("") }

    fun tiltNorth() {
        for (y in 0..<height) {
            for (x in 0..<width) {
                if (plattform[y][x] == 'O') sendNorth(y, x)
            }
        }
    }

    fun tiltWest() {
        for (x in 0 until width) {
            for (y in 0 until height) {
                if (plattform[y][x] == 'O') sendWest(y, x)
            }
        }
    }

    fun tiltSouth() {
        for (y in height - 1 downTo 0) {
            for (x in 0 until width) {
                if (plattform[y][x] == 'O') sendSouth(y, x)
            }
        }
    }

    fun tiltEast() {
        for (x in width - 1 downTo 0) {
            for (y in 0 until height) {
                if (plattform[y][x] == 'O') sendEast(y, x)
            }
        }
    }


    private fun sendNorth(y: Int, x: Int) {
        if (y >= height || x >= width) return
        val char = plattform[y][x]
        if (char != 'O') return
        var newY = y
        for (currentY in (y - 1) downTo 0) {
            if (plattform[currentY][x] != '.') break
            newY = currentY
        }
        plattform[y][x] = '.'
        plattform[newY][x] = char
    }

    private fun sendWest(y: Int, x: Int) {
        if (y >= height || x < 0) return
        val char = plattform[y][x]
        if (char != 'O') return
        var newX = x
        for (currentX in (x - 1) downTo 0) {
            if (plattform[y][currentX] != '.') break
            newX = currentX
        }
        plattform[y][x] = '.'
        plattform[y][newX] = char
    }

    private fun sendSouth(y: Int, x: Int) {
        if (y < 0 || x >= width) return
        val char = plattform[y][x]
        if (char != 'O') return
        var newY = y
        for (currentY in (y + 1) until height) {
            if (plattform[currentY][x] != '.') break
            newY = currentY
        }
        plattform[y][x] = '.'
        plattform[newY][x] = char
    }

    private fun sendEast(y: Int, x: Int) {
        if (y >= height || x >= width) return
        val char = plattform[y][x]
        if (char != 'O') return
        var newX = x
        for (currentX in (x + 1) until width) {
            if (plattform[y][currentX] != '.') break
            newX = currentX
        }
        plattform[y][x] = '.'
        plattform[y][newX] = char
    }


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Plattform) return false

        if (height != other.height) return false
        if (width != other.width) return false
        if (!plattform.contentDeepEquals(other.plattform)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = height
        result = 31 * result + width
        result = 31 * result + plattform.contentDeepHashCode()
        return result
    }
}