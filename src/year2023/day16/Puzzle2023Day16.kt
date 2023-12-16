package year2023.day16

import Puzzle

fun main() {
    val puzzle = Puzzle2023Day16()
    puzzle.testAndSolveAndPrint()
}

class Puzzle2023Day16 : Puzzle<Int, Int>("2023", "16", 46, -1) {
    override fun solvePart1(input: List<String>): Int {
        return input.size
    }

    override fun solvePart2(input: List<String>): Int {
        return input.size
    }
}