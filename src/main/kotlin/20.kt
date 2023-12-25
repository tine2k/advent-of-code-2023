fun main() {
    val testInput1 = "broadcaster -> a, b, c\n" +
            "%a -> b\n" +
            "%b -> c\n" +
            "%c -> inv\n" +
            "&inv -> a\n"

    val testInput2 = "broadcaster -> a\n" +
            "%a -> inv, con\n" +
            "&inv -> b\n" +
            "%b -> con\n" +
            "&con -> output"

    fun sendPulse(
        pulse: Pulse,
        rules: Map<String, List<String>>,
        flipflops: MutableMap<String, Boolean>,
        inverters: Map<String, MutableMap<String, Boolean>>
    ): List<Pulse> {
        if (!rules.contains(pulse.output)) {
            return emptyList()
        }
        val targets = rules[pulse.output]!!
        var modul = pulse.high
        if (flipflops.contains(pulse.output)) {
            if (pulse.high) {
                return emptyList()
            } else {
                modul = !flipflops[pulse.output]!!
                flipflops[pulse.output] = modul
            }
        } else if (inverters.contains(pulse.output)) {
            inverters[pulse.output]!![pulse.source] = pulse.high
            modul = !inverters[pulse.output]!!.all { it.value }
        }
        return targets.map { Pulse(pulse.output, it, modul) }
    }

    fun pushButton(
        rules: Map<String, List<String>>,
        flipflops: MutableMap<String, Boolean>,
        inverters: Map<String, MutableMap<String, Boolean>>
    ): List<Pulse> {
        val pulses = mutableListOf<Pulse>()
        val stack = mutableListOf(Pulse("button", "broadcaster", false))
        pulses.add(stack[0])
        while (stack.isNotEmpty()) {
            val next = stack.removeFirst()
            val nextPulses = sendPulse(next, rules, flipflops, inverters)
            if (nextPulses.isNotEmpty()) {
                stack.addAll(nextPulses)
            }
            pulses.addAll(nextPulses)
        }
        return pulses
    }

    fun parseInputData(lines: List<String>): Triple<Map<String, List<String>>, MutableMap<String, Boolean>, Map<String, MutableMap<String, Boolean>>> {
        val input = lines.associate { line ->
            val token = line.split(" -> ")
            token[0] to token[1].split(", ").map { it.trim() }
        }
        val rules = input.mapKeys { (k: String) -> if (k != "broadcaster") k.substring(1) else k }
        val flipflops =
            input.keys.filter { it.startsWith("%") }.map { it.substring(1) }.associateWith { false }.toMutableMap()
        val inverters = input.filter { it.key.startsWith("&") }
            .mapKeys { (key: String) -> key.substring(1) }
            .map { e ->
                e.key to rules.filter { it.value.contains(e.key) }.keys.associateWith { false }.toMutableMap()
            }.toMap()
        return Triple(rules, flipflops, inverters)
    }

    fun solve1(lines: List<String>): Long {
        val (rules, flipflops, inverters) = parseInputData(lines)

        val allPulses = mutableListOf<Pulse>()
        for (i in 0..<1000) {
            printProgress(i, 1000)
            val newPulses = pushButton(rules, flipflops, inverters)
            allPulses.addAll(newPulses)
        }
        return allPulses.count { it.high }.toLong() * allPulses.count { !it.high }
    }

    fun solve2(lines: List<String>): Long {
        val (rules, flipflops, inverters) = parseInputData(lines)

        var i = 0
        while(true) {
            printProgress(i)
            i++
            val newPulses = pushButton(rules, flipflops, inverters)
            if (newPulses.any { p -> !p.high && p.output == "rx" }) {
                return i.toLong()
            }
        }
    }

    header(1)
    test(::solve1, testInput1, 32000000)
    test(::solve1, testInput2, 11687500)
    solve(::solve1)

    header(2)
    solve(::solve2)
}

data class Pulse(val source: String, val output: String, val high: Boolean) {
    override fun toString(): String {
        val modul = if (high) {
            "high"
        } else {
            "low"
        }
        return "$source -${modul}-> $output"
    }
}
