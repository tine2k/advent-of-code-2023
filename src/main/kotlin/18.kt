import kotlin.math.abs

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

    fun getDirections(lines: List<String>): List<Pair<Char, Long>> {
        return lines.map { line ->
            val token = line.split(" ")
            token[0][0] to token[1].toLong()
        }
    }

    fun getDirectionsFromColorCode(lines: List<String>): List<Pair<Char, Long>> {
        return lines.map { line ->
            val token = line.split(" ")
            val distance = token[2].substring(2, 7).toLong(radix = 16)
            val direction = token[2][7]
            direction to distance
        }
    }

    fun getField(directions: List<Pair<Char, Long>>): Set<LPoint> {
        var p = LPoint(0, 0)
        val field = mutableSetOf<LPoint>()
        directions.forEach { direction ->
            when (direction.first) {
                'R', '0' -> p = LPoint(p.x + direction.second, p.y)
                'L', '2' -> p = LPoint(p.x - direction.second, p.y)
                'U', '3' -> p = LPoint(p.x, p.y - direction.second)
                'D', '1' -> p = LPoint(p.x, p.y + direction.second)
            }
            field.add(p)
        }
        return field
    }

    fun solve(directions: List<Pair<Char, Long>>): Long {
        val field = getField(directions).reversed()
        val closedField = field + field.first()
        val border = closedField.zipWithNext { a, b -> abs(a.x - b.x) + abs(a.y - b.y) }.sum()
        val shoelace1 = closedField.zipWithNext { a, b -> a.x * b.y }.sum()
        val showlace2 = closedField.zipWithNext { a, b -> a.y * b.x }.sum()
        val inside = abs(shoelace1 - showlace2) / 2 //https://en.wikipedia.org/wiki/Shoelace_formula
        return inside + border / 2 + 1 // see https://en.wikipedia.org/wiki/Pick%27s_theorem
    }

    fun solve1(lines: List<String>): Long {
        return solve(getDirections(lines))
    }

    fun solve2(lines: List<String>): Long {
        return solve(getDirectionsFromColorCode(lines))
    }

    header(1)
    test(::solve1, testInput, 62)
    solve(::solve1)

    header(2)
    test(::solve2, testInput, 952408144115)
    solve(::solve2)
}

data class LPoint(val x: Long, val y: Long)
