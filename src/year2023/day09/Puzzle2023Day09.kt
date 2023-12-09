package year2023.day09

import Puzzle

fun main() {
    val puzzle = Puzzle2023Day09()
    puzzle.testAndSolveAndPrint()
}

class Puzzle2023Day09 : Puzzle<Int, Int>("2023", "09", 114, -1) {
    override fun solvePart1(input: List<String>): Int {
        return input.size
    }

    override fun solvePart2(input: List<String>): Int {
        return input.size
    }
}