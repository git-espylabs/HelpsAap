package com.janustech.helpsaap.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.library.baseAdapters.BR
import androidx.recyclerview.widget.RecyclerView
import com.janustech.helpsaap.R
import com.janustech.helpsaap.databinding.ItemNotificationListBinding
import com.janustech.helpsaap.model.NotificationDataModel

class NotificationListAdapter internal constructor(private val mData: List<NotificationDataModel>): RecyclerView.Adapter<NotificationListAdapter.ViewHolder>(){

    inner class ViewHolder(itemNotificationListBinding: ItemNotificationListBinding) : RecyclerView.ViewHolder(itemNotificationListBinding.root) {
        private var itemNotificationListBinding: ItemNotificationListBinding? = null
        fun bind(obj: Any?) {
            itemNotificationListBinding?.setVariable(BR.model, obj)
            itemNotificationListBinding?.executePendingBindings()
        }

        init {
            this.itemNotificationListBinding = itemNotificationListBinding
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemNotificationListBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_notification_list, parent, false
        )

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mData[position])
    }

    override fun getItemCount(): Int {
        return mData.size
    }
}