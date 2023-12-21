fun main() {
    val testInput = "0 3 6 9 12 15\n" +
            "1 3 6 10 15 21\n" +
            "10 13 16 21 30 45"

    fun findNextSequence(input: List<Long>): List<Long> {
        return input.mapIndexedNotNull { i, v ->
            if (i == input.size - 1) {
                null
            } else {
                input[i + 1] - v
            }
        }
    }

    fun solve(lines: List<String>, reduction: (acc: List<Long>, List<Long>) -> List<Long>): Long {
        val inputLines = lines.map { line -> line.split(" ").map { it.toLong() } }

        return inputLines.sumOf { input ->
            val sequences = mutableListOf(input)
            do {
                sequences.add(findNextSequence(sequences.last()))
            } while (sequences.last().any { it != 0L })
            sequences.reversed().reduce(reduction).first()
        }
    }

    fun solve1(lines: List<String>): Long {
        return solve(lines) { a, b -> listOf(a.last() + b.last()) }
    }

    fun solve2(lines: List<String>): Long {
        return solve(lines) { a, b -> listOf(b.first() - a.first()) }
    }

    header(1)
    test(::solve1, testInput, 114)
    solve(::solve1)

    header(2)
    test(::solve2, testInput, 2)
    solve(::solve2)
}
