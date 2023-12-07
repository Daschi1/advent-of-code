package year2023.day07

import Puzzle

fun main() {
    val puzzle = Puzzle2023Day07()
    puzzle.testAndSolveAndPrint()
}

class Puzzle2023Day07 : Puzzle<Int, Int>("2023", "07", 6440, -1) {
    override fun solvePart1(input: List<String>): Int {
        return input.size
    }

    override fun solvePart2(input: List<String>): Int {
        return input.size
    }
}