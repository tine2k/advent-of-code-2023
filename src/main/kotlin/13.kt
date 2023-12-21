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

    fun printField(field: List<String>) {
        field.forEach { println(it) }
    }

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

    fun findSolution(field: List<String>): Long? =
        field.map { getReflections(it).toSet() }.reduce { a, b -> a.intersect(b) }.firstOrNull()

    fun solveField(field: List<String>): Long {
        val horizontalSolution = findSolution(field)
        val solution =horizontalSolution ?: ((findSolution(rotateField(field)) ?: 0) * 100)

        if (solution == 0L) {
            println("no solution found:")
            println("////")
            printField(field)
            println("////")
            printField(rotateField(field))
            println("////\n")
        }

        return solution
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

    fun solve1(lines: List<String>): Long {
        val fields = splitFields(lines)
        return fields.mapIndexed { i, field ->
            printProgress(i, fields)
            solveField(field)
        }.sum()
    }

    fun solve2(lines: List<String>): Long {
        return -1
    }

    header(1)
    test(::solve1, testInput, 405)
    solve(::solve1)

    header(2)
    test(::solve2, testInput, 1337)
    solve(::solve2)
}
