package com.es.weatherprojects_tuk.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.es.weatherprojects_tuk.R
import com.example.domain.model.ITEM
import com.google.android.gms.awareness.state.Weather
import org.w3c.dom.Text
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class RecyclerViewAdapter(private var weatherList: List<ITEM>) : RecyclerView.Adapter<RecyclerViewAdapter.WeatherViewHolder>() {

    class WeatherViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tempText: TextView = itemView.findViewById(R.id.detail_temp)
        val dateText: TextView = itemView.findViewById(R.id.detail_tv)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_list_item, parent, false)
        return WeatherViewHolder(view)
    }

    override fun onBindViewHolder(holder: WeatherViewHolder, position: Int) {
        val weather = weatherList[position]
        holder.tempText.text = "${weather.fcstValue}°C"

        val amPmHourFormat = SimpleDateFormat("h:mm", Locale.ENGLISH)
        // 시간 문자열 변환 함수
        fun convertToAmPmFormat(time: String): String {
            val hour = time.substring(0, 2).toInt()
            val minute = time.substring(2, 4)
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.MINUTE, minute.toInt())

            return amPmHourFormat.format(calendar.time)
        }
        holder.dateText.text = convertToAmPmFormat(weather.fcstTime)
    }

    override fun getItemCount() = weatherList.size
}