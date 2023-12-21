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

    fun isSymbol(lines: List<String>, lineIndex: Int, charIndex: Int): Boolean {
        if (lineIndex < 0 || lineIndex >= lines.size) return false
        if (charIndex < 0 || charIndex >= lines[lineIndex].length) return false
        val char = lines[lineIndex][charIndex]
        return !char.isDigit() && char != '.'
    }

    fun isSymbolInRange(lines: List<String>, lineIndex: Int, indexStart: Int, indexEnd: Int): Boolean {
        return (indexStart..indexEnd).map { isSymbol(lines, lineIndex, it) }.any { it }
    }

    fun solve1(lines: List<String>): Long {
        val parts = mutableListOf<Long>()
        lines.forEachIndexed { lineIndex, line ->
            var partIndex = -1
            for (i in line.indices) {
                if (line[i].isDigit()) {
                    if (i == line.length - 1 || !line[i + 1].isDigit()) {
                        if (isSymbol(lines, lineIndex, partIndex) ||
                            isSymbol(lines, lineIndex, i + 1) ||
                            isSymbolInRange(lines, lineIndex - 1, partIndex, i + 1) ||
                            isSymbolInRange(lines, lineIndex + 1, partIndex, i + 1)
                        ) {
                            parts.add(line.substring(partIndex + 1, i + 1).toLong())
                        }
                        partIndex = i
                    }
                } else {
                    partIndex = i
                }
            }
        }
        return parts.sum()
    }

    data class Star(val x: Int, val y: Int)

    fun getStar(lines: List<String>, lineIndex: Int, charIndex: Int): List<Star> {
        if (lineIndex < 0 || lineIndex >= lines.size) return emptyList()
        if (charIndex < 0 || charIndex >= lines[lineIndex].length) return emptyList()
        val char = lines[lineIndex][charIndex]
        if (char == '*') {
            return listOf(Star(lineIndex, charIndex))
        }
        return emptyList()
    }

    fun getStars(lines: List<String>, lineIndex: Int, indexStart: Int, indexEnd: Int): List<Star> {
        return (indexStart..indexEnd).flatMap { getStar(lines, lineIndex, it) }
    }

    fun solve2(lines: List<String>): Long {
        val starsWithParts = mutableMapOf<Star, List<Long>>()
        lines.forEachIndexed { lineIndex, line ->
            var partIndex = -1
            for (i in line.indices) {
                if (line[i].isDigit()) {
                    if (i == line.length - 1 || !line[i + 1].isDigit()) {
                        val stars = getStar(lines, lineIndex, partIndex) +
                                getStar(lines, lineIndex, i + 1) +
                                getStars(lines, lineIndex - 1, partIndex, i + 1) +
                                getStars(lines, lineIndex + 1, partIndex, i + 1)
                        if (stars.isNotEmpty()) {
                            val part = line.substring(partIndex + 1, i + 1).toLong()
                            stars.forEach {
                                starsWithParts[it] = (starsWithParts[it] ?: emptyList()) + listOf(part)
                            }
                        }
                        partIndex = i
                    }
                } else {
                    partIndex = i
                }
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
