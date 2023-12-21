fun main() {
    val testInput = "Time:      7  15   30\n" +
            "Distance:  9  40  200"

    fun getDistance(time: Long, hold: Long): Long = hold * (time - hold)

    fun solveWins(times: List<Long>, distances: List<Long>): Long {
        return times.mapIndexed { i, time ->
            var wins = 0L
            for (timePart in 0..time) {
                if (getDistance(time, timePart) > distances[i]) {
                    wins++
                }
            }
            wins
        }.reduce { acc, i -> acc * i }
    }

    fun solve1(lines: List<String>): Long {
        val times = lines[0].split(":")[1].trim().split(" ").filter { it.isNotEmpty() }.map { it.toLong() }
        val distances = lines[1].split(":")[1].trim().split(" ").filter { it.isNotEmpty() }.map { it.trim().toLong() }
        return solveWins(times, distances)
    }

    fun solve2(lines: List<String>): Long {
        val times = listOf(lines[0].split(":")[1].trim().replace(" ", "").toLong())
        val distances = listOf(lines[1].split(":")[1].trim().replace(" ", "").toLong())
        return solveWins(times, distances)
    }

    header(1)
    test(::solve1, testInput, 288)
    solve(::solve1)

    header(2)
    test(::solve2, testInput, 71503)
    solve(::solve2)
}
