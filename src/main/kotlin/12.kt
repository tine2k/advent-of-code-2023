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

    fun findPath(
        input: CharArray,
        results: List<Int>,
        indexInput: Int,
        groupCountInput: Int,
        groupOpenInput: Boolean,
        groupMemberCountInput: Int,
        solutionOutput: CharArray
    ): Long {
        var index = indexInput
        var groupCount = groupCountInput
        var groupMemberCount = groupMemberCountInput
        var groupOpen = groupOpenInput
        var solutions = 0L
        while (index < input.size) {
            val missingGroups = results.size - (groupCount + 1)
            if (missingGroups * 2 > input.size - index + 1) {
                return solutions
            }

            when (input[index]) {
                '#' -> {
                    if (!groupOpen) {
                        groupCount++
                        groupOpen = true
                        if (groupCount > results.size - 1) {
                            return solutions
                        }
                    }
                    groupMemberCount++
                }

                '?' -> {
                    if (groupOpen) {
                        if (groupMemberCount < results[groupCount]) {
                            groupMemberCount++ // use #
                            solutionOutput[index] = '#'
                        } else if (groupMemberCount == results[groupCount]) {
                            groupOpen = false
                            groupMemberCount = 0
                            solutionOutput[index] = '.'
                        } else {
                            return solutions
                        }
                    } else {
                        if (groupCount < results.size - 1) {
                            val forkedSolution = solutionOutput.clone()
                            forkedSolution[index] = '#'
                            solutions += findPath(
                                input,
                                results,
                                index + 1,
                                groupCount + 1,
                                true,
                                groupMemberCount + 1,
                                forkedSolution
                            )
                        }

                        solutionOutput[index] = '.'
                    }
                }

                '.' -> {
                    if (groupOpen) {
                        if (groupMemberCount != results[groupCount]) {
                            return solutions
                        }
                        groupOpen = false
                        groupMemberCount = 0
                    }
                }
            }
            index++
        }
        if (groupOpen && groupMemberCount != results[groupCount] || groupCount != results.size - 1) {
            return solutions
        } else {
            solutions++
//            solutionsArray.add(solutionOutput)
//            if (findPattern(solutionOutput) != results) {
//                println("Solution found: ${String(solutionOutput)}")
//            }
//            if (solutions % 10_000 == 0L) {
//                println("solutions found: $solutions")
//            }
//            println("Solution found: ${String(solutionOutput)}")
        }
        return solutions
    }


    fun solveLine(input: CharArray, results: List<Int>): Long {
        val result = findPath(input, results, 0, -1, false, 0, input.clone())
        return result
    }

    fun solve1(lines: List<String>): Long {
        return lines.sumOf { line ->
            val tokens = line.split(" ")
            val input = tokens[0].toCharArray()
            val results = tokens[1].split(",").map { it.toInt() }
//            println("checking ${String(input)}")
            val solutions = solveLine(input, results)
            solutions
        }
    }

    fun solve2(lines: List<String>): Long {
        return lines.mapIndexed { i, line ->
            printProgress(i, lines)
            val tokens = line.split(" ")
            val input = "${tokens[0]}?${tokens[0]}?${tokens[0]}?${tokens[0]}?${tokens[0]}".toCharArray()
            val results = "${tokens[1]},${tokens[1]},${tokens[1]},${tokens[1]},${tokens[1]}".split(",").map { it.toInt() }
            val solutions = solveLine(input, results)
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
