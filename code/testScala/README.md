# First Scala project 

#### Table of Contents

1. [Install and using sbt](#sbt)
2. [Simple example](#code)
3. [How to run the example in mesos/spark cluster using the sparkmesoshdfs docker](#run)

## Install and using sbt

sbt is a build tool for Scala, Java, and more. It requires Java 1.8 or later. 
 * [More documentation](https://www.scala-sbt.org/1.x/docs/index.html)

You should install java jdk 1.8 and sbt in this way in a CentOS7 host: (the first command make you super user (root))
   $ sudo su -
   $ wget --no-cookies --no-check-certificate --header "Cookie: gpw_e24=http%3A%2F%2Fwww.oracle.com%2F; oraclelicense=accept-securebackup-cookie" "http://download.oracle.com/otn-pub/java/jdk/8u161-b12/2f38c3b165be4555a1fa6e98c45e0808/jdk-8u161-linux-x64.rpm"
   $ yum install jdk-8u161-linux-x64.rpm
   $ curl https://bintray.com/sbt/rpm/rpm | sudo tee /etc/yum.repos.d/bintray-sbt-rpm.repo
   $ yum install sbt

## Simple example

Create the sbt directory infrastructure and buil file like eh one in this example or followig sbt socuemntation (src project target).
In src directory put you scala code (src/main/scala/FirstTest.scala)
```
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
```
Compile and create package with sbt:
   $ sbt compile
   $ sbt package

## How to run the example in mesos/spark cluster using the sparkmesoshdfs docker 

Remember to install docker and run the docker service:
Have a look to this [docuemntarion](https://github.com/CloudPadovana/BigData/tree/master/centosSparkmesos#install-docker-in-your-host)

Run the docker command with some parameter:
 * If the mesos master is just one:
   $ docker run -it -v /home/straldi/FirstTest/target/scala-2.10/sparksample_2.10-1.0.jar:/root/sparksample_2.10-1.0.jar -e SPARK_MASTER=mesos://10.64.22.79:5050 -e SPARK_IMAGE=straldi/sparkhdfsmesos --name sparkTest1 --net host --privileged --pid host straldi/sparkhdfsmesos /opt/spark/bin/spark-submit --class "FirstTest" --master mesos://10.64.22.79:5050 /root/sparksample_2.10-1.0.jar

 * If the mesos cluster has three masters in HA:
   $ docker run -it -v /home/straldi/FirstTest/target/scala-2.10/sparksample_2.10-1.0.jar:/root/sparksample_2.10-1.0.jar -e SPARK_MASTER=mesos://zk://10.64.22.79:2181,10.64.22.81:2181,10.64.22.82:2181/mesos -e SPARK_IMAGE=straldi/sparkhdfsmesos --name sparFirstTest --net host --privileged --pid host straldi/sparkhdfsmesos /opt/spark/bin/spark-submit --class "FirstTest" --master mesos://zk://10.64.22.79:2181,10.64.22.81:2181,10.64.22.82:2181/mesos /root/sparksample_2.10-1.0.jar
 
