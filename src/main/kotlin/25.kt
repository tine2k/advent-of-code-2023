import org.jgrapht.alg.flow.EdmondsKarpMFImpl
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.graph.SimpleGraph

fun main() {
    val testInput = "jqt: rhn xhk nvd\n" +
            "rsh: frs pzl lsr\n" +
            "xhk: hfx\n" +
            "cmg: qnr nvd lhk bvb\n" +
            "rhn: xhk bvb hfx\n" +
            "bvb: xhk hfx\n" +
            "pzl: lsr hfx nvd\n" +
            "qnr: nvd\n" +
            "ntq: jqt hfx bvb xhk\n" +
            "nvd: lhk\n" +
            "lsr: lhk\n" +
            "rzs: qnr cmg lsr rsh\n" +
            "frs: qnr lhk lsr"

    fun solve1(lines: List<String>): Long {
        val graph = SimpleGraph<String, DefaultEdge>(DefaultEdge::class.java)
        lines.forEach { line ->
            val tokens = line.split(": ")
            graph.addVertex(tokens[0])
            tokens[1].split(" ").forEach {
                graph.addVertex(it)
                graph.addEdge(tokens[0], it)
            }
        }

//         slow approach (about 5s)
//        val girvanNewmanClustering = GirvanNewmanClustering(graph, 2)
//        val clusters = girvanNewmanClustering.clustering.clusters
//        return (clusters[0].size * clusters[1].size).toLong()

        // faster (2.5s)
//        val cut = StoerWagnerMinimumCut(graph).minCut().size
//        return (cut * (graph.vertexSet().size - cut)).toLong()

        // fastest
        val algo = EdmondsKarpMFImpl(graph)
        val vertices = graph.vertexSet().toList()
        var index = 1
        do {
            algo.calculateMaximumFlow(vertices.first(), vertices[++index])
        } while (algo.cutEdges.size != 3)
        return (algo.sinkPartition.size * algo.sourcePartition.size).toLong()
    }

    header(1)
    test(::solve1, testInput, 54)
    solve(::solve1)
}
