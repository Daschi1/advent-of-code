package year2023.day12

import Puzzle

fun main() {
    val puzzle = Puzzle2023Day12()
    puzzle.testAndSolveAndPrint()
}

class Puzzle2023Day12 : Puzzle<Int, Int>("2023", "12", 21, -1) {
    override fun solvePart1(input: List<String>): Int {
        return input.size
    }

    override fun solvePart2(input: List<String>): Int {
        return input.size
    }
}