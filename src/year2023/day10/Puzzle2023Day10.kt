package year2023.day10

import Puzzle

fun main() {
    val puzzle = Puzzle2023Day10()
    puzzle.testAndSolveAndPrint()
}

class Puzzle2023Day10 : Puzzle<Int, Int>("2023", "10", 4, 4) {
    override fun solvePart1(input: List<String>): Int {
        val pipeSymbols = parsePipeSymbolsFromInput(input)
        val pipeNetwork = PipeNetwork(pipeSymbols)
        val loop = pipeNetwork.findLoopFromStart()
        return (loop.size + 1) / 2
    }

    override fun solvePart2(input: List<String>): Int {
        val pipeSymbols = parsePipeSymbolsFromInput(input)
        val pipeNetwork = PipeNetwork(pipeSymbols)
        val insideLoop = pipeNetwork.calculatePipesInsideLoop()
        return insideLoop.size
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

    /**
     * Calculates the pipes inside the loop by counting how often a ray-cast northward from each point crosses the loop.
     * If it is an even number of times, the point is outside the loop. If it is an odd number of times, it is inside.
     */
    fun calculatePipesInsideLoop(): List<PipeSymbol> {
        // replace start char with actual pipe
        val directionsFromStart = Direction.entries.mapNotNull {
            val next = getLeadingBack(start, it)
            if (next != null) it else null
        }
        if (directionsFromStart.size != 2) throw IllegalStateException("Could not find two directions from start.")
        val startReplacement = when (directionsFromStart.sorted()) {
            listOf(Direction.NORTH, Direction.WEST).sorted() -> 'J'
            listOf(Direction.NORTH, Direction.SOUTH).sorted() -> '|'
            listOf(Direction.NORTH, Direction.EAST).sorted() -> 'L'
            listOf(Direction.WEST, Direction.SOUTH).sorted() -> '7'
            listOf(Direction.WEST, Direction.EAST).sorted() -> '-'
            listOf(Direction.SOUTH, Direction.EAST).sorted() -> 'F'
            else -> throw IllegalStateException("Could not convert start to pipe.")
        }
        // calculate loop and add start replacement (start not in loop calculation list)
        val loop = findLoopFromStart().toMutableList().apply {
            this.add(start)
        }
        val notLoop = pipeSymbols.filterNot { it in loop }

        // calculate how often a tile hits a pipe on the loop when ray-casting north
        val pipeLoopHitAmount = mutableMapOf<PipeSymbol, Int>()
        for (pipe in notLoop) {
            val loopTilesEncountered = mutableListOf<PipeSymbol>()
            // calculate loop tiles encountered (northward of current tile)
            for (y in pipe.y downTo 0) {
                val current = grid[y][pipe.x]
                if (current in loop) {
                    if (current.isStart()) loopTilesEncountered.add(current.copy(symbol = startReplacement))
                    else loopTilesEncountered.add(current)
                }
            }
            // filter out pipes that only elongate turns
            val filtered = loopTilesEncountered.filterNot { it.symbol == '|' }
            var amountOfHits = 0
            var previous = PipeSymbol.PLACEHOLDER
            for (filteredPipe in filtered) {
                if (filteredPipe.symbol == '-') {
                    // is straight pipe, no turn
                    amountOfHits++
                } else {
                    // only increment hits if top end of turn is viewed
                    if (filteredPipe.symbol == 'F' || filteredPipe.symbol == '7') {
                        if ((filteredPipe.symbol == 'F' && previous.symbol == 'L') || (filteredPipe.symbol == '7' && previous.symbol == 'J')) {
                            // same direction turn
                            amountOfHits += 2
                        } else if ((filteredPipe.symbol == 'F' && previous.symbol == 'J') || (filteredPipe.symbol == '7' && previous.symbol == 'L')) {
                            // opposite direction turn
                            amountOfHits++
                        }
                    }
                }
                previous = filteredPipe
            }
            pipeLoopHitAmount[pipe] = amountOfHits
        }
        // only tiles with an odd number of hits are inside the loop
        return pipeLoopHitAmount.filterNot { it.value % 2 == 0 }.keys.toList()
    }

    /*fun calculatePipesInsideLoop(): List<PipeSymbol> {
        val loop = findLoopFromStart()
        val notLoop = pipeSymbols.filterNot { it in loop }

        val pipeLoopHitAmount = mutableMapOf<PipeSymbol, Int>()
        for (pipe in notLoop) {
            var amountOfHits = 0
            for (y in pipe.y downTo 0) {
                val current = grid[y][pipe.x]
                if (current in loop && current.symbol == '-') {
                    amountOfHits++
                }
            }
            pipeLoopHitAmount[pipe] = amountOfHits
        }
        return pipeLoopHitAmount.filter { it.value % 2 != 0 }.keys.toList()
    }*/

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