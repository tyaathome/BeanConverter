package com.github.tyaathome.beanconverter.ui.model

import com.github.tyaathome.beanconverter.ui.bean.FieldBean
import com.intellij.ui.layout.selected
import java.awt.Component
import java.awt.event.ItemEvent
import javax.swing.BorderFactory
import javax.swing.JCheckBox
import javax.swing.JTable
import javax.swing.SwingConstants
import javax.swing.table.DefaultTableCellRenderer

/**
 * Author: tya
 * Date: 2021/09/29
 * Desc:
 */
class FieldTableCellRenderer: DefaultTableCellRenderer() {

    override fun getTableCellRendererComponent(
        table: JTable?,
        value: Any?,
        isSelected: Boolean,
        hasFocus: Boolean,
        row: Int,
        column: Int
    ): Component {
        if(column != 0) {
            return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column)
        }
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