package year2023.day06

import Puzzle

fun main() {
    val puzzle = Puzzle2023Day06()
    puzzle.testAndSolveAndPrint()
}

class Puzzle2023Day06 : Puzzle<Int, Int>("2023", "06", 288, -1) {
    override fun solvePart1(input: List<String>): Int {
        return input.size
    }

    override fun solvePart2(input: List<String>): Int {
        return input.size
    }
}