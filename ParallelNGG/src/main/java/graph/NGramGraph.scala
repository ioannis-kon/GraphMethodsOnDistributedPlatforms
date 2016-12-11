package graph

import java.io.IOException

/**
  * @author Kontopoulos Ioannis
  */
class NGramGraph(ngram: Int, dwin: Int) {

  // variable that holds the edges of the graph
  private var graphEdges: Map[(String,String),Double] = Map()
  var numEdges = 0

  /**
    * Gets the edges of the graph
    * @return array of edges
    */
  def edges: Map[(String,String),Double] = graphEdges

  /**
    * Creates a graph from given string
    * @param dataString string text
    */
  def fromString(dataString: String): Unit = {
    val vertices = dataString
      .sliding(ngram)
      .map(atom => (("_" + atom), atom))
      .toArray

    val edges = (vertices ++ Array.fill(dwin)(("", null))) //add dummy vertices at the end
      .sliding(dwin + 1) //slide over dwin + 1 vertices at the time
      .flatMap(arr => {
      val (srcId, _) = arr.head //take first
      // generate 2n edges
      arr.tail.flatMap{case (dstId, _) =>
        Array(((srcId, dstId), 1.0), ((dstId, srcId), 1.0))
      }}.filter(e => e._1._1 != "" & e._1._2 != "")) //drop dummies
      .toArray
      .groupBy(e => e._1)
      .mapValues(e => e.length.toDouble).toArray

    graphEdges = edges.toMap
    numEdges = graphEdges.size
  }

  /**
    * Creates a graph from specified file
    * @param document file
    */
  def fromFile(document: String): Unit = {
    try {
      fromString(scala.io.Source.fromFile(document).mkString)
    }
    catch {
      case e: IOException => println(s"File not found: $document")
      case ex: Exception => println(ex.getMessage)
    }
  }

  /**
    * Saves graph to dot format file
    * @param fileName
    */
  def saveToDotFormat(fileName: String): Unit = {
    val writer = new java.io.FileWriter(s"$fileName.dot")
    writer.write("digraph nGramGraph {\n")
    val formattedEdges = edges.map{
      case (vertices,weight) =>
        s"\t${vertices._1.replaceAll("[ !@#$%^&*()_+-={}|,.<>/?:;']", "_")} -> ${vertices._2.replaceAll("[ !@#$%^&*()_+-={}|,.<>/?:;']", "_")} [label=$weight];"
    }.mkString("\n")
    writer.write(formattedEdges)
    writer.write("\n}")
    writer.close
  }

}
