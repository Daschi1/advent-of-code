package year2023.day15

import Puzzle

fun main() {
    val puzzle = Puzzle2023Day15()
    puzzle.testAndSolveAndPrint()
}

class Puzzle2023Day15 : Puzzle<Int, Int>("2023", "15", 1320, -1) {
    override fun solvePart1(input: List<String>): Int {
        val steps = input[0].split(",")
        var sum = 0
        for (step in steps) {
            sum += calculateHash(step)
        }
        return sum
    }

    override fun solvePart2(input: List<String>): Int {
        return input.size
    }
}

private fun calculateHash(input: String): Int {
    var currentValue = 0
    for (c in input.toCharArray()) {
        val asciValue = c.code
        currentValue += asciValue
        currentValue *= 17
        currentValue %= 256
    }
    return currentValue
}