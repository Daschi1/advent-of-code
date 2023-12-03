abstract class Puzzle<O, T>(
    private val year: String,
    private val day: String,
    private val expectedTest1Result: O,
    private val expectedTest2Result: T
) {
    private val input = readInput("year$year/day$day/Day$day")
    private val inputTest1 = readInput("year$year/day$day/Day${day}_test1")
    private val inputTest2 = readInput("year$year/day$day/Day${day}_test2")

    abstract fun solvePart1(input: List<String>): O
    abstract fun solvePart2(input: List<String>): T

    fun testAndSolveAndPrint() {
        println("Puzzle year $year, day $day:")
        val test1Result = testPartOne()
        val test2Result = testPartTwo()
        println("test part one: ${if (test1Result) "passed" else "failed"}")
        println("test part two: ${if (test2Result) "passed" else "failed"}")
        println("solution part one: ${if (test1Result) solvePart1(input).toString() else "-"}")
        println("solution part two: ${if (test2Result) solvePart2(input).toString() else "-"}")
        println("---")
    }

    private fun testPartOne(): Boolean {
        return solvePart1(inputTest1) == expectedTest1Result
    }

    private fun testPartTwo(): Boolean {
        return solvePart2(inputTest2) == expectedTest2Result
    }
}
