import kotlin.math.pow

fun main() {
    val testInput = "Card 1: 41 48 83 86 17 | 83 86  6 31 17  9 48 53\n" +
            "Card 2: 13 32 20 16 61 | 61 30 68 82 17 32 24 19\n" +
            "Card 3:  1 21 53 59 44 | 69 82 63 72 16 21 14  1\n" +
            "Card 4: 41 92 73 84 69 | 59 84 76 51 58  5 54 83\n" +
            "Card 5: 87 83 26 28 32 | 88 30 70 12 93 22 82 36\n" +
            "Card 6: 31 18 13 56 72 | 74 77 10 23 35 67 36 11\n"

    fun convertToLongArray(input: String): List<Long> {
        return input.trim()
            .split(" ")
            .filter { it.isNotEmpty() }
            .map { it.trim().toLong() }
    }

    fun solve(lines: List<String>): List<Long> {
        return lines.map { line ->
            val lineTokens = line.split("|")
            val winningNumbers = convertToLongArray(lineTokens[0].split(":")[1])
            val myNumbers = convertToLongArray(lineTokens[1])
            myNumbers.intersect(winningNumbers.toSet()).size.toLong()
        }
    }

    fun solve1(lines: List<String>): Long {
        return solve(lines).sumOf { 2F.pow(it.toInt() - 1).toLong() }
    }

    fun solve2(lines: List<String>): Long {
        val wins = solve(lines)
        val buckets = mutableMapOf<Long, Long>()
        wins.forEachIndexed { index, _ -> buckets[index.toLong()] = 1 }
        wins.forEachIndexed { index, win ->
            (0..<buckets[index.toLong()]!!).forEach { _ ->
                (0..<win).forEach {
                    buckets[it + index + 1] = (buckets[it + index + 1] ?: 0) + 1
                }
            }
        }
        return buckets.values.sum()
    }

    header(1)
    test(::solve1, testInput, 13)
    solve(::solve1)

    header(2)
    test(::solve2, testInput, 30)
    solve(::solve2)
}
