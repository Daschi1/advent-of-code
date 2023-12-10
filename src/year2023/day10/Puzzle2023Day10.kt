package year2023.day10

import Puzzle

fun main() {
    val puzzle = Puzzle2023Day10()
    puzzle.testAndSolveAndPrint()
}

class Puzzle2023Day10 : Puzzle<Int, Int>("2023", "10", 4, -1) {
    override fun solvePart1(input: List<String>): Int {
        return input.size
    }

    override fun solvePart2(input: List<String>): Int {
        return input.size
    }
}