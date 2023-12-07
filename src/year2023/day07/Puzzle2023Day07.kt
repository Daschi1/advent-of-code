package year2023.day07

import Puzzle

fun main() {
    val puzzle = Puzzle2023Day07()
    puzzle.testAndSolveAndPrint()
}

class Puzzle2023Day07 : Puzzle<Int, Int>("2023", "07", 6440, 5905) {
    override fun solvePart1(input: List<String>): Int {
        val hands = parseHandsFromInput(input)
        var totalWinnings = 0
        for ((i, hand) in hands.sorted().withIndex()) {
            totalWinnings += (i + 1) * hand.bid
        }
        return totalWinnings
    }

    override fun solvePart2(input: List<String>): Int {
        val hands = parseHandsFromInput(input, true)
        return input.size
    }
}

private fun parseHandsFromInput(input: List<String>, withJokers: Boolean = false): List<Hand> {
    return input.map { s ->
        val split = s.split(" ")
        val hand = split[0]
        val bid = split[1].toInt()
        Hand(hand, bid, withJokers)
    }
}

private data class Hand(val hand: String, val bid: Int, val withJokers: Boolean) : Comparable<Hand> {
    companion object {
        val charValues = mapOf(
            'A' to 14, 'K' to 13, 'Q' to 12, 'J' to 11, 'T' to 10,
            '9' to 9, '8' to 8, '7' to 7, '6' to 6, '5' to 5,
            '4' to 4, '3' to 3, '2' to 2
        )
    }

    init {
        require(hand.length == 5) { "Hand is only allowed to be a length of five, here: ${hand.length}" }
    }

    fun isFiveOfAKind(): Boolean {
        return hand.groupingBy { it }.eachCount().any { it.value == 5 }
    }

    fun isFourOfAKind(): Boolean {
        return hand.groupingBy { it }.eachCount().any { it.value == 4 }
    }

    fun isFullHouse(): Boolean {
        val charCounts = hand.groupingBy { it }.eachCount()
        return charCounts.values.sorted() == listOf(2, 3)
    }

    fun isThreeOfAKind(): Boolean {
        return hand.groupingBy { it }.eachCount().any { it.value == 3 }
    }

    fun isTwoPair(): Boolean {
        val charCounts = hand.groupingBy { it }.eachCount()
        return charCounts.values.sorted() == listOf(1, 2, 2)
    }

    fun isOnePair(): Boolean {
        val charCounts = hand.groupingBy { it }.eachCount()
        return charCounts.values.sorted() == listOf(1, 1, 1, 2)
    }

    fun isHighCard(): Boolean {
        return hand.groupingBy { it }.eachCount().all { it.value == 1 }
    }

    fun calculateHandValue(): Int {
        return when {
            isFiveOfAKind() -> 7
            isFourOfAKind() -> 6
            isFullHouse() -> 5
            isThreeOfAKind() -> 4
            isTwoPair() -> 3
            isOnePair() -> 2
            isHighCard() -> 1
            else -> 0
        }
    }

    override fun compareTo(other: Hand): Int {
        val handComparison = this.calculateHandValue().compareTo(other.calculateHandValue())
        if (handComparison != 0) return handComparison

        for (i in hand.indices) {
            val thisCharValue = charValues[this.hand[i]] ?: 0
            val otherCharValue = charValues[other.hand[i]] ?: 0

            val charComparison = thisCharValue.compareTo(otherCharValue)
            if (charComparison != 0) return charComparison
        }
        return 0
    }
}