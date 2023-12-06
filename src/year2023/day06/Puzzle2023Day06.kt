package year2023.day06

import Puzzle

fun main() {
    val puzzle = Puzzle2023Day06()
    puzzle.testAndSolveAndPrint()
}

class Puzzle2023Day06 : Puzzle<Int, Int>("2023", "06", 288, -1) {
    override fun solvePart1(input: List<String>): Int {
        if (input.size != 2) return -1
        val durations = parseInputToIntList(input[0])
        val records = parseInputToIntList(input[1])

        if (durations.size != records.size) return -1
        var product = 1
        for ((i, duration) in durations.withIndex()) {
            val record = records[i]
            val race = Race(duration, record)
            product *= race.calculateAmountOfWaysToWin()
        }
        return product
    }

    override fun solvePart2(input: List<String>): Int {
        return input.size
    }
}

private fun parseInputToIntList(input: String): List<Int> {
    return input.split(":")[1].split(" ").filter { s -> s.all { it.isDigit() } }.mapNotNull { it.toIntOrNull() }
}

private data class Race(val duration: Int, val record: Int) {
    fun calculateAmountOfWaysToWin(): Int {
        var amountOfWaysToWin = 0
        for (buttonPressedFor in 0..duration) {
            val timeToTravel = duration - buttonPressedFor
            val distance = buttonPressedFor * timeToTravel
            if (distance > record) amountOfWaysToWin++
        }
        return amountOfWaysToWin
    }
}