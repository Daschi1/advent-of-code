package year2023.day24

import Puzzle
import eq
import minus
import plus
import times
import z3
import kotlin.math.sign

fun main() {
    val puzzle = Puzzle2023Day24()
    puzzle.testAndSolveAndPrint()
}

class Puzzle2023Day24 : Puzzle<Int, Long>("2023", "24", 2, 47) {
    override fun solvePart1(input: List<String>): Int {
        // Parse the input to get a list of Hails
        val hails = parseHailsFromInput(input)
        // Define a range based on the input size
        val range = determineRangeBasedOnInput(input.size)

        // Calculate and return the sum based on specific conditions
        return calculateSumBasedOnConditions(hails, range)
    }

    //** Credits: https://github.com/madisp/aoc_kotlin/blob/main/2023/src/main/kotlin/day24.kt */
    override fun solvePart2(input: List<String>): Long {
        // Parse the input to get a list of Hails
        val hails = parseHailsFromInput(input)

        return z3 {
            // Define variables for target positions and velocities
            val targetPosX = int("targetPosX")
            val targetPosY = int("targetPosY")
            val targetPosZ = int("targetPosZ")
            val velocityX = int("velocityX")
            val velocityY = int("velocityY")
            val velocityZ = int("velocityZ")

            // Define delta time variables for calculations
            val deltaTime1 = int("deltaTime1")
            val deltaTime2 = int("deltaTime2")
            val deltaTime3 = int("deltaTime3")

            val deltaTimes = listOf(deltaTime1, deltaTime2, deltaTime3)

            // Construct equations based on the first three hails
            val equations = hails.take(3).flatMapIndexed { index, hail ->
                listOf(
                    (targetPosX - hail.position[0]) eq (deltaTimes[index] * (hail.vector[0].toLong() - velocityX)),
                    (targetPosY - hail.position[1]) eq (deltaTimes[index] * (hail.vector[1].toLong() - velocityY)),
                    (targetPosZ - hail.position[2]) eq (deltaTimes[index] * (hail.vector[2].toLong() - velocityZ)),
                )
            }

            // Solve the constructed equations
            solve(equations)

            // Calculate and return the sum of target positions
            eval(targetPosX + targetPosY + targetPosZ).toLong()
        }
    }
}

private fun determineRangeBasedOnInput(inputSize: Int): ClosedFloatingPointRange<Double> {
    // Determines the range based on the input size
    return if (inputSize == 5) 7.0..27.0 else 200000000000000.0..400000000000000.0
}

private fun calculateSumBasedOnConditions(hails: List<Hail>, range: ClosedFloatingPointRange<Double>): Int {
    // Calculates the sum based on specific conditions involving pairs of Hail objects
    return hails.indices.sumOf { i ->
        (i + 1 until hails.size).count { j ->
            // Calculate if the pair of hails (i, j) meet the specified conditions
            hails[i].doesPairMeetConditions(hails[j], range)
        }
    }
}

private fun parseHailsFromInput(input: List<String>): List<Hail> {
    // parse hails from input
    return input.map { s ->
        val (position, vector) = s.split(" @ ").map { it.split(", ").map(String::trim) }
        Hail(position.map(String::toLong), vector.map(String::toInt))
    }
}

private data class Hail(
    val position: List<Long>,
    val vector: List<Int>
) {
    // Calculated properties for the hail's trajectory
    val a: Double = (position[1] + vector[1] - position[1]) / (position[0] + vector[0] - position[0]).toDouble()
    val b: Double = position[1] - a * position[0]

    // Checks if the pair of hails meets the specified conditions
    fun doesPairMeetConditions(other: Hail, range: ClosedFloatingPointRange<Double>): Boolean {
        // Calculation for the intersection point and other conditions
        val x = (b - other.b) / (other.a - a)
        val y = a * x + b

        // Conditions to check if the pair meets the criteria
        val firstInFuture = isPointInFutureDirection(x, y, position[0], position[1], vector[0], vector[1])
        val secondInFuture =
            isPointInFutureDirection(x, y, other.position[0], other.position[1], other.vector[0], other.vector[1])

        return x in range && y in range && firstInFuture && secondInFuture
    }
}

private fun isPointInFutureDirection(x: Double, y: Double, posX: Long, posY: Long, vecX: Int, vecY: Int): Boolean {
    // Checks if a point (x, y) is in the future direction of a hail's movement
    return (x - posX).sign.toInt() == vecX.sign && (y - posY).sign.toInt() == vecY.sign
}