package com.pepcy.nihongo.initwords

import com.pepcy.nihongo.word.*

data class InitWords(
    val words: List<InitWord>
)

data class InitWord(
    val title: String,
    val kana: String,
    val suru: Boolean,
    val isKanji: Boolean,
    val items: List<InitWordItem>,
) {
    fun getWord(): Word {
        val word = Word()
        word.title = title
        word.kana = kana
        word.suru = suru
        word.isKanji = isKanji
        for (item in items) {
            val itemW = WordItem(WordType.fromInt(item.type)!!)
            for (meaning in item.meanings) {
                val meaningW = WordMeaning(meaning.indexMajor, meaning.indexMinor, meaning.meaning)
                for (example in meaning.examples) {
                    meaningW.examples.add(example)
                }
                itemW.meanings.add(meaningW)
            }
            for (extra in item.extra) {
                itemW.extra.add(WordExtraInfo(extra.key, extra.value))
            }
            word.items.add(itemW)
        }
        return word
    }
}

data class InitWordItem(
    val type: Int,
    val meanings: List<InitWordMeaning>,
    val extra: List<InitWordExtra>,
)

data class InitWordMeaning(
    val indexMajor: Int,
    val indexMinor: Int,
    val meaning: String,
    val examples: List<String>
)

data class InitWordExtra(
    val key: String,
    val value: String,
)