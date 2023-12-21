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

    fun solve1(lines: List<String>): Long {
        val platform = lines.map { it.toCharArray() }.toTypedArray()

        for (col in platform.indices) {
            for (row in 0..<platform[0].size) {
                if (platform[row][col] == '.') {
                    var nextStone = row
                    while(nextStone < platform[0].size - 1 && platform[nextStone][col] == '.') {
                        nextStone++
                    }
                    if (platform[nextStone][col] == 'O') {
                        platform[nextStone][col] = '.'
                        platform[row][col] = 'O'
                    }
                }
            }
        }

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

    fun solve2(lines: List<String>): Long {
        return -1
    }

    header(1)
    test(::solve1, testInput, 136)
    solve(::solve1)

    header(2)
    test(::solve2, testInput, 1337)
    solve(::solve2)
}
