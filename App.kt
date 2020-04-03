package search

import java.io.File
import java.util.*

val scanner = Scanner(System.`in`)

enum class Strategy {
    ALL, ANY, NONE
}

class SearchEngine {
    private var data: List<String>
    private val index: MutableMap<String, MutableSet<Int>>
    private val separator: String = " "

    constructor(args: Array<String>) {
        val filename = args[1]
        data = collectData(filename)
        index = buildIndex()
    }
    fun printMenu() {
        println("=== Menu ===\n" +
                "1. Find a person\n" +
                "2. Print all people\n" +
                "0. Exit")
    }

    fun printData() {
        println("=== List of people ===")
        println(data.joinToString(separator = "\n"))
    }

    private fun collectData(filename: String): List<String> {
        val file = File(filename)
        val data = mutableListOf<String>()
        file.forEachLine { data.add(it) }
        return data
    }

    private fun buildIndex(): MutableMap<String, MutableSet<Int>> {
        val index = mutableMapOf<String, MutableSet<Int>> ()
        for (i in data.indices) {
            val words = data[i].toLowerCase().split(separator)
            words.forEach { index.getOrPut(it, { mutableSetOf() }).add(i) }
        }
        return index
    }

    private fun getQuery(): String {
        println("Enter a name or email to search all suitable people.")
        return scanner.nextLine().toLowerCase()
    }

    private fun processWord(word: String): Set<Int> {
        if (index.contains(word)) {
            return index[word]!!
        }
        return emptySet<Int>()
    }

    private fun makeQuery() {
        println("Select a matching strategy: ALL, ANY, NONE")
        val strategy: Strategy = Strategy.valueOf(scanner.nextLine())
        processQuery(strategy, getQuery())
    }

    private fun processQuery(strategy: Strategy, query: String) {
        when (strategy) {
            Strategy.ALL -> getAll(query)
            Strategy.ANY -> getAny(query)
            Strategy.NONE -> getNone(query)
        }
    }

    private fun printQueryResult(result: Set<Int>) {
        if (result.isEmpty()) {
            println("No matching people found.")
        } else {
            println("${result.size} persons found:")
            result.forEach { println(data[it]) }
        }
    }
    private fun getAll(query: String) {
        var result: Set<Int> = data.indices.toSet<Int>()
        query.split(separator).forEach { result = result.intersect(processWord(it)) }
        printQueryResult(result)
    }

    private fun getAny(query: String) {
        var result = setOf<Int>()
        query.split(separator).forEach { result = result.union(processWord(it)) }
        printQueryResult(result)
    }

    private fun getNone(query: String) {
        var result: Set<Int> = data.indices.toSet<Int>()
        query.split(separator).forEach { result = result.minus(processWord(it)) }
        printQueryResult(result)

    }

    fun poll() {
        while (true) {
            printMenu()
            val option = scanner.nextLine()
            if (option == "0") break
            when (option) {
                "1" -> makeQuery()
                "2" -> printData()
                else -> println("Incorrect option! Try again.")
            }
        }
        println("Bye!")
    }
}

fun main(args: Array<String>) {
    val searchEngine = SearchEngine(args)
    searchEngine.poll()
}


