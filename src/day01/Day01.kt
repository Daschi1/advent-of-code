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

    fun part1(input: List<String>): Int {
        var sum = 0;
        for (s in input) {
            val first = findFirstNumber(s)
            val last = findFirstNumber(s.reversed())
            val concatenated = "$first$last".toInt()
            sum += concatenated
        }
        return sum
    }

    // part 2
    val numberRegex = "".toRegex()

    fun part2(input: List<String>): Int {
return 0
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