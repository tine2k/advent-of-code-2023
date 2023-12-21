import java.awt.Desktop
import java.io.File
import java.io.FileWriter

fun main() {
    val testInput = "R 6 (#70c710)\n" +
            "D 5 (#0dc571)\n" +
            "L 2 (#5713f0)\n" +
            "D 2 (#d2c081)\n" +
            "R 2 (#59c680)\n" +
            "D 2 (#411b91)\n" +
            "L 5 (#8ceee2)\n" +
            "U 2 (#caa173)\n" +
            "L 1 (#1b58a2)\n" +
            "U 2 (#caa171)\n" +
            "R 2 (#7807d2)\n" +
            "U 3 (#a77fa3)\n" +
            "L 2 (#015232)\n" +
            "U 2 (#7a21e3)"

    fun printField(field: Set<Point>) {
        val f = File.createTempFile("abc", ".txt")
        val w = FileWriter(f)
        for (y in field.minOf { it.y }..field.maxOf { it.y }) {
            for (x in field.minOf { it.x }..field.maxOf { it.x }) {
                if (field.contains(Point(x, y))) {
                    w.write("#")
                } else {
                    w.write(" ")
                }
            }
            w.write("\n")
        }
        w.close()
        Desktop.getDesktop().open(f)
    }

    fun findEmptySquares(field: Set<Point>): MutableList<Point> {
        val empty = mutableListOf<Point>()
        for (y in field.minOf { it.y }..field.maxOf { it.y }) {
            for (x in field.minOf { it.x }..field.maxOf { it.x }) {
                if (!field.contains(Point(x, y))) {
                    empty.add(Point(x, y))
                }
            }
        }
        return empty
    }

    fun findNeighbours(p: Point): List<Point> {
        return listOf(
            Point(p.x - 1, p.y - 1),
            Point(p.x - 1, p.y),
            Point(p.x - 1, p.y + 1),
            Point(p.x, p.y - 1),
            Point(p.x, p.y + 1),
            Point(p.x + 1, p.y - 1),
            Point(p.x + 1, p.y),
            Point(p.x + 1, p.y + 1)
        )
    }

    fun calcFieldSize(field: MutableSet<Point>): Long {
        var result = field.size.toLong()
        for (x in field.minOf { it.x }..field.maxOf { it.x }) {
            var edgeCount = 0
            var inside = false
            for (y in field.minOf { it.y } - 1..field.maxOf { it.y }) {
                if (field.contains(Point(x, y))) {
                    edgeCount += 1
                } else {
                    if (edgeCount == 1) {
                        inside = !inside
                    }
                    edgeCount = 0
                }

                if (inside && !field.contains(Point(x, y))) {
                    result += 1
                }
            }
        }
        return result
    }

    fun solve1(lines: List<String>): Long {
        var p = Point(0, 0)
        val field = mutableSetOf<Point>()
        lines.forEach { line ->
            val token = line.split(" ")
            for (i in 0..<token[1].toInt()) {
                when (token[0]) {
                    "R" -> p = Point(p.x + 1, p.y)
                    "L" -> p = Point(p.x - 1, p.y)
                    "U" -> p = Point(p.x, p.y - 1)
                    "D" -> p = Point(p.x, p.y + 1)
                }
                field.add(p)
            }
        }
//        printField(field)

        return calcFieldSize(field)

//        val emptySquares = findEmptySquares(field).toMutableSet()
//        val xRange = field.minOf { it.x }..field.maxOf { it.x }
//        val yRange = field.minOf { it.y }..field.maxOf { it.y }
//        while (emptySquares.isNotEmpty()) {
//            val newField = mutableSetOf<Point>()
//            val valuesToCheck = mutableSetOf<Point>()
//            valuesToCheck.add(emptySquares.first())
//            while(valuesToCheck.isNotEmpty()) {
//                val currentField = valuesToCheck.first()
//                val emptyNeighbours = findNeighbours(currentField).intersect(emptySquares)
//                newField.addAll(emptyNeighbours)
//                valuesToCheck.addAll(emptyNeighbours)
//                valuesToCheck.remove(currentField)
//                emptySquares.removeAll(newField)
//            }
//            println("left to check: ${emptySquares.size}")
//            if (newField.none { it.x == xRange.first || it.x == xRange.last || it.y == yRange.first || it.y == yRange.last }) {
//                field.addAll(newField)
//            }
//        }
//
//        return field.size.toLong()
    }

    fun solve2(lines: List<String>): Long {
        var p = Point(0, 0)
        val field = mutableSetOf<Point>()
        lines.forEach { line ->
            val token = line.split(" ")
            val distance = token[2].substring(2, 7).toLong(radix = 16)
            val direction = token[2][7]
            for (i in 0..<distance) {
                when (direction) {
                    '0' -> p = Point(p.x + 1, p.y)
                    '2' -> p = Point(p.x - 1, p.y)
                    '3' -> p = Point(p.x, p.y - 1)
                    '1' -> p = Point(p.x, p.y + 1)
                }
                field.add(p)
            }
        }
//        printField(field)

        return calcFieldSize(field)
    }

    header(1)
    test(::solve1, testInput, 62)
    solve(::solve1)

    header(2)
    test(::solve2, testInput, 952408144115)
    solve(::solve2)
}
