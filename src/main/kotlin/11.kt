data class Galaxy(val x: Int, val y: Int)

fun main() {
    val testInput = "...#......\n" +
            ".......#..\n" +
            "#.........\n" +
            "..........\n" +
            "......#...\n" +
            ".#........\n" +
            ".........#\n" +
            "..........\n" +
            ".......#..\n" +
            "#...#....."

    fun getRange(v1: Int, v2: Int): IntRange {
        return if (v1 < v2) {
            v1..<v2
        } else {
            v2..<v1
        }
    }

    fun calculateDistance(range: IntRange, doubleGaps: List<Int>, multiplier: Long): Long {
        return range.sumOf { v ->
            if (doubleGaps.contains(v)) {
                multiplier
            } else {
                1L
            }
        }
    }

    fun calculateDistance(pair: Pair<Galaxy, Galaxy>, doubleRows: List<Int>, doubleCols: List<Int>, m: Long): Long {
        return calculateDistance(getRange(pair.first.x, pair.second.x), doubleCols, m) +
                calculateDistance(getRange(pair.first.y, pair.second.y), doubleRows, m)
    }

    fun solve(lines: List<String>, multiplier: Long): Long {
        val universe = lines.map { it.toCharArray() }
        val doubleRows = universe.mapIndexed { index, chars -> index to chars }
            .filter { it.second.all { c -> c == '.' } }
            .map { it.first }
        val doubleCols = (0..<universe[0].size)
            .filter { i -> universe.map { it[i] }.count { it == '.' } == universe[0].size }
        val galaxies = universe.flatMapIndexed { i, y ->
            universe[i].mapIndexed { j, _ -> Galaxy(j, i) }.filter { y[it.x] == '#' }
        }

        return galaxies.flatMapIndexed { i, g1 -> galaxies.filterIndexed { j, _ -> j > i }.map { g2 -> g1 to g2 } }
            .sumOf { calculateDistance(it, doubleRows, doubleCols, multiplier) }
    }

    fun solve1(lines: List<String>): Long {
        return solve(lines, 2)
    }

    fun solve2Test1(lines: List<String>): Long {
        return solve(lines, 10)
    }

    fun solve2Test2(lines: List<String>): Long {
        return solve(lines, 100)
    }

    fun solve2(lines: List<String>): Long {
        return solve(lines, 1_000_000)
    }

    header(1)
    test(::solve1, testInput, 374)
    solve(::solve1)

    header(2)
    test(::solve2Test1, testInput, 1030)
    test(::solve2Test2, testInput, 8410)
    solve(::solve2)
}
