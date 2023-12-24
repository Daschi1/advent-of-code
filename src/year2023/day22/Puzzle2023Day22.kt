package year2023.day22

import Puzzle

fun main() {
    val puzzle = Puzzle2023Day22()
    puzzle.testAndSolveAndPrint()
}

class Puzzle2023Day22 : Puzzle<Int, Int>("2023", "22", 5, -1) {
    override fun solvePart1(input: List<String>): Int {
        return input.size
    }

    override fun solvePart2(input: List<String>): Int {
        return input.size
    }
}