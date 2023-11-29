package com.openclassrooms.realestatemanager.ui

import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingListener
import androidx.databinding.InverseBindingMethod
import androidx.databinding.InverseBindingMethods
import com.google.android.material.chip.ChipGroup

@InverseBindingMethods(
    InverseBindingMethod(
        type = ChipGroup::class,
        attribute = "android:checkedChips",
        method = "getCheckedChipIds"
    )
)
object ChipGroupBindingAdapter {

    @JvmStatic
    @BindingAdapter("android:checkedChips")
    fun setCheckedChips(view: ChipGroup?, ids: MutableList<Int>) {
        for (id in ids) {
            if (!ids.contains(view?.checkedChipId)) {
                view?.check(id)
            }
        }
    }

    @JvmStatic
    @BindingAdapter(
        value = ["android:onCheckedChanged", "android:checkedChipsAttrChanged"],
        requireAll = false
    )
    fun setChipsListeners(
        view: ChipGroup?, listener: ChipGroup.OnCheckedStateChangeListener?,
        attrChange: InverseBindingListener?
    ) {
        if (attrChange == null) {
            view?.setOnCheckedStateChangeListener(listener)
        } else {
            view?.setOnCheckedStateChangeListener { group, checkedIds ->
                listener?.onCheckedChanged(group, checkedIds)
                attrChange.onChange()
            }
        }
    }
}