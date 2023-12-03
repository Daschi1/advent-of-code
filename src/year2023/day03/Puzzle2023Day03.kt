package year2023.day03

import Puzzle

fun main() {
    val puzzle = Puzzle2023Day03()
    puzzle.testAndSolveAndPrint()
}

class Puzzle2023Day03 : Puzzle<Int, Int>("2023", "03", -1, -1) {
    override fun solvePart1(input: List<String>): Int {
        return input.size
    }

    override fun solvePart2(input: List<String>): Int {
        return input.size
    }
}