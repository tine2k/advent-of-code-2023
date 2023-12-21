fun main() {
    val testInput1 = "#.#?### 1,1,3\n" +
            ".#...#....###. 1,1,3\n" +
            ".#.###.#.###### 1,3,1,6\n" +
            "####.#...#... 4,1,1\n" +
            "#....######..#####. 1,6,5\n" +
            ".###.##....# 3,2,1"

    val testInput2 = "???.### 1,1,3\n" +
            ".??..??...?##. 1,1,3\n" +
            "?#?#?#?#?#?#?#? 1,3,1,6\n" +
            "????.#...#... 4,1,1\n" +
            "????.######..#####. 1,6,5\n" +
            "?###???????? 3,2,1"


    fun findPath(inv: Invocation, recursiveCallback: (inv: Invocation) -> Long): Long {
        val input = inv.input.toCharArray()
        val results = inv.results
        var index = 0
        var groupCount = inv.groupCountInput
        var groupMemberCount = inv.groupMemberCountInput
        var groupOpen = inv.groupOpenInput
        var solutions = 0L
        val invocationsToDo = mutableListOf<Invocation>()
        while (index < input.size) {
            val missingGroups = results.size - (groupCount + 1)
            if (missingGroups * 2 > input.size - index + 1) {
                break
            }

            when (input[index]) {
                '#' -> {
                    if (!groupOpen) {
                        groupCount++
                        groupOpen = true
                        if (groupCount > results.size - 1) {
                            break
                        }
                    }
                    groupMemberCount++
                }

                '?' -> {
                    if (groupOpen) {
                        if (groupMemberCount < results[groupCount]) {
                            groupMemberCount++ // use #
                        } else if (groupMemberCount == results[groupCount]) {
                            groupOpen = false
                            groupMemberCount = 0
                        } else {
                            break
                        }
                    } else {
                        if (groupCount < results.size - 1) {
                            val newInput = if (index + 1 < input.size) {
                                input.copyOfRange(index + 1, input.size)
                            } else {
                                CharArray(0)
                            }
                            val newResults = if (groupCount >= 0) {
                                results.subList(groupCount + 1, results.size)
                            } else {
                                results
                            }
                            invocationsToDo.add(Invocation(String(newInput), newResults, 0, true, groupMemberCount + 1))
                        }
                    }
                }

                '.' -> {
                    if (groupOpen) {
                        if (groupMemberCount != results[groupCount]) {
                            break
                        }
                        groupOpen = false
                        groupMemberCount = 0
                    }
                }
            }
            index++
        }

        if (!(groupOpen && groupCount < results.size && groupMemberCount != results[groupCount] || groupCount != results.size - 1)) {
            solutions++
        }
        solutions += invocationsToDo.sumOf { recursiveCallback(it) }
        return solutions
    }

    fun findPathCached(invocation: Invocation, cache: MutableMap<Invocation, Long>): Long {
        return if (!cache.contains(invocation)) {
            val result = findPath(invocation) { findPathCached(it, cache) }
            cache[invocation] = result
            result
        } else {
            cache[invocation]!!
        }
    }

    fun solveLine(input: String, results: List<Int>): Long {
        val cache = mutableMapOf<Invocation, Long>()
        val invocation = Invocation(input, results, -1, false, 0)
        val result = findPathCached(invocation, cache)
        return result
    }

    fun solve1(lines: List<String>): Long {
        return lines.sumOf { line ->
            val tokens = line.split(" ")
            val input = tokens[0].toCharArray()
            val results = tokens[1].split(",").map { it.toInt() }
            val solutions = solveLine(String(input), results)
            solutions
        }
    }

    fun solve2(lines: List<String>): Long {
        return lines.mapIndexed { i, line ->
            printProgress(i, lines)
            val tokens = line.split(" ")
            val input = "${tokens[0]}?${tokens[0]}?${tokens[0]}?${tokens[0]}?${tokens[0]}".toCharArray()
            val results =
                "${tokens[1]},${tokens[1]},${tokens[1]},${tokens[1]},${tokens[1]}".split(",").map { it.toInt() }
            val solutions = solveLine(String(input), results)
            solutions
        }.sum()
    }

    header(1)
    test(::solve1, testInput1, 6)
    test(::solve1, testInput2, 21)
    solve(::solve1)

    header(2)
    test(::solve2, testInput2, 525152)
    solve(::solve2)
}

data class Invocation(
    val input: String,
    val results: List<Int>,
    val groupCountInput: Int,
    val groupOpenInput: Boolean,
    val groupMemberCountInput: Int
)
