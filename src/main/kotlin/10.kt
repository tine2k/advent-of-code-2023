enum class Direction {
    NORTH, SOUTH, WEST, EAST
}

fun main() {
    val testInput1 = ".....\n" +
            ".S-7.\n" +
            ".|.|.\n" +
            ".L-J.\n" +
            "....."

    val testInput2 = "..F7.\n" +
            ".FJ|.\n" +
            "SJ.L7\n" +
            "|F--J\n" +
            "LJ..."

    val testInput3 = "...........\n" +
            ".S-------7.\n" +
            ".|F-----7|.\n" +
            ".||.....||.\n" +
            ".||.....||.\n" +
            ".|L-7.F-J|.\n" +
            ".|..|.|..|.\n" +
            ".L--J.L--J.\n" +
            "..........."

    val testInput4 = "..........\n" +
            ".S------7.\n" +
            ".|F----7|.\n" +
            ".||....||.\n" +
            ".||....||.\n" +
            ".|L-7F-J|.\n" +
            ".|..||..|.\n" +
            ".L--JL--J.\n" +
            ".........."

    val testInput5 = ".F----7F7F7F7F-7....\n" +
            ".|F--7||||||||FJ....\n" +
            ".||.FJ||||||||L7....\n" +
            "FJL7L7LJLJ||LJ.L-7..\n" +
            "L--J.L7...LJS7F-7L7.\n" +
            "....F-J..F7FJ|L7L7L7\n" +
            "....L7.F7||L7|.L7L7|\n" +
            ".....|FJLJ|FJ|F7|.LJ\n" +
            "....FJL-7.||.||||...\n" +
            "....L---J.LJ.LJLJ..."

    val testInput6 = "FF7FSF7F7F7F7F7F---7\n" +
            "L|LJ||||||||||||F--J\n" +
            "FL-7LJLJ||||||LJL-77\n" +
            "F--JF--7||LJLJ7F7FJ-\n" +
            "L---JF-JLJ.||-FJLJJ7\n" +
            "|F|F-JF---7F7-L7L|7|\n" +
            "|FFJF7L7F-JF7|JL---7\n" +
            "7-L-JL7||F7|L7F-7F7|\n" +
            "L.L7LFJ|||||FJL7||LJ\n" +
            "L7JLJL-JLJLJL--JLJ.L"

    fun findNextDirection(field: Char, prevDirection: Direction): Direction {
        when (field) {
            'S' -> return prevDirection
            '-' -> {
                return if (prevDirection == Direction.EAST) {
                    Direction.EAST
                } else {
                    Direction.WEST
                }
            }

            '|' -> {
                return if (prevDirection == Direction.SOUTH) {
                    Direction.SOUTH
                } else {
                    Direction.NORTH
                }
            }

            '7' -> {
                return if (prevDirection == Direction.NORTH) {
                    Direction.WEST
                } else {
                    Direction.SOUTH
                }
            }

            'F' -> {
                return if (prevDirection == Direction.NORTH) {
                    Direction.EAST
                } else {
                    Direction.SOUTH
                }
            }

            'J' -> {
                return if (prevDirection == Direction.SOUTH) {
                    Direction.WEST
                } else {
                    Direction.NORTH
                }
            }

            'L' -> {
                return if (prevDirection == Direction.SOUTH) {
                    Direction.EAST
                } else {
                    Direction.NORTH
                }
            }

            else -> error("unknown state")
        }
    }

    fun getField(laby: List<CharArray>, x: Int, y: Int): Char {
        if (y >= laby.size || x >= laby.first().size) {
            return 'X'
        }
        return laby[y][x]
    }

    fun findFirstDirection(laby: List<CharArray>, x: Int, y: Int): Direction {
        return when {
            listOf('J', '7', '-').contains(getField(laby, x + 1, y)) -> {
                Direction.EAST
            }

            listOf('F', 'L', '-').contains(getField(laby, x - 1, y)) -> {
                Direction.WEST
            }

            listOf('L', 'J', '|').contains(getField(laby, x, y + 1)) -> {
                Direction.SOUTH
            }

            else -> {
                Direction.NORTH
            }
        }
    }

    fun iterateLoop(
        laby: List<CharArray>,
        startX: Int,
        startY: Int,
        startDirection: Direction,
        callback: ((Direction, Int, Int) -> Unit)? = null
    ): List<Pair<Int, Int>> {
        var x = startX
        var y = startY
        var currentDirection = startDirection

        val mainLoop = mutableListOf<Pair<Int, Int>>()
        do {
            val newDirection = findNextDirection(getField(laby, x, y), currentDirection)
            if (callback != null) {
                callback(newDirection, x, y)
            }
            when (newDirection) {
                Direction.EAST -> x++
                Direction.WEST -> x--
                Direction.NORTH -> y--
                Direction.SOUTH -> y++
            }
            currentDirection = newDirection
            mainLoop.add(x to y)
        } while (getField(laby, x, y) != 'S')
        return mainLoop
    }

    fun findStartPosition(laby: List<CharArray>): Pair<Int, Int> {
        val startPosition = laby.map { l -> l.indexOf('S') }
        val x = startPosition.first { it != -1 }
        val y = startPosition.indexOfFirst { it != -1 }
        return Pair(x, y)
    }

    fun solve1(lines: List<String>): Long {
        val laby = lines.map { it.toCharArray() }
        val (x, y) = findStartPosition(laby)
        return iterateLoop(laby, x, y, findFirstDirection(laby, x, y)).size.toLong() / 2
    }

    fun isNextToOutside(coordinates: Pair<Int, Int>, outside: Set<Pair<Int, Int>>): Boolean {
        return outside.contains(coordinates.first + 1 to coordinates.second) ||
                outside.contains(coordinates.first - 1 to coordinates.second) ||
                outside.contains(coordinates.first to coordinates.second + 1) ||
                outside.contains(coordinates.first to coordinates.second - 1)
    }

    fun isNotInLoop(x: Int, y: Int, mainLoop: List<Pair<Int, Int>>): Pair<Int, Int>? {
        return if ((x >= 0 && y >= 0) && !mainLoop.contains(x to y)) {
             x to y
        } else {
            null
        }
    }

    fun solve2(lines: List<String>): Long {
        val laby = lines.map { it.toCharArray() }
        val (startX, startY) = findStartPosition(laby)
        val mainLoop = iterateLoop(laby, startX, startY, findFirstDirection(laby, startX, startY))

        val outside = mutableSetOf<Pair<Int, Int>>()
        // start with top left part of main loop
        val tl = mainLoop.sortedWith(compareBy({ it.second }, { it.first })).first()
        // clockwise
        iterateLoop(laby, tl.first + 1, tl.second, Direction.EAST) { direction: Direction, x: Int, y: Int ->
            when (direction) {
                Direction.EAST -> isNotInLoop(x, y - 1, mainLoop)?.let { outside.add(it) }
                Direction.WEST -> isNotInLoop(x, y + 1, mainLoop)?.let { outside.add(it) }
                Direction.NORTH -> isNotInLoop(x - 1, y, mainLoop)?.let { outside.add(it) }
                Direction.SOUTH -> isNotInLoop(x + 1, y, mainLoop)?.let { outside.add(it) }
            }
        }
        // counter clockwise
        iterateLoop(laby, tl.first, tl.second + 1, Direction.SOUTH) { direction: Direction, x: Int, y: Int ->
            when (direction) {
                Direction.EAST -> isNotInLoop(x, y + 1, mainLoop)?.let { outside.add(it) }
                Direction.WEST -> isNotInLoop(x, y - 1, mainLoop)?.let { outside.add(it) }
                Direction.NORTH -> isNotInLoop(x + 1, y, mainLoop)?.let { outside.add(it) }
                Direction.SOUTH -> isNotInLoop(x - 1, y, mainLoop)?.let { outside.add(it) }
            }
        }

        val potentialNests = (laby.flatMapIndexed { ly, v -> v.mapIndexed { lx, _ -> lx to ly } }
                - mainLoop.toSet() - outside).toMutableSet()

        // remove neighbours
        do {
            val newOutside = potentialNests.filter { isNextToOutside(it, outside) }.toSet()
            potentialNests.removeAll(newOutside)
            outside += newOutside
        } while (newOutside.isNotEmpty())

        return potentialNests.size.toLong()
    }

    header(1)
    test(::solve1, testInput1, 4)
    test(::solve1, testInput2, 8)
    solve(::solve1)

    header(2)
    test(::solve2, testInput3, 4)
    test(::solve2, testInput4, 4)
    test(::solve2, testInput5, 8)
    test(::solve2, testInput6, 10)
    solve(::solve2)
}
