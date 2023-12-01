package day01

import println
import readInput

fun main() {
    fun part1(input: List<String>): Int {
        return input.size
    }

    fun part2(input: List<String>): Int {
        return input.size
    }

    // test if implementation meets criteria from the description:
    val testInput = readInput("day01/Day01_test")
    check(part1(testInput) == 142)

    // calculate solution
    val input = readInput("day01/Day01")
    part1(input).println()
    part2(input).println()
}