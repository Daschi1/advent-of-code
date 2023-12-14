package year2023.day14

import Puzzle

fun main() {
    val puzzle = Puzzle2023Day14()
    puzzle.testAndSolveAndPrint()
}

class Puzzle2023Day14 : Puzzle<Int, Int>("2023", "14", 136, 64) {
    override fun solvePart1(input: List<String>): Int {
        val plattform = parsePlattformFromInput(input)
        var sum = 0
        for (y in 0..<plattform.height) {
            for (x in 0..<plattform.width) {
                if (plattform.plattform[y][x] == 'O') sum += plattform.height - y
            }
        }
        return sum
    }

    override fun solvePart2(input: List<String>): Int {
        return input.size
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

    fun tiltNorth() {
        for (y in 0..<height) {
            for (x in 0..<width) {
                if (plattform[y][x] == 'O') sendNorth(y, x)
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