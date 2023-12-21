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

    fun getPathInDirection(lastDir: Direction, pathInsameDirection: Int, intendedDir: Direction): Int {
        return if (lastDir == intendedDir) {
            pathInsameDirection + 1
        } else {
            1
        }
    }

    fun findPath(lines: List<String>, p: Point, curLength: Long, direction: Direction, pathInsameDirection: Int): Long {
        val values = mutableListOf<Long>()
        val heat = lines[p.y][p.x].digitToInt()
        if (p.x < lines[0].length - 1 && !(direction == Direction.EAST && pathInsameDirection >= 3)) {
            val nextPath = getPathInDirection(direction, pathInsameDirection, Direction.EAST)
            values.add(findPath(lines, Point(p.x + 1, p.y), curLength + heat, Direction.EAST, nextPath))
        }
        if (p.y < lines.size - 1 && !(direction == Direction.SOUTH && pathInsameDirection >= 3) && direction != Direction.NORTH) {
            val nextPath = getPathInDirection(direction, pathInsameDirection, Direction.SOUTH)
            values.add(findPath(lines, Point(p.x, p.y + 1), curLength + heat, Direction.SOUTH, nextPath))
        }
        if (p.y > 0 && !(direction == Direction.NORTH && pathInsameDirection >= 3) && direction != Direction.SOUTH) {
            val nextPath = getPathInDirection(direction, pathInsameDirection, Direction.NORTH )
            values.add(findPath(lines, Point(p.x, p.y - 1), curLength + heat, Direction.NORTH, nextPath))
        }

        return if (values.isEmpty()) {
            curLength
        } else {
            curLength + values.min()
        }
    }

    fun solve1(lines: List<String>): Long {
        return findPath(lines, Point(0, 0), 0, Direction.EAST, 0)
    }

    fun solve2(lines: List<String>): Long {
        return -1
    }

    header(1)
    test(::solve1, testInput, 102)
    solve(::solve1)

    header(2)
    test(::solve2, testInput, 1337)
    solve(::solve2)
}
