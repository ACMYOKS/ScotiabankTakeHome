package com.acapp1412.scotiabanktakehome

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration

class SpacingItemDecoration(private val verticalSpacing: Int, private val horizontalSpacing: Int) :
    ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect, view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.left = horizontalSpacing
        outRect.right = horizontalSpacing

        // Apply spacing only to the top and bottom of the items
        if (parent.getChildAdapterPosition(view) == 0) {
            outRect.top = verticalSpacing // Add top spacing for the first item
        } else {
            outRect.top = verticalSpacing / 2 // Add half spacing for other items
        }
        outRect.bottom = verticalSpacing / 2 // Add half spacing for other items
    }
}