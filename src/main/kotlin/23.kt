fun main() {
    val testInput = "#.#####################\n" +
            "#.......#########...###\n" +
            "#######.#########.#.###\n" +
            "###.....#.>.>.###.#.###\n" +
            "###v#####.#v#.###.#.###\n" +
            "###.>...#.#.#.....#...#\n" +
            "###v###.#.#.#########.#\n" +
            "###...#.#.#.......#...#\n" +
            "#####.#.#.#######.#.###\n" +
            "#.....#.#.#.......#...#\n" +
            "#.#####.#.#.#########v#\n" +
            "#.#...#...#...###...>.#\n" +
            "#.#.#v#######v###.###v#\n" +
            "#...#.>.#...>.>.#.###.#\n" +
            "#####v#.#.###v#.#.###.#\n" +
            "#.....#...#...#.#.#...#\n" +
            "#.#########.###.#.#.###\n" +
            "#...###...#...#...#.###\n" +
            "###.###.#.###v#####v###\n" +
            "#...#...#.#.>.>.#.>.###\n" +
            "#.###.###.#.###.#.#v###\n" +
            "#.....###...###...#...#\n" +
            "#####################.#"

    fun getLoc(lines: List<String>, location: Point): Char {
        return if (location.x < 0 || location.y < 0 || location.x >= lines[0].length || location.y >= lines.size) {
            '#'
        } else {
            lines[location.y][location.x]
        }
    }

    fun getValidNextStep(lines: List<String>, loc: Point, forbidden: Char): Point? {
        val step = getLoc(lines, loc)
        return if (step != '#' && step != forbidden) {
            loc
        } else {
            null
        }
    }

    fun getNextDirections(lines: List<String>, loc: Point, honorSlopes: Boolean): List<Point> {
        val forbiddenChars = if (honorSlopes) {
            listOf('<', '>', 'v', '^')
        } else {
            listOf('#', '#', '#', '#')
        }
        return listOfNotNull(
            getValidNextStep(lines, Point(loc.x + 1, loc.y), forbiddenChars[0]),
            getValidNextStep(lines, Point(loc.x - 1, loc.y), forbiddenChars[1]),
            getValidNextStep(lines, Point(loc.x, loc.y - 1), forbiddenChars[2]),
            getValidNextStep(lines, Point(loc.x, loc.y + 1), forbiddenChars[3])
        )
    }

    fun findPath(lines: List<String>, pastPath: Set<Point>, honorSlopes: Boolean): List<Set<Point>> {
        val currentPath = pastPath.toMutableSet()
        val allPaths = mutableListOf<Set<Point>>()
        while (true) {
            val location = currentPath.last()
            val nextDirections = getNextDirections(lines, location, honorSlopes) - currentPath
            if (nextDirections.isEmpty()) {
                if (location == Point(lines[0].length - 2, lines.size - 1)) {
                    allPaths.add(currentPath)
                }
                break
            } else if (nextDirections.size == 1) {
                currentPath.add(nextDirections[0])
            } else {
                allPaths.addAll(nextDirections.flatMap { findPath(lines, currentPath + listOf(it), honorSlopes) })
                break
            }
        }
        return allPaths
    }

    fun printMap(lines: List<String>, points: Set<Point> ) {
        for (y in 0..<lines.size) {
            for (x in 0..<lines[0].length) {
                if (points.contains(Point(x,y))) {
                    print("O")
                } else {
                    print(lines[y][x])
                }
            }
            println("")
        }
        println("")
    }

    fun solve1(lines: List<String>): Long {
        val allPaths = findPath(lines, setOf(Point(1, 1)), true)
        return allPaths.maxOf { it.size }.toLong()
    }

    fun solve2(lines: List<String>): Long {
        val allPaths = findPath(lines, setOf(Point(1, 1)), false)
        printMap(lines, allPaths.sortedByDescending { it.size }.first())
        return allPaths.maxOf { it.size }.toLong()
    }

    header(1)
    test(::solve1, testInput, 94)
    solve(::solve1)

    header(2)
    test(::solve2, testInput, 154)
    solve(::solve2)
}
