package com.mohan.stack

import scala.xml.XML

import org.apache.spark.SparkContext
import org.apache.spark.SparkConf

import java.text.SimpleDateFormat
import java.lang.String
import scala.xml.Elem
import org.apache.spark.sql.SparkSession

object xml_logs {
	def main(args: Array[String]) = {
	  
	    if (args.length < 2) {
      System.err.println("Usage: Spark-xml <Input-File> <Output-File>");
      System.exit(1);
      }
	    
			val spark = SparkSession
				.builder
				.appName("Spark-xml")
				.master("local[2]")
				.getOrCreate()
				
			//Read some example file to a test RDD
			val data = spark.read.textFile(args(0)).rdd

			val result = data.filter{line => {line.trim().startsWith("<row")}
			}
			.filter { line => {line.contains("""PostTypeId="1"""")}
			}
			.map {line => {
//			  line
			  val xml = XML.loadString(line)
			  xml.attribute("Tags").get.toString()
//			  tagString
			  }
			}
			.flatMap { data => {
//			  tagString
			  data.replaceAll("&lt;", " ").replaceAll("&gt;", " ").split(" ")
//			  individual tag like spark
			}
			}
			.filter { tag => {tag.length() > 0 }
			}
			.map { data => {
			  (data, 1)
			}
			}
			.reduceByKey(_ + _)
			.sortByKey(true)
			.map(x=>x._1+" "+x._2)
			result.saveAsTextFile(args(1))
			//result.foreach { println }
//			println(result.count())

			spark.stop
	}
}