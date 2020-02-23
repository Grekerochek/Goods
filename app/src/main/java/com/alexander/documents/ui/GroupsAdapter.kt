package com.alexander.documents.ui

import android.view.*
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.group_item.view.*
import com.alexander.documents.R
import com.alexander.documents.entity.Group
import com.bumptech.glide.load.resource.bitmap.CircleCrop

/**
 * author alex
 */
class GroupsAdapter(
    val groupClickListener: (position: Int, Group) -> Unit
) : ListAdapter<Group, RecyclerView.ViewHolder>(DiffCallback()) {

    var groups: MutableList<Group> = mutableListOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.group_item, parent, false)
        return GroupsViewHolder(view)
    }

    override fun getItemCount(): Int = groups.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val group = groups[position]
        with(holder.itemView) {
            setOnClickListener { groupClickListener(holder.adapterPosition, group) }

            groupTitleView.text = group.name
            groupDescriptionView.text = context.getString(
                when (group.isClosed) {
                    0 -> R.string.opened_group
                    1 -> R.string.closed_group
                    else -> R.string.closed_group
                }
            )

            Glide.with(groupImageView.context)
                .load(group.photo_100)
                .apply(
                    RequestOptions().transforms(
                        CenterCrop(),
                        CircleCrop()
                    )
                )
                .into(groupImageView)
        }
    }

    fun selectGroup(position: Int) {
        groups[position].isSelected = 1
        notifyItemChanged(position)
    }

    fun unSelectGroup(position: Int) {
        groups[position].isSelected = 0
        notifyItemChanged(position)
    }

    fun deleteGroups(groupsPositions: List<Int>) {
        groupsPositions
            .sortedDescending()
            .forEach { groupPosition ->
                groups.removeAt(groupPosition)
                notifyItemRemoved(groupPosition)
            }
    }

    class DiffCallback : DiffUtil.ItemCallback<Group>() {
        override fun areItemsTheSame(oldItem: Group, newItem: Group): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Group, newItem: Group): Boolean {
            return oldItem == newItem
        }
    }

    class GroupsViewHolder(override val containerView: View) :
        RecyclerView.ViewHolder(containerView),
        LayoutContainer
}