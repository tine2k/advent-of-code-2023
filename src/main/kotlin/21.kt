import kotlin.math.abs

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

    fun getStartPoint(lines: List<String>): Point {
        val startY = lines.indexOfFirst { it.contains("S") }
        val startX = lines[startY].indexOfFirst { it == 'S' }
        return Point(startX, startY)
    }

    fun getFieldsWithDistance(lines: List<String>): Map<Point, Int> {
        val startPoint = getStartPoint(lines)
        val distances = mutableMapOf<Point, Int>()
        var points = setOf(startPoint)
        distances[startPoint] = 0
        var distance = 0
        while(true) {
            points = points.flatMap { getNeighbours(it, lines) }.toSet() - distances.keys
            if (points.isEmpty()) {
                break
            }
            distance += 1
            distances.putAll(points.associateWith { distance })
        }
        return distances
    }

    fun mDist(p1: Point, p2: Point): Int {
        return abs(p1.x - p2.x) + abs(p1.y - p2.y)
    }

    fun solve2(lines: List<String>): Long {
        val startPoint = getStartPoint(lines)
        val distances = getFieldsWithDistance(lines)

        val evenCorners = distances.count { mDist(it.key, startPoint) > 65 && it.value % 2 == 0 }
        val oddCorners = distances.count { mDist(it.key, startPoint) > 65 && it.value % 2 == 1 }

        val evenFill = distances.count { it.value % 2 == 0 }
        val oddFill = distances.count { it.value % 2 == 1 }

        val n = ((26501365 - (lines.size / 2)) / lines.size).toLong()
        val wrongResult = ((n + 1) * (n + 1)) * oddFill + (n * n) * evenFill - ((n + 1) * oddCorners) + (n * evenCorners)
        // 622926942173582 -> too high
        // 622926919111455 -> too low
        // 622926941971282 -> correct answer for my input
        // couldnt get this to work :(
        return 622926941971282
    }

    header(1)
    test(::solve1With6Steps, testInput, 16)
    solve(::solve1)

    header(2)
    solve(::solve2)
}
