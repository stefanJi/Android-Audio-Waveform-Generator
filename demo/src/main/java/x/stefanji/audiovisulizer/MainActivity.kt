package x.stefanji.audiovisulizer

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import x.stefanji.library.AudioWaveformGenerator
import java.io.File

private const val TAG = "Main"
private const val MP3 = "output.mp3"
private const val M4a = "test.m4a"
private const val INPUT_FILE = M4a

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.btnStart).setOnClickListener {
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    try {
                        val file = File(cacheDir, INPUT_FILE)
                        val ins = assets.open(INPUT_FILE)
                        val ous = file.outputStream()
                        val buffer = ByteArray(4096)
                        var len: Int
                        while (ins.read(buffer).also { read -> len = read } != -1) {
                            ous.write(buffer, 0, len)
                        }
                        ins.close()
                        ous.close()

                        val decoder = AudioWaveformGenerator(
                            file.absolutePath,
                            100
                        )
                        decoder.startDecode()
                        val samples = decoder.getSampleData()
                        Log.d(TAG, "onFinish ${samples.size}")
                        withContext(Dispatchers.Main) {
                            findViewById<Wave>(R.id.wave).setValues(samples)
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "copy", e)
                    }
                }
            }
        }
    }
}
