import kotlin.math.min

fun main() {
    val testInput = "1abc2\n" +
            "pqr3stu8vwx\n" +
            "a1b2c3d4e5f\n" +
            "treb7uchet"

    fun solve1(lines: List<String>): Long {
        return lines.sumOf { l ->
            val numbers = l.filter { it.isDigit() }
            "${numbers.first()}${numbers.last()}".toLong()
        }
    }

    val testInput2 = "two1nine\n" +
            "eightwothree\n" +
            "abcone2threexyz\n" +
            "xtwone3four\n" +
            "4nineeightseven2\n" +
            "zoneight234\n" +
            "7pqrstsixteen"

    val numberMap = listOf("one", "two", "three", "four", "five", "six", "seven", "eight", "nine")

    fun solve2(lines: List<String>): Long {
        return lines.sumOf { l ->
            val numbers = mutableListOf<Int>()
            l.forEachIndexed { index, c ->
                if (c.isDigit()) {
                    numbers.add(c.digitToInt())
                } else {
                    numberMap.forEachIndexed { indexWord, word ->
                        val token = l.substring(index, min(l.length, index + word.length))
                        if (token == word) {
                            numbers.add(indexWord + 1)
                        }
                    }
                }
            }
            "${numbers.first()}${numbers.last()}".toLong()
        }
    }

    header(1)
    test(::solve1, testInput, 142)
    solve(::solve1)

    header(2)
    test(::solve2, testInput2, 281)
    solve(::solve2)
}
