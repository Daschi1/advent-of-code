package year2023.day08

import Puzzle

fun main() {
    val puzzle = Puzzle2023Day08()
    puzzle.testAndSolveAndPrint()
}

class Puzzle2023Day08 : Puzzle<Int, Int>("2023", "08", 2, 6) {
    override fun solvePart1(input: List<String>): Int {
        val steps = parseDirectionsFromInput(input)
        val nodes = parseNodesFromInput(input)
        val network = Network(nodes)
        return network.calculateAmountOfStepsToFinalNode(steps)
    }

    override fun solvePart2(input: List<String>): Int {
        return input.size
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
        for (node in nodes) {
            indexedNodes[node.label] = node
        }
        require(indexedNodes.containsKey("AAA")) { "A start node labeled 'AAA' is required!" }
        require(indexedNodes.containsKey("ZZZ")) { "A final node labeled 'ZZZ' is required!" }
    }

    fun calculateAmountOfStepsToFinalNode(directions: List<Char>): Int {
        var currentNode = indexedNodes["AAA"]!!
        var stepAmount = 0
        while (currentNode.label != "ZZZ") {
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
}


private data class Node(val label: String, val left: String, val right: String)