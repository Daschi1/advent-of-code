package year2023.day17

import Puzzle

fun main() {
    val puzzle = Puzzle2023Day17()
    puzzle.testAndSolveAndPrint()
}

class Puzzle2023Day17 : Puzzle<Int, Int>("2023", "17", -1, -1) {
    override fun solvePart1(input: List<String>): Int {
        return input.size
    }

    override fun solvePart2(input: List<String>): Int {
        return input.size
    }
}