package com.codebyzebru.myapplication.fragments

import android.annotation.SuppressLint
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codebyzebru.myapplication.R
import com.codebyzebru.myapplication.activities.HomeActivity
import com.codebyzebru.myapplication.adapters.PurchasedItemAdapter
import com.codebyzebru.myapplication.dataclasses.BillDataClass
import com.codebyzebru.myapplication.dataclasses.PurchasedItemDataClass
import com.codebyzebru.myapplication.dataclasses.ViewInventoryDataClass
import com.codebyzebru.myapplication.dataclasses.ViewPartyDataClass
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_bill.*
import kotlinx.android.synthetic.main.fragment_bill.view.*
import kotlinx.android.synthetic.main.popup_layout_additem.*
import kotlinx.android.synthetic.main.popup_layout_billfrag_additem.*
import kotlinx.android.synthetic.main.popup_layout_billfrag_additem.view.*
import java.text.SimpleDateFormat
import java.util.*

class BillFragment : Fragment(), PurchasedItemAdapter.SubTotalListener {

    private var billNo = 0
    private lateinit var popupView: View

    private lateinit var database: DatabaseReference
    private var billDbRef = FirebaseDatabase.getInstance()

    private var dataList = arrayListOf<String>()
    private var itemList = arrayListOf<String>()
    private var purchasedItemList = arrayListOf<PurchasedItemDataClass>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        database = Firebase.database.reference
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

        val txtDate = view.findViewById<TextView>(R.id.billFrag_txt_date)
        val edtxtBillNo = view.findViewById<EditText>(R.id.billFrag_txt_billNo)
        val autoCompleteTextView = view.findViewById<AutoCompleteTextView>(R.id.billFrag_autoTxt_name)
        val email = view.findViewById<EditText>(R.id.billFrag_edtxt_email)
        val organization = view.findViewById<EditText>(R.id.billFrag_edtxt_companyName)
        val address = view.findViewById<EditText>(R.id.billFrag_edtxt_companyAddr)
        val contact = view.findViewById<EditText>(R.id.billFrag_edtxt_contact)
        val billTotal = view.findViewById<EditText>(R.id.billFrag_edtxt_total)

        val purchaseRecyclerView = view.findViewById<RecyclerView>(R.id.purchase_recyclerView)

        purchaseRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        val sharedPreferences = requireActivity().getSharedPreferences("MySharedPref", MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        billNo = sharedPreferences.getInt("billNo", 1)
        billFrag_txt_billNo.setText(billNo.toString())
        Log.d("billNo", billNo.toString())


        view.billFrag_txt_date?.text = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date()).toString()
        /**
        OR YOU CAN USE THIS METHOD ALSO TO GET LOCAL DATE, BY GETTING INSTANCE OF CALENDAR

        val calendar = Calendar.getInstance()

        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH)        //  month start from 0. (ex. Jan-0, Feb-1,...,Dec-11)
        val year = calendar.get(Calendar.YEAR)

        view.billFrag_txt_date.text = day.toString() + "-" + (month+1).toString() + "-" + year.toString()
        */

        val userID = FirebaseAuth.getInstance().currentUser!!.uid
        val dbRef = FirebaseDatabase.getInstance().getReference("Users/$userID/Party Data")

        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (item in snapshot.children) {
                        val listedData = item.getValue(ViewPartyDataClass::class.java)
                        listedData!!.key = item.key.toString()
                        dataList.add(listedData.partyName)
                        Log.d("dataList", dataList.toString())

                        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, dataList)
                        autoCompleteTextView.setAdapter(adapter)

                        autoCompleteTextView.setOnItemClickListener(object : AdapterView.OnItemClickListener {
                            override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                                val selected = parent!!.getItemAtPosition(position) as String
                                Log.d("Selected Position", dataList.indexOf(autoCompleteTextView.text.toString()).toString())
                                Log.d("Selected Name", selected)

                                for (party in snapshot.children) {
                                    val partyNameData = party.getValue(ViewPartyDataClass::class.java)
                                    if (partyNameData?.partyName.equals(selected)) {
                                        Log.d("partyNameData", partyNameData.toString())

                                        billFrag_edtxt_email.setText(partyNameData?.email)
                                        billFrag_edtxt_companyName.setText(partyNameData?.companyName)
                                        billFrag_edtxt_companyAddr.setText(partyNameData?.address)
                                        billFrag_edtxt_contact.setText(partyNameData?.contact)
                                    }
                                }
                            }
                        })
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("Error", error.toString())
            }
        })


        view.findViewById<Button>(R.id.billFrag_btn_addItem).setOnClickListener {
            itemList.clear()
            //  subclass of `Dialog`
            val builder = AlertDialog.Builder(requireContext())
            //  Instantiates a layout XML file into its corresponding `View` objects
            val inflater = LayoutInflater.from(context)
            popupView = inflater.inflate(R.layout.popup_layout_billfrag_additem, null)
            builder.setView(popupView)

            val alertDialog = builder.create()
            alertDialog.show()

            val pName = popupView.findViewById<AutoCompleteTextView>(R.id.additem_billFrag_autotxt_productName)
            val pQty = popupView.findViewById<EditText>(R.id.additem_billFrag_edittxt_qty)
            val pPrice = popupView.findViewById<EditText>(R.id.additem_billFrag_edittxt_price)

            pName.setPadding(0, 25, 0, 0)

            val inventoryRef = FirebaseDatabase.getInstance().getReference("Users/$userID/Inventory Data")

            inventoryRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (item in snapshot.children) {
                            val listedItem = item.getValue(ViewInventoryDataClass::class.java)
                            listedItem!!.key = item.key.toString()
                            itemList.add(listedItem.productName)
                            Log.d("dataList", itemList.toString())

                            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, itemList)
                            pName.setAdapter(adapter)

                            pName.setOnItemClickListener(object : AdapterView.OnItemClickListener {
                                override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                                    val selected = parent!!.getItemAtPosition(position) as String
                                    Log.d("Selected Position", itemList.indexOf(pName.text.toString()).toString())
                                    Log.d("Selected Product", selected)

                                    for (product in snapshot.children) {
                                        val pNameData = product.getValue(ViewInventoryDataClass::class.java)
                                        if (pNameData?.productName.equals(selected)) {
                                            Log.d("partyNameData", pNameData.toString())
                                            pPrice.setText(pNameData?.purchasingPrice.toString())

                                        }
                                    }
                                }
                            })
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("Error", error.toString())
                }
            })


            popupView.findViewById<Button>(R.id.billFrag_addItem_btnAdd).setOnClickListener {
                purchasedItemList.add(
                    PurchasedItemDataClass(pName.text.toString(), pQty.text.toString(), pPrice.text.toString().toInt())
                )
                Log.d("purchasedItemList", purchaseRecyclerView.toString())



                purchaseRecyclerView.adapter = PurchasedItemAdapter(requireContext(), purchasedItemList, this)
                alertDialog.dismiss()
            }
        }



        view.btnGenerateBill.setOnClickListener {
            billNo += 1
            Log.d("billNo", billNo.toString())
            editor.putInt("billNo", billNo)
                .apply()

            val bill = BillDataClass(
                date = txtDate.text.toString(),
                billNo = edtxtBillNo.text.toString(),
                buyer = autoCompleteTextView.text.toString(),
                email = email.text.toString(),
                organization = organization.text.toString(),
                address = address.text.toString(),
                contact = contact.text.toString(),
                purchasedItems = purchasedItemList,
                billTotal = billTotal.text.toString().toInt()
            )

            val billKey = database.child("Bills").push().key.toString()
            billDbRef.getReference("Users/$userID").child("Bills").child(billKey).setValue(bill)

            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.layout_home, HomeFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    override fun onSubTotalUpdate(total: Int) {
        view?.billFrag_edtxt_total?.setText(total.toString())
    }

}