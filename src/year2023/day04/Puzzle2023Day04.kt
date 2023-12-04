package year2023.day04

import Puzzle
import kotlin.math.pow

fun main() {
    val puzzle = Puzzle2023Day04()
    puzzle.testAndSolveAndPrint()
}

class Puzzle2023Day04 : Puzzle<Int, Int>("2023", "04", 13, 30) {
    override fun solvePart1(input: List<String>): Int {
        return input.map { Card.parseFromString(it) }.sumOf { card ->
            card.calculatePoints()
        }
    }

    override fun solvePart2(input: List<String>): Int {
        return input.size
    }
}

private data class Card(
    private val number: Int,
    private val winningNumbers: List<Int>,
    private val numbers: List<Int>
) {
    companion object {
        fun parseFromString(s: String): Card {
            val numberOrNumbers = s.split(":")
            val number = numberOrNumbers[0].filter { it.isDigit() }.toInt()

            val winningNumbersOrNumbers = numberOrNumbers[1].split("|")
            val winningNumbers = winningNumbersOrNumbers[0].split(" ").filter {
                it.all { char -> char.isDigit() }
            }.mapNotNull { it.toIntOrNull() }

            val numbers = winningNumbersOrNumbers[1].split(" ").filter {
                it.all { char -> char.isDigit() }
            }.mapNotNull { it.toIntOrNull() }

            return Card(number, winningNumbers, numbers)
        }
    }

    fun calculatePoints(): Int {
        return when (val amountInWinningNumbers = calculateAmountOfNumbersInWinningNumbers()) {
            0 -> 0
            1 -> 1
            else -> 2.toDouble().pow(amountInWinningNumbers - 1).toInt()
        }
    }

    private fun calculateAmountOfNumbersInWinningNumbers(): Int {
        return numbers.count { it in winningNumbers }
    }
}