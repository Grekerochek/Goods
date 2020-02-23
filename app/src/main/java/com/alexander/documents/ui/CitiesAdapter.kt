package com.alexander.documents.ui

import android.view.*
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer
import com.alexander.documents.R
import com.alexander.documents.entity.City
import kotlinx.android.synthetic.main.city_item.view.*

/**
 * author alex
 */
class CitiesAdapter(
    val cityClickListener: (position: Int, City) -> Unit
) : ListAdapter<City, RecyclerView.ViewHolder>(DiffCallback()) {

    var cities: MutableList<City> = mutableListOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.city_item, parent, false)
        return CityViewHolder(view)
    }

    override fun getItemCount(): Int = cities.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val city = cities[position]
        with(holder.itemView) {
            setOnClickListener { cityClickListener(holder.adapterPosition, city) }
            cityTitleView.text = city.title
            cityCheckView.visibility = if (city.isSelected == 1) View.VISIBLE else View.GONE
        }
    }

    fun selectCity(position: Int) {
        cities[position].isSelected = 1
        notifyItemChanged(position)
    }

    fun unSelectCity(position: Int) {
        cities[position].isSelected = 0
        notifyItemChanged(position)
    }

    class DiffCallback : DiffUtil.ItemCallback<City>() {
        override fun areItemsTheSame(oldItem: City, newItem: City): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: City, newItem: City): Boolean {
            return oldItem == newItem
        }
    }

    class CityViewHolder(override val containerView: View) :
        RecyclerView.ViewHolder(containerView),
        LayoutContainer
}