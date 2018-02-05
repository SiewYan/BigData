import org.apache.spark.SparkConf 
import org.apache.spark._
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
 
object FirstTest {
  def main(args: Array[String]) {
    val conf = new SparkConf().setAppName("FirstTest")
    val spark = new SparkContext(conf)
    val logFile = "hdfs://10.64.22.72:9000/testFileLines.txt"
    val logData = spark.textFile(logFile, 2).cache()
    val numAs = logData.filter(line => line.contains("a")).count()
    val numBs = logData.filter(line => line.contains("b")).count()
    println("Lines with a: %s, Lines with b: %s".format(numAs, numBs))
    spark.stop()
  }
}
