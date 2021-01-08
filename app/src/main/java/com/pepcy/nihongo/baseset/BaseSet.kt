package com.pepcy.nihongo.baseset

import com.pepcy.nihongo.word.WordLabel

class BaseSet {
    companion object {
        fun getSet(name: String): ArrayList<WordLabel> {
            return when {
                BASE_SET.containsKey(name) -> {
                    BASE_SET[name]!!.toCollection(ArrayList())
                }
                name == "jp1" -> {
                    BaseSetJp1.SET
                }
                name == "jp2" -> {
                    BaseSetJp2.SET
                }
                name == "jp3" -> {
                    BaseSetJp3.SET
                }
                name == "jp4" -> {
                    BaseSetJp4.SET
                }
                else -> {
                    arrayListOf()
                }
            }
        }

        private val BASE_SET: HashMap<String, Array<WordLabel>> = hashMapOf(
            "empty" to arrayOf(),
            "test" to arrayOf(
                WordLabel("春", "はる", false),
                WordLabel("夏", "なつ", false),
                WordLabel("秋", "あき", false),
                WordLabel("冬", "ふゆ", false),
                WordLabel("行く", "いく", false),
                WordLabel("食べる", "たべる", false),
                WordLabel("書く", "かく", false),
                WordLabel("熱い", "あつい", false),
            ),
        )
    }
}