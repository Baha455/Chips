package com.example.chips

import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.net.toUri
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.BoundingBox
import com.yandex.mapkit.geometry.Geometry
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.InputListener
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.search.*
import com.yandex.mapkit.uri.Uri
import com.yandex.mapkit.uri.UriObjectMetadata
import com.yandex.runtime.Error
import com.yandex.runtime.ui_view.ViewProvider
import org.w3c.dom.Text
import java.io.*
import kotlin.properties.Delegates


class MainActivity : AppCompatActivity() {

    lateinit var listener: InputListener
    lateinit var mapView: MapView
    private lateinit var searchManager: SearchManager
    private lateinit var searchSession: Session
    var point1 by Delegates.notNull<Double>()
    var point2 by Delegates.notNull<Double>()
    lateinit var uri: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapKitFactory.setApiKey("3b93339d-bcba-43ed-a270-a92079534723")
        MapKitFactory.initialize(this)
        setContentView(R.layout.activity_main)

        mapView = findViewById(R.id.map)
        mapView.map.move(
            CameraPosition(Point(42.879416, 74.618057), 15.0f, 0.0f, 0.0f),
            Animation(Animation.Type.SMOOTH, 0F), null
        )

        setMark()
        mapClick()
        click()

    }

    private fun setMark(){
        val view = View(this).apply {
            background = AppCompatResources.getDrawable(context, R.drawable.ic_baseline_location_on_24)
        }
        val point = Point(42.879416, 74.618057)
        mapView.map.mapObjects.addPlacemark(point, ViewProvider(view))
    }

    private fun mapClick(){
        listener = object :InputListener {
             override fun onMapTap(p0: Map, p1: Point) {
                //val textview = findViewById<TextView>(R.id.text)
                point1 = p1.latitude
                 point2 = p1.longitude


            }

            override fun onMapLongTap(p0: Map, p1: Point) {

            }
        }

        mapView.map.addInputListener(listener)
    }

    private fun click(){
        val btn = findViewById<Button>(R.id.btnSearch)
        btn.setOnClickListener { searchBy() }
    }

    private fun search(){
        val pont1 = Point(42.979744, 80.275537)
        val point2 = Point(39.407811, 68.937386)
        val box = BoundingBox(pont1, point2)
        val list = ArrayList<String>() as MutableList<Category>
        searchManager = SearchFactory.getInstance().createSearchManager(
            SearchManagerType.ONLINE
        )
        val point = Geometry.fromBoundingBox(box)
        val ed = findViewById<EditText>(R.id.edit)
        val ed1= findViewById<EditText>(R.id.edit1)
        searchSession = searchManager.submit(ed.text.toString(), point, SearchOptions(),
            object: Session.SearchListener {
                override fun onSearchError(p0: Error) {
                    Toast.makeText(applicationContext, "Error", Toast.LENGTH_SHORT).show() }
                override fun onSearchResponse(p0: Response) {
                    val textView1 = findViewById<TextView>(R.id.text)
                    val city = p0.collection.children.firstOrNull()?.obj
                        ?.metadataContainer
                        ?.getItem(ToponymObjectMetadata::class.java)
                            ?.address?.formattedAddress

                    textView1.text = city


                }
            }
        )

        val spinner = findViewById<Spinner>(R.id.spinner)

        val adapter = ArrayAdapter(
            applicationContext,
            R.layout.customtxt,
            list
        )
        adapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }

    private fun searchBy(){
        val textView1 = findViewById<TextView>(R.id.text)
        val point = Point(point1, point2)
        searchManager = SearchFactory.getInstance().createSearchManager(
            SearchManagerType.ONLINE
        )

        searchSession = searchManager.submit(point, 20, SearchOptions(), object : Session.SearchListener{
            override fun onSearchResponse(p0: Response) {
                val city = p0.collection.children.firstOrNull()
                    ?.obj?.metadataContainer
                    ?.getItem(ToponymObjectMetadata::class.java)
                    ?.address
                    ?.formattedAddress

                val url = p0.collection.children.firstOrNull()
                    ?.obj?.metadataContainer
                    ?.getItem(UriObjectMetadata::class.java)
                    ?.uris
                    ?.firstOrNull()
                    ?.value.toString()

                    textView1.text = url

            }

            override fun onSearchError(p0: Error) {

            }

        })

    }

    private fun searchByUri(){
        val textView1 = findViewById<TextView>(R.id.text)
        searchManager = SearchFactory.getInstance().createSearchManager(
            SearchManagerType.ONLINE
        )
        searchSession = searchManager.resolveURI(uri, SearchOptions(), object : Session.SearchListener{
            override fun onSearchResponse(p0: Response) {
                val city3 = p0.collection.children.firstOrNull()
                    ?.obj?.metadataContainer
                    ?.getItem(BusinessObjectMetadata::class.java)
                    ?.name

                textView1.text = city3
            }

            override fun onSearchError(p0: Error) {
                TODO("Not yet implemented")
            }

        })
    }
    override fun onStart() {
        super.onStart()
        mapView.onStart()
        MapKitFactory.getInstance().onStart()
    }


    override fun onStop() {
        super.onStop()
        mapView.onStop()
        MapKitFactory.getInstance().onStop()
    }


}