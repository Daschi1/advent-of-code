package year2023.day23

import Puzzle

fun main() {
    val puzzle = Puzzle2023Day23()
    puzzle.testAndSolveAndPrint()
}

class Puzzle2023Day23 : Puzzle<Int, Int>("2023", "23", 94, -1) {
    override fun solvePart1(input: List<String>): Int {
        return input.size
    }

    override fun solvePart2(input: List<String>): Int {
        return input.size
    }
}