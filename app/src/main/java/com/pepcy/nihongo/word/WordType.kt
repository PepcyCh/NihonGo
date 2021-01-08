package com.pepcy.nihongo.word

enum class WordType(val value: Int) {
    NONE(0) {
        override fun toString(): String = "???"
        override fun abstractString(): String = ""
    },
    KANJI(1) {
        override fun toString(): String = "漢字"
        override fun abstractString(): String = "漢字"
    },
    KANA(2) {
        override fun toString(): String = "仮名"
        override fun abstractString(): String = "仮名"
    },
    MEI(3) {
        override fun toString(): String = "名詞"
        override fun abstractString(): String = "名"
    },
    DOU_5(4) {
        override fun toString(): String = "五段活用動詞"
        override fun abstractString(): String = "五段動"
    },
    DOU_4(5) {
        override fun toString(): String = "（古）四段活用動詞"
        override fun abstractString(): String = "四段動"
    },
    DOU_1_I(6) {
        override fun toString(): String = "上一段活用動詞"
        override fun abstractString(): String = "一段動"
    },
    DOU_1_E(7) {
        override fun toString(): String = "下一段活用動詞"
        override fun abstractString(): String = "一段動"
    },
    DOU_2_I(8) {
        override fun toString(): String = "（古）上二段活用動詞"
        override fun abstractString(): String = "二段動"
    },
    DOU_2_E(9) {
        override fun toString(): String = "（古）下二段活用動詞"
        override fun abstractString(): String = "二段動"
    },
    DOU_SA(10) {
        override fun toString(): String = "サ行変格活用動詞"
        override fun abstractString(): String = "サ変動"
    },
    DOU_KA(11) {
        override fun toString(): String = "カ行変格活用動詞"
        override fun abstractString(): String = "カ変動"
    },
    KEIYOU(12) {
        override fun toString(): String = "形容詞"
        override fun abstractString(): String = "形"
    },
    KEIDOU(13) {
        override fun toString(): String = "形容動詞"
        override fun abstractString(): String = "形動"
    },
    FUKU(14) {
        override fun toString(): String = "副詞"
        override fun abstractString(): String = "副"
    },
    KAN(15) {
        override fun toString(): String = "感動詞"
        override fun abstractString(): String = "感"
    },
    JODOU(16) {
        override fun toString(): String = "助動詞"
        override fun abstractString(): String = "助動"
    },
    JO_KAKU(17) {
        override fun toString(): String = "格助詞"
        override fun abstractString(): String = "格助"
    },
    JO_KAKARI(18) {
        override fun toString(): String = "係助詞"
        override fun abstractString(): String = "係助"
    },
    JO_SYUU(19) {
        override fun toString(): String = "終助詞"
        override fun abstractString(): String = "終助"
    },
    JO_SETSU(20) {
        override fun toString(): String = "接続助詞"
        override fun abstractString(): String = "接助"
    },
    JO_KANTOU(21) {
        override fun toString(): String = "間投助詞"
        override fun abstractString(): String = "間助"
    },
    JO_FUKU(22) {
        override fun toString(): String = "副助詞"
        override fun abstractString(): String = "副助"
    },
    DAI(23) {
        override fun toString(): String = "代名詞"
        override fun abstractString(): String = "代"
    },
    SETSUBI(24) {
        override fun toString(): String = "接尾語"
        override fun abstractString(): String = "接尾"
    },
    SETTOU(25) {
        override fun toString(): String = "接頭語"
        override fun abstractString(): String = "接頭"
    },
    MEI_KEIDOU(26) {
        override fun toString(): String = "名詞・形容動詞"
        override fun abstractString(): String = "名・形動"
    },
    HUKU_KEIDOU(27) {
        override fun toString(): String = "副詞・形容動詞"
        override fun abstractString(): String = "副・形動"
    };

    abstract fun abstractString(): String

    companion object {
        fun fromString(str: String): WordType {
            if (str[0] == '動') {
                when (str[2]) {
                    '五' -> return DOU_5
                    '四' -> return DOU_4
                    '上' -> return if (str[3] == '一') DOU_1_I else DOU_2_I
                    '下' -> return if (str[3] == '一') DOU_1_E else DOU_2_E
                    '変' -> return if (str[1] == 'カ') DOU_KA else DOU_SA
                }
            }
            return when (str) {
                "音" -> KANJI
                "形" -> KEIYOU
                "形動" -> KEIDOU
                "感" -> KAN
                "助動" -> JODOU
                "格助" -> JO_KAKU
                "係助" -> JO_KAKARI
                "接助" -> JO_SETSU
                "終助" -> JO_SYUU
                "副助" -> JO_FUKU
                "間助" -> JO_KANTOU
                "代" -> DAI
                "接尾" -> SETSUBI
                "接頭" -> SETTOU
                "名・形動" -> MEI_KEIDOU
                "名・形動ナリ" -> MEI_KEIDOU
                "副・形動" -> HUKU_KEIDOU
                "名" -> MEI
                "副" -> FUKU
                else -> NONE
            }
        }

        private val VALUES = values();
        fun fromInt(value: Int) = VALUES.firstOrNull { it.value == value }
    }
}