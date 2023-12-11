package year2023.day11

import Puzzle

fun main() {
    val puzzle = Puzzle2023Day11()
    puzzle.testAndSolveAndPrint()
}

class Puzzle2023Day11 : Puzzle<Int, Int>("2023", "11", 374, -1) {
    override fun solvePart1(input: List<String>): Int {
        return input.size
    }

    override fun solvePart2(input: List<String>): Int {
        return input.size
    }
}