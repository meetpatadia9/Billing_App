package com.codebyzebru.myapplication.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.codebyzebru.myapplication.databinding.SingleViewPurchaseItemBinding
import com.codebyzebru.myapplication.dataclasses.PurchasedItemDataClass

class PurchasedItemAdapter(val context: Context, private val itemList: List<PurchasedItemDataClass>, private val totalListener: SubTotalListener, private val listener: OnClick)
    : RecyclerView.Adapter<PurchasedItemAdapter.PurchasedItemViewHolder>() {

    private var myTotal = arrayListOf<Float>()

    /** WITHOUT VIEW-BINDING
    class PurchaseViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val name:TextView = itemView.findViewById(R.id.purchased_pName)
        val qty:TextView = itemView.findViewById(R.id.purchased_pQty)
        val price:TextView = itemView.findViewById(R.id.purchased_pPrice)
        val btnRemove: ImageButton = itemView.findViewById(R.id.btn_remove_purchased_item)
    }
    */

     class PurchasedItemViewHolder(val itemBinding: SingleViewPurchaseItemBinding)
        : RecyclerView.ViewHolder(itemBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PurchasedItemViewHolder {
        /** WITHOUT VIEW-BINDING
        val view = LayoutInflater.from(context).inflate(R.layout.single_view_purchase_item, parent, false)
        return PurchaseViewHolder(view)
         */

        val itemBinding = SingleViewPurchaseItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return PurchasedItemViewHolder(itemBinding)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: PurchasedItemViewHolder, position: Int) {
        holder.apply {
            /** WITHOUT VIEW-BINDING
            val iPrice = itemList[position].pPrice
            val iQty = itemList[position].pQty
            val total = iPrice * iQty

            name.text = itemList[position].pName
            qty.text = itemList[position].pQty
            price.text = total.toString()

            myTotal.add(total)
            Log.d("myTotal", myTotal.toString())

            val sum = myTotal.sum()
            Log.d("sum", sum.toString())
            totalListener.onSubTotalUpdate(sum)
             */

            with(itemList[position]) {
                val iPrice = itemList[position].pPrice
                val iQty = itemList[position].pQty
                val total = iPrice * iQty

                itemBinding.purchasedPName.text = this.pName
                itemBinding.purchasedPAmount.text = this.pPrice.toString()
                itemBinding.purchasedPQty.text = this.pQty.toString()
                itemBinding.purchasedPPrice.text = total.toString()

                myTotal.add(total)
                Log.d("myTotal", myTotal.toString())

                val sum = myTotal.sum()
                Log.d("sum", sum.toString())
                totalListener.onSubTotalUpdate(sum)

                itemBinding.btnRemovePurchasedItem.setOnClickListener {
                    myTotal.clear()
                    totalListener.onSubTotalUpdate(sum)
                    listener.removeItem(this)
                    notifyDataSetChanged()
                }
            }
        }
    }

    interface SubTotalListener {
        fun onSubTotalUpdate(total: Float)
    }

    interface OnClick {
        fun removeItem(purchasedItemDataClass: PurchasedItemDataClass)
    }
}