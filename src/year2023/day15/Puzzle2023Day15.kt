package year2023.day15

import Puzzle

fun main() {
    val puzzle = Puzzle2023Day15()
    puzzle.testAndSolveAndPrint()
}

class Puzzle2023Day15 : Puzzle<Int, Int>("2023", "15", 1320, 145) {
    override fun solvePart1(input: List<String>): Int {
        val instructions = input[0].split(",")
        var sum = 0
        for (instruction in instructions) {
            sum += calculateHash(instruction)
        }
        return sum
    }

    override fun solvePart2(input: List<String>): Int {
        val instructions = input[0].split(",")
        val boxes = Boxes()
        for (instruction in instructions) {
            boxes.processInstruction(instruction)
        }
        var sum = 0
        for (box in boxes.boxes) {
            for ((i, lens) in box.value.withIndex()) {
                val focussingPower = (1 + box.key) * (i + 1) * lens.focalLength
                sum += focussingPower
            }
        }
        return sum
    }
}

private fun calculateHash(input: String): Int {
    var currentValue = 0
    for (c in input.toCharArray()) {
        val asciValue = c.code
        currentValue += asciValue
        currentValue *= 17
        currentValue %= 256
    }
    return currentValue
}

private class Boxes {
    val boxes = mutableMapOf<Int, MutableList<Lens>>()

    fun processInstruction(instruction: String) {
        if (instruction.contains("-")) {
            val label = instruction.replace("-", "")
            removeLabel(label)
        } else if (instruction.contains("=")) {
            val split = instruction.split("=")
            val label = split[0]
            val focalLength = split[1].toInt()
            addLabel(label, focalLength)
        }
    }

    private fun removeLabel(label: String) {
        val hash = calculateHash(label)
        if (hash !in boxes) return
        val lenses = boxes[hash]!!
        lenses.remove(Lens(label, -1))
        boxes[hash] = lenses
    }

    private fun addLabel(label: String, focalLength: Int) {
        val hash = calculateHash(label)
        if (hash !in boxes) boxes[hash] = mutableListOf()
        val lenses = boxes[hash]!!
        val lens = Lens(label, focalLength)
        if (lens in lenses) lenses[lenses.indexOf(lens)] = lens
        else lenses.add(lens)
        boxes[hash] = lenses
    }
}

private data class Lens(val label: String, val focalLength: Int) {
    override fun equals(other: Any?): Boolean {
        if (other is Lens) {
            return other.label == label
        } else if (other is String) {
            return other == label
        }
        return false
    }

    override fun hashCode(): Int {
        return label.hashCode()
    }
}