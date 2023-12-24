package year2023.day22

import Puzzle

fun main() {
    val puzzle = Puzzle2023Day22()
    puzzle.testAndSolveAndPrint()
}

class Puzzle2023Day22 : Puzzle<Int, Int>("2023", "22", 5, 7) {
    override fun solvePart1(input: List<String>): Int {
        val brickTower = parseBrickTowerFromInput(input)
        return brickTower.calculateSafeDisintegrationBrickAmount()
    }

    override fun solvePart2(input: List<String>): Int {
        return input.size
    }
}

private fun parseBrickTowerFromInput(input: List<String>): BrickTower {
    val bricks = input.mapIndexed { index, s ->
        val split = s.split("~")
        val (startX, startY, startZ) = split[0].split(",").map { it.toInt() }
        val (endX, endY, endZ) = split[1].split(",").map { it.toInt() }
        Brick(index, startX..endX, startY..endY, startZ..endZ)
    }
    return BrickTower(bricks)
}

private class BrickTower(inputBricks: List<Brick>) {
    val bricks = mutableListOf<Brick>()
    val supports = mutableMapOf<Int, MutableSet<Int>>()
    val supported = mutableMapOf<Int, MutableSet<Int>>()

    init {
        // Sort the input bricks by the first value of their zRange and add them to bricks
        bricks.addAll(inputBricks.sortedBy { it.zRange.first })

        // Map to store the highest brick (id and its top Z-level) at each XY point
        val highestBricksAtPoints = mutableMapOf<Point2D, Pair<Int, Int>>().withDefault { Pair(-1, 0) }

        // Process each brick in the list
        for (brick in bricks) {
            // Get all XY points that the brick covers
            val coveredPoints = brick.getXYPoints()

            // Determine the highest existing Z-level among all covered points
            var highestZLevel = 0
            for (point in coveredPoints) {
                val (_, zLevel) = highestBricksAtPoints.getValue(point)
                if (zLevel > highestZLevel) {
                    highestZLevel = zLevel
                }
            }

            // Set the Z range for the brick based on the highest Z-level found
            val newZStart = highestZLevel + 1
            brick.zRange = newZStart until newZStart + brick.zRange.count()

            // Update the map and support relationships for each covered point
            for (point in coveredPoints) {
                val (existingBrickId, existingTopZ) = highestBricksAtPoints.getValue(point)

                // If this point was at the top of another brick, update the supports
                if (existingTopZ == highestZLevel && existingBrickId != -1) {
                    supports.getOrPut(existingBrickId) { mutableSetOf() }.add(brick.id)
                    supported.getOrPut(brick.id) { mutableSetOf() }.add(existingBrickId)
                }

                // Update the highest brick information for this point
                highestBricksAtPoints[point] = brick.id to brick.zRange.last
            }
        }
    }

    fun calculateSafeDisintegrationBrickAmount(): Int {
        // Collect all bricks that are supported by only one other brick
        val uniquelySupportedBricks = supported.values
            .filter { it.size == 1 }
            .map { it.single() }
            .toSet()

        // Calculate the number of bricks that cannot be safely removed
        val nonRemovableBricksCount = uniquelySupportedBricks.size

        // The total number of bricks minus the number of non-removable bricks
        // gives the number of bricks that can be safely removed
        return bricks.size - nonRemovableBricksCount
    }
}

private data class Brick(val id: Int, val xRange: IntRange, val yRange: IntRange, var zRange: IntRange) {
    /** Generate all 2D points covered by the brick in the XY-plane */
    fun getXYPoints(): List<Point2D> {
        val points = mutableListOf<Point2D>()

        for (x in xRange) {
            for (y in yRange) {
                val point = Point2D(x, y)
                points.add(point)
            }
        }
        return points
    }
}

private data class Point2D(val x: Int, val y: Int)