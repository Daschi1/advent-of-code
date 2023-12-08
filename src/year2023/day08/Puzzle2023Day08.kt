package year2023.day08

import Puzzle

fun main() {
    val puzzle = Puzzle2023Day08()
    puzzle.testAndSolveAndPrint()
}

class Puzzle2023Day08 : Puzzle<Int, Long>("2023", "08", 2, 6) {
    override fun solvePart1(input: List<String>): Int {
        val steps = parseDirectionsFromInput(input)
        val nodes = parseNodesFromInput(input)
        val network = Network(nodes)
        return network.calculateAmountOfStepsToFinalNode(steps, "AAA")
    }

    override fun solvePart2(input: List<String>): Long {
        val steps = parseDirectionsFromInput(input)
        val nodes = parseNodesFromInput(input)
        val network = Network(nodes)
        return network.calculateAmountOfStepsToAllFinalNodes(steps)
    }
}

private fun parseDirectionsFromInput(input: List<String>): List<Char> {
    if (input.isEmpty()) return emptyList()
    return input[0].toCharArray().toList()
}

private fun parseNodesFromInput(input: List<String>): List<Node> {
    if (input.size < 3) return emptyList()
    val rawNodes = input.subList(2, input.size)
    val nodes = mutableListOf<Node>()

    for (rawNode in rawNodes) {
        val split = rawNode.split("=")
        val directionSplit = split[1].replace("(", "").replace(")", "").split(",")

        val label = split[0].trim()
        val left = directionSplit[0].trim()
        val right = directionSplit[1].trim()
        nodes.add(Node(label, left, right))
    }
    return nodes
}

private data class Network(val nodes: List<Node>) {
    private val indexedNodes = mutableMapOf<String, Node>()

    init {
        require(nodes.any { it.label.endsWith('A') }) { "A start node ending with 'A' is required!" }
        require(nodes.any { it.label.endsWith('Z') }) { "A final node ending with 'Z' is required!" }
        for (node in nodes) {
            indexedNodes[node.label] = node
        }
    }

    fun calculateAmountOfStepsToFinalNode(directions: List<Char>, startNode: String): Int {
        var currentNode = indexedNodes[startNode]!!
        var stepAmount = 0
        while (!currentNode.label.endsWith('Z')) {
            for (direction in directions) {
                currentNode = if (direction == 'L') {
                    val nextNode = indexedNodes[currentNode.left]
                        ?: throw IndexOutOfBoundsException("A node labeled '${currentNode.left}' could not be found, but is linked to by '${currentNode.label}' left entry.")
                    nextNode
                } else {
                    val nextNode = indexedNodes[currentNode.right]
                        ?: throw IndexOutOfBoundsException("A node labeled '${currentNode.right}' could not be found, but is linked to by '${currentNode.label}' right entry.")
                    nextNode
                }
                stepAmount++
            }
        }
        return stepAmount
    }

    fun calculateAmountOfStepsToAllFinalNodes(directions: List<Char>): Long {
        val startNodes = indexedNodes.filterKeys { it.endsWith('A') }.values
        // Finding cycle count of each startNode first, then computing lcm to figure out number of steps required when searching for them simultaneously
        return startNodes.map { calculateAmountOfStepsToFinalNode(directions, it.label) }
            .map { it.toLong() }
            .reduce { acc, i -> findLCM(acc, i) }
    }
}

private data class Node(val label: String, val left: String, val right: String)

private fun findLCM(a: Long, b: Long): Long {
    val larger = if (a > b) a else b
    val maxLcm = a * b
    var lcm = larger
    while (lcm <= maxLcm) {
        if (lcm % a == 0L && lcm % b == 0L) {
            return lcm
        }
        lcm += larger
    }
    return maxLcm
}