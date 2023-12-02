package day02

import println
import readInput

fun main() {
    // part 1
    fun part1(input: List<String>): Int {
        return input.size
    }

    // part 2
    fun part2(input: List<String>): Int {
        return input.size
    }

    // test if implementation meets criteria from the description:
    val testInput1 = readInput("day02/Day02_test1")
    check(part1(testInput1) == 8)
//    val testInput2 = readInput("day02/Day02_test2")
//    check(part2(testInput2) == 281)

    // calculate solution
    val input = readInput("day02/Day02")
    part1(input).println()
//    part2(input).println()
}