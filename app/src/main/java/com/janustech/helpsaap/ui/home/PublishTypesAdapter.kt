package com.janustech.helpsaap.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.library.baseAdapters.BR
import androidx.recyclerview.widget.RecyclerView
import com.janustech.helpsaap.R
import com.janustech.helpsaap.databinding.ItemPublishLocationBinding
import com.janustech.helpsaap.model.PublishTypeModel

class PublishTypesAdapter internal constructor(private val context: Context, private val mData: List<PublishTypeModel>, val adsPackageSelectedListener: (packageTypeId: String, packageTypeName: String, packageId: String, packagePrice: String, packageDuration: Int) -> Unit): RecyclerView.Adapter<PublishTypesAdapter.ViewHolder>() {

    inner class ViewHolder(itemPublishLocationBinding: ItemPublishLocationBinding) : RecyclerView.ViewHolder(itemPublishLocationBinding.root) {
        private var itemPublishLocationBinding: ItemPublishLocationBinding? = null
        fun bind(obj: PublishTypeModel) {
            itemPublishLocationBinding?.apply {
                setVariable(BR.model, obj)
                executePendingBindings()

                if (obj.isSelected > 0){
                    when(obj.isSelected){
                        1 -> {
                            setViewSelector(isSelected = true, btnPrice1)
                            setViewSelector(isSelected = false, btnPrice2)
                            setViewSelector(isSelected = false, btnPrice3)
                        }
                        2 -> {
                            setViewSelector(isSelected = true, btnPrice2)
                            setViewSelector(isSelected = false, btnPrice1)
                            setViewSelector(isSelected = false, btnPrice3)
                        }
                        3 -> {
                            setViewSelector(isSelected = true, btnPrice3)
                            setViewSelector(isSelected = false, btnPrice1)
                            setViewSelector(isSelected = false, btnPrice2)
                        }
                    }
                }else{
                    setViewSelector(isSelected = false, btnPrice1)
                    setViewSelector(isSelected = false, btnPrice2)
                    setViewSelector(isSelected = false, btnPrice3)
                }

                btnPrice1.setOnClickListener { v->
                    setViewSelector(isSelected = true, btnPrice1)
                    setViewSelector(isSelected = false, btnPrice2)
                    setViewSelector(isSelected = false, btnPrice3)
                    obj.isSelected = 1

                    adsPackageSelectedListener(obj.publishTypeId, obj.publishTypeIdName, obj.packageList[0].packageId, obj.packageList[0].packagePrice, obj.packageList[0].packageDuration)
                    notifyDataSetChanged()
                }

                btnPrice2.setOnClickListener {
                    setViewSelector(isSelected = true, btnPrice2)
                    setViewSelector(isSelected = false, btnPrice1)
                    setViewSelector(isSelected = false, btnPrice3)
                    obj.isSelected = 2

                    adsPackageSelectedListener(obj.publishTypeId, obj.publishTypeIdName, obj.packageList[1].packageId, obj.packageList[1].packagePrice, obj.packageList[1].packageDuration)
                    notifyDataSetChanged()
                }

                btnPrice3.setOnClickListener {
                    setViewSelector(isSelected = true, btnPrice3)
                    setViewSelector(isSelected = false, btnPrice1)
                    setViewSelector(isSelected = false, btnPrice2)
                    obj.isSelected = 3

                    adsPackageSelectedListener(obj.publishTypeId, obj.publishTypeIdName, obj.packageList[2].packageId, obj.packageList[2].packagePrice, obj.packageList[2].packageDuration)
                    notifyDataSetChanged()
                }
            }
        }

        init {
            this.itemPublishLocationBinding = itemPublishLocationBinding
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemPublishLocationBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_publish_location, parent, false
        )

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mData[position])
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    private fun setViewSelector(isSelected: Boolean, view: TextView){
        if (isSelected){
            view.setTextColor(ContextCompat.getColor(context, R.color.white))
            view.setBackgroundResource(R.drawable.rounded_rect_green_filled)
        }else{
            view.setTextColor(ContextCompat.getColor(context, R.color.black))
            view.setBackgroundResource(R.drawable.rounded_rect_grey_filled)
        }
    }
}