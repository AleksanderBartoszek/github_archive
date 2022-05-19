import java.io.File
import little.io.FileMethods

/** Function printing formatted daily raport to console */
def printDay(day: Int, month: Int, year: Int, repoName: String, uniqueStars: Long, openedPRs: Long): Unit = {
  println("+--------------------------+")
  println(s"DATE:\t$day.$month.$year")
  println(s"project name:\t$repoName")
  println(s"unique username stars:\t$uniqueStars")
  println(s"opened pull requests:\t$openedPRs")
  println("+--------------------------+")
}

/** Function saving results to file
 * @param file [File] file to save to 
 * @param input [String] content to add to file
 */
def saveDay(file : File, input: String): Unit = {
  file << input
}