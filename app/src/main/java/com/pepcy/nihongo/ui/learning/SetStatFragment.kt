package com.pepcy.nihongo.ui.learning

import android.graphics.Color
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.github.mikephil.charting.data.*
import com.pepcy.nihongo.R
import com.pepcy.nihongo.database.WordsDatabase
import com.pepcy.nihongo.databinding.FragmentSetStatBinding

class SetStatFragment : Fragment() {

    private lateinit var viewModel: SetStatViewModel

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentSetStatBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_set_stat, container, false)
        binding.lifecycleOwner = this

        val arguments = SetStatFragmentArgs.fromBundle(arguments)
        val viewModelFactory = SetStatViewModelFactory(
            WordsDatabase.getInstance(requireNotNull(this.activity).application).wordsDao(), arguments.setName)
        viewModel = ViewModelProvider(this, viewModelFactory).get(SetStatViewModel::class.java)

        viewModel.initData()

        val wordDialogFragment = WaitingDialogFragment(getString(R.string.waiting))
        wordDialogFragment.show(parentFragmentManager, "waiting_dialog")
        viewModel.initDataDown.observe(viewLifecycleOwner) {
            if (it == true) {
                setChartData(binding)
                wordDialogFragment.dismiss()
            }
        }

        binding.setStatViewModel = viewModel

        return binding.root
    }

    private fun setChartData(binding: FragmentSetStatBinding) {
        setAccuracyLineChartData(binding)
        setLearnLineChartData(binding)
        setTimeLineChartData(binding)
        setRatioChartData(binding)
    }

    private fun setAccuracyLineChartData(binding: FragmentSetStatBinding) {
        val entries = ArrayList<Entry>()
        for ((day, accuracy) in viewModel.setDayData zip viewModel.setAccuracyData) {
            entries.add(Entry(day.toFloat(), accuracy.toFloat()))
        }
        val dataSet = LineDataSet(entries, getString(R.string.chart_accuracy))
        val data = LineData(dataSet)
        binding.setStatAccuracyChart.data = data
        binding.setStatAccuracyChart.invalidate()
    }

    private fun setLearnLineChartData(binding: FragmentSetStatBinding) {
        val entriesNew = ArrayList<Entry>()
        for ((day, count) in viewModel.setDayData zip viewModel.setNewCountData) {
            entriesNew.add(Entry(day.toFloat(), count.toFloat()))
        }
        val dataSetNew = LineDataSet(entriesNew, getString(R.string.chart_new))
        dataSetNew.color = Color.parseColor("#ff0000")

        val entriesReview = ArrayList<Entry>()
        for ((day, count) in viewModel.setDayData zip viewModel.setReviewCountData) {
            entriesReview.add(Entry(day.toFloat(), count.toFloat()))
        }
        val dataSetReview = LineDataSet(entriesNew, getString(R.string.chart_review))
        dataSetReview.color = Color.parseColor("#ffff00")

        val entriesLearn = ArrayList<Entry>()
        for ((day, count) in viewModel.setDayData zip viewModel.setLearnCountData) {
            entriesLearn.add(Entry(day.toFloat(), count.toFloat()))
        }
        val dataSetLearn = LineDataSet(entriesNew, getString(R.string.chart_learn))
        dataSetLearn.color = Color.parseColor("#0000ff")

        val data = LineData(dataSetNew, dataSetReview, dataSetLearn)
        binding.setStatLearnChart.data = data
        binding.setStatLearnChart.invalidate()
    }

    private fun setTimeLineChartData(binding: FragmentSetStatBinding) {
        val entries = ArrayList<Entry>()
        for ((day, time) in viewModel.setDayData zip viewModel.setTimeData) {
            entries.add(Entry(day.toFloat(), time.toFloat()))
        }
        val dataSet = LineDataSet(entries, getString(R.string.chart_accuracy))
        dataSet.color = Color.parseColor("#00ffff")
        val data = LineData(dataSet)
        binding.setStatTimeChart.data = data
        binding.setStatTimeChart.invalidate()
    }

    private fun setRatioChartData(binding: FragmentSetStatBinding) {
        val entries = arrayListOf(
                PieEntry(viewModel.setRatioData[0].toFloat(), getString(R.string.chart_ratio_level0)),
                PieEntry(viewModel.setRatioData[1].toFloat(), getString(R.string.chart_ratio_level1)),
                PieEntry(viewModel.setRatioData[2].toFloat(), getString(R.string.chart_ratio_level2)),
                PieEntry(viewModel.setRatioData[3].toFloat(), getString(R.string.chart_ratio_level3)),
        )
        val dataSet = PieDataSet(entries, "")
        dataSet.colors = listOf(
                Color.parseColor("#a7ffeb"),
                Color.parseColor("#64ffda"),
                Color.parseColor("#1de9b6"),
                Color.parseColor("#00bfa5"),
        )
        val data = PieData(dataSet)
        binding.setStatRatioChart.data = data
        binding.setStatRatioChart.invalidate()
    }
}