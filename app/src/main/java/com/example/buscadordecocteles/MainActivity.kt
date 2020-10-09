package com.example.buscadordecocteles


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.RelativeLayout
import android.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import okhttp3.*
import java.io.IOException
import org.json.JSONObject
import org.json.JSONArray




class MainActivity : AppCompatActivity() {

    //Variables par el manejo de la lista.
    private lateinit var recyclerView : RecyclerView
    private lateinit var  viewManager :RecyclerView.LayoutManager
    lateinit var  viewAdapter: RecyclerView.Adapter<*>
    lateinit var  layout: RelativeLayout
    val urlBase: String = "https://www.thecocktaildb.com/api/json/v1/1/search.php?s="
    val client = OkHttpClient()
    var cocteles: JSONArray = JSONArray()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Llamada inicial de la api para mostrar la lista
        callApi("margarita")

        //Paso contexto al viewManager
        viewManager = LinearLayoutManager(this)

        //Paso el array de JSON al adaptador para inicializarlo en la lista
        viewAdapter = MyAdapter(cocteles)

        //Liigo la vista de mensaje de error
        layout = findViewById<RelativeLayout>(R.id.not_found)




        //Defino la lista
        recyclerView = findViewById<RecyclerView>(R.id.lista).apply {

            setHasFixedSize(true)
            layoutManager = viewManager

            adapter = viewAdapter

        }


    }

    //Funcion para hacer peticion a la api de cocteles
    fun callApi(cadena: String){
        //Creamos peticion con la url base y la cadena que se busco
        val request = Request.Builder().url(urlBase + cadena).build()
        //Se manda la petición y se manejan las respuesas
        client.newCall(request).enqueue(object: Callback{
            //Manejamos la falla en consola
            override fun onFailure(call: Call, e: IOException) {
                Log.e("Query", e.message)
            }

            //Manejamos la respuesta para pasarla a la lista
            override fun onResponse(call: Call, response: Response) {
                    val bodyString = response.body()?.string()
                    //En caso de no tener resultados
                    if(bodyString == "{\"drinks\":null}"){
                        cocteles = JSONArray()
                        //Paso un arreglo vacio al adaptador
                        this@MainActivity.runOnUiThread(Runnable {
                            //Mostramos el mensaje de error
                            layout.visibility = View.VISIBLE
                            recyclerView.adapter = MyAdapter(cocteles)
                            //Actualizo la lista
                            recyclerView.adapter!!.notifyDataSetChanged()
                        })
                    }

                    else {

                        val Jobject = JSONObject(bodyString)
                        val Jarray = Jobject.getJSONArray("drinks")
                        Log.e("rESPONSE", Jarray.getJSONObject(0).toString())
                        //Acatualizamos la lista con la respuesta en el hilo principal
                        this@MainActivity.runOnUiThread(Runnable {
                            //Quitamos el mensaje de error
                            layout.visibility = View.GONE
                            //Paso el valor obtenido al adaptador
                            recyclerView.adapter = MyAdapter(Jarray)
                            //Actualizo la lista
                            recyclerView.adapter!!.notifyDataSetChanged()
                        })
                    }

            }
        })

    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // creamos el menu desde el archivo xml
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)

         val search = menu.findItem(R.id.search)
         val searchView = search.actionView as SearchView
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                callApi(p0!!)

                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                return true
            }

        })

        return super.onCreateOptionsMenu(menu)
    }

}
