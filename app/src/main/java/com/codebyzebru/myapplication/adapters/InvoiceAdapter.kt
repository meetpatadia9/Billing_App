package com.codebyzebru.myapplication.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.codebyzebru.myapplication.databinding.SingleViewInvoicedItemBinding
import com.codebyzebru.myapplication.dataclasses.PurchasedItemDataClass

class InvoiceAdapter(val context: Context, private val list: List<PurchasedItemDataClass>)
    : RecyclerView.Adapter<InvoiceAdapter.InvoiceViewHolder>() {

    class InvoiceViewHolder(val itemBinding: SingleViewInvoicedItemBinding): RecyclerView.ViewHolder(itemBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InvoiceViewHolder {
        val itemBinding = SingleViewInvoicedItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return InvoiceViewHolder(itemBinding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: InvoiceViewHolder, position: Int) {
        holder.apply {
            with(list[position]) {
                itemBinding.txtItem.text = this.pname
                itemBinding.txtQty.text = this.pqty.toString()
                itemBinding.txtRate.text = this.pprice.toString()
                itemBinding.txtAmount.text = this.totalAmt.toString()
            }
        }
    }

}