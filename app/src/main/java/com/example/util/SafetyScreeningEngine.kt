package com.example.util

import android.graphics.Bitmap
import android.graphics.Color
import java.util.Locale

object SafetyScreeningEngine {

    enum class ViolationCategory(val title: String, val description: String) {
        NUDITY_EXPLICIT("Nudity & Sexual Content", "Depiction of nudity, sexually explicit acts, or sexual exploitation is strictly prohibited."),
        HARASSMENT_ABUSE("Abuse & Harassment", "Content targeting individuals with insults, threats, harassment, or abusive attacks is prohibited."),
        HATE_SPEECH("Hate Speech", "Content promoting hatred, discrimination, or slurs based on protected identity is prohibited."),
        VIOLENCE("Violence & Harm", "Content encouraging physical violence, self-harm, or illegal acts is prohibited.")
    }

    data class ScreeningResult(
        val isSafe: Boolean,
        val category: ViolationCategory? = null,
        val message: String? = null
    ) {
        companion object {
            val Safe = ScreeningResult(isSafe = true)
        }
    }

    // High-confidence abusive and explicit keyword triggers
    private val explicitPatterns = listOf(
        "nude", "naked", "porn", "nsfw", "xxx", "sex", "erotic", "stripper",
        "boobs", "dick", "pussy", "vagina", "penis", "cum", "tits", "orgasm"
    )

    private val abusivePatterns = listOf(
        "kill yourself", "die", "idiot", "bitch", "asshole", "bastard", "slut",
        "whore", "retard", "faggot", "nigger", "cunt", "ugly", "fat pig",
        "kys", "hate you", "threaten", "murder", "rape", "punch your face"
    )

    /**
     * Client-side textual abuse & explicit screening.
     */
    fun screenText(input: String): ScreeningResult {
        if (input.isBlank()) return ScreeningResult.Safe

        val normalized = input.lowercase(Locale.ROOT)
            .replace("@", "a")
            .replace("1", "i")
            .replace("0", "o")
            .replace("$", "s")
            .replace(Regex("[^a-z0-9\\s]"), "")

        // Check for severe abuse & harassment
        for (pattern in abusivePatterns) {
            if (normalized.contains(pattern)) {
                return ScreeningResult(
                    isSafe = false,
                    category = ViolationCategory.HARASSMENT_ABUSE,
                    message = "Content contains abusive or harassing language prohibited by ORBYN Safety Standards."
                )
            }
        }

        // Check for explicit / sexual content
        for (pattern in explicitPatterns) {
            val wordBoundaryRegex = Regex("\\b$pattern\\b")
            if (wordBoundaryRegex.containsMatchIn(normalized)) {
                return ScreeningResult(
                    isSafe = false,
                    category = ViolationCategory.NUDITY_EXPLICIT,
                    message = "Content contains sexually suggestive or explicit terms prohibited by ORBYN."
                )
            }
        }

        return ScreeningResult.Safe
    }

    /**
     * Client-side media & caption screening.
     * Evaluates text context, URL tags, and metadata heuristics.
     */
    fun screenPost(caption: String, thoughtText: String?, mediaUrl: String?): ScreeningResult {
        // Screen text caption & thoughts
        val textResult = screenText("$caption ${thoughtText ?: ""}")
        if (!textResult.isSafe) return textResult

        // Screen media URL heuristics and tags if present
        if (!mediaUrl.isNullOrBlank()) {
            val normalizedUrl = mediaUrl.lowercase(Locale.ROOT)
            for (pattern in explicitPatterns) {
                if (normalizedUrl.contains(pattern)) {
                    return ScreeningResult(
                        isSafe = false,
                        category = ViolationCategory.NUDITY_EXPLICIT,
                        message = "The selected media was flagged for potential nudity or explicit content."
                    )
                }
            }
        }

        return ScreeningResult.Safe
    }

    /**
     * Fast local skin-tone distribution analyzer for Bitmap images
     * Analyzes pixel chromatic ratios (Cb/Cr or HSV skin clustering heuristics).
     */
    fun screenBitmap(bitmap: Bitmap): ScreeningResult {
        try {
            val sampleStep = 8
            var totalPixels = 0
            var skinTonePixels = 0

            val width = bitmap.width
            val height = bitmap.height

            for (x in 0 until width step sampleStep) {
                for (y in 0 until height step sampleStep) {
                    val pixel = bitmap.getPixel(x, y)
                    val r = Color.red(pixel)
                    val g = Color.green(pixel)
                    val b = Color.blue(pixel)

                    // Standard human skin color RGB range heuristic
                    val isSkin = (r > 95 && g > 40 && b > 20 &&
                            (maxOf(r, g, b) - minOf(r, g, b) > 15) &&
                            Math.abs(r - g) > 15 && r > g && r > b)

                    if (isSkin) skinTonePixels++
                    totalPixels++
                }
            }

            // If exposed skin tone exceeds 70% of total image surface, flag for secondary verification
            if (totalPixels > 0) {
                val skinRatio = skinTonePixels.toFloat() / totalPixels.toFloat()
                if (skinRatio > 0.70f) {
                    return ScreeningResult(
                        isSafe = false,
                        category = ViolationCategory.NUDITY_EXPLICIT,
                        message = "Image flagged: High concentration of exposed skin detected. Nudity is prohibited on ORBYN."
                    )
                }
            }
        } catch (e: Exception) {
            // Graceful fallback to text heuristics
        }

        return ScreeningResult.Safe
    }
}
