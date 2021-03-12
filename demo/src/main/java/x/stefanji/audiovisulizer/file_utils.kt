package x.stefanji.audiovisulizer

import java.io.*

/**
 * pcm文件转wav文件
 *
 * @param inFilename  源文件路径
 * @param outFilename 目标文件路径
 * @param sampleRate 采样率 44100 HZ/...
 * @param channels 通道数 1/2
 * @param bit 采样大小 8/16/32/64
 */
fun pcmToWav(inFilename: String, outFilename: String, sampleRate: Int, channels: Int, bit: Int) {
    val ins: InputStream
    val ous: OutputStream
    val totalAudioLen: Long
    val totalDataLen: Long
    val byteRate = bit.toLong() * sampleRate * channels / 8 //字节码率
    val buf = ByteArray(4096)
    try {
        ins = FileInputStream(inFilename)
        ous = FileOutputStream(outFilename)
        totalAudioLen = ins.channel.size()
        totalDataLen = totalAudioLen + 36
        writeWaveFileHeader(
            ous,
            totalAudioLen,
            totalDataLen,
            sampleRate,
            channels,
            byteRate,
            bit.toByte()
        )
        var read: Int
        while (ins.read(buf).also { read = it } != -1) {
            ous.write(buf, 0, read)
        }

        ins.close()
        ous.close()
    } catch (e: IOException) {
        e.printStackTrace()
    }
}

/**
 * 加入wav文件头
 */
fun writeWaveFileHeader(
    ous: OutputStream,
    totalAudioLen: Long,
    totalDataLen: Long,
    sampleRate: Int,
    channels: Int,
    byteRate: Long,
    bitsPerSample: Byte
) {
    val header = ByteArray(44)
    header[0] = 'R'.toByte() // RIFF/WAVE header
    header[1] = 'I'.toByte()
    header[2] = 'F'.toByte()
    header[3] = 'F'.toByte()
    header[4] = (totalDataLen and 0xff).toByte()
    header[5] = (totalDataLen shr 8 and 0xff).toByte()
    header[6] = (totalDataLen shr 16 and 0xff).toByte()
    header[7] = (totalDataLen shr 24 and 0xff).toByte()
    header[8] = 'W'.toByte() //WAVE
    header[9] = 'A'.toByte()
    header[10] = 'V'.toByte()
    header[11] = 'E'.toByte()
    header[12] = 'f'.toByte() // 'fmt ' chunk
    header[13] = 'm'.toByte()
    header[14] = 't'.toByte()
    header[15] = ' '.toByte()
    header[16] = 16 // 4 bytes: size of 'fmt ' chunk
    header[17] = 0
    header[18] = 0
    header[19] = 0
    header[20] = 1 // format = 1
    header[21] = 0
    header[22] = channels.toByte()
    header[23] = 0
    header[24] = (sampleRate and 0xff).toByte()
    header[25] = (sampleRate shr 8 and 0xff).toByte()
    header[26] = (sampleRate shr 16 and 0xff).toByte()
    header[27] = (sampleRate shr 24 and 0xff).toByte()
    header[28] = (byteRate and 0xff).toByte()
    header[29] = (byteRate shr 8 and 0xff).toByte()
    header[30] = (byteRate shr 16 and 0xff).toByte()
    header[31] = (byteRate shr 24 and 0xff).toByte()
    header[32] = (channels * bitsPerSample / 8).toByte() // block align
    header[33] = 0
    header[34] = bitsPerSample // bits per sample
    header[35] = 0
    header[36] = 'd'.toByte() //data
    header[37] = 'a'.toByte()
    header[38] = 't'.toByte()
    header[39] = 'a'.toByte()
    header[40] = (totalAudioLen and 0xff).toByte()
    header[41] = (totalAudioLen shr 8 and 0xff).toByte()
    header[42] = (totalAudioLen shr 16 and 0xff).toByte()
    header[43] = (totalAudioLen shr 24 and 0xff).toByte()
    ous.write(header, 0, 44)
}
