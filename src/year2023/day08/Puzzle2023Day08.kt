package year2023.day08

import Puzzle

fun main() {
    val puzzle = Puzzle2023Day08()
    puzzle.testAndSolveAndPrint()
}

class Puzzle2023Day08 : Puzzle<Int, Int>("2023", "08", 2, -1) {
    override fun solvePart1(input: List<String>): Int {
        return input.size
    }

    override fun solvePart2(input: List<String>): Int {
        return input.size
    }
}