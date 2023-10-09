package com.codebyzebru.myapplication.fragments

import android.content.Context
import android.graphics.Canvas
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.codebyzebru.myapplication.R
import com.codebyzebru.myapplication.activities.HomeActivity
import com.codebyzebru.myapplication.adapters.InvoiceAdapter
import com.codebyzebru.myapplication.databinding.FragmentViewInvoiceBinding
import com.codebyzebru.myapplication.dataclasses.BillDataClass
import com.codebyzebru.myapplication.dataclasses.ProfileDataClass
import com.codebyzebru.myapplication.dataclasses.PurchasedItemDataClass
import com.codebyzebru.myapplication.dataclasses.ViewPartyDataClass
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class ViewInvoiceFragment : Fragment() {

    lateinit var binding: FragmentViewInvoiceBinding

    private lateinit var invoiceKey: String
    private lateinit var userID: String
    lateinit var buyerUID: String
    val invoicedItems = arrayListOf<PurchasedItemDataClass>()

    private lateinit var userDatabase: DatabaseReference
    private lateinit var billDatabase: DatabaseReference
    private lateinit var partyDatabase: DatabaseReference
    private lateinit var purchasedItem: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userID = FirebaseAuth.getInstance().currentUser!!.uid
        userDatabase = Firebase.database.reference.child("Users/$userID")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        binding = FragmentViewInvoiceBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as HomeActivity).supportActionBar?.hide()

        invoiceKey = this.arguments?.getString("invoiceNo").toString()
        billDatabase = Firebase.database.reference.child("Users/$userID/Bills/$invoiceKey")

        binding.btnClose.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        //  FETCHING LOGGED-IN USER'S DETAILS
        userDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("snapshot", snapshot.toString())
                if (snapshot.exists()) {
                    val userData = snapshot.getValue(ProfileDataClass::class.java)
                    userData!!.key = snapshot.key.toString()

                    if (userData.companyName == "") {
                        binding.txtSellerOrg.text = getString(R.string.default_org)
                    } else {
                        binding.txtSellerOrg.text = userData.companyName
                    }
                    binding.txtSellerContact.text = userData.contact
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Failed to fetch user's detail", error.message)
            }
        })

        //  FETCHING INVOICE'S DETAILS
        billDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    Log.d("billDatabase.snapshot", snapshot.toString())
                    val invoiceData = snapshot.getValue(BillDataClass::class.java)
                    invoiceData!!.key = snapshot.key.toString()

                    Log.d("invoiceData.purchasedItems", invoiceData.purchasedItems.toString())

                    val total = invoiceData.subTotal + invoiceData.tax /*+ invoiceData.billTotal*/

                    binding.txtInvoiceNo.text = invoiceData.billNo
                    binding.txtInvoiceDate.text = invoiceData.date
                    buyerUID = invoiceData.buyerUID
                    binding.txtPartyName.text = invoiceData.buyer
                    binding.txtSubAmt.text = invoiceData.subTotal.toString()
                    binding.txtTotalAmount.text = total.toString()

                    //  FETCHING PURCHASED ITEMS DETAILS
                    purchasedItem = Firebase.database.reference.child("Users/$userID/Bills/${invoiceData.key}/purchasedItems/")
                    purchasedItem.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists()) {
                                Log.d("purchasedItem.snapshot", snapshot.toString())
                                for (item in snapshot.children) {
                                    val a = item.getValue(PurchasedItemDataClass::class.java)
                                    Log.d("a", a.toString())
                                    invoicedItems.add(a!!)
                                    Log.d("invoicedItems", invoicedItems.toString())
                                    binding.recyclerViewInvoicedItem.apply {
                                        layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                                        adapter = InvoiceAdapter(requireContext(), invoicedItems)
                                    }
                                }
                            } else {
                                binding.cardVInvoicedItems.visibility = View.GONE
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.e("no purchasedItem", error.message)
                        }
                    })

                    //  FETCHING BUYER'S DETAILS
                    partyDatabase = Firebase.database.reference.child("Users/$userID/Party Data/$buyerUID")
                    partyDatabase.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            Log.d("partyDatabase.snapshot", snapshot.toString())
                            val buyerData = snapshot.getValue(ViewPartyDataClass::class.java)
                            binding.txtPartyOrg.text = buyerData?.companyName
                            if (buyerData!!.companyName == "") {
                                binding.txtPartyOrg.visibility = View.GONE
                                if (buyerData.address == "") {
                                    binding.txtPartyOrg.visibility = View.GONE
                                } else {
                                    binding.txtPartyOrg.visibility = View.VISIBLE
                                    binding.txtPartyOrg.text = buyerData.address
                                }
                            } else {
                                binding.txtPartyOrg.visibility = View.VISIBLE
                                binding.txtPartyOrg.text = buyerData.companyName
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.e("Failed to fetch buyer's detail", error.message)
                        }
                    })
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Failed to fetch invoice detail", error.message)
            }
        })

        binding.btnPdfShare.setOnClickListener {
            createAndSharePDF()
        }
    }


    private fun createAndSharePDF() {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(1080, 1920, 1).create()   // A4 size in points
        val page = pdfDocument.startPage(pageInfo)
        val displayMetrics = DisplayMetrics()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            requireActivity().display?.getRealMetrics(displayMetrics)
        } else {
            requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
        }

        //  inflate layout
        val inflater = context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.fragment_view_invoice, null)

        binding.invoiceView.apply {
            measure(View.MeasureSpec.makeMeasureSpec(displayMetrics.widthPixels, View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(displayMetrics.heightPixels, View.MeasureSpec.EXACTLY))
            layout(0, 0, pageInfo.pageWidth, pageInfo.pageHeight)
        }

        //  draw view on Canvas
        val canvas: Canvas = page.canvas
        binding.invoiceView.draw(canvas)
        pdfDocument.finishPage(page)

        // Create a file to save the PDF
        val pdfFile = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "Invoice-${binding.txtInvoiceNo.text}.pdf")

        try {
            val outputStream = FileOutputStream(pdfFile)
            pdfDocument.writeTo(outputStream)
            Toast.makeText(context, "saved", Toast.LENGTH_SHORT).show()
            outputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
            Log.e("createAndSharePDF: IOException", e.message.toString())
        }

        Log.i("pdfFile.absolutePath", pdfFile.absolutePath)

        // Close the PdfDocument
        pdfDocument.close()
    }
}