package com.pepcy.nihongo.fakekana

class KanaUtil {
    companion object {
        fun isHiraGana(ch: Char): Boolean = ch.toString() in HIRAGANA
        fun isSmallHiraGana(ch: Char): Boolean {
            return ch in arrayOf('ぁ', 'ぃ', 'ぅ', 'ぇ', 'ぉ', 'ゃ', 'ゅ', 'ょ', 'っ', 'ゖ')
        }
        fun isNormalHiraGana(ch: Char): Boolean = isHiraGana(ch) && !isSmallHiraGana(ch)
        fun isHiraGana(str: String): Boolean {
            return when (str.length) {
                1 -> str in HIRAGANA
                2 -> {
                    val ao = str[0] in arrayOf('き', 'ぎ', 'し', 'じ', 'ち', 'ぢ', 'に', 'み', 'ひ', 'ぴ', 'び', 'り') &&
                            str[1] in arrayOf('ゃ', 'ゅ', 'ょ')
                    val wv = str[0] in arrayOf('う', 'ゔ') && str[1] in arrayOf('ぁ', 'ぃ', 'ぅ', 'ぇ', 'ぉ')
                    val other = str == "てぃ"
                    ao || wv || other
                }
                else -> false
            }
        }

        fun isKataKana(ch: Char): Boolean = ch.toString() in KATAKANA
        fun isSmallKataKana(ch: Char): Boolean {
            return ch in arrayOf('ァ', 'ィ', 'ゥ', 'ェ', 'ォ', 'ャ', 'ュ', 'ョ', 'ッ', 'ヶ')
        }
        fun isNormalKataKana(ch: Char): Boolean = isKataKana(ch) && !isSmallKataKana(ch)
        fun isKataKana(str: String): Boolean {
            return when (str.length) {
                1 -> str in KATAKANA
                2 -> {
                    val ao = str[0] in arrayOf('キ', 'ギ', 'シ', 'ジ', 'チ', 'ヂ', 'ニ', 'ミ', 'ヒ', 'ピ', 'ビ', 'リ') &&
                            str[1] in arrayOf('ャ', 'ュ', 'ョ')
                    val wv = str[0] in arrayOf('ウ', 'ヴ') && str[1] in arrayOf('ァ', 'ィ', 'ゥ', 'ェ', 'ォ')
                    val other = str == "ティ"
                    ao || wv || other
                }
                else -> false
            }
        }

        fun isKana(ch: Char): Boolean = isHiraGana(ch) || isKataKana(ch)
        fun isSmallKana(ch: Char): Boolean = isSmallHiraGana(ch) || isSmallKataKana(ch)
        fun isNormalKana(ch: Char): Boolean = isNormalHiraGana(ch) || isNormalKataKana(ch)
        fun isKana(str: String): Boolean = isHiraGana(str) || isKataKana(str)

        fun splitKanaString(str: String): ArrayList<String> {
            var i = 0
            val kanaList = ArrayList<String>()
            while (i < str.length) {
                if (!isKana(str[i]) && (i == 0 || str[i] != 'ー')) {
                    return arrayListOf(str)
                }

                if (i + 1 < str.length && isNormalKana(str[i]) && isSmallKana(str[i + 1]) &&
                        str[i + 1] !in arrayOf('っ', 'ッ', 'ゖ', 'ヶ')) {
                    val kana = str.substring(i, i + 2)
                    if (isKana(kana)) {
                        kanaList.add(kana)
                        ++i
                    } else {
                        return arrayListOf(str)
                    }
                } else {
                    kanaList.add(str[i].toString())
                }

                ++i
            }
            return kanaList
        }
        fun kanaToRomajiList(str: String): ArrayList<String> {
            val split = splitKanaString(str)
            val romajiList = ArrayList<String>()
            var lastAdded = ""
            for (kana in split) {
                if (kana.length !in 1..2) {
                    return arrayListOf(str)
                }

                if (kana[0] == 'ー') {
                    if (lastAdded.isEmpty()) {
                        return arrayListOf(str)
                    } else {
                        val ch = lastAdded.last()
                        lastAdded = when (ch) {
                            'e' -> 'i'
                            'o' -> 'u'
                            else -> ch
                        }.toString()
                        romajiList.add(lastAdded)
                    }
                } else {
                    val index = if (kana[0].toString() in HIRAGANA) {
                        HIRAGANA.indexOf(kana[0].toString())
                    } else {
                        KATAKANA.indexOf(kana[0].toString())
                    }
                    val romaji = ROMAJI[index]
                    if (kana.length == 1) {
                        lastAdded = romaji
                        romajiList.add(lastAdded)
                    } else {
                        val romaji2 = ROMAJI[if (kana[1].toString() in HIRAGANA) {
                            HIRAGANA.indexOf(kana[1].toString())
                        } else {
                            KATAKANA.indexOf(kana[1].toString())
                        }]
                        lastAdded = romaji[0] + romaji2.substring(1)
                        romajiList.add(lastAdded)
                    }
                }
            }
            return romajiList
        }
        fun kanaToRomaji(str: String): String {
            val list = kanaToRomajiList(str)
            return list.joinToString("")
        }

        fun isValidRomaji(str: String): Boolean {
            for (ch in str) {
                if (!ch.isLowerCase()) {
                    return false
                }
            }

            var i = 0
            while (i < str.length) {
                when (str[i]) {
                    'y' -> {
                        if (i + 1 >= str.length || str[i + 1] !in arrayOf('a', 'u', 'o')) {
                            return false
                        }
                        ++i
                    }
                    'w' -> {
                        if (i + 1 >= str.length || str[i + 1] !in arrayOf('a', 'i', 'e', 'o')) {
                            return false
                        }
                        ++i
                    }
                    'n' -> {
                        if (i + 1 >= str.length || str[i + 1] !in arrayOf('a', 'i', 'u', 'e', 'o', 'n', 'y')) {
                            return false
                        }
                        if (str[i + 1] != 'y') {
                            ++i
                        }
                    }
                    'l' -> {
                        if (i + 1 >= str.length) {
                            return false
                        }
                    }
                    in arrayOf('k', 'g', 's', 'z', 't', 'd', 'n', 'm', 'h', 'p', 'b', 'r') -> {
                        if (str[i + 1] !in arrayOf('a', 'i', 'u', 'e', 'o', 'y')) {
                            return false
                        }
                        if (str[i + 1] != 'y') {
                            ++i
                        }
                    }
                    !in arrayOf('a', 'i', 'u', 'e', 'o') -> {
                        return false
                    }
                }
                ++i
            }
            return true
        }
        fun splitRomajiString(str: String): ArrayList<String> {
            if (!isValidRomaji(str)) {
                return arrayListOf(str)
            }

            var i = 0
            val list = ArrayList<String>()
            var temp = ""
            while (i < str.length) {
                when {
                    str[i] in arrayOf('a', 'i', 'u', 'e', 'o') -> {
                        list.add(temp + str[i].toString())
                        temp = ""
                        ++i
                    }
                    str[i] == 'l' -> {
                        temp = "l"
                        ++i
                    }
                    else -> {
                        if (str[i + 1] == 'y') {
                            list.add(str[i] + "i")
                            temp = "l"
                            ++i
                        } else {
                            list.add(temp + str.substring(i, i + 2))
                            temp = ""
                            i += 2
                        }
                    }
                }
            }

            return list
        }
        fun romajiListToHiraGana(list: ArrayList<String>): String {
            val sb = StringBuffer()
            for (romaji in list) {
                sb.append(HIRAGANA[ROMAJI.indexOf(romaji)])
            }
            return sb.toString()
        }
        fun romajiToHiraGana(str: String): String = romajiListToHiraGana(splitRomajiString(str))

        private val HIRAGANA = arrayOf(
                "あ", "い", "う", "え", "お",
                "ぁ", "ぃ", "ぅ", "ぇ", "ぉ",
                "か", "き", "く", "け", "こ",
                "が", "ぎ", "ぐ", "げ", "ご",
                "さ", "し", "す", "せ", "そ",
                "ざ", "じ", "ず", "ぜ", "ぞ",
                "た", "ち", "つ", "て", "と",
                "だ", "ぢ", "づ", "で", "ど",
                "な", "に", "ぬ", "ね", "の",
                "ま", "み", "む", "め", "も",
                "は", "ひ", "ふ", "へ", "ほ",
                "ぱ", "ぴ", "ぷ", "ぺ", "ぽ",
                "ば", "び", "ぶ", "べ", "ぼ",
                "や", "ゆ", "よ",
                "ゃ", "ゅ", "ょ",
                "ら", "り", "る", "れ", "ろ",
                "わ", "ゐ", "ゑ", "を",
                "ん", "っ", "ゖ",
        )
        private val KATAKANA = arrayOf(
                "ア", "イ", "ウ", "エ", "オ",
                "ァ", "ィ", "ゥ", "ェ", "ォ",
                "カ", "キ", "ク", "ケ", "コ",
                "ガ", "ギ", "グ", "ゲ", "ゴ",
                "サ", "シ", "ス", "セ", "ソ",
                "ザ", "ジ", "ズ", "ゼ", "ゾ",
                "タ", "チ", "ツ", "テ", "ト",
                "ダ", "ヂ", "ヅ", "デ", "ド",
                "ナ", "ニ", "ヌ", "ネ", "ノ",
                "マ", "ミ", "ム", "メ", "モ",
                "ハ", "ヒ", "フ", "ヘ", "ホ",
                "パ", "ピ", "プ", "ペ", "ポ",
                "バ", "ビ", "ブ", "ベ", "ボ",
                "ヤ", "ユ", "ヨ",
                "ャ", "ュ", "ョ",
                "ラ", "リ", "ル", "レ", "ロ",
                "ワ", "ヰ", "ヱ", "ヲ",
                "ン", "ッ", "ヶ",
        )
        private val ROMAJI = arrayOf(
                "a", "i", "u", "e", "o",
                "la", "li", "lu", "le", "lo",
                "ka", "ki", "ku", "ke", "ko",
                "ga", "gi", "gu", "ge", "go",
                "sa", "si", "su", "se", "so",
                "za", "zi", "zu", "ze", "zo",
                "ta", "ti", "tu", "te", "to",
                "da", "di", "du", "de", "do",
                "na", "ni", "nu", "ne", "no",
                "ma", "mi", "mu", "me", "mo",
                "ha", "hi", "hu", "he", "ho",
                "pa", "pi", "pu", "pe", "po",
                "ba", "bi", "bu", "be", "bo",
                "ya", "yu", "yo",
                "lya", "lyu", "lyo",
                "ra", "ri", "ru", "re", "ro",
                "wa", "wi", "we", "wo",
                "nn", "ltu", "lka"
        )
    }
}