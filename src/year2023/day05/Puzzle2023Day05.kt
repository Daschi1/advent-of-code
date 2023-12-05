package year2023.day05

import Puzzle
import println

fun main() {
    val puzzle = Puzzle2023Day05()
    puzzle.testAndSolveAndPrint()
}

class Puzzle2023Day05 : Puzzle<Int, Int>("2023", "05", 35, -1) {
    override fun solvePart1(input: List<String>): Int {
        if (input.isEmpty()) return -1
        "parsing".println()
        val seeds =
            input[0].split(":")[1].split(" ").filter { s -> s.all { it.isDigit() } }.mapNotNull { it.toLongOrNull() }

        val converters = mutableListOf<Converter>()
        var from = ""
        var to = ""
        var ranges = mutableListOf<LinkedLongRange>()
        for (s in input.drop(1)) {
            if (s.isEmpty()) continue

            if (s.endsWith(":")) {
                if (ranges.isNotEmpty()) {
                    val converter = Converter(from, to, ranges)
                    converters.add(converter)
                }

                val split = s.split(" ")[0].split("-")
                from = split[0]
                to = split[2]
                ranges = mutableListOf()
                continue
            }

            val splitInts = s.split(" ").mapNotNull { it.toLongOrNull() }
            val linkedLongRange = LinkedLongRange(
                LongRange(splitInts[1], splitInts[1] + splitInts[2]),
                LongRange(splitInts[0], splitInts[0] + splitInts[2])
            )
            ranges.add(linkedLongRange)
        }
        if (ranges.isNotEmpty()) {
            val converter = Converter(from, to, ranges)
            converters.add(converter)
        }
        "finished parsing".println()

        var lowestLocation = Long.MAX_VALUE
        for (seed in seeds) {
            var number = seed
            for (converter in converters) {
                number = converter.convertFromTo(number)
            }
            if (number < lowestLocation) lowestLocation = number
        }
        return lowestLocation.toInt()
    }

    override fun solvePart2(input: List<String>): Int {
        return input.size
    }
}

private data class Converter(val from: String, val to: String, val ranges: List<LinkedLongRange>) {
    fun convertFromTo(input: Long): Long {
        val linkedIntRange = ranges.find { it.canConvertFromSource(input) }
        return linkedIntRange?.convertFromSourceToDestination(input) ?: return input
    }

    fun convertToFrom(input: Long): Long {
        val linkedIntRange = ranges.find { it.canConvertFromDestination(input) }
        return linkedIntRange?.convertFromDestinationToSource(input) ?: return input
    }
}

private data class LinkedLongRange(private val source: LongRange, private val destination: LongRange) {
    init {
        require(source.count() == destination.count()) { "Source length ${source.count()} and destination length ${destination.count()} must have the same length." }
    }

    fun canConvertFromSource(input: Long): Boolean {
        return source.contains(input)
    }

    fun canConvertFromDestination(input: Long): Boolean {
        return destination.contains(input)
    }

    fun convertFromSourceToDestination(input: Long): Long {
        if (!canConvertFromSource(input)) throw IndexOutOfBoundsException("Input $input outside of source range: $source.")
        return destination.first + (input - source.first)
    }

    fun convertFromDestinationToSource(input: Long): Long {
        if (!canConvertFromDestination(input)) throw IndexOutOfBoundsException("Input $input outside of destination range: $destination.")
        return source.first + (input - destination.first)
    }
}