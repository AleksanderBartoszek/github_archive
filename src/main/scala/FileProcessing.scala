import java.io.File
import scala.reflect.io.Directory
import little.io.FileMethods

/** Function deleting already processed zipped and unzipped files for given day
 * @param day [Int] 
 */
def deleteFiles(day: Int, zip : String, unzip: String): Unit = {
  val unzippedData = Directory(s"$unzip$day")
  val zippedData = Directory(s"$zip$day")
  unzippedData.deleteRecursively()
  zippedData.deleteRecursively()
}

/** Function creating new File or emptying existing one
 * @param path [String] path to desired file
 * @return [File] new file
 */
def emptyFile(path: String): File = {
  val file = File(path)
  file.setText("")
  file
}

/** Function creating new Directory if one doesn't exist
 * @param path [String] path to desired directory
 * @return [File] new Directory
 */
def emptyDirectory(path: String): File = {
  val dir = File(path)
  dir.mkdir()
  dir
}