import kotlin.math.max
import kotlin.math.min

fun main() {
    val testInput = "1,0,1~1,2,1\n" +
            "0,0,2~2,0,2\n" +
            "0,2,3~2,2,3\n" +
            "0,0,4~0,2,4\n" +
            "2,0,5~2,2,5\n" +
            "0,1,6~2,1,6\n" +
            "1,1,8~1,1,9"

    fun getBricks(lines: List<String>): List<Brick> {
        return lines.mapIndexed { i, it ->
            val points = it.split("~").map { t ->
                val x = t.split(",")
                Point3(x[0].toInt(), x[1].toInt(), x[2].toInt())
            }
            if (points[0].x > points[1].x || points[0].y > points[1].y || points[0].z > points[1].z) {
                error("fail")
            }
            Brick("abcdefghijklmnopqrstuvwxyz".toCharArray()[i % 26].uppercaseChar(), points[0], points[1])
        }
    }

    fun getFallenBricks(lines: List<String>): MutableList<Brick> {
        val bricks = getBricks(lines).sortedBy { min(it.a.z, it.b.z) }.toMutableList()
        for (i in 0..<bricks.size) {
            var fallingBrick = bricks[i]
            while (min(fallingBrick.a.z, fallingBrick.b.z) > 1 && bricks.none { it.supports(fallingBrick) }) {
                fallingBrick = fallingBrick.moveDown()
            }
            bricks[i] = fallingBrick
        }
        return bricks
    }

    fun solve1(lines: List<String>): Long {
        val bricks = getFallenBricks(lines)
        val supports = bricks.associateWith { lower -> bricks.filter { upper -> lower.supports(upper) } }
        val supportedByCount = supports.flatMap { it.value }.groupBy { it }.mapValues { e -> e.value.size }
        return supports.count { it.value.all { brick -> (supportedByCount[brick] ?: 0) > 1 } }.toLong()
    }

    fun solve2(lines: List<String>): Long {
        val bricks = getFallenBricks(lines)
        val supports = bricks.associateWith { lower -> bricks.filter { upper -> lower.supports(upper) } }
        val supportedBy = supports.flatMap { it.value }.toSet()
            .associateWith { supportee: Brick -> supports.filter { it.value.contains(supportee) }.map { it.key } }

        return bricks.reversed().sumOf { brick ->
            printProgress(bricks.indexOf(brick), bricks)
            val bricksToCheck = mutableListOf(brick)
            val fallenBricks = mutableSetOf(brick)
            while (bricksToCheck.isNotEmpty()) {
                val brickToCheck = bricksToCheck.removeFirst()
                val supporters = supports[brickToCheck]?.toSet() ?: emptySet()
                supporters.forEach {
                    if ((supportedBy[it]
                            ?: emptyList()).all { s -> supporters.contains(s) || fallenBricks.contains(s) }
                    ) {
                        fallenBricks.add(it)
                        bricksToCheck.add(it)
                    }
                }
            }
            fallenBricks.size.toLong() - 1
        }
    }

    header(1)
    test(::solve1, testInput, 5)
    solve(::solve1)

    header(2)
    test(::solve2, testInput, 7)
    solve(::solve2)
}

data class Point3(val x: Int, val y: Int, val z: Int) {
    override fun toString(): String {
        return "$x,$y,$z"
    }
}

data class Brick(val name: Char, val a: Point3, val b: Point3) {
    override fun toString(): String {
        return "$name: $a ~ $b"
    }

    fun supports(v: Brick): Boolean {
        return max(a.z, b.z) + 1 == min(v.a.z, v.b.z) &&
                (v.a.x <= b.x && v.b.x >= a.x) &&
                (v.a.y <= b.y && v.b.y >= a.y)
    }
    
    fun moveDown(): Brick {
        return this.copy(
            a = this.a.copy(z = this.a.z - 1),
            b = this.b.copy(z = this.b.z - 1)
        )
    }
}
