package com.wayneyong.distancetracker.util

import android.view.View
import android.widget.Button

object ExtensionFunctions {

    //hiding and showing visibility of views

    fun View.show() {
        this.visibility = View.VISIBLE
    }

    fun View.hide() {
        this.visibility = View.INVISIBLE
    }

    fun Button.enable() {
        this.isEnabled = true
    }

    fun Button.disable() {
        this.isEnabled = false
    }
}