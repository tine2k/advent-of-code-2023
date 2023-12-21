import kotlin.math.max

fun main() {
    val testInput = "#.##..##.\n" +
            "..#.##.#.\n" +
            "##......#\n" +
            "##......#\n" +
            "..#.##.#.\n" +
            "..##..##.\n" +
            "#.#.##.#.\n" +
            "\n" +
            "#...##..#\n" +
            "#....#..#\n" +
            "..##..###\n" +
            "#####.##.\n" +
            "#####.##.\n" +
            "..##..###\n" +
            "#....#..#"

    fun isReflection(input: String, column: Int): Boolean {
        var i = 0
        do {
            if (column - i - 1 < 0 || column + i >= input.length) {
                return true
            }
            if (input[column - i - 1] != input[column + i]) {
                return false
            }
            i++
        } while (true)
    }

    fun rotateField(field: List<String>): List<String> {
        return List(field[0].length) { i -> String(List(field.size) { j -> (field[j][field[0].length - i - 1]) }.toCharArray()) }
    }

    fun getReflections(input: String): List<Long> {
        return (1L..<input.length).filter { isReflection(input, it.toInt()) }
    }

    fun findSolution(field: List<String>): Set<Long> =
        field.map { getReflections(it).toSet() }.reduce { a, b -> a.intersect(b) }

    fun solveField(field: List<String>): Pair<Set<Long>, Set<Long>> {
        return (findSolution(field)) to (findSolution(rotateField(field)).map { it * 100 }).toSet()
    }

    fun splitFields(lines: List<String>): MutableList<MutableList<String>> {
        return lines.foldIndexed(mutableListOf(mutableListOf())) { _, acc, item ->
            if (item.isEmpty()) {
                acc.add(mutableListOf())
            } else {
                acc.last().add(item)
            }
            acc
        }
    }

    fun flip(input: Char): Char {
        return if (input == '.') {
            '#'
        } else {
            '.'
        }
    }

    fun solveWithSmudge(field: List<String>, oldSolutions: Pair<Set<Long>, Set<Long>>): Pair<Set<Long>, Set<Long>> {
        field.forEachIndexed { i, line ->
            line.forEachIndexed { j, _ ->
                val newField = field.toMutableList()
                val newChar = flip(newField[i][j])
                newField[i] = newField[i].substring(0, j) + newChar + newField[i].substring(j + 1)

                val newSolution = solveField(newField)
                val veryNewSolution =
                    (newSolution.first - oldSolutions.first) to (newSolution.second - oldSolutions.second)
                if ((veryNewSolution.first.any { it > 0 } || veryNewSolution.second.any { it > 0 })) {
                    return veryNewSolution
                }
            }
        }
        error("no solution found!")
    }

    fun solveField2(field: List<String>): Long {
        val newSolution = solveWithSmudge(field, solveField(field))
        return max(newSolution.first.firstOrNull() ?: 0L, newSolution.second.firstOrNull() ?: 0L)
    }

    fun solve1(lines: List<String>): Long {
        val fields = splitFields(lines)
        return fields.mapIndexed { i, field ->
            printProgress(i, fields)
            val solution = solveField(field)
            max(solution.first.firstOrNull() ?: 0L, solution.second.firstOrNull() ?: 0L)
        }.sum()
    }

    fun solve2(lines: List<String>): Long {
        val fields = splitFields(lines)
        return fields.mapIndexed { i, field ->
            printProgress(i, fields)
            solveField2(field)
        }.sum()
    }

    header(1)
    test(::solve1, testInput, 405)
    solve(::solve1)

    header(2)
    test(::solve2, testInput, 400)
    solve(::solve2)
}
