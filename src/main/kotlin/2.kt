import kotlin.math.max

fun main() {
    val testInput = "Game 1: 3 blue, 4 red; 1 red, 2 green, 6 blue; 2 green\n" +
            "Game 2: 1 blue, 2 green; 3 green, 4 blue, 1 red; 1 green, 1 blue\n" +
            "Game 3: 8 green, 6 blue, 20 red; 5 blue, 4 red, 13 green; 5 green, 1 red\n" +
            "Game 4: 1 green, 3 red, 6 blue; 3 green, 6 red; 3 green, 15 blue, 14 red\n" +
            "Game 5: 6 red, 1 blue, 3 green; 2 blue, 1 red, 2 green"

    fun getGameData(line: String): Pair<Long, List<List<List<String>>>> {
        val lineSplit = line.split(":")
        val gameNumber = lineSplit[0].substring("Game ".length).toLong()
        val gameSplit = lineSplit[1].split(";")
        val data = gameSplit.map { gameToken ->
            val games = gameToken.split(",")
            games.map { game ->
                game.trim().split(" ")
            }
        }
        return gameNumber to data
    }

    fun getCubes(line: String): Pair<Long, MutableMap<String, Int>> {
        val cubes = mutableMapOf<String, Int>()
        val game = getGameData(line)
        game.second.forEach { round ->
            round.forEach { token -> cubes[token[1]] = max(cubes[token[1]] ?: 0, token[0].toInt()) }
        }
        return Pair(game.first, cubes)
    }

    fun solve1(lines: List<String>): Long {
        return lines.sumOf { line ->
            val (gameNumber, cubes) = getCubes(line)
            if ((cubes["red"] ?: 0) <= 12 && (cubes["green"] ?: 0) <= 13 && (cubes["blue"] ?: 0) <= 14) {
                gameNumber
            } else {
                0L
            }
        }
    }

    fun solve2(lines: List<String>): Long {
        return lines.sumOf { line ->
            val (_, cubes) = getCubes(line)
            (cubes["red"] ?: 1).toLong() * (cubes["green"] ?: 1) .toLong()* (cubes["blue"] ?: 1).toLong()
        }
    }

    header(1)
    test(::solve1, testInput, 8)
    solve(::solve1)

    header(2)
    test(::solve2, testInput, 2286)
    solve(::solve2)
}
