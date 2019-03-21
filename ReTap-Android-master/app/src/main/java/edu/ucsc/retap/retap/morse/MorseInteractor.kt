package edu.ucsc.retap.retap.morse

import android.os.Vibrator
import android.util.Log
import edu.ucsc.retap.retap.common.di.ApplicationScope
import edu.ucsc.retap.retap.messages.model.Message
import javax.inject.Inject

/**
 * Converted to Kotlin based on
 * https://github.com/JakeWharton/SMSMorse/blob/master/src/com/jakewharton/smsmorse/transaction/EventReceiver.java
 */
@ApplicationScope
class MorseInteractor @Inject constructor(
        private val vibrator: Vibrator
) {
    companion object {
        // Preference defaults
        private const val DEFAULT_VIBRATE_COUNTS = false
        private const val DEFAULT_DOT_LENGTH = 150
        private const val DEFAULT_INITIAL_PAUSE = 500L

        // Morse code
        private const val DOTS_IN_DASH = 3
        private const val DOTS_IN_GAP = 1
        private const val DOTS_IN_LETTER_GAP = 3
        private const val DOTS_IN_WORD_GAP = 7
    }

    /**
     * Vibrates a message in Morse, stopping all previous vibrations.
     * @param message the message to be vibrated in morse
     */
    fun vibrate(message: Message) {
        vibrator.cancel()
        val vibrations = convertToVibrations(message.contact.phoneNumber, true)
        vibrations.addAll(convertToVibrations(message.contents, false))
        vibrateMorse(vibrations)
    }

    /**
     * Stops vibrating all messages.
     */
    fun stop() {
        vibrator.cancel()
    }

    private fun vibrateMorse(vibrationLongs: ArrayList<Long>) {
        val vibrations = LongArray(vibrationLongs.size)
        val morseVibrations = StringBuffer("Vibrating Morse: ")

        //Unbox the array and generate a log line simultaneously
        for (i in 0 until vibrationLongs.size) {
            vibrations[i] = vibrationLongs[i]
            morseVibrations.append(if (i % 2 == 0) '-' else '+')
            morseVibrations.append(vibrationLongs[i])
        }

        this.vibrator.vibrate(vibrations, -1)
        Log.d(this::class.java.name, morseVibrations.toString())
    }

    private fun convertToVibrations(message: String, isNumber: Boolean): ArrayList<Long> {
        val vibrateCounts = DEFAULT_VIBRATE_COUNTS

        //Establish all lengths
        val dot = DEFAULT_DOT_LENGTH

        val dash = dot * DOTS_IN_DASH
        val gap = dot * DOTS_IN_GAP
        val letterGap = dot * DOTS_IN_LETTER_GAP
        val wordGap = dot * DOTS_IN_WORD_GAP

        val words =
                message.toUpperCase().trim { it <= ' ' }
                        .split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val vibrationObjects = ArrayList<Long>()
        val charset = if (isNumber && vibrateCounts) MorseHelper.CHARSET_COUNTS
        else MorseHelper.CHARSET_MORSE
        val lookups = if (isNumber && vibrateCounts) MorseHelper.COUNTS else MorseHelper.MORSE

        //Add initial pause
        vibrationObjects.add(DEFAULT_INITIAL_PAUSE)

        var word: String
        var letterBooleans: BooleanArray
        var letterIndex: Int
        for (i in words.indices) {
            word = words[i]

            for (j in 0 until word.length) {
                letterIndex = charset.indexOf(word[j].toString())

                if (letterIndex >= 0) {
                    letterBooleans = lookups[letterIndex]

                    for (k in letterBooleans.indices) {
                        vibrationObjects.add(if (letterBooleans[k]) dot.toLong() else dash.toLong())

                        if (k < letterBooleans.size - 1)
                            vibrationObjects.add(gap.toLong())
                    }
                    if (j < word.length - 1)
                        vibrationObjects.add(letterGap.toLong())
                }
            }
            if (i < words.size - 1)
                vibrationObjects.add(wordGap.toLong())
        }

        return vibrationObjects
    }
}
