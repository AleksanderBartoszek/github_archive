
@main
def githubArchive(repoOwner: String, repo: String,  month: Int, year: Int, delete: Boolean): Unit = {

  // hiding info logs for debug convenience
  import org.apache.log4j.{Level, Logger}
  val rootLogger = Logger.getRootLogger
  rootLogger.setLevel(Level.ERROR)
  Logger.getLogger("org.apache.spark").setLevel(Level.WARN)
  Logger.getLogger("org.spark-project").setLevel(Level.WARN)

  // getting repo id from name for searching
  val id = getArchiveID(repoOwner, repo)

  // function responsible for all calculations
  archiveProcessing(month, year, id, repo, delete, "results.txt", "./zippedData", "./unzippedData")
}
