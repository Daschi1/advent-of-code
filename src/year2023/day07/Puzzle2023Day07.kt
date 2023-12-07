package year2023.day07

import Puzzle
import println

fun main() {
    val puzzle = Puzzle2023Day07()
    puzzle.testAndSolveAndPrint()
}

class Puzzle2023Day07 : Puzzle<Int, Int>("2023", "07", 6440, -1) {
    override fun solvePart1(input: List<String>): Int {
        val hands = parseHandsFromInput(input)
        hands.println()
        return input.size
    }

    override fun solvePart2(input: List<String>): Int {
        return input.size
    }
}

private fun parseHandsFromInput(input: List<String>): List<Hand> {
    return input.map { s ->
        val split = s.split(" ")
        val hand = split[0]
        val bid = split[1].toInt()
        Hand(hand, bid)
    }
}

private data class Hand(val hand: String, val bid: Int) : Comparable<Hand> {
    init {
        require(hand.length == 5) { "Hand is only allowed to be a length of five, here: ${hand.length}" }
    }

    fun isFiveOfAKind(): Boolean {
        // (.) capturing group for any char
        // \\1* matches zero or more of the same char of the previous group
        return hand.matches("^(.)\\1*$".toRegex())
    }

    fun isFourOfAKind(): Boolean {
        // \\1{3} matches three or more of the same char of the previous group
        return hand.matches("^(.)\\1{3}$".toRegex())
    }

    fun isFullHouse(): Boolean {
        val charCounts = hand.groupingBy { it }.eachCount()
        return charCounts.values.sorted() == listOf(2, 3)
    }

    fun isThreeOfAKind(): Boolean {
        return hand.groupingBy { it }.eachCount().any { it.value == 3 }
    }

    override fun compareTo(other: Hand): Int {

    }
}