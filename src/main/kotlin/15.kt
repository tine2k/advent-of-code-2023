fun main() {
    val testInput = "rn=1,cm-,qp=3,cm=2,qp-,pc=4,ot=9,ab=5,pc-,pc=6,ot=7"

    fun hash(input: String): Long {
        return (listOf(0) + input.map { it.code }).reduce { acc, v -> (acc + v) * 17 % 256 }.toLong()
    }

    fun hashFirstValue(input: List<String>): Long {
        return hash(input[0])
    }

    fun solve1(lines: List<String>): Long {
        return lines.sumOf { line -> line.split(",").sumOf { token -> hash(token) } }
    }

    fun solve2(lines: List<String>): Long {
        val boxes = (0..255).map { mutableMapOf<String, Int>() }
        lines.forEach { line ->
            line.split(",").forEach { token ->
                val split = token.split('=', '-')
                val label = split[0]
                val box = hash(label).toInt()
                if (token.contains('-')) {
                    boxes[box].remove(label)
                } else {
                    val focalLength = split[1].toInt()
                    boxes[box][label] = focalLength
                }
            }
        }

        return boxes.map { it.keys }.reduce { a, b -> (a + b).toMutableSet() }
            .sumOf { label -> boxes.mapIndexed { i, box -> (box[label] ?: 0) * (i + 1) * (box.keys.indexOf(label) + 1) }.sum() }
            .toLong()
    }

    header(1)
    test(::hashFirstValue, "HASH", 52)
    test(::hashFirstValue, "rn=1", 30)
    test(::hashFirstValue, "cm-", 253)
    test(::solve1, testInput, 1320)
    solve(::solve1)

    header(2)
    test(::solve2, testInput, 145)
    solve(::solve2)
}
