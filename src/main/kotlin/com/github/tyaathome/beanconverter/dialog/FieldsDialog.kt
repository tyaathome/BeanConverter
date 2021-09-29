package com.github.tyaathome.beanconverter.dialog

import com.github.tyaathome.beanconverter.ui.bean.ExtendsBean
import com.github.tyaathome.beanconverter.ui.bean.FieldBean
import com.github.tyaathome.beanconverter.ui.model.FieldTableCellEditor
import com.github.tyaathome.beanconverter.ui.model.FieldTableCellRenderer
import com.github.tyaathome.beanconverter.ui.model.FieldTableModel
import com.intellij.ui.table.JBTable
import java.awt.Dimension
import javax.swing.*


/**
 * Author: tya
 * Date: 2021/09/28
 * Desc:
 */
class FieldsDialog(private val classFullName: String, private val fieldList: ArrayList<FieldBean>) : JFrame() {

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
    private val extendsList = listOf(ExtendsBean("BaseViewModel", "com.yryc.onecar.databinding.viewmodel.BaseViewModel"))

    init {
        title = "title"
        createCenterPanel()
        setContentPane(contentPane)
        size = Dimension(800, 350)
    }

    fun createCenterPanel(): JComponent {
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