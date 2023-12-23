package year2023.day21

import Puzzle

fun main() {
    val puzzle = Puzzle2023Day21()
    puzzle.testAndSolveAndPrint()
}

class Puzzle2023Day21 : Puzzle<Int, Int>("2023", "21", 16, -1) {
    override fun solvePart1(input: List<String>): Int {
        return input.size
    }

    override fun solvePart2(input: List<String>): Int {
        return input.size
    }
}