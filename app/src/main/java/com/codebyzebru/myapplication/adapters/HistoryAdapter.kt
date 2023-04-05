package com.codebyzebru.myapplication.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.codebyzebru.myapplication.R
import com.codebyzebru.myapplication.dataclasses.HistoryDataClass

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

class HistoryAdapter(val context: Context, private val billList: List<HistoryDataClass>): RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    class HistoryViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val billNo: TextView = itemView.findViewById(R.id.singleView_history_billNo)
        val buyer: TextView = itemView.findViewById(R.id.singleView_history_buyer)
        val billAmount: TextView = itemView.findViewById(R.id.singleView_history_billAmount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.single_view_history, parent, false)
        return HistoryViewHolder(view)
    }

    override fun getItemCount(): Int {
        return billList.size
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.apply {
            billNo.text = billList[position].billNo
            buyer.text = billList[position].buyer
            billAmount.text = billList[position].billTotal.toString()

            //  Animation on Recyclerview, when it loads new ItemView
            itemView.animation = AnimationUtils.loadAnimation(holder.itemView.context, R.anim.anim_recyclerview)
        }
    }
}