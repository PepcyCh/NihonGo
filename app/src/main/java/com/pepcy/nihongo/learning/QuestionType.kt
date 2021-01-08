package com.pepcy.nihongo.learning

enum class QuestionType(val value: Int) {
    KANJI_TO_MEANING(0),
    MEANING_TO_KANJI(1),
    KANA_TO_MEANING(2),
    MEANING_TO_KANA(3),
    KANJI_TO_KANA(4);

    companion object {
        private val VALUES = values();
        fun fromInt(value: Int) = VALUES.firstOrNull { it.value == value }
    }
}