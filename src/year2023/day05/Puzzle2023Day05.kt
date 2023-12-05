package year2023.day05

import Puzzle

fun main() {
    val puzzle = Puzzle2023Day05()
    puzzle.testAndSolveAndPrint()
}

class Puzzle2023Day05 : Puzzle<Int, Int>("2023", "05", 35, -1) {
    override fun solvePart1(input: List<String>): Int {
        val linkedIntRange = LinkedIntRange(IntRange(5, 10), IntRange(15, 20))
        return input.size
    }

    override fun solvePart2(input: List<String>): Int {
        return input.size
    }
}

private data class Converter(val from: String, val to: String) {
    companion object {

    }
}

private data class LinkedIntRange(private val source: IntRange, private val destination: IntRange) {
    init {
        require(source.count() == destination.count()) { "Source length ${source.count()} and destination length ${destination.count()} must have the same length." }
    }

    fun canConvertFromSource(input: Int): Boolean {
        return source.contains(input)
    }

    fun canConvertFromDestination(input: Int): Boolean {
        return destination.contains(input)
    }

    fun convertFromSourceToDestination(input: Int): Int {
        if (!canConvertFromSource(input)) throw IndexOutOfBoundsException("Input $input outside of source range: $source.")
        val index = source.indexOf(input)
        return destination.elementAt(index)
    }

    fun convertFromDestinationToSource(input: Int): Int {
        if (!canConvertFromDestination(input)) throw IndexOutOfBoundsException("Input $input outside of destination range: $destination.")
        val index = destination.indexOf(input)
        return source.elementAt(index)
    }
}