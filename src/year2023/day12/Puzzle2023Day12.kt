package year2023.day12

import Puzzle

fun main() {
    val puzzle = Puzzle2023Day12()
    puzzle.testAndSolveAndPrint()
}

class Puzzle2023Day12 : Puzzle<Int, Long>("2023", "12", 21, 525152) {
    override fun solvePart1(input: List<String>): Int {
        val hotSpringRows = parseHotSpringRowsFromInput(input)
        var sum = 0
        for (hotSpringRow in hotSpringRows) {
            sum += hotSpringRow.generateAllCombinations().filter { it.isValidArrangement() }.size
        }
        return sum
    }

    override fun solvePart2(input: List<String>): Long {
        val hotSpringRows = parseHotSpringRowsFromInput(input)
        return hotSpringRows.sumOf { hotSpringRow ->
            hotSpringRow.unfold(5).countValidArrangements()
        }
    }
}

private fun parseHotSpringRowsFromInput(input: List<String>): List<HotSpringRow> {
    return input.map { s ->
        val split = s.split(" ")
        val hotSprings = split[0].toCharArray()
        val checksums = split[1].split(",").map { it.toInt() }.toIntArray()
        HotSpringRow(hotSprings, checksums)
    }
}

private data class HotSpringRow(val hotSprings: CharArray, val checksums: IntArray) {
    fun unfold(amount: Int): HotSpringRow {
        val newHotSprings = ArrayList<Char>()
        val newChecksums = ArrayList<Int>()
        for (i in 0 until amount) {
            newHotSprings.addAll(hotSprings.toList())
            if (i < amount - 1) newHotSprings.add('?')
            newChecksums.addAll(checksums.toList())
        }
        return HotSpringRow(newHotSprings.toCharArray(), newChecksums.toIntArray())
    }

    fun isValidArrangement(): Boolean {
        val amountOfBrokenBatches = mutableListOf<Int>()
        var count = 0
        for (hotSpring in hotSprings) {
            if (isBroken(hotSpring)) {
                count++
            } else {
                if (count != 0) {
                    amountOfBrokenBatches.add(count)
                    count = 0
                }
            }
        }
        if (count != 0) amountOfBrokenBatches.add(count)
        return amountOfBrokenBatches.toIntArray().contentEquals(checksums)
    }

    fun generateAllCombinations(): List<HotSpringRow> {
        return generateCombinations(hotSprings.copyOf(), 0)
    }

    private fun generateCombinations(hotSprings: CharArray, index: Int): List<HotSpringRow> {
        if (index >= hotSprings.size) {
            return listOf(HotSpringRow(hotSprings, checksums.copyOf()))
        }

        val current = hotSprings[index]
        if (!isUnknown(current)) {
            return generateCombinations(hotSprings, index + 1)
        }

        // Branch for '.'
        hotSprings[index] = '.'
        val combinationsDot = generateCombinations(hotSprings.copyOf(), index + 1)

        // Branch for '#'
        hotSprings[index] = '#'
        val combinationsHash = generateCombinations(hotSprings.copyOf(), index + 1)

        // Restore original '?'
        hotSprings[index] = current

        // Combine results and return
        return combinationsDot + combinationsHash
    }

    fun countValidArrangements(): Long {
        val springStateString = String(hotSprings)
        val checksumList = checksums.toList()
        val contiguousBrokenLengths = precomputeContiguousBrokenLengths(springStateString)
        val dpMemoizationMap = mutableMapOf<Pair<Int, Int>, Long>()

        return calculateArrangements(springStateString, checksumList, contiguousBrokenLengths, dpMemoizationMap, 0, 0)
    }

    private fun precomputeContiguousBrokenLengths(s: String): IntArray =
        IntArray(s.length) { i -> s.drop(i).takeWhile { c -> c != '.' }.length }

    private fun canTakeBrokenGroup(s: String, start: Int, length: Int, contiguousBrokenLengths: IntArray): Boolean =
        contiguousBrokenLengths[start] >= length && (start + length == s.length || s[start + length] != '#')

    private fun calculateArrangements(
        s: String,
        checksums: List<Int>,
        contiguousBrokenLengths: IntArray,
        dpMemoizationMap: MutableMap<Pair<Int, Int>, Long>,
        springIndex: Int,
        checksumIndex: Int
    ): Long = dpMemoizationMap.getOrPut(springIndex to checksumIndex) {
        when {
            checksumIndex == checksums.size -> if (s.drop(springIndex).none { c -> c == '#' }) 1L else 0
            springIndex >= s.length -> 0L
            else -> {
                val take = if (canTakeBrokenGroup(s, springIndex, checksums[checksumIndex], contiguousBrokenLengths)) {
                    calculateArrangements(
                        s,
                        checksums,
                        contiguousBrokenLengths,
                        dpMemoizationMap,
                        springIndex + checksums[checksumIndex] + 1,
                        checksumIndex + 1
                    )
                } else 0L
                val dontTake = if (s[springIndex] != '#') {
                    calculateArrangements(
                        s,
                        checksums,
                        contiguousBrokenLengths,
                        dpMemoizationMap,
                        springIndex + 1,
                        checksumIndex
                    )
                } else 0L
                take + dontTake
            }
        }
    }

    @Suppress("unused")
    fun isOperational(symbol: Char): Boolean = symbol == '.'

    fun isBroken(symbol: Char): Boolean = symbol == '#'

    fun isUnknown(symbol: Char): Boolean = symbol == '?'

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is HotSpringRow) return false

        if (!hotSprings.contentEquals(other.hotSprings)) return false
        if (!checksums.contentEquals(other.checksums)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = hotSprings.contentHashCode()
        result = 31 * result + checksums.contentHashCode()
        return result
    }
}