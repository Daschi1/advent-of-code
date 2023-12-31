package year2023.day03

import Puzzle

fun main() {
    val puzzle = Puzzle2023Day03()
    puzzle.testAndSolveAndPrint()
}

class Puzzle2023Day03 : Puzzle<Int, Int>("2023", "03", 4361, 467835) {
    override fun solvePart1(input: List<String>): Int {
        val schematic = Schematic(input)
        return schematic.computeSchematicNumbersAdjacentToSchematicSymbols().sumOf { it.number }
    }

    override fun solvePart2(input: List<String>): Int {
        val schematic = Schematic(input)
        return schematic.computeSchematicGears().sumOf { it.gearRatio }
    }
}

private class Schematic(private val input: List<String>) {
    private val schematicNumbers = parseSchematicNumbers()
    private val schematicSymbols = parseSchematicSymbols()

    private inline fun <T> parseSchematics(
        regexPattern: String,
        crossinline createObject: (MatchResult, Int) -> T
    ): List<T> {
        val schematics: MutableList<T> = mutableListOf()
        val regex = regexPattern.toRegex()
        for ((i, s) in input.withIndex()) {
            val matchResults = regex.findAll(s)
            for (matchResult in matchResults) {
                schematics.add(createObject(matchResult, i))
            }
        }
        return schematics
    }

    private fun parseSchematicNumbers(): List<SchematicNumber> {
        // \d matches any digit 0-9
        // + matches one or more of previous element
        return parseSchematics("\\d+") { matchResult, i ->
            SchematicNumber(matchResult.value.toInt(), i, matchResult.range)
        }
    }

    private fun parseSchematicSymbols(): List<SchematicSymbol> {
        // [^...] - Matches any char not listed in brackets
        // \\d - Matches any digit (0-9)
        // . - Matches a literal '.'
        return parseSchematics("[^\\d.]") { matchResult, i ->
            SchematicSymbol(matchResult.value.single(), i, matchResult.range.first)
        }
    }

    fun computeSchematicNumbersAdjacentToSchematicSymbols(): List<SchematicNumber> {
        return schematicNumbers.filter { number ->
            val possibleSchematicSymbols = schematicSymbols.filter { symbol ->
                symbol.y in IntRange(number.y - 1, number.y + 1)
            }.filter { symbol ->
                symbol.x in IntRange(number.x.first - 1, number.x.last + 1)
            }
            possibleSchematicSymbols.isNotEmpty()
        }
    }

    fun computeSchematicGears(): List<SchematicGear> {
        return schematicSymbols.filter { symbol ->
            symbol.symbol == '*'
        }.mapNotNull { symbol ->
            val possibleSchematicNumbers = computeSurroundingNumbers(symbol)
            if (possibleSchematicNumbers.size == 2) {
                val ratio = possibleSchematicNumbers[0].number * possibleSchematicNumbers[1].number
                SchematicGear(ratio)
            } else null
        }
    }

    private fun computeSurroundingNumbers(symbol: SchematicSymbol): List<SchematicNumber> {
        return schematicNumbers.filter { number ->
            number.y in IntRange(symbol.y - 1, symbol.y + 1)
        }.filter { number ->
            number.x intersects IntRange(symbol.x - 1, symbol.x + 1)
        }
    }
}

private data class SchematicNumber(val number: Int, val y: Int, val x: IntRange)
private data class SchematicSymbol(val symbol: Char, val y: Int, val x: Int)
private data class SchematicGear(val gearRatio: Int)

private infix fun IntRange.intersects(range: IntRange): Boolean {
    return this.last >= range.first && this.first <= range.last
}