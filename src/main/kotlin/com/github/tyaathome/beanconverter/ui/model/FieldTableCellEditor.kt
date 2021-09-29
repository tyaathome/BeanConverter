package com.github.tyaathome.beanconverter.ui.model

import com.github.tyaathome.beanconverter.ui.bean.FieldBean
import java.awt.Component
import java.awt.event.ItemEvent
import java.util.*
import javax.swing.*

/**
 * Author: tya
 * Date: 2021/09/29
 * Desc:
 */
class FieldTableCellEditor: DefaultCellEditor(JCheckBox()) {

    override fun getTableCellEditorComponent(
        table: JTable?,
        value: Any?,
        isSelected: Boolean,
        row: Int,
        column: Int
    ): Component {
        if(column != 0) {
            return super.getTableCellEditorComponent(table, value, isSelected, row, column)
        }
        val view = component
        if(view is JCheckBox) {
            view.apply {
                horizontalAlignment = SwingConstants.CENTER
                verticalAlignment = SwingConstants.CENTER
                isEnabled = true
                isFocusable = false
                if (value is Boolean) {
                    view.isSelected = value
                }
            }
            return view
        } else {
            return JCheckBox().apply {
                //isOpaque = true
                horizontalAlignment = SwingConstants.CENTER
                verticalAlignment = SwingConstants.CENTER
                isEnabled = true
                isFocusable = false
                if (value is Boolean) {
                    this.isSelected = value
                }
            }
        }
    }

    override fun isCellEditable(anEvent: EventObject?): Boolean {
        return true
    }

}