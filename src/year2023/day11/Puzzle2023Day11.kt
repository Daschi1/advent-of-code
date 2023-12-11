package year2023.day11

import Puzzle
import java.util.*

fun main() {
    val puzzle = Puzzle2023Day11()
    puzzle.testAndSolveAndPrint()
}

class Puzzle2023Day11 : Puzzle<Int, Long>("2023", "11", 374, 82000210) {
    override fun solvePart1(input: List<String>): Int {
        val expandedObservation = parseObservationFromInput(input).expandEmptySpace()
        val galaxiePairs = expandedObservation.getAllPairsOfGalaxies()
        val rowExpansion = IntArray(expandedObservation.observation.size) { 0 }
        val columnExpansion = IntArray(expandedObservation.observation[0].size) { 0 }
        return galaxiePairs.sumOf {
            expandedObservation.calculateShortestPathLengthBetween(
                it.first,
                it.second,
                rowExpansion,
                columnExpansion
            )
        }
    }

    override fun solvePart2(input: List<String>): Long {
        val observation = parseObservationFromInput(input)
        val galaxiePairs = observation.getAllPairsOfGalaxies()

        val rowExpansion = IntArray(observation.observation.size) { 0 }
        observation.getEmptyRows().forEach { rowExpansion[it] = 1_000_000 }
        val columnExpansion = IntArray(observation.observation[0].size) { 0 }
        observation.getEmptyColumns().forEach { columnExpansion[it] = 1_000_000 }

        return galaxiePairs.sumOf {
            observation.calculateShortestPathLengthBetween(
                it.first,
                it.second,
                rowExpansion,
                columnExpansion
            ).toLong()
        }
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

    fun getAllPairsOfGalaxies(): List<Pair<ObservedObject, ObservedObject>> {
        val galaxies = this.getAllGalaxies()
        val galaxiePairs = mutableListOf<Pair<ObservedObject, ObservedObject>>()
        for (i in galaxies.indices) {
            for (j in i + 1 until galaxies.size) {
                galaxiePairs.add(galaxies[i] to galaxies[j])
            }
        }
        return galaxiePairs
    }

    /**
     * Finds the shortest path using Breadth-First Search (BFS) algorithm.
     * @param rowExpansion is the number of how much each row should be expanded
     * @param columnExpansion is the number of how much each column should be expanded
     */
    fun calculateShortestPathLengthBetween(
        start: ObservedObject,
        end: ObservedObject,
        rowExpansion: IntArray,
        columnExpansion: IntArray
    ): Int {
        // Get the starting and ending positions of the observed objects
        val startPosition = getPositionOf(start)
        val endPosition = getPositionOf(end)

        // Initialize a 2D array to keep track of visited positions
        val visited = Array(observation.size) { BooleanArray(observation[0].size) }

        // Queue for BFS, storing positions and the number of steps taken to reach them
        val queue: Queue<Pair<ObservedObjectPosition, Int>> = LinkedList()
        queue.add(Pair(startPosition, 0))
        visited[startPosition.y][startPosition.x] = true

        // Perform BFS
        while (queue.isNotEmpty()) {
            val (currentPosition, steps) = queue.remove()

            // Check if the current position is the destination
            if (currentPosition == endPosition) return steps

            // Explore adjacent positions (up, left, down, right)
            val directions = listOf(-1 to 0, 0 to -1, 1 to 0, 0 to 1)
            for ((directionX, directionY) in directions) {
                val newX = currentPosition.x + directionX
                val newY = currentPosition.y + directionY

                // Check if the new position is within bounds and not visited
                if (newX in observation[0].indices && newY in observation.indices && !visited[newY][newX]) {
                    // Calculate extra steps based on row and column expansions
                    val extraSteps = rowExpansion[newY] + columnExpansion[newX]
                    val newSteps = if (extraSteps == 0) steps + 1 else steps + extraSteps
                    visited[newY][newX] = true
                    queue.add(Pair(ObservedObjectPosition(newX, newY), newSteps))
                }
            }
        }
        // Throw an exception if no path is found
        throw IllegalArgumentException("Path between $start and $end could not be found")
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