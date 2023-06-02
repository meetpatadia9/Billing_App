package com.codebyzebru.myapplication.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.codebyzebru.myapplication.R
import com.codebyzebru.myapplication.databinding.SingleViewPartyBinding
import com.codebyzebru.myapplication.dataclasses.ViewPartyDataClass

class PartyAdapter(val context: Context, private val partyList: ArrayList<ViewPartyDataClass>, private val onItemClick: OnItemClick)
    : RecyclerView.Adapter<PartyAdapter.PartyViewHolders>() {

    class PartyViewHolders(val itemBinding: SingleViewPartyBinding): RecyclerView.ViewHolder(itemBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PartyViewHolders {
        val itemBinding = SingleViewPartyBinding.inflate(LayoutInflater.from(context), parent, false)
        return PartyViewHolders(itemBinding)
    }

    override fun getItemCount(): Int {
        return partyList.size
    }

    override fun onBindViewHolder(holder: PartyViewHolders, position: Int) {
        //  scope functions `apply`
        holder.apply {
            with(partyList[position]) {
                itemBinding.singleViewPartyName.text = this.partyName
                if (this.companyName == "") {
                    itemBinding.singleViewPartyCompany.visibility = View.GONE
                }
                else {
                    itemBinding.singleViewPartyCompany.visibility = View.VISIBLE
                    itemBinding.singleViewPartyCompany.text = this.companyName
                }
                itemBinding.singleViewPartyTotalPurchase.text = this.totalPurchase.toString()
            }

            itemView.setOnClickListener {
                onItemClick.onClick(partyList[position])
                notifyDataSetChanged()
            }

            //  Animation on Recyclerview, when it loads new ItemView
            itemView.animation = AnimationUtils.loadAnimation(holder.itemView.context, R.anim.anim_recyclerview)
        }
    }

    interface OnItemClick {
        fun onClick(listDataClass: ViewPartyDataClass)
    }

}