package year2023.day20

import Puzzle

fun main() {
    val puzzle = Puzzle2023Day20()
    puzzle.testAndSolveAndPrint()
}

class Puzzle2023Day20 : Puzzle<Int, Long>("2023", "20", 32000000, -1) {
    override fun solvePart1(input: List<String>): Int {
        val modules = parseModulesFromInput(input)
        val modulesDestinations = parseModulesDestinationsFromInput(input)
        for ((sourceModule, destinationModules) in modulesDestinations) {
            for (destinationModule in destinationModules) {
                val module = modules.getValue(destinationModule)
                if (module is ConjunctionModule) {
                    module.mostRecentPulses[sourceModule] = Pulse.LOW
                }
            }
        }
        var amountOfHighPules = 0
        var amountOfLowPules = 0

        repeat(1000) {
            var currentModules = mutableListOf<Pair<Module, Pulse>>()
            currentModules.add(Pair(modules.getValue("broadcaster"), Pulse.LOW))

            while (currentModules.isNotEmpty()) {
                val nextModules = mutableListOf<Pair<Module, Pulse>>()
                for ((currentModule, currentPulse) in currentModules) {
                    when (currentPulse) {
                        Pulse.HIGH -> amountOfHighPules++
                        Pulse.LOW -> amountOfLowPules++
                    }

                    val output = currentModule.outputPulse(currentPulse) ?: continue
                    for (moduleDestination in modulesDestinations.getValue(currentModule.name)) {
                        val destinationModule = modules.getValue(moduleDestination)
                        destinationModule.receivePulse(output, currentModule.name)
                        nextModules.add(Pair(destinationModule, output))
                    }
                }
                currentModules = nextModules
            }
        }
        return amountOfHighPules * amountOfLowPules
    }

    override fun solvePart2(input: List<String>): Long {
        // no test input / result provided for part2
        if (input.isEmpty()) return -1

        val modules = parseModulesFromInput(input)
        val modulesDestinations = parseModulesDestinationsFromInput(input)
        for ((sourceModule, destinationModules) in modulesDestinations) {
            for (destinationModule in destinationModules) {
                val module = modules.getValue(destinationModule)
                if (module is ConjunctionModule) {
                    module.mostRecentPulses[sourceModule] = Pulse.LOW
                }
            }
        }

        // Extract the names of all FlipFlop modules.
        val flipFlopNames = getFlipFlopNames(modules)

        // Calculate results for each binary counter.
        val binaryCounterResults = modulesDestinations.getValue("broadcaster")
            .map { name -> calculateBinaryCounterResult(name, modules, modulesDestinations, flipFlopNames) }

        // Combine the results to find the Least Common Multiple (LCM).
        return binaryCounterResults.reduce(::findLCM)
    }
}

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

// Extracts the names of all FlipFlop modules.
private fun getFlipFlopNames(modules: Map<String, Module>): Set<String> {
    return modules.values.filterIsInstance<FlipFlopModule>().map { it.name }.toSet()
}

// Calculates the result for a single binary counter.
private fun calculateBinaryCounterResult(
    name: String,
    modules: Map<String, Module>,
    modulesDestinations: Map<String, List<String>>,
    flipFlopNames: Set<String>
): Long {
    val startModule = modules.getValue(name)
    val conjunctionName = findConjunctionForModule(name, modules, modulesDestinations)
    return generateBinarySequence(startModule, modules, modulesDestinations, flipFlopNames)
        .map { if (conjunctionName in modulesDestinations.getValue(it.name)) 1 else 0 }
        .foldIndexed(0L) { index, acc, value -> acc + (value shl index) }
}

// Finds the conjunction associated with a module.
private fun findConjunctionForModule(
    moduleName: String,
    modules: Map<String, Module>,
    modulesDestinations: Map<String, List<String>>
): String {
    return modulesDestinations.getValue(moduleName)
        .first { modules.getValue(it) is ConjunctionModule }
}

// Generates a sequence of modules starting from a given module.
private fun generateBinarySequence(
    startModule: Module,
    modules: Map<String, Module>,
    modulesDestinations: Map<String, List<String>>,
    flipFlopNames: Set<String>
): Sequence<Module> {
    return generateSequence(startModule) { module ->
        modules[modulesDestinations.getValue(module.name).firstOrNull { it in flipFlopNames }]
    }
}

private fun parseModulesDestinationsFromInput(input: List<String>): Map<String, List<String>> {
    val modules = mutableMapOf<String, List<String>>()

    for (s in input) {
        val split = s.split(" -> ")
        val destinations = split[1].split(", ")

        val module = createModuleFromIdentifier(split[0])
        modules[module.name] = destinations
    }
    return modules
}

private fun parseModulesFromInput(input: List<String>): Map<String, Module> {
    val modules = mutableMapOf<String, Module>()

    for (s in input) {
        val split = s.split(" -> ")
        val destinations = split[1].split(", ")
        for (destination in destinations) {
            if (!modules.containsKey(destination)) modules[destination] = BlankModule(destination)
        }

        val module = createModuleFromIdentifier(split[0])
        modules[module.name] = module
    }
    return modules
}

private fun createModuleFromIdentifier(identifier: String): Module {
    val type = identifier.first()
    val name = identifier.drop(1)
    return when (type) {
        '%' -> FlipFlopModule(name, false)
        '&' -> ConjunctionModule(name, mutableMapOf())
        'b' -> BroadcastModule(type + name)
        else -> error("No module is associated with '$type'")
    }
}

private sealed class Module(val name: String) {
    open fun receivePulse(pulse: Pulse, from: String) {}

    open fun outputPulse(pulse: Pulse): Pulse? = pulse
}

private class FlipFlopModule(name: String, private var on: Boolean) : Module(name) {
    override fun receivePulse(pulse: Pulse, from: String) {
        if (pulse == Pulse.LOW) on = !on
    }

    override fun outputPulse(pulse: Pulse): Pulse? {
        return when (pulse) {
            Pulse.HIGH -> null
            Pulse.LOW -> if (on) Pulse.HIGH else Pulse.LOW
        }
    }
}

private class ConjunctionModule(name: String, val mostRecentPulses: MutableMap<String, Pulse>) : Module(name) {

    override fun receivePulse(pulse: Pulse, from: String) {
        mostRecentPulses[from] = pulse
    }

    override fun outputPulse(pulse: Pulse): Pulse {
        val allHighs = mostRecentPulses.values.all { it == Pulse.HIGH }
        return if (allHighs) Pulse.LOW else Pulse.HIGH
    }
}

private class BlankModule(name: String) : Module(name) {
    override fun outputPulse(pulse: Pulse): Pulse? {
        return null
    }
}

private class BroadcastModule(name: String) : Module(name)

private enum class Pulse {
    HIGH,
    LOW
}