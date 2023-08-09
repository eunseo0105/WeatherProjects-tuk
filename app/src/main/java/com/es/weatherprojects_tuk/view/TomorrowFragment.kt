package com.es.weatherprojects_tuk.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.es.weatherprojects_tuk.R
import com.es.weatherprojects_tuk.adapter.RecyclerViewAdapter
import com.es.weatherprojects_tuk.databinding.FragmentTodayBinding
import com.es.weatherprojects_tuk.databinding.FragmentTomorrowBinding
import com.es.weatherprojects_tuk.viewmodel.WeatherViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TomorrowFragment : Fragment() {
    private var _binding: FragmentTomorrowBinding? = null
    private val binding get() = _binding!!

    //MainActivity와 동일한 인스턴스 가져오려면 activityViewModels 사용
    private val viewModel: WeatherViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTomorrowBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("hi","hi")

        binding.recyclerView2.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerView2.adapter = RecyclerViewAdapter(emptyList())

        // ViewModel -  observe
        viewModel.tomorrowData.observe(viewLifecycleOwner) {
            Log.d("it",it.toString())
            (binding.recyclerView2.adapter as RecyclerViewAdapter).updateData(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}