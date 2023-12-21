fun main() {
    val testInput = ".|...\\....\n" +
            "|.-.\\.....\n" +
            ".....|-...\n" +
            "........|.\n" +
            "..........\n" +
            ".........\\\n" +
            "..../.\\\\..\n" +
            ".-.-/..|..\n" +
            ".|....-|.\\\n" +
            "..//.|...."

    fun move(p: Point, d: Direction): Point {
        return when (d) {
            Direction.NORTH -> Point(p.x, p.y - 1)
            Direction.EAST -> Point(p.x + 1, p.y)
            Direction.SOUTH -> Point(p.x, p.y + 1)
            Direction.WEST -> Point(p.x - 1, p.y)
        }
    }

    fun turn(f: Char, d: Direction): List<Direction> {
        return when {
            f == '/' -> {
                when (d) {
                    Direction.NORTH -> listOf(Direction.EAST)
                    Direction.EAST -> listOf(Direction.NORTH)
                    Direction.SOUTH -> listOf(Direction.WEST)
                    Direction.WEST -> listOf(Direction.SOUTH)
                }
            }

            f == '\\' -> {
                when (d) {
                    Direction.NORTH -> listOf(Direction.WEST)
                    Direction.WEST -> listOf(Direction.NORTH)
                    Direction.SOUTH -> listOf(Direction.EAST)
                    Direction.EAST -> listOf(Direction.SOUTH)
                }
            }

            f == '-' && (d == Direction.SOUTH || d == Direction.NORTH) -> {
                listOf(Direction.EAST, Direction.WEST)
            }

            f == '|' && (d == Direction.EAST || d == Direction.WEST) -> {
                listOf(Direction.SOUTH, Direction.NORTH)
            }

            else -> listOf(d)
        }
    }

    fun solve(lines: List<String>, start: Pair<Direction, Point>): Long {
        val beamsHistory = mutableListOf<Pair<Direction, Point>>()

        val beamsToFollow = ArrayDeque<Pair<Direction, Point>>()
        beamsToFollow.add(start)

        while (beamsToFollow.isNotEmpty()) {
            val beam = beamsToFollow.removeLast()
            var loc = beam.second
            var direction = beam.first
            while (loc.x >= 0 && loc.x < lines[0].length && loc.y >= 0 && loc.y < lines.size) {
                if (beamsHistory.contains(direction to loc)) {
                    break
                } else {
                    beamsHistory.add(direction to loc)
                }
                val directions = turn(lines[loc.y][loc.x], direction)
                if (directions.size == 1) {
                    direction = directions.first()
                    loc = move(loc, direction)
                } else {
                    beamsToFollow.addAll(directions.map { it to loc })
                    break
                }
            }
        }
        return beamsHistory.map { it.second }.toSet().count().toLong()
    }

    fun solve1(lines: List<String>): Long {
        return solve(lines, Direction.EAST to Point(0, 0))
    }

    fun solve2(lines: List<String>): Long {
        return listOf(
            lines[0].indices.maxOf { solve(lines, Direction.SOUTH to Point(it, 0)) },
            lines[0].indices.maxOf { solve(lines, Direction.NORTH to Point(it, lines.size - 1)) },
            lines.indices.maxOf { solve(lines, Direction.EAST to Point(0, it)) },
            lines.indices.maxOf { solve(lines, Direction.WEST to Point(lines[0].length - 1, it)) }
        ).max()
    }

    header(1)
    test(::solve1, testInput, 46)
    solve(::solve1)

    header(2)
    test(::solve2, testInput, 51)
    solve(::solve2)
}
