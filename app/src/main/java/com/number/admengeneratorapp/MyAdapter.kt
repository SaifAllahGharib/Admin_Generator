package com.number.admengeneratorapp

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.number.admengeneratorapp.model.Codes

class MyAdapter(
    private val context: Context? = null,
    val list: ArrayList<Codes>? = null,
) :
    RecyclerView.Adapter<MyAdapter.MyViewHolder>() {
    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.code, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = list!![position]
        holder.code.text = currentItem.code
        holder.btnCopy.setOnClickListener {
            val clipboardManager: ClipboardManager =
                context!!.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipboardManager.setPrimaryClip(ClipData.newPlainText("text", currentItem.code))
            Toast.makeText(context, "Code copied!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int {
        return list!!.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var code: TextView = itemView.findViewById(R.id.code)
        var btnCopy: ImageButton = itemView.findViewById(R.id.btn_copy_code)
    }
}