/**
  * Copyright 2017 idio Ltd
  *
  * Permission is hereby granted, free of charge, to any person obtaining a copy of
  * this software and associated documentation files (the "Software"), to deal in
  * the Software without restriction, including without limitation the rights to
  * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
  * of the Software, and to permit persons to whom the Software is furnished to do
  * so, subject to the following conditions:
  *
  * The above copyright notice and this permission notice shall be included in all
  * copies or substantial portions of the Software.
  *
  * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
  * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
  * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL THE
  * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
  * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
  * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
  * SOFTWARE.
  */
import java.io.{File, FileOutputStream}

import scala.collection.JavaConverters.asJavaEnumerationConverter
import org.apache.logging.log4j.core.config.plugins.processor.PluginCache
import sbtassembly.MergeStrategy

object Log4j2MergeStrategy {
  val plugincache: MergeStrategy = new MergeStrategy {
    val name = "log4j2::plugincache"
    def apply(tempDir: File, path: String, files: Seq[File]): Either[String, Seq[(File, String)]] = {
      val file = MergeStrategy.createMergeTarget(tempDir, path)
      val out  = new FileOutputStream(file)

      val aggregator = new PluginCache()
      val filesEnum  = files.toIterator.map(_.toURI.toURL).asJavaEnumeration

      try {
        aggregator.loadCacheFiles(filesEnum)
        aggregator.writeCache(out)
        Right(Seq(file -> path))
      } finally {
        out.close()
      }
    }
  }
}
