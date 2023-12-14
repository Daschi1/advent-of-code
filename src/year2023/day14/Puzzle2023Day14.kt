package year2023.day14

import Puzzle

fun main() {
    val puzzle = Puzzle2023Day14()
    puzzle.testAndSolveAndPrint()
}

class Puzzle2023Day14 : Puzzle<Int, Int>("2023", "14", 136, -1) {
    override fun solvePart1(input: List<String>): Int {
        return input.size
    }

    override fun solvePart2(input: List<String>): Int {
        return input.size
    }
}