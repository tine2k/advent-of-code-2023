fun main() {
    val testInput =
        "O....#....\n" +
                "O.OO#....#\n" +
                ".....##...\n" +
                "OO.#O....O\n" +
                ".O.....O#.\n" +
                "O.#..O.#.#\n" +
                "..O..#O..O\n" +
                ".......O..\n" +
                "#....###..\n" +
                "#OO..#...."

    val testResult1 = ".....#....\n" +
            "....#...O#\n" +
            "...OO##...\n" +
            ".OO#......\n" +
            ".....OOO#.\n" +
            ".O#...O#.#\n" +
            "....O#....\n" +
            "......OOOO\n" +
            "#...O###..\n" +
            "#..OO#...."

    val testResult2 = ".....#....\n" +
            "....#...O#\n" +
            ".....##...\n" +
            "..O#......\n" +
            ".....OOO#.\n" +
            ".O#...O#.#\n" +
            "....O#...O\n" +
            ".......OOO\n" +
            "#..OO###..\n" +
            "#.OOO#...O"

    val testResult3 = ".....#....\n" +
            "....#...O#\n" +
            ".....##...\n" +
            "..O#......\n" +
            ".....OOO#.\n" +
            ".O#...O#.#\n" +
            "....O#...O\n" +
            ".......OOO\n" +
            "#...O###.O\n" +
            "#.OOO#...O"

    fun tilt(platform: Array<CharArray>) {
        for (col in platform.indices) {
            for (row in 0..<platform[0].size) {
                if (platform[row][col] == '.') {
                    var nextStone = row
                    while (nextStone < platform[0].size - 1 && platform[nextStone][col] == '.') {
                        nextStone++
                    }
                    if (platform[nextStone][col] == 'O') {
                        platform[nextStone][col] = '.'
                        platform[row][col] = 'O'
                    }
                }
            }
        }
    }

    fun calcLoad(platform: Array<CharArray>): Long {
        val results = mutableListOf<Int>()
        for (col in platform.indices) {
            for (row in 0..<platform[0].size) {
                if (platform[row][col] == 'O') {
                    results.add(platform[0].size - row)
                }
            }
        }
        return results.sum().toLong()
    }

    fun convertToPlatform(lines: List<String>) = lines.map { it.toCharArray() }.toTypedArray()

    fun convertToPlatform(input: String) = convertToPlatform(input.split("\n"))

    fun solve1(lines: List<String>): Long {
        val platform = convertToPlatform(lines)
        tilt(platform)
        return calcLoad(platform)
    }

    fun transpose(array: Array<CharArray>): Array<CharArray> {
        val width = array[0].size
        val height = array.size
        val newArray = Array(width) { CharArray(height) }
        for (x in 0..<width) {
            for (y in 0..<height) {
                newArray[y][x] = array[height - x - 1][y]
            }
        }
        return newArray;
    }

    fun runCycle(platform: Array<CharArray>): Array<CharArray> {
        var newPlatform = platform
        for (i in 0..3) {
            tilt(newPlatform)
            newPlatform = transpose(newPlatform)
        }
        return newPlatform
    }

    fun isEqual(platform1: Array<CharArray>, platform2: Array<CharArray>): Boolean {
        platform1.forEachIndexed { i, v ->
            if (!v.contentEquals(platform2[i])) {
                return false
            }
        }
        return true
    }

    fun testCycle(lines: List<String>): Long {
        var platform = convertToPlatform(lines)
        val testResults = listOf(testResult1, testResult2, testResult3)
        testResults.forEachIndexed { i, result ->
            platform = runCycle(platform)
            if (!isEqual(platform, convertToPlatform(result))) {
                return -(i + 1).toLong()
            }
        }
        return 0
    }

    fun findLoop(results: List<Long>, targetCycle: Long): Long {
        val cycle = mutableListOf<Long>()
        if (results.size <= 2) return -1
        for (i in 0..< results.size / 2 - 1) {
            cycle.add(results[results.size - 1 -i])
            val fromIndex = results.size - (cycle.size * 2)
            if (cycle.size > 1 && results.subList(fromIndex, fromIndex + cycle.size).reversed() == cycle) {
                return cycle.reversed()[((targetCycle - (results.size - cycle.size - cycle.size)) % cycle.size).toInt() - 1]
            }
        }
        return -1
    }

    fun solve2WithCycleCount(lines: List<String>, cycleCount: Long): Long {
        var platform = convertToPlatform(lines)
        val results = mutableListOf<Long>()
        for (j in 0..cycleCount) {
            platform = runCycle(platform)
            results.add(calcLoad(platform))
            val cycle = findLoop(results, cycleCount)
            if (cycle != -1L) {
                return cycle
            }
        }
        return calcLoad(platform)
    }

    fun solve2(lines: List<String>): Long {
        return solve2WithCycleCount(lines, 1_000_000_000)
    }

    header(1)
    test(::solve1, testInput, 136)
    solve(::solve1)

    header(2)
    test(::testCycle, testInput, 0)
    test(::solve2, testInput, 64)
    solve(::solve2)
}
