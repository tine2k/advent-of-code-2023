import org.apache.commons.math3.util.ArithmeticUtils

fun main() {
    val testInput1 = "RL\n" +
            "\n" +
            "AAA = (BBB, CCC)\n" +
            "BBB = (DDD, EEE)\n" +
            "CCC = (ZZZ, GGG)\n" +
            "DDD = (DDD, DDD)\n" +
            "EEE = (EEE, EEE)\n" +
            "GGG = (GGG, GGG)\n" +
            "ZZZ = (ZZZ, ZZZ)"

    val testInput2 = "LLR\n" +
            "\n" +
            "AAA = (BBB, BBB)\n" +
            "BBB = (AAA, ZZZ)\n" +
            "ZZZ = (ZZZ, ZZZ)"

    val testInput3 = "LR\n" +
            "\n" +
            "11A = (11B, XXX)\n" +
            "11B = (XXX, 11Z)\n" +
            "11Z = (11B, XXX)\n" +
            "22A = (22B, XXX)\n" +
            "22B = (22C, 22C)\n" +
            "22C = (22Z, 22Z)\n" +
            "22Z = (22B, 22B)\n" +
            "XXX = (XXX, XXX)"

    fun parseInput(lines: List<String>): Pair<CharArray, Map<String, Pair<String, String>>> {
        val instructions = lines[0].toCharArray()
        val steps = lines.subList(2, lines.size).associate {
            val tokens = it.split(" = ")
            val start = tokens[0]
            val lrTokens = tokens[1].replace("(", "").replace(")", "").split(", ")
            start to (lrTokens[0] to lrTokens[1])
        }

        return instructions to steps
    }

    fun solve(instructions: CharArray, steps: Map<String, Pair<String, String>>, startLocation: String): Long {
        var stepCount = 0L
        var currentInstructionIndex = 0
        var currentLocation = startLocation
        do {
            val currentInstruction = instructions[currentInstructionIndex]
            val currentLocationMapping = steps[currentLocation]!!
            currentLocation = if (currentInstruction == 'L') {
                currentLocationMapping.first
            } else {
                currentLocationMapping.second
            }

            stepCount++
            currentInstructionIndex++
            if (currentInstructionIndex >= instructions.size) {
                currentInstructionIndex = 0
            }
        } while (!currentLocation.endsWith("Z"))
        return stepCount
    }

    fun solve1(lines: List<String>): Long {
        val (instructions, steps) = parseInput(lines)
        return solve(instructions, steps, "AAA")
    }

    fun solve2(lines: List<String>): Long {
        val (instructions, steps) = parseInput(lines)
        return steps.keys.filter { it.endsWith("A") }
            .map { solve(instructions, steps, it) }
            .reduce { a, b -> ArithmeticUtils.lcm(a, b) }
    }

    header(1)
    test(::solve1, testInput1, 2)
    test(::solve1, testInput2, 6)
    solve(::solve1)

    header(2)
    test(::solve2, testInput3, 6)
    solve(::solve2)
}
