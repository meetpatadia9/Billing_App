package com.codebyzebru.myapplication.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.SubMenu
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.codebyzebru.myapplication.R
import com.codebyzebru.myapplication.activities.HomeActivity


class BillFragment : Fragment() {

    lateinit var popupView: View
    lateinit var subMenu: SubMenu

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        (activity as HomeActivity).naviView.menu.findItem(R.id.drawer_item_bill).isChecked = true
        (activity as HomeActivity).setTitle("Create Bill")
        return inflater.inflate(R.layout.fragment_bill, container, false)
    }


    @SuppressLint("InflateParams")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val customerName = view.findViewById<AutoCompleteTextView>(R.id.billFrag_autoTxt_name)




        view.findViewById<Button>(R.id.billFrag_btn_addItem).setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())


            val inflater = LayoutInflater.from(context)
            popupView = inflater.inflate(R.layout.popup_layout_billfrag_additem, null)

            builder.setView(popupView)

            val alertDialog = builder.create()

            alertDialog.show()

            popupView.findViewById<Button>(R.id.billFrag_addItem_btnAdd).setOnClickListener {
                Toast.makeText(context, "Item Added to Purchase List", Toast.LENGTH_SHORT).show()
                alertDialog.dismiss()
            }

        }

    }

}