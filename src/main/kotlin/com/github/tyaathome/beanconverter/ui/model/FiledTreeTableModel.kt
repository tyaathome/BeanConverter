package com.github.tyaathome.beanconverter.ui.model

import com.github.tyaathome.beanconverter.ui.bean.FieldBean
import com.intellij.psi.PsiField
import org.jdesktop.swingx.treetable.DefaultMutableTreeTableNode
import org.jdesktop.swingx.treetable.DefaultTreeTableModel
import org.jdesktop.swingx.treetable.TreeTableNode
import org.jetbrains.kotlin.idea.caches.project.collectModuleInfoByUserData

class FiledTreeTableModel(node: TreeTableNode) : DefaultTreeTableModel(node) {

    private val names = listOf("Key", "Data Type", "Field name", "Field Comment")
    private val types = listOf(Object::class.java, Object::class.java, Object::class.java, Object::class.java)

    override fun getColumnClass(column: Int): Class<*> {
        return types[column]
    }

    override fun getColumnCount(): Int {
        return names.size
    }

    override fun getColumnName(column: Int): String {
        return names[column]
    }

    override fun getValueAt(node: Any?, column: Int): Any? {
        if(node is DefaultMutableTreeTableNode) {
            val value = node.userObject
            if(value is FieldBean) {
                return when(column) {
                    1 -> value.fieldType?.nanoTypeName ?: ""
                    2 -> value.fieldName
                    3 -> value.comment
                    else -> ""
                }
            }
        }
        return ""
    }

    override fun setValueAt(value: Any?, node: Any?, column: Int) {
        super.setValueAt(value, node, column)
        if(node is DefaultMutableTreeTableNode && value is String) {
            val data = node.userObject
            if(data is FieldBean) {
                when(column) {
                    //1 -> data.fieldType?.nanoTypeName =
                    2 -> data.fieldName = value
                    3 -> data.comment = value
                }
            }
        }
    }

    override fun isCellEditable(node: Any?, column: Int): Boolean {
        return when(column) {
            2 -> true
            3 -> true
            else -> false
        }
    }

}