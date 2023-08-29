package com.number.admengeneratorapp

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.number.admengeneratorapp.databinding.ActivityMainBinding
import com.number.admengeneratorapp.model.Codes

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var alertDialogBuilder: AlertDialog.Builder
    private lateinit var alertDialog: AlertDialog
    private lateinit var codeRecyclerView: RecyclerView
    private lateinit var list: ArrayList<Codes>
    private lateinit var adapter: MyAdapter

    @SuppressLint("InflateParams")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = Firebase.database
        codeRecyclerView = binding.recyclerView
        codeRecyclerView.layoutManager = LinearLayoutManager(this)
        codeRecyclerView.setHasFixedSize(true)
        list = arrayListOf()
        adapter = MyAdapter(this@MainActivity, list)

        getCodeFormFirebase()

        binding.btnShowAlertToAddCode.setOnClickListener {
            showAlert()
        }
    }

    @SuppressLint("InflateParams", "MissingInflatedId")
    private fun showAlert() {
        alertDialogBuilder = AlertDialog.Builder(this@MainActivity, R.style.CustomAlert)
        val view = layoutInflater.inflate(R.layout.alert_add_code, null, false)
        view.findViewById<MaterialButton>(R.id.btn_add_code).setOnClickListener {
            addCode(view.findViewById(R.id.inputCode))
        }
        alertDialogBuilder.setView(view)
        alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    @SuppressLint("InflateParams")
    private fun addCode(textLayout: TextInputLayout) {
        val txtCode = textLayout.editText!!.text.toString()
        try {
            if (txtCode.isNotEmpty()) {
                database.reference.child("Codes").child(txtCode).setValue(Codes(txtCode))
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this@MainActivity, "Code added", Toast.LENGTH_SHORT)
                                .show()
                            textLayout.editText!!.setText("")
                            alertDialog.dismiss()
                        } else {
                            Toast.makeText(
                                this@MainActivity,
                                task.exception!!.message,
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            textLayout.error = task.exception!!.message
                        }
                    }
            } else {
                textLayout.error = "This field cannot be empty"
            }
        } catch (e: Exception) {
            textLayout.error = e.message
        }
    }

    private fun getCodeFormFirebase() {
        database.reference.child("Codes").addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                binding.progressBar.visibility = View.GONE
                list.clear()
                if (snapshot.exists()) {
                    for (codeSnapshot in snapshot.children) {
                        val code = codeSnapshot.getValue(Codes::class.java)
                        list.add(code!!)
                    }
                    codeRecyclerView.adapter = adapter
                    adapter.notifyDataSetChanged()
                }
                if (list.isEmpty()) {
                    binding.txtNoAnyCode.visibility = View.VISIBLE
                } else {
                    binding.txtNoAnyCode.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
        swipeToGesture(codeRecyclerView)
    }

    private fun swipeToGesture(itemRv: RecyclerView?) {
        val swipeGesture = object : SwipeGesture(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                var actionBtnTapped = false
                try {
                    when (direction) {
                        ItemTouchHelper.LEFT -> {
                            database.reference.child("Codes")
                                .child(adapter.list!![position].code!!.toString()).removeValue()

                            val deleteItem = list[position]
                            list.removeAt(position)
                            adapter.notifyItemRemoved(position)
                            Toast.makeText(this@MainActivity, "Code deleted", Toast.LENGTH_LONG)
                                .show()
//                            val snackBar = Snackbar.make(
//                                this@MainActivity.codeRecyclerView,
//                                "Code deleted",
//                                Snackbar.LENGTH_LONG
//                            ).addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
//                                override fun onDismissed(
//                                    transientBottomBar: Snackbar?,
//                                    event: Int
//                                ) {
//                                    super.onDismissed(transientBottomBar, event)
//                                }
//
//                                override fun onShown(transientBottomBar: Snackbar?) {
//                                    super.onShown(transientBottomBar)
//                                    transientBottomBar?.setAction("Undo") {
//                                        list.add(position, deleteItem)
//                                        adapter.notifyItemInserted(position)
//                                        actionBtnTapped = true
//                                    }
//                                }
//                            }).apply {
//                                animationMode = Snackbar.ANIMATION_MODE_FADE
//                            }
//                            snackBar.setActionTextColor(
//                                ContextCompat.getColor(
//                                    this@MainActivity,
//                                    R.color.white
//                                )
//                            )
//                            snackBar.show()
                        }
                    }
                } catch (e: Exception) {
                    println(e)
                    Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_LONG).show()
                }
            }
        }
        val touchHelper = ItemTouchHelper(swipeGesture)
        touchHelper.attachToRecyclerView(itemRv)
    }
}