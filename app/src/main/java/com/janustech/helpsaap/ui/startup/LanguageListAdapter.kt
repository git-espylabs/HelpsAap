package com.janustech.helpsaap.ui.startup

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.databinding.DataBindingUtil
import androidx.databinding.library.baseAdapters.BR
import androidx.recyclerview.widget.RecyclerView
import com.janustech.helpsaap.R
import com.janustech.helpsaap.databinding.ItLanguageBinding
import com.janustech.helpsaap.model.LanguageDataModel


class LanguageListAdapter internal constructor(
    private val context: Context,
    private val mData: List<LanguageDataModel>,
    private var selectedPosition: Int,
    val languageSelectListener: (model: LanguageDataModel) -> Unit
): RecyclerView.Adapter<LanguageListAdapter.ViewHolder>() {
    /*private var selectedPosition = -1*/
    inner class ViewHolder(itLanguageBinding: ItLanguageBinding) : RecyclerView.ViewHolder(itLanguageBinding.root) {
        private var itLanguageBinding: ItLanguageBinding? = null

        fun bind(
            obj: Any?,
            languageSelectListener: (model: LanguageDataModel) -> Unit,
            position: Int

        ) {
            itLanguageBinding?.setVariable(BR.model, obj)
            itLanguageBinding?.executePendingBindings()

            itLanguageBinding?.root?.setOnClickListener {
                val animation: Animation = AnimationUtils.loadAnimation(
                    context,
                    R.anim.click
                )
                it?.startAnimation(animation)
                languageSelectListener(obj as LanguageDataModel)
            }


            if ((selectedPosition == -1 && position == 0))
                itLanguageBinding?.rdBtn?.isChecked = true
            else
                itLanguageBinding?.rdBtn?.isChecked = selectedPosition == position


            itLanguageBinding?.rdBtn?.setOnClickListener {
                selectedPosition= adapterPosition
                val animation: Animation = AnimationUtils.loadAnimation(
                    context,
                    R.anim.click
                )
                it?.startAnimation(animation)
                languageSelectListener(obj as LanguageDataModel)
                notifyDataSetChanged()
            }



        }

        init {
            this.itLanguageBinding = itLanguageBinding
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItLanguageBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.it_language, parent, false
        )

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mData[position], languageSelectListener,position)

    }

    override fun getItemCount(): Int {
        return mData.size
    }
}