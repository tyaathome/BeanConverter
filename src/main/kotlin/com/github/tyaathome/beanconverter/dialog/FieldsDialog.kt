package com.github.tyaathome.beanconverter.dialog

import com.github.tyaathome.beanconverter.ui.bean.ExtendsBean
import com.github.tyaathome.beanconverter.ui.bean.FieldBean
import com.github.tyaathome.beanconverter.ui.model.FieldTableCellEditor
import com.github.tyaathome.beanconverter.ui.model.FieldTableCellRenderer
import com.github.tyaathome.beanconverter.ui.model.FieldTableModel
import com.intellij.ui.table.JBTable
import org.apache.commons.lang.StringUtils
import java.awt.Dimension
import javax.swing.*


/**
 * Author: tya
 * Date: 2021/09/28
 * Desc:
 */
class FieldsDialog() : JFrame() {

    private lateinit var contentPane: JPanel
    private lateinit var filedPanel: JPanel
    private lateinit var sp: JScrollPane
    private lateinit var classPanel: JPanel
    private lateinit var generateClass: JTextField
    private lateinit var extentClassComboBox: JComboBox<ExtendsBean>
    private lateinit var extentLabel: JLabel
    private lateinit var treeTable: JBTable
    private lateinit var btnOK: JButton
    private lateinit var btnCancel: JButton
    private val extendsList = listOf(
        ExtendsBean("BaseViewModel", "com.yryc.onecar.databinding.viewmodel.BaseViewModel"),
        ExtendsBean("BaseItemViewModel", "com.yryc.onecar.databinding.viewmodel.BaseItemViewModel"),
    )

    private lateinit var classFullName: String
    private lateinit var fieldList: ArrayList<FieldBean>

    init {
        title = "BeanConverter"
        setContentPane(contentPane)
        size = Dimension(800, 350)
    }

    private constructor(classFullName: String, fieldList: ArrayList<FieldBean>) : this() {
        this.classFullName = classFullName
        this.fieldList = fieldList
        createCenterPanel()
        classPanel.isVisible = !StringUtils.isEmpty(classFullName)
    }

    private constructor(fieldList: ArrayList<FieldBean>) : this("", fieldList)

    companion object {
        fun instance(classFullName: String, fieldList: ArrayList<FieldBean>): FieldsDialog {
            return FieldsDialog(classFullName, fieldList)
        }

        fun instance(fieldList: ArrayList<FieldBean>): FieldsDialog {
            return FieldsDialog(fieldList)
        }
    }

    private fun createCenterPanel(): JComponent {
        generateClass.text = classFullName
        for(item in extendsList) {
            extentClassComboBox.addItem(item)
        }

        val defaultListSelectionModel = DefaultListSelectionModel()
        defaultListSelectionModel.apply {
            selectionMode = ListSelectionModel.SINGLE_SELECTION
            addListSelectionListener {
                clearSelection()
            }
        }

        treeTable = JBTable(FieldTableModel(fieldList, 4))
        treeTable.apply {
            columnModel.getColumn(0).apply {
                maxWidth = 50
                cellRenderer = FieldTableCellRenderer()
                cellEditor = FieldTableCellEditor()
            }
            rowHeight = 30
            cellSelectionEnabled = false
            putClientProperty("terminateEditOnFocusLost", true)
            isFocusable = false
        }
        sp.setViewportView(treeTable)

        btnOK.addActionListener {
            if(okAction != null) {
                val classPath = generateClass.text.toString()
                val extendsBean = extentClassComboBox.selectedItem as ExtendsBean
                okAction?.invoke(classPath, extendsBean, fieldList)
            } else {
                dismiss()
            }
        }

        btnCancel.addActionListener {
            dismiss()
        }

        return contentPane
    }

    fun dismiss() {
        isVisible = false
        dispose()
    }

    var okAction: ((String, ExtendsBean, List<FieldBean>) -> Unit)? = null
}