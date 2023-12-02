package day02

import println
import readInput

data class Game(val id: Int, val gameSets: List<GameSet> = emptyList()) {
    companion object {
        fun parseFromString(input: String): Game {
            val gameSetsArray = input.split(":")
            // parse gameId
            val gameId = gameSetsArray[0].filter { it.isDigit() }.toInt()

            // parse gameSets
            val setsArray = gameSetsArray[1].split(";")
            val gameSets = mutableListOf<GameSet>()
            for (set in setsArray) {
                gameSets.addLast(GameSet.parseFromString(set))
            }

            return Game(gameId, gameSets)
        }
    }

    fun isPossibleWithLimitedCubes(cubeLimit: GameSet): Boolean {
        return gameSets.all {
            it.red <= cubeLimit.red &&
                    it.green <= cubeLimit.green &&
                    it.blue <= cubeLimit.blue
        }
    }

    fun calculateNumberOfFewestCubesRequired(): GameSet {
        var highestRed = 0
        var highestGreen = 0
        var highestBlue = 0
        for (gameSet in gameSets) {
            if (gameSet.red > highestRed) highestRed = gameSet.red
            if (gameSet.green > highestGreen) highestGreen = gameSet.green
            if (gameSet.blue > highestBlue) highestBlue = gameSet.blue
        }
        return GameSet(highestRed, highestGreen, highestBlue)
    }
}

data class GameSet(val red: Int = 0, val green: Int = 0, val blue: Int = 0) {
    companion object {
        fun parseFromString(input: String): GameSet {
            val cubeArray = input.split(",")
            var red = 0
            var green = 0
            var blue = 0
            for (cube in cubeArray) {
                when {
                    "red" in cube -> red = cube.filter { it.isDigit() }.toInt()
                    "green" in cube -> green = cube.filter { it.isDigit() }.toInt()
                    "blue" in cube -> blue = cube.filter { it.isDigit() }.toInt()
                }
            }
            return GameSet(red, green, blue)
        }
    }

    fun calculatePower(): Int {
        return red * green * blue
    }
}

fun main() {
    // part 1
    fun part1(input: List<String>): Int {
        val cubeLimit = GameSet(12, 13, 14)
        var sum = 0
        for (s in input) {
            val game = Game.parseFromString(s)
            if (game.isPossibleWithLimitedCubes(cubeLimit)) {
                sum += game.id
            }
        }
        return sum
    }

    // part 2
    fun part2(input: List<String>): Int {
        var sum = 0
        for (s in input) {
            val game = Game.parseFromString(s)
            val fewestCubesRequired = game.calculateNumberOfFewestCubesRequired()
            sum += fewestCubesRequired.calculatePower()
        }
        return sum
    }

    // test if implementation meets criteria from the description:
    val testInput1 = readInput("day02/Day02_test1")
    check(part1(testInput1) == 8)
    val testInput2 = readInput("day02/Day02_test2")
    check(part2(testInput2) == 2286)

    // calculate solution
    val input = readInput("day02/Day02")
    part1(input).println()
    part2(input).println()
}