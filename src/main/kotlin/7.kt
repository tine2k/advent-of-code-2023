fun main() {
    val testInput = "32T3K 765\n" +
            "T55J5 684\n" +
            "KK677 28\n" +
            "KTJJT 220\n" +
            "QQQJA 483"


    fun parseInput(lines: List<String>) = lines.map { line ->
        val tokens = line.split(" ")
        val hand = tokens[0].toCharArray()
        val bid = tokens[1].toLong()
        hand to bid
    }

    val strengthsWithoutJoker = listOf('2', '3', '4', '5', '6', '7', '8', '9', 'T', 'J', 'Q', 'K', 'A')
    val strengthsWithJoker = listOf('J', '2', '3', '4', '5', '6', '7', '8', '9', 'T', 'Q', 'K', 'A')

    fun getType(hand: CharArray): Long {
        val grouped = hand.groupBy { it }
        val occurrences = grouped.entries.map { it.value.size }
        val maxOccurrence = occurrences.maxOf { it }
        return when (maxOccurrence) {
            5 -> 1000L // five of a kind
            4 -> 900L // four of a kind
            3 -> if (occurrences.size == 2) {
                800L // full house
            } else {
                700L // tripple
            }

            2 -> if (occurrences.size == 3) {
                600L // two pair
            } else {
                500L // one pair
            }

            1 -> 0L // all different
            else -> error("invalid state: $maxOccurrence")
        }
    }

    fun compareHands(a: CharArray, b: CharArray, strengths: List<Char>): Int {
        for (i in 0..a.size) {
            val aIndex = strengths.indexOf(a[i])
            val bIndex = strengths.indexOf(b[i])
            if (aIndex > bIndex) {
                return 1
            } else if (aIndex < bIndex) {
                return -1
            }
        }
        return 0
    }

    fun findHandsWithJokerAtIndex(hand: CharArray, index: Int, hands: MutableList<CharArray>): List<CharArray> {
        if (index >= hand.size) {
            return listOf(hand)
        }
        return if (hand[index] != 'J') {
            findHandsWithJokerAtIndex(hand, index + 1, hands)
        } else {
            strengthsWithoutJoker.flatMap {
                val clonedHand = hand.clone()
                clonedHand[index] = it
                findHandsWithJokerAtIndex(clonedHand, index + 1, hands)
            }
        }
    }

    fun getTypeWithJokerRekursive(hand: CharArray): Long {
        val hands = findHandsWithJokerAtIndex(hand, 0, mutableListOf())
        return hands.maxOf { getType(it) }
    }

    fun solve(lines: List<String>, strengths: List<Char>, typeProvider: (Pair<CharArray, Long>) -> Long): Long {
        val comparator = compareBy(typeProvider).thenComparing { a, b -> compareHands(a.first, b.first, strengths) }
        val sortedHands = parseInput(lines).sortedWith(comparator)
        return sortedHands.mapIndexed { index, hand -> (index + 1) * hand.second }.sum()
    }

    fun solve1(lines: List<String>): Long {
        return solve(lines, strengthsWithoutJoker) { getType(it.first) }
    }


    fun solve2(lines: List<String>): Long {
        return solve(lines, strengthsWithJoker) { getTypeWithJokerRekursive(it.first) }
    }

    header(1)
    test(::solve1, testInput, 6440)
    solve(::solve1)

    header(2)
    test(::solve2, testInput, 5905)
    solve(::solve2)
}
