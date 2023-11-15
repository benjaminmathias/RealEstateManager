package com.openclassrooms.realestatemanager.utils

import android.text.InputFilter
import android.text.Spanned

class InputFilterMinMax: InputFilter {
    private var min:Int = 0
    private var max:Int = 0
    constructor(min:Int, max:Int) {
        this.min = min
        this.max = max
    }
    constructor(min:String, max:String) {
        this.min = Integer.parseInt(min)
        this.max = Integer.parseInt(max)
    }
    override fun filter(source:CharSequence, start:Int, end:Int, dest: Spanned, dstart:Int, dend:Int): CharSequence? {
        try
        {
            val input = kotlin.math.floor((dest.toString() + source.toString()).toDouble()).toInt()
            if (isInRange(min, max, input))
                return null
        }
        catch (_:NumberFormatException) {}
        return ""
    }
    private fun isInRange(a:Int, b:Int, c:Int):Boolean {
        return if (b > a) c in a..b else c in b..a
    }
}