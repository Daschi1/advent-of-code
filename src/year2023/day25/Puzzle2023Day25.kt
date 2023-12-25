package year2023.day25

import Puzzle

fun main() {
    val puzzle = Puzzle2023Day25()
    puzzle.testAndSolveAndPrint()
}

class Puzzle2023Day25 : Puzzle<Int, Int>("2023", "25", 54, -1) {
    override fun solvePart1(input: List<String>): Int {
        return input.size
    }

    override fun solvePart2(input: List<String>): Int {
        return input.size
    }
}