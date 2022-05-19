import little.io.BufferSize
import little.io.Compressor.gunzip
import little.io.FileMethods

/** Function requesting data from gharchive.org for given day
 * @param date [String] formatted as "yyyy-MM-dd"
 * @param day [Int] repeated "dd" for convenience
 * @return [String] path to folder containing requested unzipped data for given day
 */
def getGHArchives(date: String, day: Int): String = {
  for(hour <- 0 to 23) {
    val zippedFile = emptyFile("./zippedData/"+day+"/"+hour+".txt.gz")
    val unzippedFile = emptyFile("./unzippedData/"+day+"/"+hour+".json")
    val uri = "http://data.gharchive.org/" + date + "-" + hour + ".json.gz"

    val request = requests.get(uri)
    zippedFile.setBytes(request.data.array)

    given BufferSize = BufferSize(1024)
    gunzip(zippedFile, unzippedFile)
  }
  "./unzippedData/"+day
}

/** Functions requesting ID of given github repository
 * @param owner [String] repository owner
 * @param repo [String] repository name
 * @return [Int] repository ID
 */
def getArchiveID(owner: String, repo: String): Int = {
  val request = requests.get("https://api.github.com/repos/"+owner+"/"+repo)
  val parsed: ujson.Value = ujson.read(request.text())
  parsed("id").num.toInt
}