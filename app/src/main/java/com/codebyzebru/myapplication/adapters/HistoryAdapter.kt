package com.codebyzebru.myapplication.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.codebyzebru.myapplication.R
import com.codebyzebru.myapplication.databinding.SingleViewHistoryBinding
import com.codebyzebru.myapplication.dataclasses.HistoryDataClass

class HistoryAdapter(val context: Context, private val billList: List<HistoryDataClass>, private val listener: OnClick)
    : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    class HistoryViewHolder(val itemBinding: SingleViewHistoryBinding) :RecyclerView.ViewHolder(itemBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val itemBinding = SingleViewHistoryBinding.inflate(LayoutInflater.from(context), parent, false)
        return HistoryViewHolder(itemBinding)
    }

    override fun getItemCount(): Int {
        return billList.size
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.apply {
            with(billList[position]) {
                itemBinding.singleViewHistoryBillNo.text = this.billNo
                itemBinding.singleViewHistoryBuyer.text = this.buyer
                itemBinding.singleViewHistoryBillAmount.text = this.billTotal.toString()
            }

            //  Animation on Recyclerview, when it loads new ItemView
            itemView.animation = AnimationUtils.loadAnimation(holder.itemView.context, R.anim.anim_recyclerview)

            itemView.setOnClickListener {
                listener.openInvoice(billList[position])
            }
        }
    }

    interface OnClick {
        fun openInvoice(historyDataClass: HistoryDataClass)
    }
}