package com.codebyzebru.myapplication.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.codebyzebru.myapplication.R
import com.codebyzebru.myapplication.dataclasses.PartyDataClass

/*
        RECYCLERVIEW ADAPTER:

        1)  AdapterFile requires two parameters
            i.  Context
            ii. ArrayList<DataClass>
        2)  AdapterFile extends RecyclerView.Adapter<AdapterFile.viewHolders>()
            ~ where "viewHolders" is a ViewHolder class   --->  get IDs of view to be implement
                having View as parameters and
                extending RecyclerView.ViewHolder(view)
        3) Now, Implement three methods of RecyclerView
            i.   onCreateViewHolder()  --->  inflate view here
            ii.  getItemCount()        --->  returns the size of DtaClass
            iii. onBindViewHolder()    --->  fetch appropriate data and uses data to fill in ViewHolder's layout
*/

class PartyAdapter(val context: Context, private val partyList: ArrayList<PartyDataClass>) : RecyclerView.Adapter<PartyAdapter.PartyViewHolders>() {

    class PartyViewHolders(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var partyName:TextView = itemView.findViewById(R.id.singleView_party_name)
        var companyName:TextView = itemView.findViewById(R.id.singleView_party_company)
        var totalPurchase:TextView = itemView.findViewById(R.id.singleView_party_totalPurchase)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PartyViewHolders {
        val view  = LayoutInflater.from(parent.context).inflate(R.layout.single_view_party, parent, false)
        return PartyViewHolders(view)
    }

    override fun getItemCount(): Int {
        return partyList.size
    }

    override fun onBindViewHolder(holder: PartyViewHolders, position: Int) {
        //  scope functions `apply`
        holder.apply {
            partyName.text = partyList[position].partyName
            companyName.text = partyList[position].companyName
            totalPurchase.text = partyList[position].totalPurchase.toString()
            //  Animation on Recyclerview, when it loads new ItemView
            itemView.animation = AnimationUtils.loadAnimation(holder.itemView.context, R.anim.anim_recyclerview)
        }
    }

}