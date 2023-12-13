package year2023.day13

import Puzzle

fun main() {
    val puzzle = Puzzle2023Day13()
    puzzle.testAndSolveAndPrint()
}

class Puzzle2023Day13 : Puzzle<Int, Int>("2023", "13", 405, -1) {
    override fun solvePart1(input: List<String>): Int {
        return input.size
    }

    override fun solvePart2(input: List<String>): Int {
        return input.size
    }
}