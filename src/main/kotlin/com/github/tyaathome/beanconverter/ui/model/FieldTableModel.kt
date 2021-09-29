package com.github.tyaathome.beanconverter.ui.model

import com.github.tyaathome.beanconverter.ui.bean.FieldBean
import javax.swing.table.AbstractTableModel
import javax.swing.table.DefaultTableModel

/**
 * Author: tya
 * Date: 2021/09/29
 * Desc:
 */
class FieldTableModel(val fieldList: ArrayList<FieldBean>, private val columnCount: Int) : AbstractTableModel() {

    private val names = listOf("", "Data Type", "Field name", "Field Comment")
    private val types = listOf(Object::class.java, Object::class.java, Object::class.java, Object::class.java)

    override fun getColumnClass(column: Int): Class<*> {
        return types[column]
    }

    override fun getRowCount(): Int {
        return fieldList.size
    }

    override fun getColumnCount(): Int {
        return columnCount
    }

    override fun getColumnName(column: Int): String {
        return names[column]
    }

    override fun getValueAt(row: Int, column: Int): Any {
        if (fieldList.isNotEmpty() && fieldList.size > row) {
            val rowValue = fieldList[row]
            return when (column) {
                0 -> rowValue.selected
                1 -> rowValue.fieldType?.nanoTypeName ?: ""
                2 -> rowValue.fieldName
                3 -> rowValue.comment
                else -> ""
            }
        }
        return ""
    }

    override fun setValueAt(value: Any?, row: Int, column: Int) {
        if (fieldList.isNotEmpty() && fieldList.size > row) {
            val rowValue = fieldList[row]
            when (column) {
                0 -> if (value is Boolean) {
                    rowValue.selected = value
                }
                //1 -> data.fieldType?.nanoTypeName =
                2 -> if (value is String) {
                    rowValue.fieldName = value
                }
                3 -> if (value is String) {
                    rowValue.comment = value
                }
            }
        }
    }

    override fun isCellEditable(row: Int, column: Int): Boolean {
        return when(column) {
            0 -> true
            2 -> true
            3 -> true
            else -> false
        }
    }



}