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
        return if (location.x < 0 || location.y < 1 || location.x >= lines[0].length || location.y >= lines.size) {
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

    fun fillConnections(
        lines: List<String>,
        connections: MutableSet<Connection>,
        prevLocation: Point,
        location: Point,
        directed: Boolean
    ): Point? {
        var curLoc = location
        val currentPath = mutableSetOf(location)
        var end: Point? = null
        while (true) {
            val nextDirections = getNextDirections(lines, curLoc, directed) - currentPath - prevLocation
            if (nextDirections.isEmpty()) {
                break
            } else if (nextDirections.size == 1) {
                curLoc = nextDirections[0]
                currentPath.add(curLoc)
                if (curLoc.y == lines.size - 1) {
                    connections.add(Connection(prevLocation, curLoc, currentPath.size))
                    end = curLoc
                    break
                }
            } else {
                val existingConnection = connections.filter { it.isConnectedTo(curLoc, directed) }
                if (existingConnection.any { it.isConnectedTo(prevLocation, directed) && it.weight != currentPath.size }) {
                    error("Invalid state")
                }
                if (existingConnection.none { it.isConnectedTo(prevLocation, directed) }) {
                    connections.add(Connection(prevLocation, curLoc, currentPath.size))
                }

                if (existingConnection.isEmpty()) {
                    nextDirections.forEach {
                        fillConnections(lines, connections, curLoc, it, directed)?.let { p -> end = p }
                    }
                }
                break
            }
        }
        return end
    }

    fun printMap(lines: List<String>, points: Set<Point>) {
        for (y in 0..<lines.size) {
            for (x in 0..<lines[0].length) {
                if (points.contains(Point(x, y))) {
                    print(points.indexOf(Point(x, y)))
                } else {
                    print(lines[y][x])
                }
            }
            println("")
        }
        println("")
    }

    fun findPaths(
        lines: List<String>,
        connections: Set<Connection>,
        loc: Point,
        end: Point,
        path: Set<Point>,
        weight: Long,
        directed: Boolean
    ): List<Long> {
        if (loc == end) {
            return listOf(weight)
        }
        val solutions = mutableListOf<Long>()
        val nextPaths = connections.filter { it.isConnectedTo(loc, directed) }
        nextPaths.forEach {
            val connectedNode = it.getConnectionTo(loc)!!
            if (!path.contains(connectedNode.first)) {
                val newSolutions = findPaths(
                    lines,
                    connections,
                    connectedNode.first,
                    end,
                    path + loc,
                    weight + connectedNode.second,
                    directed
                )
                val bestSolution = solutions.maxOrNull() ?: 0
                solutions.addAll(newSolutions.filter { sol -> sol > bestSolution })
            }
        }
        return solutions
    }

    fun solve(lines: List<String>, directed: Boolean): Long {
        val start = Point(1, 1)
        val connections = mutableSetOf<Connection>()
        val end = fillConnections(lines, connections, start, start, directed)!!
        val paths = findPaths(lines, connections, start, end, mutableSetOf(start), 0, directed)
        return paths.max()
    }

    fun solve1(lines: List<String>): Long {
        return solve(lines, true)
    }

    fun solve2(lines: List<String>): Long {
        return solve(lines, false)
    }

    header(1)
    test(::solve1, testInput, 94)
    solve(::solve1)

    header(2)
    test(::solve2, testInput, 154)
    solve(::solve2)
}

data class Connection(val a: Point, val b: Point, val weight: Int) {

    fun isConnectedTo(p: Point, directed: Boolean): Boolean {
        return if (directed) {
            p == a
        } else {
            p == a || p == b
        }
    }

    fun getConnectionTo(p: Point): Pair<Point, Int>? {
        return when (p) {
            a -> b to weight
            b -> a to weight
            else -> null
        }
    }
}
