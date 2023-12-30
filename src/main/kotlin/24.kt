import org.la4j.Matrix
import org.la4j.Vector
import org.la4j.linear.GaussianSolver

fun main() {
    val testInput = "19, 13, 30 @ -2,  1, -2\n" +
            "18, 19, 22 @ -1, -1, -2\n" +
            "20, 25, 34 @ -2, -2, -4\n" +
            "12, 31, 28 @ -1, -2, -1\n" +
            "20, 19, 15 @  1, -5, -3"

    fun parseInputData(lines: List<String>): List<Stone> {
        return lines.map { line ->
            val tokens = line.split(" @ ")
            val point = tokens[0].split(", ").map { it.trim().toLong() }
            val vel = tokens[1].split(", ").map { it.trim().toLong() }
            Stone(StonePoint(point[0], point[1], point[2]), StonePoint(vel[0], vel[1], vel[2]))
        }
    }

    fun <T> createPairs(c: List<T>): List<Pair<T, T>> {
        val pairs = mutableListOf<Pair<T, T>>()
        for (i in c.indices) {
            for (j in i + 1..<c.size) {
                pairs.add(c[i] to c[j])
            }
        }
        return pairs
    }

    fun findIntersection(first: Stone, second: Stone): Intersection {
        // y = mx + b
        val m1 = first.vel.y.toDouble() / first.vel.x
        val b1 = first.p.y.toDouble() - m1 * first.p.x

        val m2 = second.vel.y.toDouble() / second.vel.x
        val b2 = second.p.y.toDouble() - m2 * second.p.x

        val x = (b1 - b2) / (m2 - m1)
        val y = m1 * x + b1
        return Intersection(x, y)
    }

    fun isInFuture(stone: Stone, intersection: Intersection): Boolean {
        return (intersection.x - stone.p.x) / stone.vel.x > 0
    }

    fun isInFuture(stones: Pair<Stone, Stone>, intersection: Intersection): Boolean {
        return isInFuture(stones.first, intersection) && isInFuture(stones.second, intersection)
    }

    fun solve1(lines: List<String>, ta: Pair<Double, Double>): Long {
        val stones = parseInputData(lines)
        val intersections = createPairs(stones)
            .associateWith { findIntersection(it.first, it.second) }
        val futureIntersections = intersections.filter { isInFuture(it.key, it.value) }
            .map { it.value }
        return futureIntersections.count { it.x >= ta.first && it.x <= ta.second && it.y >= ta.first && it.y <= ta.second }
            .toLong()
    }

    fun solve1Small(lines: List<String>): Long {
        return solve1(lines, 7.0 to 27.0)
    }

    fun solve1Big(lines: List<String>): Long {
        return solve1(lines, 200000000000000.0 to 400000000000000.0)
    }

    fun solveWithGaussianElimination(input: List<Pair<DoubleArray, Double>>): Vector {
        val matrix = Matrix.from2DArray(input.map { it.first }.toTypedArray())
        val vector = Vector.fromArray(input.map { it.second }.toDoubleArray())
        val gs = GaussianSolver(matrix)
        return gs.solve(vector)
    }

    fun solve2(lines: List<String>): Long {
        val stones = parseInputData(lines).subList(0,5)

        // inspired by explanation of @ash42: https://github.com/ash42/adventofcode/blob/main/adventofcode2023/src/nl/michielgraat/adventofcode2023/day24/Day24.java
        // desired rock as X,Y,Z,VX,VY,VZ.
        // any hailstone x,y,z,vx,vy,vz
        // t = (X-x)/(vx-VX)

        // formula for X, Y:
        // (vy'-vy)X + (vx-vx')Y + (y-y')VX + (x'-x)VY = - xvy + yvx + x'vy' - y'vx'
        val input = stones.zipWithNext { a, b ->
            arrayOf(
                b.vel.y - a.vel.y,
                a.vel.x - b.vel.x,
                a.p.y - b.p.y,
                b.p.x - a.p.x
            ).map { it.toDouble() }.toDoubleArray() to
                    (-a.p.x * a.vel.y + a.p.y * a.vel.x + b.p.x * b.vel.y - b.p.y * b.vel.x).toDouble()
        }
        val outputXY = solveWithGaussianElimination(input)

        // formula for Z:
        // (vz'-vz)X + (vx-vx')Z + (z-z')VX + (x'-x)VZ = - xvz + zvx + x'vz' - z'vx'
        // or (vx-vx')Z + (x'-x)VZ = - xvz + zvx + x'vz' - z'vx' - (vz'-vz)X - (z-z')VX

        val input2 = stones.zipWithNext { a, b ->
            arrayOf(
                b.vel.z - a.vel.z,
                a.vel.x - b.vel.x,
                a.p.z - b.p.z,
                b.p.x - a.p.x,
            ).map { it.toDouble() }.toDoubleArray() to
                    (-a.p.x * a.vel.z + a.p.z * a.vel.x + b.p.x * b.vel.z - b.p.z * b.vel.x).toDouble()
        }
        val outputZ = solveWithGaussianElimination(input2)

        return (outputXY[0] + outputXY[1] + outputZ[1]).toLong()
    }

    header(1)
    test(::solve1Small, testInput, 2)
    solve(::solve1Big)

    header(2)
    test(::solve2, testInput, 47)
    solve(::solve2)
}

data class StonePoint(val x: Long, val y: Long, val z: Long) {
    override fun toString(): String {
        return "$x, $y, $z"
    }
}

data class Stone(val p: StonePoint, val vel: StonePoint) {
    override fun toString(): String {
        return "[$p -> $vel]"
    }
}

data class Intersection(val x: Double, val y: Double)
