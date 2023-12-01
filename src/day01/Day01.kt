package day01

import println
import readInput

fun main() {
    // part 1
    fun findFirstNumber(input: String): Int? {
        for (char in input) {
            if (char.isDigit()) {
                return char.digitToInt()
            }
        }
        return null
    }

    fun concatenateFirstAndLastNumber(firstNumberInput: String, lastNumberInput: String): Int? {
        val first = findFirstNumber(firstNumberInput)
        val last = findFirstNumber(lastNumberInput.reversed())
        if (first == null || last == null) return null
        return "$first$last".toInt()
    }

    fun part1(input: List<String>): Int {
        var sum = 0
        for (s in input) {
            val concatenated = concatenateFirstAndLastNumber(s, s) ?: continue
            sum += concatenated
        }
        return sum
    }

    // part 2
    fun replaceFirstSpelledNumberWithIntegers(input: String): String {
        val regex = "one|two|three|four|five|six|seven|eight|nine".toRegex()
        val matchResult = regex.find(input) ?: return input

        val numericRepresentation = when (matchResult.value) {
            "one" -> "1"
            "two" -> "2"
            "three" -> "3"
            "four" -> "4"
            "five" -> "5"
            "six" -> "6"
            "seven" -> "7"
            "eight" -> "8"
            "nine" -> "9"
            else -> return input
        }
        return input.replaceRange(matchResult.range, numericRepresentation)
    }

    fun replaceLastSpelledNumberWithIntegers(input: String): String {
        val inputReversed = input.reversed()
        val regexReversed = "one|two|three|four|five|six|seven|eight|nine".reversed().toRegex()
        val matchResult = regexReversed.find(inputReversed) ?: return input

        val numericRepresentation = when (matchResult.value) {
            "one".reversed() -> "1"
            "two".reversed() -> "2"
            "three".reversed() -> "3"
            "four".reversed() -> "4"
            "five".reversed() -> "5"
            "six".reversed() -> "6"
            "seven".reversed() -> "7"
            "eight".reversed() -> "8"
            "nine".reversed() -> "9"
            else -> return input
        }
        val replacedReversedString = inputReversed.replaceRange(matchResult.range, numericRepresentation)
        return replacedReversedString.reversed()
    }

    fun part2(input: List<String>): Int {
        var sum = 0
        for (s in input) {
            val forwardReplacedString = replaceFirstSpelledNumberWithIntegers(s)
            val backwardReplacedString = replaceLastSpelledNumberWithIntegers(s)
            val concatenated = concatenateFirstAndLastNumber(forwardReplacedString, backwardReplacedString) ?: continue
            sum += concatenated
        }
        return sum
    }

    // test if implementation meets criteria from the description:
    val testInput1 = readInput("day01/Day01_test1")
    check(part1(testInput1) == 142)
    val testInput2 = readInput("day01/Day01_test2")
    check(part2(testInput2) == 281)

    // calculate solution
    val input = readInput("day01/Day01")
    part1(input).println()
    part2(input).println()
}