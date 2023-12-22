fun main() {
    val testInput = "px{a<2006:qkq,m>2090:A,rfg}\n" +
            "pv{a>1716:R,A}\n" +
            "lnx{m>1548:A,A}\n" +
            "rfg{s<537:gd,x>2440:R,A}\n" +
            "qs{s>3448:A,lnx}\n" +
            "qkq{x<1416:A,crn}\n" +
            "crn{x>2662:A,R}\n" +
            "in{s<1351:px,qqz}\n" +
            "qqz{s>2770:qs,m<1801:hdj,R}\n" +
            "gd{a>3333:R,R}\n" +
            "hdj{m>838:A,pv}\n" +
            "\n" +
            "{x=787,m=2655,a=1222,s=2876}\n" +
            "{x=1679,m=44,a=2067,s=496}\n" +
            "{x=2036,m=264,a=79,s=2244}\n" +
            "{x=2461,m=1339,a=466,s=291}\n" +
            "{x=2127,m=1623,a=2188,s=1013}"

    fun parseWorkflow(input: String): Pair<String, Workflow> {
        val inputTokens = input.split("{")
        val rules = inputTokens[1].substring(0, inputTokens[1].length - 1).split(",").map { cond ->
            if (cond.contains(":")) {
                val condTokens = cond.split(":")
                val compareTokens = condTokens[0].split("<", ">")
                Rule(Condition(compareTokens[0], condTokens[0].contains("<"), compareTokens[1].toLong()), condTokens[1])
            } else {
                Rule(null, cond)
            }
        }
        return inputTokens[0] to Workflow(rules)
    }

    fun getResult(rating: Map<String, Long>, workflow: Workflow): String {
        workflow.rules.forEach { rule ->
            if (rule.condition == null ||
                (rule.condition.lessThen && rating[rule.condition.variable]!! < rule.condition.value) ||
                (!rule.condition.lessThen && rating[rule.condition.variable]!! > rule.condition.value)
            ) {
                return rule.result
            }
        }
        error("no rules matches")
    }

    fun solve1(lines: List<String>): Long {
        val blankLine = lines.indexOf("")
        val workflows = lines.subList(0, blankLine).associate { parseWorkflow(it) }
        val ratings = lines.subList(blankLine + 1, lines.size)
            .map { it.substring(1, it.length - 1) }
            .map { it.split(",").associate { v -> v.split("=").let { t -> t[0] to t[1].toLong() } } }
        return ratings.sumOf { rating ->
            var nextFlow: Workflow?
            var result = "in"
            while (result != "A" && result != "R") {
                nextFlow = workflows[result]!!
                result = getResult(rating, nextFlow)
            }
            if (result == "A") {
                rating.values.sum()
            } else {
                0L
            }
        }
    }

    fun solve2(lines: List<String>): Long {
        return -1
    }

    header(1)
    test(::solve1, testInput, 19114)
    solve(::solve1)

    header(2)
    test(::solve2, testInput, 1337)
    solve(::solve2)
}

data class Workflow(val rules: List<Rule>)

data class Rule(val condition: Condition?, val result: String)

data class Condition(val variable: String, val lessThen: Boolean, val value: Long)
