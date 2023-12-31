package year2023.day19

import Puzzle

fun main() {
    val puzzle = Puzzle2023Day19()
    puzzle.testAndSolveAndPrint()
}

class Puzzle2023Day19 : Puzzle<Int, Long>("2023", "19", 19114, 167409079868000) {
    override fun solvePart1(input: List<String>): Int {
        val workflows = parseWorkflowsFromInput(input)
        val parts = parsePartsFromInput(input)

        var sum = 0
        for (part in parts) {
            val accepted = isPartAccepted(workflows, part)
            if (accepted) {
                sum += part.x + part.m + part.a + part.s
            }
        }
        return sum
    }

    override fun solvePart2(input: List<String>): Long {
        val workflows = parseWorkflowsFromInput(input)

        val acceptedParts = calculateAcceptedParts(
            workflows, mapOf(
                'x' to (1..4000),
                'm' to (1..4000),
                'a' to (1..4000),
                's' to (1..4000)
            ), "in"
        )
        return acceptedParts
    }
}

private fun parseWorkflowsFromInput(input: List<String>): Map<String, Workflow> {
    val emptyLineIndex = input.indexOfFirst { it.isEmpty() }

    val workflows = mutableMapOf<String, Workflow>()
    for (s in input.subList(0, emptyLineIndex)) {
        val split = s.split("{")
        val name = split[0]

        val stepsString = split[1].removeSuffix("}").split(",")
        val steps = mutableListOf<WorkflowStep>()
        for (stepString in stepsString) {
            val step = if (stepString.contains(":")) {
                val stepSplit = stepString.split(":")
                val condition = stepSplit[0]
                val nextStep = stepSplit[1]
                WorkflowStep(condition, nextStep)
            } else {
                WorkflowStep("", stepString)
            }
            steps.add(step)
        }

        workflows[name] = Workflow(name, steps)
    }
    if (!workflows.contains("in")) error("Could find a workflow named 'in'")
    return workflows
}

private fun parsePartsFromInput(input: List<String>): List<Part> {
    val emptyLineIndex = input.indexOfFirst { it.isEmpty() }

    val parts = mutableListOf<Part>()
    for (str in input.drop(emptyLineIndex + 1)) {
        val split = str.removePrefix("{").removeSuffix("}").split(",")
        val x = split[0].substring(2).toInt()
        val m = split[1].substring(2).toInt()
        val a = split[2].substring(2).toInt()
        val s = split[3].substring(2).toInt()
        parts.add(Part(x, m, a, s))
    }
    return parts
}

private fun isPartAccepted(workflows: Map<String, Workflow>, part: Part): Boolean {
    var currentWorkflowName = "in"
    while (currentWorkflowName !in listOf("A", "R")) {
        val currentWorkflow =
            workflows[currentWorkflowName] ?: error("Could not parse a workflow named '$currentWorkflowName'")
        currentWorkflowName = currentWorkflow.calculateNextStep(part)
    }
    return currentWorkflowName == "A"
}

private fun calculateAcceptedParts(
    workflows: Map<String, Workflow>,
    acceptedRanges: Map<Char, IntRange>,
    workflowIn: String
): Long {
    return when (workflowIn) {
        "A" -> acceptedRanges.values.map { it.size().toLong() }.reduce(Long::times)
        "R" -> 0
        else -> {
            val newAcceptedRanges = acceptedRanges.toMutableMap()
            val nextWorkflow = workflows[workflowIn] ?: error("Could not find a workflow named '$workflowIn'")
            nextWorkflow.steps.sumOf { step ->
                when (step.parameter) {
                    ' ' -> calculateAcceptedParts(workflows, newAcceptedRanges, step.nextStep)
                    else -> {
                        val parameter = step.parameter
                        val newPassingRange = newAcceptedRanges.getValue(parameter).combineWith(step.getPassingRange())
                        val newPassingRangeReversed =
                            newAcceptedRanges.getValue(parameter).combineWith(step.getReversedPassingRange())

                        newAcceptedRanges[parameter] = newPassingRange
                        calculateAcceptedParts(
                            workflows,
                            newAcceptedRanges,
                            step.nextStep
                        ).also { newAcceptedRanges[parameter] = newPassingRangeReversed }
                    }
                }
            }
        }
    }
}

private fun IntRange.size() = last - start + 1

private fun IntRange.combineWith(other: IntRange) = maxOf(first, other.first)..minOf(last, other.last)

private data class Workflow(val name: String, val steps: List<WorkflowStep>) {
    fun calculateNextStep(part: Part): String {
        for (step in steps) {
            val passes = step.passesCondition(part)
            if (passes) return step.nextStep
        }
        error("Could not find next step for part $part in workflow $this")
    }
}

private data class WorkflowStep(val condition: String, val nextStep: String) {

    val parameter: Char
    private val comparison: Char
    private val value: Int

    init {
        if (condition.isEmpty()) {
            parameter = ' '
            comparison = ' '
            value = -1
        } else {
            parameter = condition.substring(0, 1).first()
            comparison = condition.substring(1, 2).first()
            value = condition.substring(2).toInt()
        }
    }

    fun passesCondition(part: Part): Boolean {
        if (parameter == ' ' && comparison == ' ' && value == -1) return true
        val toEvaluate = when (parameter) {
            'x' -> part.x
            'm' -> part.m
            'a' -> part.a
            's' -> part.s
            else -> error("Invalid parameter '$parameter'")
        }
        return when (comparison) {
            '<' -> toEvaluate < value
            '>' -> toEvaluate > value
            else -> error("Invalid comparison '$comparison'")
        }
    }

    fun getPassingRange(): IntRange {
        if (parameter == ' ' && comparison == ' ' && value == -1) return 1..4000
        return when (comparison) {
            '<' -> 1..<value
            '>' -> value + 1..4000
            else -> error("Invalid comparison '$comparison'")
        }
    }

    fun getReversedPassingRange(): IntRange {
        if (parameter == ' ' && comparison == ' ' && value == -1) return IntRange(1, 4000)
        return when (comparison) {
            '<' -> value..4000
            '>' -> 1..value
            else -> error("Invalid comparison '$comparison'")
        }
    }
}

private data class Part(val x: Int, val m: Int, val a: Int, val s: Int)