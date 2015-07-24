package gr.demokritos.iit.nGramGraphMethods

import org.apache.spark.graphx._

/**
 * @author Kontopoulos Ioannis
 */
class GraphInverseIntersector extends BinaryGraphOperator {

  /**
   * Creates a graph that contains the uncommon edges, the edges could be from any graph
   * @param g1 graph1
   * @param g2 graph2
   * @return graph
   */
  override def getResult(g1: Graph[String, Double], g2: Graph[String, Double]): Graph[String, Double] = {
    //m holds the edges from the first graph
    var m:Map[String, Tuple2[Long, Long]] = Map()
    //collect edges from the first graph
    g1.edges.collect.foreach{ e => m+=(e.srcId + "," + e.dstId -> new Tuple2(e.srcId,e.dstId)) }
    //m2 holds the edges from the second graph
    var m2:Map[String, Tuple2[Long, Long]] = Map()
    //collect edges from the second graph
    g2.edges.collect.foreach{ e => m2+=(e.srcId + "," + e.dstId -> new Tuple2(e.srcId,e.dstId)) }
    //map holds the uncommon edges
    var map:Map[String, Tuple2[Long, Long]] = Map()
    if(m.size > m2.size) {
      //search for the uncommon edges, if it does not exist add the key from the other in order not to have exception in the subgraph method
      m.keys.foreach{ i => if(!m2.contains(i)) map += (i -> m(i)) else map += (i -> new Tuple2(0L, 0L)) }
    }
    //search for the uncommon edges, if it does not exist add the key from the other in order not to have exception in the subgraph method
    else {
      m2.keys.foreach{ i => if(!m.contains(i)) map += (i -> m2(i)) else map += (i -> new Tuple2(0L, 0L)) }
    }
    if (g2.numEdges > g1.numEdges) {
      val inverseIntersectedGraph = g2.subgraph(epred = e => e.srcId == map(e.srcId + "," + e.dstId)._1 && e.dstId == map(e.srcId + "," + e.dstId)._2)
      //return the graph with the subset of edges
      inverseIntersectedGraph
    }
    else {
      val inverseIntersectedGraph = g1.subgraph(epred = e => e.srcId == map(e.srcId + "," + e.dstId)._1 && e.dstId == map(e.srcId + "," + e.dstId)._2)
      //return the graph with the subset of edges
      inverseIntersectedGraph}
  }

}