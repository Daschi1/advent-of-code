package year2023.day20

import Puzzle

fun main() {
    val puzzle = Puzzle2023Day20()
    puzzle.testAndSolveAndPrint()
}

class Puzzle2023Day20 : Puzzle<Int, Int>("2023", "20", 32000000, -1) {
    override fun solvePart1(input: List<String>): Int {
        return input.size
    }

    override fun solvePart2(input: List<String>): Int {
        return input.size
    }
}