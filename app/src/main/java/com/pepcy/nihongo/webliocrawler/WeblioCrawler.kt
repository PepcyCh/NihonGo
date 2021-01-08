package com.pepcy.nihongo.webliocrawler

import android.util.Log
import java.lang.StringBuilder
import java.io.IOException

import org.jsoup.Jsoup
import org.jsoup.select.Elements

import com.pepcy.nihongo.word.*
import java.util.*
import kotlin.collections.ArrayList

class WeblioCrawler(private val parseRef: Boolean = true) {
    private var lastIndexMajor = 0
    private var expectedLabel: WordLabel? = null

    companion object {
        private val BLACK_LIST = arrayOf(
            "による連作長編小説", "による彫刻作品", "気象衛星の愛称", "の自伝的長編小説", "の交響曲", "の弦楽四重奏曲",
            "の絵画", "先生。親方。長。", "の処女小説"
        )
    }

    private fun getLengthOfUtf8(b: Byte): Int {
        if (b >= 0) {
            return 1
        }
        for (i in 1..7) {
            if ((b.toInt() and (1 shl (7 - i))) == 0) {
                return i
            }
        }
        return 8
    }

    private fun stringToUtf8(str: String): String {
        val byte2char = "0123456789ABCDEF".toCharArray()
        val bytes = str.toByteArray(Charsets.UTF_8)
        val utf8Str = StringBuilder()
        var i = 0
        while (i < bytes.size) {
            if (bytes[i] >= 0) {
                utf8Str.append(bytes[i].toChar())
                ++i
            } else {
                val len = getLengthOfUtf8(bytes[i])
                for (j in 0 until len) {
                    utf8Str.append('%')
                    val ind = if (bytes[i + j] < 0) bytes[i + j] + 256 else bytes[i + j].toInt()
                    utf8Str.append(byte2char[ind shr 4])
                    utf8Str.append(byte2char[ind and 0xf])
                }
                i += len
            }
        }
        return utf8Str.toString()
    }

    private fun checkIfNeedToBeIgnored(word: Word): Boolean {
        for (item in word.items) {
            for (meaning in item.meanings) {
                for (blackStr in BLACK_LIST) {
                    if (meaning.meaning.contains(blackStr)) {
                        return true
                    }
                }
            }
        }
        return false
    }

    private fun parseReference(str: String): String {
        if (!parseRef) {
            return str
        }
        val numEndIndex = str.indexOf('」')
        if (numEndIndex == -1) {
            return str
        }
        var numBeginIndex = numEndIndex - 1
        while (numBeginIndex >= 1 && str[numBeginIndex - 1].isDigit()) {
            --numBeginIndex
        }
        val refIndex: Int
        try {
            refIndex = str.substring(numBeginIndex, numEndIndex).toInt()
        } catch (e: NumberFormatException) {
            return str
        }
        val refStr = str.substring(1, numBeginIndex)
        val words = WeblioCrawler(false).queryWord(refStr)
        if (words.isEmpty()) {
            return str
        }
        var newMeaning = str
        for (meaning in words[0].items[0].meanings) {
            if (meaning.indexMajor == refIndex) {
                newMeaning += meaning.meaning
            }
        }
        return newMeaning
    }

    private fun parseExamples(str: String, wordMeaning: WordMeaning): String {
        val leftIndices = ArrayList<Int>()
        val rightIndices = ArrayList<Int>()

        var temp = 0
        do {
            val ind = str.indexOf('「', temp)
            if (ind < 0) {
                break
            }
            leftIndices.add(ind)
            temp = ind + 1
        } while (temp < str.length)
        temp = 0
        do {
            val ind = str.indexOf('」', temp)
            if (ind < 0) {
                break
            }
            rightIndices.add(ind)
            temp = ind + 1
        } while (temp < str.length)

        var last = str.indexOf('〈')
        if (last == 0) {
            return str
        }
        if (last == -1) {
            last = str.length
        }

        val examplesTemp = ArrayList<String>()
        for (i in leftIndices.size - 1 downTo 0) {
            if (rightIndices[i] + 1 == last) {
                examplesTemp.add(str.substring(leftIndices[i] + 1, rightIndices[i]))
                last = leftIndices[i]
                if (leftIndices[i] >= 5) {
                    val kigo = str.substring(leftIndices[i] - 5, leftIndices[i])
                    if (kigo == "《季 春》" || kigo == "《季 夏》" || kigo == "《季 秋》" || kigo == "《季 冬》") {
                        last = leftIndices[i] - 5
                    }
                }
            } else {
                break
            }
        }

        for (i in examplesTemp.size - 1 downTo 0) {
            wordMeaning.examples.add(examplesTemp[i])
        }

        return str.substring(0, last)
    }

    private fun parseWordMeaning(str: String, wordItem: WordItem) {
        if (str.isEmpty()) {
            return
        }

        var startIndex = 0
        var indexMajor = lastIndexMajor
        var indexMinor = 0
        if (str[0] in '㋐'..'㋾') {
            startIndex = 1
            indexMinor = str[0] - '㋐' + 1
        }
        if (str[0] in '０'..'９' || str[0] in '0'..'9') {
            startIndex = str.indexOf(' ') + 1
            indexMajor = ++lastIndexMajor
            if (startIndex == 0) {
                return
            }
        }

        val meaning = WordMeaning(indexMajor, indexMinor, "")
        meaning.meaning = parseExamples(str.substring(startIndex), meaning)

        if (meaning.meaning.endsWith("に同じ。")) {
            meaning.meaning = parseReference(meaning.meaning)
        }

        if (meaning.meaning.isNotEmpty()) {
            wordItem.meanings.add(meaning)
        } else {
            if (wordItem.meanings.isEmpty()) {
                Log.e("Crawler", "meaning = ${meaning.meaning}, example[0] = ${meaning.examples[0]}")
                Log.e("Crawler", "type = ${wordItem.type}")
            }
            for (example in meaning.examples) {
                wordItem.meanings.last().examples.add(example)
            }
        }
    }

    private fun parseKanji(str: String, wordItem: WordItem) {
        val index = str.indexOfFirst { it == '[' || it == '［' }
        if (index == -1) {
            wordItem.extra.add(WordExtraInfo("音読み", str))
        } else {
            wordItem.extra.add(WordExtraInfo("音読み", str.substring(0, index - 1)))
            wordItem.extra.add(WordExtraInfo("訓読み", str.substring(index + 3)))
        }
    }

    private fun parseWord(sgkdjElements: Elements, word: Word) {
        var wordItem: WordItem? = null
        lastIndexMajor = 0
        for (line in sgkdjElements) {
            if (line.tagName() != "p") {
                continue
            }
            val str = line.text().trim { it.isWhitespace() || it in 0xd800.toChar() until 0xf900.toChar() }
            var startIndex = 0
            if (str.isNotEmpty() && (str[0] == '[' || str[0] == '［')) {
                startIndex = str.indexOfFirst { it == ']' || it == '］' }
                var bracketStr = str.substring(1, startIndex)
                if (bracketStr == "常用漢字" || bracketStr == "人名用漢字") {
                    bracketStr = str.substring(startIndex + 3, startIndex + 4)
                    startIndex += 4
                }
                ++startIndex
                val type = WordType.fromString(bracketStr)
                if (type == WordType.KANJI) {
                    word.isKanji = true
                    wordItem = WordItem(type)
                    parseKanji(str.substring(startIndex), wordItem)
                    continue
                } else if (type != WordType.NONE) {
                    if (wordItem != null) {
                        word.items.add(wordItem)
                        lastIndexMajor = 0
                    }
                    wordItem = WordItem(type)
                } else {
                    if (bracketStr == "学習漢字") {
                        continue
                    }
                    if (wordItem == null) {
                        wordItem = WordItem(WordType.MEI)
                    }
                    val extraInfo = WordExtraInfo(str.substring(1, startIndex - 1), str.substring(startIndex).trim())
                    if (!extraInfo.value.startsWith("作品名別項")) {
                        wordItem.extra.add(extraInfo)
                    }
                    continue
                }
            }
            if (wordItem == null) {
                wordItem = WordItem(WordType.NONE)
            }
            var meaningStr = str.substring(startIndex).trim()
            if (meaningStr.startsWith("(スル)")) {
                word.suru = true
                meaningStr = meaningStr.substring(4)
            }
            parseWordMeaning(meaningStr, wordItem)
        }
        if (wordItem!!.type == WordType.NONE && wordItem.meanings.size == 2 &&
                wordItem.meanings[0].meaning.startsWith("五十音図")) {
            wordItem.type = WordType.KANA
        }
        word.items.add(wordItem)
    }

    private fun parseWordReference(word: Word): Word? {
        var isWordRef = false
        var refWordStr = ""
        for (item in word.items) {
            for (meaning in item.meanings) {
                if (meaning.meaning.startsWith("⇒")) {
                    isWordRef = true
                    refWordStr = meaning.meaning.substring(1)
                    break
                }
            }
        }
        if (!isWordRef) {
            return null
        }
        val refWord = WeblioCrawler().queryWord(WordLabel(word.title, refWordStr, word.isKanji)) ?: return null
        refWord.kana = word.kana
        return refWord
    }

    private fun addWords(baseWord: Word, titles: ArrayList<String>, words: ArrayList<Word>) {
        for (title in titles) {
            val word = baseWord.clone()

            if (title.startsWith("&#x")) {
                val code = title.substring(3, 7).toInt(16)
                word.title = code.toChar().toString()
            } else {
                word.title = title
            }
            word.title = title
            val refWord = parseWordReference(word)
            words.add(refWord ?: word)
        }
    }

    private fun handleTitleBracket(title: String): ArrayList<String> {
        val titles = ArrayList<String>()

        val queue = LinkedList<String>(listOf(title))
        while (queue.isNotEmpty()) {
            val top = queue.first()
            queue.remove()
            if (top.contains('（')) {
                val indexLeft = top.indexOf('（')
                val indexRight = top.indexOf('）')
                val left = top.substring(0, indexLeft)
                val mid = top.substring(indexLeft + 1, indexRight)
                val right = top.substring(indexRight + 1)
                queue.add(left + mid + right)
                queue.add(left + right)
            } else {
                titles.add(top)
            }
        }

        return titles
    }

    private fun parseWords(kijiElements: Elements, words: ArrayList<Word>) {
        var word: Word? = null
        val titles = ArrayList<String>()

        for (ele in kijiElements) {
            if (ele.className() == "midashigo") {
                if (word != null && !checkIfNeedToBeIgnored(word)) {
                    addWords(word, titles, words)
                }
                word = Word()
                titles.clear()

                var p = 0
                var title = ele.attr("title")
//                if (title.contains('〔')) {
//                    val index = title.indexOf('〔')
//                    title = title.substring(0, index)
//                }
                title += '／'
                for (i in title.indices) {
                    if (title[i] == '／') {
                        var addedTitle = title.substring(p, i)
                                .replace("‐", "")
                                .replace("・", "")
                                .replace("◦", "")
                        if (addedTitle.contains('（')) {
                            val titlesTemp = handleTitleBracket(addedTitle)
                            titles.addAll(titlesTemp)
                        } else {
                            if (addedTitle.contains('〔')) {
                                val index = addedTitle.indexOf('〔')
                                titles.add(addedTitle.substring(index + 1, addedTitle.indexOf('〕')))
                                addedTitle = addedTitle.substring(0, index)
                            }
                            titles.add(addedTitle)
                        }
                        p = i + 1
                    }
                }

                if (expectedLabel != null && !titles.contains(expectedLabel!!.title)) {
                    word = null
                    continue
                }

                var ind = ele.text().indexOfFirst { it == '【' || it == '〔' }
                if (ind == -1) {
                    ind = ele.text().length
                }
                word.kana = ele.text().substring(0, ind).replace("・", "").replace("‐", "")

                if (expectedLabel != null && expectedLabel!!.kana != word.kana) {
                    word = null
                }
            } else if (ele.className() == "Sgkdj") {
                if (word != null) {
                    parseWord(ele.children(), word)
                }
            }
        }
        if (word != null) {
            addWords(word, titles, words)
        }
    }

    fun queryWord(wordStr: String, hasLabel: Boolean = false): ArrayList<Word> {
        if (!hasLabel) {
            expectedLabel = null
        }

        val url = "https://www.weblio.jp/content/" + stringToUtf8(wordStr)
        try {
            val doc = Jsoup.connect(url).get()
            val barsArr = doc.select("div.pbarTL > a")
            if (barsArr.size == 0) {
                throw UnfoundWordException()
            }
            var index = -1
            for (i in barsArr.indices) {
                if (barsArr[i].text() == "デジタル大辞泉") {
                    index = i
                    break
                }
            }
            if (index == -1) {
                throw UnfoundWordException()
            }
            val kijiArr = doc.select("div.kiji")
            if (kijiArr.size <= index) {
                throw UnfoundWordException()
            }

            val words = ArrayList<Word>()
            parseWords(kijiArr[index].children(), words)
            return words
        } catch (e: UnfoundWordException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return ArrayList()
    }

    fun queryWord(wordLabel: WordLabel): Word? {
        expectedLabel = wordLabel
        val words = queryWord(wordLabel.title, true)
        val filteredWords = words.filter { it.title == wordLabel.title && it.kana == wordLabel.kana && it.isKanji == wordLabel.isKanji }
        if (filteredWords.isEmpty()) {
            return null
        }
        val word = filteredWords[0]
        for (i in 1 until filteredWords.size) {
            word.mergeWith(filteredWords[i])
        }
        return word
    }
}