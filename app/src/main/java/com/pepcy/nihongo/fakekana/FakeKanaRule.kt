package com.pepcy.nihongo.fakekana

class FakeKanaRule(
        val src: String,
        private val dst: String,
        private val prevCheck: (str: String) -> Boolean = { true },
        private val succCheck: (str: String) -> Boolean = { true },
) {
    fun check(str: String, index: Int): Boolean {
        return prevCheck(str.substring(0, index)) && succCheck(str.substring(index + src.length))
    }

    fun apply(str: String, index: Int): String {
        return str.substring(0, index) + dst + str.substring(index + src.length)
    }

    override fun toString(): String = "Rule { '$src' to '$dst' }"
}