import org.apache.spark.sql.SparkSession

import java.text.SimpleDateFormat
import java.util.Calendar

/** Function coordinating data processing 
 * @param month [Int] 
 * @param year [Int]
 * @param repoID [Int] repository ID
 * @param repoName [String] repository name
 * @param delete [Boolean] should files be deleted after processing
 * @param outPath [String] path to .txt file for storing results
 * @param zipDirPath [String] path where to store zipped data
 * @param unzipDirPath [String] path where to store unzipped data
 */
def archiveProcessing(month: Int, year: Int, repoID: Int, repoName: String, delete: Boolean,
                      outPath: String, zipDirPath: String, unzipDirPath: String): Unit = {
  // correct date format + number of days in month to process
  val calendar = Calendar.getInstance()
  calendar.set(year, month - 1, 1)
  val maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
  val dateFormat = SimpleDateFormat("yyyy-MM-dd")

  val resultFile = emptyFile(outPath)
  val zippedData = emptyDirectory(zipDirPath)
  val unzippedData = emptyDirectory(unzipDirPath)

  for(day <- 1 to maxDay){
    // setting up files
    val zippedDay = emptyDirectory(s"$zipDirPath/$day")
    val unzippedDay = emptyDirectory(s"$unzipDirPath/$day")
    // date formatting
    calendar.set(year, month - 1, day)
    val dateTime = dateFormat.format(calendar.getTime)

    // data analysis
    val result = dfAnalysis(dateTime, day, repoID)

    // console and file printing
    printDay(day, month, year, repoName, result.uniqueStars, result.openedPRs)
    saveDay(resultFile, s"$day.$month.$year $repoName ${result.uniqueStars} ${result.openedPRs}\n")

    // optional file cleaning
    if(delete) deleteFiles(day, s"$zipDirPath/", s"$unzipDirPath/")
  }
}

/** Function operating on one day of data
 * @param format [String] date format for downloading data
 * @param day [Int] day to process
 * @param repoID [Int] repository ID
 * @return [SearchResult] containing unique stars and number of pull requests
 */
def dfAnalysis(format: String, day: Int, repoID: Int): SearchResult = {
  val spark = SparkSession.builder.appName("GH_Archive_AleksanderBartoszek").master("local[*]").getOrCreate()
  import spark.implicits._

  val df = spark.read.json(getGHArchives(format, day))
  val repoEntries = df.filter($"repo.id" === repoID)
  val repoKeys = repoEntries.select($"repo.name", $"actor.login", $"type", $"created_at", $"payload.action")
  val uniqueStars = repoKeys.filter($"type" === "WatchEvent").dropDuplicates("login").count
  val openedPRs = repoKeys.filter($"type" ==="PullRequestEvent" && $"payload.action" ==="opened").count
  SearchResult(uniqueStars, openedPRs)
}
