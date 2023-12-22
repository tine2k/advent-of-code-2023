import kotlin.math.max
import kotlin.math.min

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
                Rule(Condition(compareTokens[0], condTokens[0].contains("<"), compareTokens[1].toInt()), condTokens[1])
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

    fun parseInput(lines: List<String>): Pair<Map<String, Workflow>, List<Map<String, Long>>> {
        val blankLine = lines.indexOf("")
        val workflows = lines.subList(0, blankLine).associate { parseWorkflow(it) }
        val ratings = lines.subList(blankLine + 1, lines.size)
            .map { it.substring(1, it.length - 1) }
            .map { it.split(",").associate { v -> v.split("=").let { t -> t[0] to t[1].toLong() } } }
        return workflows to ratings
    }

    fun findResult(workflows: Map<String, Workflow>, rating: Map<String, Long>): String {
        var nextFlow: Workflow?
        var result = "in"
        while (result != "A" && result != "R") {
            nextFlow = workflows[result]!!
            result = getResult(rating, nextFlow)
        }
        return result
    }

    fun solve1(lines: List<String>): Long {
        val (workflows, ratings) = parseInput(lines)
        return ratings.sumOf { rating ->
            if (findResult(workflows, rating) == "A") {
                rating.values.sum()
            } else {
                0L
            }
        }
    }

    fun applyCondition(c: Condition, s: MutableMap<String, IntRange>) {
        if (s[c.variable]!!.isEmpty()) return
        if (c.lessThen) {
            s[c.variable] = s[c.variable]!!.first..min(s[c.variable]!!.last, c.value - 1)
        } else {
            s[c.variable] = max(c.value + 1, s[c.variable]!!.first)..s[c.variable]!!.last
        }
    }

    fun applyRule(
        s: Map<String, IntRange>,
        rule: Rule,
        rules: List<Rule>,
        key: String
    ): Pair<String, MutableMap<String, IntRange>>? {
        val newS = s.toMutableMap()
        rules.subList(0, rules.indexOf(rule) + 1)
            .filter { it.condition != null }
            .forEach { otherRule ->
                if (otherRule != rule) {
                    val condition = otherRule.condition
                    var offByOne = 1
                    if (condition!!.lessThen) {
                        offByOne = -1
                    }
                    val invertedCond = condition.copy(
                        lessThen = !condition.lessThen,
                        value = condition.value + offByOne
                    )
                    applyCondition(invertedCond, newS)

                } else {
                    applyCondition(rule.condition!!, newS)
                }
            }

        return if (!newS.values.all { it.isEmpty() }) {
            key to newS
        } else {
            null
        }
    }

    fun followPath(
        input: String,
        workflows: Map<String, Workflow>,
        s: Map<String, IntRange>
    ): List<Pair<String, MutableMap<String, IntRange>>> {
        return workflows
            .filter { e -> e.value.rules.any { rule -> rule.result == input } }
            .flatMap { e ->
                e.value.rules
                    .filter { rule -> rule.result == input }
                    .mapNotNull { rule -> applyRule(s, rule, e.value.rules, e.key) }
            }
    }

    fun solve2(lines: List<String>): Long {
        val (workflows) = parseInput(lines)
        val validCombinations = mutableMapOf("x" to 1..4000, "m" to 1..4000, "a" to 1..4000, "s" to 1..4000)
        val combinations = mutableListOf("A" to validCombinations)
        val solutions = mutableListOf<MutableMap<String, IntRange>>()
        while (combinations.isNotEmpty()) {
            val first = combinations.first()
            if (first.first == "in" && first.second.values.none { it.isEmpty() }) {
                solutions.add(first.second)
            } else {
                combinations.addAll(followPath(first.first, workflows, first.second))
            }
            combinations.remove(first)
        }

        return solutions.sumOf { it.values.map { v -> v.count().toLong() }.reduce { a, c -> a * c } }
    }

    header(1)
    test(::solve1, testInput, 19114)
    solve(::solve1)

    header(2)
    test(::solve2, testInput, 167409079868000)
    solve(::solve2)
}

data class Workflow(val rules: List<Rule>)

data class Rule(val condition: Condition?, val result: String)

data class Condition(val variable: String, val lessThen: Boolean, val value: Int) {
    override fun toString(): String {
        return if (lessThen) {
            "$variable < $value"
        } else {
            "$variable > $value"
        }
    }
}
