package year2023.day18

import Puzzle

fun main() {
    val puzzle = Puzzle2023Day18()
    puzzle.testAndSolveAndPrint()
}

class Puzzle2023Day18 : Puzzle<Int, Long>("2023", "18", 62, 952408144115) {
    override fun solvePart1(input: List<String>): Int {
        val digPlan = parseDigPlanFromInput(input)
        return digPlan.calculateCubicMeters().toInt()
    }

    override fun solvePart2(input: List<String>): Long {
        val digPlan = parseActualDigPlanFromInput(input)
        return digPlan.calculateCubicMeters()
    }
}

private fun parseDigPlanFromInput(input: List<String>): DigPlan {
    val digInstructions = mutableListOf<DigInstruction>()
    for (s in input) {
        val split = s.split(" ")
        val direction = when (split[0]) {
            "U" -> Point.NORTH
            "D" -> Point.SOUTH
            "L" -> Point.WEST
            "R" -> Point.EAST
            else -> error("Could not match '${split[0]}' to any Direction")
        }
        val amount = split[1].toInt()
        val digInstruction = DigInstruction(direction, amount)
        digInstructions.add(digInstruction)
    }
    return DigPlan(digInstructions)
}

private fun parseActualDigPlanFromInput(input: List<String>): DigPlan {
    val digInstructions = mutableListOf<DigInstruction>()
    for (s in input) {
        val color = s.split(" ")[2].removePrefix("(").removeSuffix(")")
        val direction = when (color.last()) {
            '0' -> Point.EAST
            '1' -> Point.SOUTH
            '2' -> Point.WEST
            '3' -> Point.NORTH
            else -> error("Could not match '${color.last()}' to any Direction")
        }
        // get rid of # and direction number
        val amount = color.drop(1).dropLast(1).toInt(16)
        val digInstruction = DigInstruction(direction, amount)
        digInstructions.add(digInstruction)
    }
    return DigPlan(digInstructions)
}

private data class DigInstruction(val direction: Point, val amount: Int)

private data class DigPlan(val digInstructions: List<DigInstruction>) {
    fun calculateCubicMeters(): Long {
        val borderTiles = digInstructions.sumOf { it.amount }
        val vertices = digInstructions.runningFold(Point(0, 0)) { last, dig -> last + dig.direction * dig.amount }
        val interiorTiles = shoelaceArea(vertices) - (borderTiles / 2) + 1

        return interiorTiles + borderTiles
    }

    private fun shoelaceArea(vertices: List<Point>): Long {
        return vertices.indices.sumOf { i ->
            val (x1, y1) = vertices[i]
            val (x2, y2) = vertices[(i + 1) % vertices.size]
            x1.toLong() * y2 - y1.toLong() * x2
        } / 2
    }
}

private data class Point(val x: Int, val y: Int) {
    companion object {
        val NORTH = Point(0, -1)
        val SOUTH = Point(0, 1)
        val EAST = Point(1, 0)
        val WEST = Point(-1, 0)
    }

    operator fun plus(other: Point): Point = Point(x + other.x, y + other.y)

    operator fun times(other: Int): Point = Point(x * other, y * other)
}