// apache spark
import org.apache.spark.sql.SparkSession
// file handling
import scala.reflect.io.Directory
import java.io.File
import little.io.{FileMethods, IoStringMethods, WriterMethods}
// decompressing
import little.io.Compressor.gunzip
import little.io.BufferSize
// for easy date formatting
import java.util.Calendar
import java.text.SimpleDateFormat
// scala try
import scala.util.Try


object Main {
  def main(args: Array[String]): Unit = {
    val repoOwner = args(0)
    val repo = args(1)
    val month = args(2).toInt
    val year = args(3).toInt
    val delete = Try(args(4).toBoolean).getOrElse(true)

    // hiding info logs for debug convenience
    import org.apache.log4j.{Level, Logger}
    val rootLogger = Logger.getRootLogger
    rootLogger.setLevel(Level.ERROR)
    Logger.getLogger("org.apache.spark").setLevel(Level.WARN)
    Logger.getLogger("org.spark-project").setLevel(Level.WARN)

    // preparing files and directories
    val resultPath = "results.txt"
    val resultFile = new File(resultPath)
    resultFile.setText("")
    val zippedData = new File("./zippedData")
    zippedData.mkdir()
    val unzippedData = new File("./unzippedData")
    unzippedData.mkdir()

    // getting repo id from name for searching
    val id = findArchiveID(repoOwner, repo)

    // function responsible for all calculations
    archiveProcessing(month, year, id, repo, resultPath, delete)
  }

  // function downloading and unzipping data for each hour in given day
  // input -> date formatted to match download link, day for convenience
  // output -> path to directory with unzipped data
  def getGHArchives(date: String, day: Int): String = {
    for(hour <- 0 to 23) {
      val uri = "http://data.gharchive.org/" + date + "-" + hour + ".json.gz"
      val request = requests.get(uri)
      given BufferSize = BufferSize(1024)
      val zippedFile = new File("./zippedData/"+day+"/"+hour+".txt.gz")
      zippedFile.setBytes(request.data.array)
      val unzippedFile = new File("./unzippedData/"+day+"/"+hour+".json")
      gunzip(zippedFile, unzippedFile)
    }
    "./unzippedData/"+day
  }

  // input -> repository owner, repository name
  // output -> repository id
  def findArchiveID(owner: String, repo: String): Int = {
    val request = requests.get("https://api.github.com/repos/"+owner+"/"+repo)
    val parsed: ujson.Value = ujson.read(request.text())
    parsed("id").num.toInt
  }

  // deleting already processed JSONs
  def deleteFiles(day: Int): Unit = {
    val unzippedData = new Directory(new File(s"./unzippedData/$day"))
    unzippedData.deleteRecursively()
    val zippedData = new Directory(new File(s"./zippedData/$day"))
    zippedData.deleteRecursively()
  }

  def archiveProcessing(month: Int, year: Int, repoID: Int, repoName: String, resultFilePath: String, delete: Boolean): Unit = {
    // spark setup
    val spark = SparkSession.builder.appName("GH_Archive_AleksanderBartoszek").master("local[*]").getOrCreate()
    import spark.implicits._
    // correct date format + number of days in month to process
    val calendar = Calendar.getInstance()
    calendar.set(year, month - 1, 1)
    val maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    val dateFormat = SimpleDateFormat("yyyy-MM-dd")

    for(day <- 1 to maxDay){
      // setting up files
      val zippedDay = new File(s"./zippedData/$day")
      zippedDay.mkdir()
      val unzippedDay = new File(s"./unzippedData/$day")
      unzippedDay.mkdir()
      // date formatting
      calendar.set(year, month - 1, day)
      val dateTime = dateFormat.format(calendar.getTime)
      // reading downloaded JSON
      val df = spark.read.json(getGHArchives(dateTime, day))
      // processing data
      val repoEntries = df.filter($"repo.id" === repoID)
      val repoKeys = repoEntries.select($"repo.name", $"actor.login", $"type", $"created_at", $"payload.action")
      val uniqueStars = repoKeys.filter($"type" === "WatchEvent").dropDuplicates("login").count
      val openedPRs = repoKeys.filter($"type" ==="PullRequestEvent" && $"payload.action" ==="opened").count

      // console and file printing
      println("+--------------------------+")
      println(s"DATE:\t$day.$month.$year")
      println(s"project name:\t$repoName")
      println(s"unique username stars:\t$uniqueStars")
      println(s"opened pull requests:\t$openedPRs")
      println("+--------------------------+")
      val file = resultFilePath.toFile << s"$day.$month.$year $repoName $uniqueStars $openedPRs\n"

      // optional file cleaning
      if(delete) deleteFiles(day)
    }
  }
}
