package com.fivestarmind.android.mvp.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.fivestarmind.android.R
import me.sujanpoudel.wheelview.WheelView
import java.util.Arrays


class WheelActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wheel)

        // create a string array  of tiles
        // create a string array  of tiles
        val titles = arrayOf("Bubble Sort", "Quick Sort", "Merge Sort", "Radix Sort", "Anil Sort")

        // get the reference of the wheelView

        // get the reference of the wheelView
        val wheelView = findViewById<View>(R.id.wheel_view) as WheelView

        // convert tiles array to list and pass it to setTitles

        // convert tiles array to list and pass it to setTitles
        wheelView.titles = Arrays.asList(*titles)
    }
}