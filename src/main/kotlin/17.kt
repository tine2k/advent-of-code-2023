import Direction.*

fun main() {
    val testInput = "2413432311323\n" +
            "3215453535623\n" +
            "3255245654254\n" +
            "3446585845452\n" +
            "4546657867536\n" +
            "1438598798454\n" +
            "4457876987766\n" +
            "3637877979653\n" +
            "4654967986887\n" +
            "4564679986453\n" +
            "1224686865563\n" +
            "2546548887735\n" +
            "4322674655533"

    val testInput2 = "111111111111\n" +
            "999999999991\n" +
            "999999999991\n" +
            "999999999991\n" +
            "999999999991"

    fun getPathInDirection(lastDir: Direction, pathInSameDirection: Int, intendedDir: Direction): Int {
        return if (lastDir == intendedDir) {
            pathInSameDirection + 1
        } else {
            1
        }
    }

    fun findPath(
        lines: List<String>,
        p: Point,
        prevHeat: Long,
        direction: Direction,
        pathInSameDirection: Int,
        solutions: MutableList<Long>,
        cache: MutableMap<PointDirection, Long>,
        maxPath: Int,
        minPath: Int,
        steps: List<Point>
    ) {
        val heat = lines[p.y][p.x].digitToInt()
        val curHeat = prevHeat + heat

        val pointDirection = PointDirection(p, direction, pathInSameDirection)
        if (cache.getOrDefault(pointDirection, Long.MAX_VALUE) <= curHeat) {
            return
        }

        if (solutions.isNotEmpty() && curHeat >= solutions.min()) {
            return
        }
        cache[pointDirection] = curHeat

        if (p.y == lines.size - 1 && p.x == lines[0].length - 1 && pathInSameDirection >= minPath) {
            solutions.add(curHeat)
            return
        }
        val newSteps = steps + listOf(p)

        if (p.x < lines[0].length - 1 && !(direction == EAST && pathInSameDirection >= maxPath) && (direction == EAST || pathInSameDirection >= minPath) && direction != WEST) {
            val nextPath = getPathInDirection(direction, pathInSameDirection, EAST)
            findPath(lines, Point(p.x + 1, p.y), curHeat, EAST, nextPath, solutions, cache, maxPath, minPath, newSteps)
        }
        if (p.y < lines.size - 1 && !(direction == SOUTH && pathInSameDirection >= maxPath) && (direction == SOUTH || pathInSameDirection >= minPath) && direction != NORTH) {
            val nextPath = getPathInDirection(direction, pathInSameDirection, SOUTH)
            findPath(lines, Point(p.x, p.y + 1), curHeat, SOUTH, nextPath, solutions, cache, maxPath, minPath, newSteps)
        }
        if (p.y > 0 && !(direction == NORTH && pathInSameDirection >= maxPath) && (direction == NORTH || pathInSameDirection >= minPath) && direction != SOUTH) {
            val nextPath = getPathInDirection(direction, pathInSameDirection, NORTH)
            findPath(lines, Point(p.x, p.y - 1), curHeat, NORTH, nextPath, solutions, cache, maxPath, minPath, newSteps)
        }
        if (p.x > 0 && !(direction == WEST && pathInSameDirection >= maxPath) && (direction == WEST || pathInSameDirection >= minPath) && direction != EAST) {
            val nextPath = getPathInDirection(direction, pathInSameDirection, WEST)
            findPath(lines, Point(p.x - 1, p.y), curHeat, WEST, nextPath, solutions, cache, maxPath, minPath, newSteps)
        }
    }

    fun solve(lines: List<String>, maxPath: Int, minPath: Int): Long {
        val steps = mutableListOf<Point>()
        val solutions = mutableListOf<Long>()
        val cache = mutableMapOf<PointDirection, Long>()
        val firstHeat = lines[0][0].digitToInt().toLong()
        findPath(lines, Point(0, 0), -firstHeat, EAST, 0, solutions, cache, maxPath, minPath, steps)
        findPath(lines, Point(0, 0), -firstHeat, SOUTH, 0, solutions, cache, maxPath, minPath, steps)
        return solutions.min()
    }

    fun solve1(lines: List<String>): Long {
        return solve(lines, 3, 0)
    }

    fun solve2(lines: List<String>): Long {
        return solve(lines, 10, 4)
    }

    header(1)
    test(::solve1, testInput, 102)
    solve(::solve1)

    header(2)
    test(::solve2, testInput2, 71)
    test(::solve2, testInput, 94)
    solve(::solve2)
}

data class PointDirection(val p: Point, val direction: Direction, val pathInSameDirection: Int)
