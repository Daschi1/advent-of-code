package year2023.day10

import Puzzle

fun main() {
    val puzzle = Puzzle2023Day10()
    puzzle.testAndSolveAndPrint()
}

class Puzzle2023Day10 : Puzzle<Int, Int>("2023", "10", 4, -1) {
    override fun solvePart1(input: List<String>): Int {
        val pipeSymbols = parsePipeSymbolsFromInput(input)
        val pipeNetwork = PipeNetwork(pipeSymbols)
        val loop = pipeNetwork.findLoopFromStart()
        return (loop.size + 1) / 2
    }

    override fun solvePart2(input: List<String>): Int {
        return input.size
    }
}

private fun parsePipeSymbolsFromInput(input: List<String>): List<PipeSymbol> {
    val pipeSymbols = mutableListOf<PipeSymbol>()
    input.forEachIndexed { y, s ->
        s.toCharArray().forEachIndexed { x, c ->
            val pipeSymbol = PipeSymbol(c, x, y)
            pipeSymbols.add(pipeSymbol)
        }
    }
    return pipeSymbols.toList()
}

private data class PipeNetwork(val pipeSymbols: List<PipeSymbol>) {
    val start: PipeSymbol
    val width: Int
    val height: Int
    val grid: Array<Array<PipeSymbol>>

    init {
        require(pipeSymbols.any { it.symbol == 'S' }) { "PipeNetwork requires one starting position 'S'" }
        start = pipeSymbols.find { it.symbol == 'S' }!!

        width = pipeSymbols.maxOfOrNull { it.x }!! + 1
        height = pipeSymbols.maxOfOrNull { it.y }!! + 1

        grid = Array(height) { Array(width) { PipeSymbol.PLACEHOLDER } }
        for (pipeSymbol in pipeSymbols) {
            grid[pipeSymbol.y][pipeSymbol.x] = pipeSymbol
        }
    }

    fun findLoopFromStart(): List<PipeSymbol> {
        val directionsFromStart = Direction.entries.mapNotNull {
            val next = getLeadingBack(start, it)
            if (next != null) Pair(it, next) else null
        }
        val loop = mutableListOf<PipeSymbol>()
        var currentPipe = directionsFromStart.first()
        while (!currentPipe.second.isStart()) {
            for (direction in Direction.entries) {
                if (direction == currentPipe.first.opposite()) continue
                val next = getConnecting(currentPipe.second, direction)
                if (next != null) {
                    loop.add(currentPipe.second)
                    currentPipe = Pair(direction, next)
                    break
                }
            }
        }
        return loop
    }

    fun getConnecting(from: PipeSymbol, to: Direction): PipeSymbol? {
        val next = get(from, to) ?: return null
        return if (!next.isStart()) {
            if (from.leads(to) && next.leads(to.opposite())) next else null
        } else {
            if (from.leads(to)) next else null
        }
    }

    fun getLeadingBack(from: PipeSymbol, to: Direction): PipeSymbol? {
        val next = get(from, to) ?: return null
        return if (next.leads(to.opposite())) next else null
    }

    fun get(from: PipeSymbol, to: Direction): PipeSymbol? {
        return when (to) {
            Direction.NORTH -> getNorth(from)
            Direction.SOUTH -> getSouth(from)
            Direction.WEST -> getWest(from)
            Direction.EAST -> getEast(from)
        }
    }

    fun getNorth(from: PipeSymbol): PipeSymbol? {
        val nextY = from.y - 1
        if (nextY < 0 || nextY >= height) return null
        return grid[nextY][from.x]
    }

    fun getSouth(from: PipeSymbol): PipeSymbol? {
        val nextY = from.y + 1
        if (nextY < 0 || nextY >= height) return null
        return grid[nextY][from.x]
    }

    fun getWest(from: PipeSymbol): PipeSymbol? {
        val nextX = from.x - 1
        if (nextX < 0 || nextX >= width) return null
        return grid[from.y][nextX]
    }

    fun getEast(from: PipeSymbol): PipeSymbol? {
        val nextX = from.x + 1
        if (nextX < 0 || nextX >= width) return null
        return grid[from.y][nextX]
    }
}

private enum class Direction {
    NORTH,
    SOUTH,
    WEST,
    EAST;

    fun opposite(): Direction {
        return when (this) {
            NORTH -> SOUTH
            SOUTH -> NORTH
            WEST -> EAST
            EAST -> WEST
        }
    }
}

private data class PipeSymbol(val symbol: Char, val x: Int, val y: Int) {
    companion object {
        val PLACEHOLDER = PipeSymbol('X', -1, -1)
    }

    fun leads(to: Direction): Boolean {
        return when (to) {
            Direction.NORTH -> leadsNorth()
            Direction.SOUTH -> leadsSouth()
            Direction.WEST -> leadsWest()
            Direction.EAST -> leadsEast()
        }
    }

    fun leadsNorth(): Boolean {
        return symbol in listOf('|', 'L', 'J')
    }

    fun leadsSouth(): Boolean {
        return symbol in listOf('|', '7', 'F')
    }

    fun leadsWest(): Boolean {
        return symbol in listOf('-', 'J', '7')
    }

    fun leadsEast(): Boolean {
        return symbol in listOf('-', 'L', 'F')
    }

    fun isGround(): Boolean {
        return symbol == '.'
    }

    fun isStart(): Boolean {
        return symbol == 'S'
    }
}