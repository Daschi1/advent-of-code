package year2023.day12

import Puzzle

fun main() {
    val puzzle = Puzzle2023Day12()
    puzzle.testAndSolveAndPrint()
}

class Puzzle2023Day12 : Puzzle<Int, Int>("2023", "12", 21, -1) {
    override fun solvePart1(input: List<String>): Int {
        val hotSpringRows = parseHotSpringRowsFromInput(input)
        var sum = 0
        for (hotSpringRow in hotSpringRows) {
            sum += hotSpringRow.generateAllCombinations().filter { it.isValidArrangement() }.size
        }
        return sum
    }

    override fun solvePart2(input: List<String>): Int {
        return input.size
    }
}

private fun parseHotSpringRowsFromInput(input: List<String>): List<HotSpringRow> {
    return input.map { s ->
        val split = s.split(" ")
        val hotSprings = split[0].toCharArray().map { HotSpring(it) }
        val checksums = split[1].split(",").map { it.toInt() }
        HotSpringRow(hotSprings.toMutableList(), checksums)
    }
}

private data class HotSpringRow(val hotSprings: MutableList<HotSpring>, val checksums: List<Int>) {

    fun isValidArrangement(): Boolean {
        val amountOfBrokenBatches = mutableListOf<Int>()
        var count = 0
        for (hotSpring in hotSprings) {
            if (hotSpring.isBroken()) {
                count++
            } else {
                if (count != 0) {
                    amountOfBrokenBatches.add(count)
                    count = 0
                }
            }
        }
        if (count != 0) amountOfBrokenBatches.add(count)
        return amountOfBrokenBatches == checksums
    }

    fun generateAllCombinations(): List<HotSpringRow> {
        return generateCombinations(hotSprings, 0)
    }

    private fun generateCombinations(hotSprings: MutableList<HotSpring>, index: Int): List<HotSpringRow> {
        if (index >= hotSprings.size) {
            return listOf(HotSpringRow(hotSprings.toMutableList(), checksums))
        }

        val current = hotSprings[index]
        if (!current.isUnknown()) {
            return generateCombinations(hotSprings, index + 1)
        }

        // Branch for '.'
        hotSprings[index] = HotSpring('.')
        val combinationsDot = generateCombinations(hotSprings, index + 1)

        // Branch for '#'
        hotSprings[index] = HotSpring('#')
        val combinationsHash = generateCombinations(hotSprings, index + 1)

        // Restore original '?'
        hotSprings[index] = current

        // Combine results and return
        return combinationsDot + combinationsHash
    }
}

private data class HotSpring(val symbol: Char) {
    init {
        require(symbol in listOf('.', '#', '?')) { "A HotSpring must have a symbol of '.', '#' or '?', not '$symbol'" }
    }

    fun isOperational(): Boolean {
        return symbol == '.'
    }

    fun isBroken(): Boolean {
        return symbol == '#'
    }

    fun isUnknown(): Boolean {
        return symbol == '?'
    }
}