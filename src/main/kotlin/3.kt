import java.util.function.BiConsumer

fun main() {
    val testInput =
        "467..114..\n" +
                "...*......\n" +
                "..35..633.\n" +
                "......#...\n" +
                "617*......\n" +
                ".....+.58.\n" +
                "..592.....\n" +
                "......755.\n" +
                "...\$.*....\n" +
                ".664.598.."

    data class Symbol(val char: Char, val x: Int, val y: Int)

    fun getChar(lines: List<String>, lineIndex: Int, charIndex: Int): List<Symbol> {
        if (lineIndex < 0 || lineIndex >= lines.size) return emptyList()
        if (charIndex < 0 || charIndex >= lines[lineIndex].length) return emptyList()
        return listOf(Symbol(lines[lineIndex][charIndex], lineIndex, charIndex))
    }

    fun getChars(lines: List<String>, lineIndex: Int, indexStart: Int, indexEnd: Int): List<Symbol> {
        return (indexStart..indexEnd).flatMap { getChar(lines, lineIndex, it) }
    }

    fun solveParts(lines: List<String>, consumer: BiConsumer<Long, List<Symbol>>) {
        lines.forEachIndexed { lineIndex, line ->
            var partIndex = -1
            for (i in line.indices) {
                if (line[i].isDigit()) {
                    if (i == line.length - 1 || !line[i + 1].isDigit()) {
                        val symbols = getChar(lines, lineIndex, partIndex) +
                                getChar(lines, lineIndex, i + 1) +
                                getChars(lines, lineIndex - 1, partIndex, i + 1) +
                                getChars(lines, lineIndex + 1, partIndex, i + 1)
                        consumer.accept(line.substring(partIndex + 1, i + 1).toLong(), symbols)
                        partIndex = i
                    }
                } else {
                    partIndex = i
                }
            }
        }
    }

    fun solve1(lines: List<String>): Long {
        val parts = mutableListOf<Long>()
        solveParts(lines) { part, symbols ->
            if (symbols.map { it.char }.any { !it.isDigit() && it != '.' }) {
                parts.add(part)
            }
        }
        return parts.sum()
    }

    fun solve2(lines: List<String>): Long {
        val starsWithParts = mutableMapOf<Symbol, List<Long>>()
        solveParts(lines) { part, symbols ->
            symbols.filter { it.char == '*' }.forEach {
                starsWithParts[it] = (starsWithParts[it] ?: emptyList()) + listOf(part)
            }
        }

        return starsWithParts.filter { it.value.size == 2 }
            .map { it.value[0] * it.value[1] }
            .sum()
    }

    header(1)
    test(::solve1, testInput, 4361)
    solve(::solve1)

    header(2)
    test(::solve2, testInput, 467835)
    solve(::solve2)
}
