package com.example.buscadordecocteles

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item.view.*
import org.json.JSONArray

class MyAdapter (private val data: JSONArray): RecyclerView.Adapter<MyAdapter.MyViewHolder>(){

    class MyViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    override fun getItemCount(): Int {


        return data.length()
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {


            //obtenemos el objecto en la posición
            val datos = data.getJSONObject(position)
            //Llenamos el valor de los elementos con el objeto
            holder.view.title.text = datos.getString("strDrink")
            holder.view.description.text = datos.getString("strInstructions")
            Glide.with(holder.view.context).load(datos.getString("strDrinkThumb")).into(holder.view.image)


    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val item = LayoutInflater.from(parent.context).inflate(R.layout.item, parent, false)

        return MyViewHolder(item)
    }



}