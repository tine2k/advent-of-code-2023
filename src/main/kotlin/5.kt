fun main() {
    val testInput = "seeds: 79 14 55 13\n" +
            "\n" +
            "seed-to-soil map:\n" +
            "50 98 2\n" +
            "52 50 48\n" +
            "\n" +
            "soil-to-fertilizer map:\n" +
            "0 15 37\n" +
            "37 52 2\n" +
            "39 0 15\n" +
            "\n" +
            "fertilizer-to-water map:\n" +
            "49 53 8\n" +
            "0 11 42\n" +
            "42 0 7\n" +
            "57 7 4\n" +
            "\n" +
            "water-to-light map:\n" +
            "88 18 7\n" +
            "18 25 70\n" +
            "\n" +
            "light-to-temperature map:\n" +
            "45 77 23\n" +
            "81 45 19\n" +
            "68 64 13\n" +
            "\n" +
            "temperature-to-humidity map:\n" +
            "0 69 1\n" +
            "1 0 69\n" +
            "\n" +
            "humidity-to-location map:\n" +
            "60 56 37\n" +
            "56 93 4"

    data class SeedMap(val source: Long, val target: Long, val length: Long) {
        fun map(input: Long): Long? {
            if (input in source..<(source + length)) {
                return target + input - source
            }
            return null
        }

        fun mapReverse(input: Long): Long? {
            if (input in target..<(target + length)) {
                return source + input - target
            }
            return null
        }
    }

    fun parseInput(lines: List<String>): Pair<List<Long>, List<List<SeedMap>>> {
        val seeds = lines[0].split(":")[1].trim().split(" ").map { it.toLong() }

        val seedMaps = mutableListOf<List<SeedMap>>()
        var seedMapParts = mutableListOf<SeedMap>()

        lines.subList(2, lines.size).forEach { line ->
            if (line.contains(":")) {
                seedMapParts = mutableListOf()
                seedMaps.add(seedMapParts)
            } else if (line.isNotEmpty()) {
                val data = line.split(" ").map { it.toLong() }
                seedMapParts.add(SeedMap(data[1], data[0], data[2]))
            }
        }
        return Pair(seeds, seedMaps)
    }

    fun mapSeed(input: Long, seedMaps: List<SeedMap>): Long {
        seedMaps.forEach {
            val result = it.map(input)
            if (result != null) {
                return result
            }
        }
        return input
    }

    fun mapSeedReversed(input: Long, seedMaps: List<SeedMap>): Long {
        seedMaps.reversed().forEach {
            val result = it.mapReverse(input)
            if (result != null) {
                return result
            }
        }
        return input
    }

    fun findLowestSeed(seeds: List<Long>, seedMaps: List<List<SeedMap>>): Long {
        return seeds.minOf { seed ->
            var value = seed
            seedMaps.forEach {
                value = mapSeed(value, it)
            }
            value
        }
    }

    fun findLowestLocation(seeds: List<LongRange>, seedMaps: List<List<SeedMap>>): Long {
        val maxLocation = seedMaps.last().maxOf { it.target + it.length }
        val seedMapsWithoutLocation = seedMaps.subList(0, seedMaps.size).reversed()
        for (location in 0L..maxLocation) {
            var result = location
            seedMapsWithoutLocation.forEach {
                result = mapSeedReversed(result, it)
            }
            if (seeds.any { it.contains(result) }) {
                return location
            }
        }
        error("No Location found")
    }

    fun solve1(lines: List<String>): Long {
        val (seeds, seedMaps) = parseInput(lines)
        return findLowestSeed(seeds, seedMaps)
    }

    fun solve2(lines: List<String>): Long {
        val (seeds, seedMaps) = parseInput(lines)
        val newSeeds = seeds.withIndex()
            .groupBy { it.index / 2 }
            .map { it.value[0].value..<(it.value[1].value + it.value[0].value) }
        return findLowestLocation(newSeeds, seedMaps)
    }

    header(1)
    test(::solve1, testInput, 35)
    solve(::solve1)

    header(2)
    test(::solve2, testInput, 46)
    solve(::solve2)
}
