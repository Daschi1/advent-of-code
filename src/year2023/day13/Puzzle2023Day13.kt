package year2023.day13

import Puzzle

fun main() {
    val puzzle = Puzzle2023Day13()
    puzzle.testAndSolveAndPrint()
}

class Puzzle2023Day13 : Puzzle<Int, Int>("2023", "13", 405, 400) {
    override fun solvePart1(input: List<String>): Int {
        val mirrorPatterns = parseMirrorPatternFromInput(input)
        var sum = 0
        for (mirrorPattern in mirrorPatterns) {
            val vertical = mirrorPattern.findIndexWithVerticalReflection()
            if (vertical != null) sum += vertical
            else {
                val horizontal = mirrorPattern.findIndexWithHorizontalReflection()
                if (horizontal != null) sum += 100 * horizontal
            }
        }
        return sum
    }

    override fun solvePart2(input: List<String>): Int {
        return input.size
    }
}

private fun parseMirrorPatternFromInput(input: List<String>): List<MirrorPattern> {
    val groups = input.fold(mutableListOf<MutableList<String>>()) { acc, s ->
        if (s.isEmpty()) {
            // Start a new group if the string is empty
            acc.add(mutableListOf())
        } else {
            // Add the string to the current group
            if (acc.isEmpty()) acc.add(mutableListOf())
            acc.last().add(s)
        }
        acc
    }.filter { it.isNotEmpty() } // Filter out any empty groups

    val mirrorPatterns = mutableListOf<MirrorPattern>()
    for (group in groups) {
        val height = group.size
        val width = group[0].length
        val pattern: Array<Array<Char>> = Array(height) { Array(width) { ' ' } }

        for ((y, s) in group.withIndex()) {
            for ((x, c) in s.toCharArray().withIndex()) {
                pattern[y][x] = c
            }
        }

        val mirrorPattern = MirrorPattern(height, width, pattern)
        mirrorPatterns.add(mirrorPattern)
    }
    return mirrorPatterns
}

private data class MirrorPattern(val height: Int, val width: Int, val pattern: Array<Array<Char>>) {

    fun findIndexWithVerticalReflection(): Int? {
        // earliest reflection check between 0 and 1, last between one before end and end
        for (x in 1 until width) {
            if (checkForVerticalReflectionAt(x)) return x
        }
        return null
    }

    fun findIndexWithHorizontalReflection(): Int? {
        // earliest reflection check between 0 and 1, last between one before end and end
        for (y in 1 until height) {
            if (checkForHorizontalReflectionAt(y)) return y
        }
        return null
    }

    private fun checkForVerticalReflectionAt(index: Int): Boolean {
        var reflection: Boolean
        for (x in 0 until index) {
            // index (after mirror line) + difference of x index - 1 (before mirror line)
            val mirrorX = index + (index - 1 - x)
            if (mirrorX >= width) continue
            for (y in 0 until height) {
                reflection = pattern[y][x] == pattern[y][mirrorX]
                if (!reflection) return false
            }
        }
        return true
    }

    private fun checkForHorizontalReflectionAt(index: Int): Boolean {
        var reflection: Boolean
        for (y in 0 until index) {
            // index (after mirror line) + difference of y index - 1 (before mirror line)
            val mirrorY = index + (index - 1 - y)
            if (mirrorY >= height) continue
            for (x in 0 until width) {
                reflection = pattern[y][x] == pattern[mirrorY][x]
                if (!reflection) return false
            }
        }
        return true
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MirrorPattern) return false

        if (height != other.height) return false
        if (width != other.width) return false
        if (!pattern.contentDeepEquals(other.pattern)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = height
        result = 31 * result + width
        result = 31 * result + pattern.contentDeepHashCode()
        return result
    }
}