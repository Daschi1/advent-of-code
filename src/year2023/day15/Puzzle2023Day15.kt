package year2023.day15

import Puzzle

fun main() {
    val puzzle = Puzzle2023Day15()
    puzzle.testAndSolveAndPrint()
}

class Puzzle2023Day15 : Puzzle<Int, Int>("2023", "15", 1320, -1) {
    override fun solvePart1(input: List<String>): Int {
        return input.size
    }

    override fun solvePart2(input: List<String>): Int {
        return input.size
    }
}