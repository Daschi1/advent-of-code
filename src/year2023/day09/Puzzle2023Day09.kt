package year2023.day09

import Puzzle

fun main() {
    val puzzle = Puzzle2023Day09()
    puzzle.testAndSolveAndPrint()
}

class Puzzle2023Day09 : Puzzle<Int, Int>("2023", "09", 114, 2) {
    override fun solvePart1(input: List<String>): Int {
        val sequences = input.map { s ->
            val entries = s.split(" ").map { it.toInt() }.toMutableList()
            Sequence(entries)
        }
        return sequences.sumOf { Sequence.predictNextValueInSequence(it) }
    }

    override fun solvePart2(input: List<String>): Int {
        return input.size
    }
}

private data class Sequence(private val entries: MutableList<Int>) {
    companion object {
        fun predictNextValueInSequence(sequence: Sequence): Int {
            val sequences = mutableListOf(sequence)
            while (!sequences.last().isZero()) {
                sequences.add(sequences.last().createDifferenceSequence())
            }
            for (i in (sequences.size - 2) downTo 0) {
                val difference = sequences[i + 1].getLastEntry()
                sequences[i].insertNewValue(difference)
            }
            return sequences.first().getLastEntry()
        }
    }

    fun isZero(): Boolean {
        return !entries.any { it != 0 }
    }

    fun getLastEntry(): Int {
        return entries.last()
    }

    fun createDifferenceSequence(): Sequence {
        val differenceEntries = mutableListOf<Int>()
        for (i in 0..<(entries.size - 1)) {
            val difference = entries[i + 1] - entries[i]
            differenceEntries.add(difference)
        }
        return Sequence(differenceEntries)
    }

    fun insertNewValue(difference: Int) {
        val newValue = entries.last() + difference
        entries.add(newValue)
    }
}