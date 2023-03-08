package com.codebyzebru.myapplication.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.codebyzebru.myapplication.R
import com.codebyzebru.myapplication.activities.HomeActivity
import kotlinx.android.synthetic.main.fragment_bill.*
import kotlinx.android.synthetic.main.fragment_bill.view.*
import java.text.SimpleDateFormat
import java.util.*

class BillFragment : Fragment() {

    private lateinit var popupView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        /*
                when the fragment come in picture, respected navigation `menu item` must be highlighted
                and `title` of the activity must be sync with fragment.
        */
        (activity as HomeActivity).naviView.menu.findItem(R.id.drawer_item_bill).isChecked = true
        (activity as HomeActivity).title = "Create Bill"

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bill, container, false)
    }

    @SuppressLint("InflateParams")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.billFrag_txt_date?.text = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date()).toString()
        /*
        OR YOU CAN USE THIS METHOD ALSO TO GET LOCAL DATE, BY GETTING INSTANCE OF CALENDAR

        val calendar = Calendar.getInstance()

        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH)        //  month start from 0. (ex. Jan-0, Feb-1,...,Dec-11)
        val year = calendar.get(Calendar.YEAR)

        view.billFrag_txt_date.text = day.toString() + "-" + (month+1).toString() + "-" + year.toString()
        */

        view.findViewById<Button>(R.id.billFrag_btn_addItem).setOnClickListener {
            //  subclass of `Dialog`
            val builder = AlertDialog.Builder(requireContext())
            //  Instantiates a layout XML file into its corresponding `View` objects
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

        view.btnGenerateBill.setOnClickListener {
            billFrag_autoTxt_name.text.clear()
            billFrag_edtxt_email.text.clear()
            billFrag_edtxt_companyName.text.clear()
            billFrag_edtxt_companyAddr.text.clear()
            billFrag_edtxt_contact.text.clear()
        }
    }

}