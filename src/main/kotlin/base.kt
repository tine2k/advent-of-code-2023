import org.apache.commons.io.IOUtils
import java.io.StringReader
import kotlin.time.measureTime
import kotlin.time.measureTimedValue

val day = Thread.currentThread().stackTrace[2].fileName!!.split(".")[0]

fun header(part: Int) {
    println("--- Day $day Part $part ---");
}

fun test(solveFn: (List<String>) -> Long, testInput: String, testResult: Int) {
    return test(solveFn, testInput, testResult.toLong())
}

fun test(solveFn: (List<String>) -> Long, testInput: String, testResult: Long) {
    return testString({ solveFn.invoke(it).toString() }, testInput, testResult.toString())
}

fun testString(solveFn: (List<String>) -> String, testInput: String, testResult: String) {
    val result = solveFn(IOUtils.readLines(StringReader(testInput)))
    if (result != testResult) {
        println("Test ❌: Result should be $testResult but was $result")
    } else {
        println("Test ✅")
    }
}

fun solve(solveFn: (List<String>) -> Long, input: List<String> = getInputFile()) {
    solveString({ solveFn.invoke(it).toString() }, input)
}

fun solveString(solveFn: (List<String>) -> String, input: List<String> = getInputFile()) {
    val (solution, timeTaken) = measureTimedValue {
        solveFn(input)
    }
    println()
    println("➡️ $solution ⬅️")
    println()
    println("⏳ $timeTaken ⏳")
    println()
}

fun getInputFile(): List<String> {
    return IOUtils.readLines(IOUtils::class.java.getResourceAsStream("/$day.txt"), "UTF-8")
}

fun <T> printProgress(i: Int, lines: List<T>) {
    printProgress(i, lines.size)
}

fun printProgress(i: Int, total: Int) {
    print("calculating ${i+1} of ${total}...\r")
}

enum class Direction {
    NORTH, SOUTH, WEST, EAST
}

data class Point(val x: Int, val y: Int)
