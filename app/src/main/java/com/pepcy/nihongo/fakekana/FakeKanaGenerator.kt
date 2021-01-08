package com.pepcy.nihongo.fakekana

import kotlin.collections.ArrayList

class FakeKanaGenerator {
    companion object {
        fun gen(str: String): String {
            val romajiStr = KanaUtil.kanaToRomaji(str)
            val rules = GEN_RULES.clone()
            rules.shuffle()
            for (rule in rules) {
                val indices = ArrayList<Int>()
                var ind = romajiStr.indexOf(rule.src)
                while (ind != -1) {
                    if (rule.check(romajiStr, ind)) {
                        indices.add(ind)
                    }
                    ind = romajiStr.indexOf(rule.src, ind + 1)
                }
                if (indices.isEmpty()) {
                    continue
                }

                ind = indices[(0 until indices.size).random()]
                val newRomajiStr = rule.apply(romajiStr, ind)
                return KanaUtil.romajiToHiraGana(newRomajiStr)
            }
            return str
        }

        private val GEN_RULES = arrayOf(
                FakeKanaRule("t", "d"),
                FakeKanaRule("d", "t"),
                FakeKanaRule("k", "g"),
                FakeKanaRule("g", "k"),
                FakeKanaRule("s", "z"),
                FakeKanaRule("z", "s"),
                FakeKanaRule("h", "p"),
                FakeKanaRule("p", "h"),
                FakeKanaRule("h", "b"),
                FakeKanaRule("b", "h"),
                FakeKanaRule("p", "b"),
                FakeKanaRule("b", "p"),
                FakeKanaRule("a", "aa", { true }, { it.isEmpty() || it[0] !in arrayOf('a', 'i', 'u', 'e', 'o') }),
                FakeKanaRule("aa", "a"),
                FakeKanaRule("i", "ii",
                        { it.isEmpty() || it.last() !in arrayOf('w', 'y') },
                        { it.isEmpty() || it[0] !in arrayOf('a', 'i', 'u', 'e', 'o') }),
                FakeKanaRule("ii", "i", { it.isEmpty() || it.last() !in arrayOf('w', 'y') }),
                FakeKanaRule("u", "uu",
                        { it.isEmpty() || it.last() != 'w' },
                        { it.isEmpty() || it[0] !in arrayOf('a', 'i', 'u', 'e', 'o') }),
                FakeKanaRule("uu", "u", { it.isEmpty() || it.last() != 'w' }),
                FakeKanaRule("e", "ei",
                        { it.isEmpty() || it.last() !in arrayOf('w', 'y') },
                        { it.isEmpty() || it[0] !in arrayOf('a', 'i', 'u', 'e', 'o') }),
                FakeKanaRule("ei", "e", { it.isEmpty() || it.last() !in arrayOf('w', 'y') }),
                FakeKanaRule("ei", "enn", { true },
                        { it.isEmpty() || it[0] !in arrayOf('a', 'i', 'u', 'e', 'o') }),
                FakeKanaRule("enn", "ei"),
                FakeKanaRule("o", "ou",
                        { it.isEmpty() || it.last() != 'w' },
                        { it.isEmpty() || it[0] !in arrayOf('a', 'i', 'u', 'e', 'o') }),
                FakeKanaRule("ou", "o", { it.isEmpty() || it.last() != 'w' }),
                FakeKanaRule("o", "oo",
                        { it.isEmpty() || it.last() != 'w' },
                        { it.isEmpty() || it[0] !in arrayOf('a', 'i', 'u', 'e', 'o') }),
                FakeKanaRule("oo", "o", { it.isEmpty() || it.last() != 'w' }),
                FakeKanaRule("atu", "a"),
                FakeKanaRule("a", "atu"),
                FakeKanaRule("iku", "i", { it.isEmpty() || it.last() !in arrayOf('w', 'y') }),
                FakeKanaRule("i", "iku", { it.isEmpty() || it.last() !in arrayOf('w', 'y') }),
                FakeKanaRule("iti", "i", { it.isEmpty() || it.last() !in arrayOf('w', 'y') }),
                FakeKanaRule("i", "iti", { it.isEmpty() || it.last() !in arrayOf('w', 'y') }),
                FakeKanaRule("oku", "o", { it.isEmpty() || it.last() != 'w' }),
                FakeKanaRule("o", "oku", { it.isEmpty() || it.last() != 'w' }),
                FakeKanaRule("uku", "u", { it.isEmpty() || it.last() != 'w' }),
                FakeKanaRule("u", "uku", { it.isEmpty() || it.last() != 'w' }),
                FakeKanaRule("o", "u", { it.isEmpty() || it.last() != 'w' }),
                FakeKanaRule("u", "o", { it.isEmpty() || it.last() != 'w' }),
                FakeKanaRule("a", "o", { it.isEmpty() || it.last() != 'w' }),
                FakeKanaRule("o", "a"),
                FakeKanaRule("a", "e", { it.isEmpty() || it.last() !in arrayOf('w', 'y') }),
                FakeKanaRule("e", "a"),
                FakeKanaRule("ltu", "tu"),
                FakeKanaRule("tu", "ltu", { it.isEmpty() || it.last() != 'l'} ),
                FakeKanaRule("ltu", ""),
        )
    }
}