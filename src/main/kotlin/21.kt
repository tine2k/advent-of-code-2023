fun main() {
    val testInput = "...........\n" +
            ".....###.#.\n" +
            ".###.##..#.\n" +
            "..#.#...#..\n" +
            "....#.#....\n" +
            ".##..S####.\n" +
            ".##..#...#.\n" +
            ".......##..\n" +
            ".##.#.####.\n" +
            ".##..##.##.\n" +
            "...........\n"

    fun getPoint(lines: List<String>, x: Int, y: Int): Char? {
        return if (y >= 0 && y < lines.size && x >= 0 && x < lines[0].length) {
            lines[y][x]
        } else {
            null
        }
    }

    fun getNeighbours(current: Point, lines: List<String>): List<Point> {
        return listOf(
            Point(current.x + 1, current.y),
            Point(current.x - 1, current.y),
            Point(current.x, current.y + 1),
            Point(current.x, current.y - 1),
        ).filter {
            val point = getPoint(lines, it.x, it.y)
            (point == '.' || point == 'S')
        }
    }

    fun printField(lines: List<String>, points: Collection<Point>) {
        for (y in lines.indices) {
            for (x in 0..<lines[0].length) {
                if (points.contains(Point(x, y))) {
                    print("O")
                } else {
                    print(lines[y][x])
                }
            }
            println("")
        }
    }

    fun solve1(lines: List<String>, stepCount: Int): Long {
        val startY = lines.indexOfFirst { it.contains("S") }
        val startX = lines[startY].indexOfFirst { it == 'S' }
        var points = setOf(Point(startX, startY))
        for (i in 0..<stepCount) {
            points = points.flatMap { getNeighbours(it, lines) }.toSet()
        }
        return points.size.toLong()
    }

    fun solve1With6Steps(lines: List<String>): Long {
        return solve1(lines, 6)
    }

    fun solve1(lines: List<String>): Long {
        return solve1(lines, 64)
    }

    fun solve2(lines: List<String>): Long {
        return -1
    }

    header(1)
    test(::solve1With6Steps, testInput, 16)
    solve(::solve1)

    header(2)
    test(::solve2, testInput, 1337)
    solve(::solve2)
}
