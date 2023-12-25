package year2023.day25

import Puzzle
import org.jgrapht.alg.StoerWagnerMinimumCut
import org.jgrapht.graph.DefaultWeightedEdge
import org.jgrapht.graph.SimpleWeightedGraph

fun main() {
    val puzzle = Puzzle2023Day25()
    puzzle.testAndSolveAndPrint()
}

class Puzzle2023Day25 : Puzzle<Int, Int>("2023", "25", 54, -1) {

    //** Thanks reddit for suggesting jgrapht */
    override fun solvePart1(input: List<String>): Int {
        // Create a simple weighted graph.
        val graph = SimpleWeightedGraph<String, DefaultWeightedEdge>(DefaultWeightedEdge::class.java)

        // Process each line of input to build the graph.
        input.forEach { line ->
            // Split the line into vertex name and its connections.
            val (vertexName, connections) = line.split(": ")

            // Add the vertex to the graph.
            graph.addVertex(vertexName)

            // Add each connection as a vertex and create an edge between the vertex and its connection.
            connections.split(" ").forEach { connection ->
                graph.addVertex(connection)
                graph.addEdge(vertexName, connection)
            }
        }

        // Compute the minimum cut of the graph.
        val minimumCut = StoerWagnerMinimumCut(graph).minCut()
        // Calculate the number of vertices in the graph.
        val totalVertices = graph.vertexSet().size
        // Calculate the size of the minimum cut.
        val minCutSize = minimumCut.size
        // Compute the result based on the graph's vertices and the minimum cut size.
        return (totalVertices - minCutSize) * minCutSize
    }

    override fun solvePart2(input: List<String>): Int {
        // There's no real puzzle here. It just requires obtaining all the previous stars of this year to solve it.
        return -1
    }
}