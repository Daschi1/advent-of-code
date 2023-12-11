package year2023.day11

import Puzzle
import java.util.*

fun main() {
    val puzzle = Puzzle2023Day11()
    puzzle.testAndSolveAndPrint()
}

class Puzzle2023Day11 : Puzzle<Int, Int>("2023", "11", 374, 8410) {
    override fun solvePart1(input: List<String>): Int {
        val expandedObservation = parseObservationFromInput(input).expandEmptySpace()
        val galaxies = expandedObservation.getAllGalaxies()
        val pairs = mutableListOf<Pair<ObservedObject, ObservedObject>>()
        for (i in galaxies.indices) {
            for (j in i + 1 until galaxies.size) {
                pairs.add(galaxies[i] to galaxies[j])
            }
        }
        return pairs.sumOf { expandedObservation.calculateShortestPathLengthBetween(it.first, it.second) }
    }

    override fun solvePart2(input: List<String>): Int {
        return input.size
    }
}

private fun parseObservationFromInput(input: List<String>): Observation {
    val height = input.size
    val width = input[0].length
    val observation: Array<Array<ObservedObject>> = Array(height) { Array(width) { ObservedObject.SPACE } }

    for ((y, s) in input.withIndex()) {
        for ((x, c) in s.toCharArray().withIndex()) {
            observation[y][x] = ObservedObject(c)
        }
    }
    return Observation(observation)
}

private data class Observation(val observation: Array<Array<ObservedObject>>) {
    fun expandEmptySpace(): Observation {
        val emptyColumns = getEmptyColumns()
        val emptyRows = getEmptyRows()
        val expandedObservation: Array<Array<ObservedObject>> =
            Array(observation.size + emptyRows.size) { Array(observation[0].size + emptyColumns.size) { ObservedObject.SPACE } }

        var yOffset = 0
        for (y in observation.indices) {
            if (y in emptyRows) yOffset++
            var xOffset = 0
            for (x in observation[0].indices) {
                if (x in emptyColumns) xOffset++
                expandedObservation[y + yOffset][x + xOffset] = observation[y][x]
            }
        }
        return Observation(expandedObservation)
    }

    fun getEmptyColumns(): List<Int> {
        val emptyColumns = mutableListOf<Int>()
        for (x in observation[0].indices) {
            var empty = true
            for (y in observation.indices) {
                if (observation[y][x].isGalaxy()) empty = false
            }
            if (empty) emptyColumns.add(x)
        }
        return emptyColumns
    }

    fun getEmptyRows(): List<Int> {
        val emptyRows = mutableListOf<Int>()
        for (y in observation.indices) {
            var empty = true
            for (x in observation[0].indices) {
                if (observation[y][x].isGalaxy()) empty = false
            }
            if (empty) emptyRows.add(y)
        }
        return emptyRows
    }

    fun getAllGalaxies(): List<ObservedObject> {
        val galaxies = mutableListOf<ObservedObject>()
        for (y in observation.indices) {
            for (x in observation[0].indices) {
                val observedObject = observation[y][x]
                if (observedObject.isGalaxy()) galaxies.add(observedObject)
            }
        }
        return galaxies
    }

    /**
     * Finds the shortest path using Breadth-First Search (BFS) algorithm.
     */
    fun calculateShortestPathLengthBetween(start: ObservedObject, end: ObservedObject): Int {
        val startPosition = getPositionOf(start)
        val endPosition = getPositionOf(end)

        val visited = Array(observation.size) { BooleanArray(observation[0].size) }
        val queue: Queue<Pair<ObservedObjectPosition, Int>> = LinkedList() // Position and steps
        queue.add(Pair(startPosition, 0))
        visited[startPosition.y][startPosition.x] = true

        while (queue.isNotEmpty()) {
            val (currentPosition, steps) = queue.remove()

            if (currentPosition == endPosition) return steps // Found the end

            val directions = listOf(-1 to 0, 0 to -1, 1 to 0, 0 to 1) // up, left, down, right
            for ((dx, dy) in directions) {
                val newX = currentPosition.x + dx
                val newY = currentPosition.y + dy

                if (newX in observation[0].indices && newY in observation.indices && !visited[newY][newX]) {
                    visited[newY][newX] = true
                    queue.add(Pair(ObservedObjectPosition(newX, newY), steps + 1))
                }
            }
        }
        throw IllegalArgumentException("Path between $start and $end could not be found") // Path not found
    }

    fun getPositionOf(observedObject: ObservedObject): ObservedObjectPosition {
        for (y in observation.indices) {
            for (x in observation[0].indices) {
                if (observation[y][x].hashCode() == observedObject.hashCode()) return ObservedObjectPosition(x, y)
            }
        }
        throw IllegalArgumentException("Position of $observedObject could not be located")
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Observation) return false

        if (!observation.contentDeepEquals(other.observation)) return false

        return true
    }

    override fun hashCode(): Int {
        return observation.contentDeepHashCode()
    }
}

private data class ObservedObject(val symbol: Char, val uuid: UUID = UUID.randomUUID()) {
    companion object {
        val SPACE = ObservedObject('.')
    }

    fun isGalaxy(): Boolean {
        return symbol == '#'
    }

    @Suppress("unused")
    fun isSpace(): Boolean {
        return symbol == '.'
    }
}

private data class ObservedObjectPosition(val x: Int, val y: Int)