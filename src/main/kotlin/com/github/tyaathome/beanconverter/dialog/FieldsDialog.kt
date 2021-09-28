package com.github.tyaathome.beanconverter.dialog

import com.github.tyaathome.beanconverter.ui.bean.ExtendsBean
import com.github.tyaathome.beanconverter.ui.bean.FieldBean
import com.intellij.openapi.ui.DialogWrapper
import org.jdesktop.swingx.treetable.DefaultMutableTreeTableNode
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Dimension
import java.awt.GridLayout
import javax.swing.*


/**
 * Author: tya
 * Date: 2021/09/28
 * Desc:
 */
class FieldsDialog(val fieldList: ArrayList<FieldBean>) : DialogWrapper(true) {

    private lateinit var contentPane: JPanel
    private lateinit var filedPanel: JPanel
    private lateinit var sp: JScrollPane
    private lateinit var buttonOK: JButton
    private lateinit var buttonCancel: JButton
    private lateinit var classPanel: JPanel
    private lateinit var generateClass: JTextField
    private lateinit var extentClassComboBox: JComboBox<ExtendsBean>
    private lateinit var extentLabel: JLabel
    private val extendsList = listOf(ExtendsBean("BaseViewModel", "com.yryc.onecar.databinding.viewmodel.BaseViewModel"))
    private var defaultMutableTreeTableNodeList = ArrayList<DefaultMutableTreeTableNode>()

    init {
        title = "title"
        init()
    }

    override fun createCenterPanel(): JComponent {
        contentPane.preferredSize = Dimension(900, 500)
        for(item in extendsList) {
            extentClassComboBox.addItem(item)
        }

//        val defaultListSelectionModel = DefaultListSelectionModel()
//        defaultListSelectionModel.apply {
//            selectionMode = ListSelectionModel.SINGLE_SELECTION
//            addListSelectionListener {
//                clearSelection()
//            }
//        }
//
//        defaultMutableTreeTableNodeList.clear()
//        val treeTable = JXTreeTable(FiledTreeTableModel(createData(fieldList)))
//        val manager = CheckTreeTableManager(treeTable)
//        manager.selectionModel.addPathsByNodes(defaultMutableTreeTableNodeList)
//        treeTable.apply {
//            columnModel.getColumn(0).preferredWidth = 150
//            expandAll()
//            cellSelectionEnabled = false
//            //selectionModel = defaultListSelectionModel
//            rowHeight = 30
//        }
//        sp.setViewportView(treeTable)

        val panel = JPanel(GridLayout(1, 4))
        panel.add(getLabel("Key").apply { preferredSize = Dimension(50, 30) })
        panel.add(getLabel("Data Type"))
        panel.add(getLabel("Field name"))
        panel.add(getLabel("Field Comment"))

        //panel.preferredSize = Dimension(900, 30)
        sp.setViewportView(panel)

        val view = sp.viewport.view
        if(view is JPanel) {
            for(item in view.components) {
                if(item is JLabel) {
                    println(item.text)
                }
            }
            println(view)
        }
        return contentPane
    }

    private fun getLabel(text: String): JLabel {
        return JLabel(text).apply {
            preferredSize = Dimension(-1, 30)
            horizontalAlignment = SwingConstants.CENTER
            verticalAlignment = SwingConstants.CENTER
//            val lineBorder = BorderFactory.createEmptyBorder(1, 1, 1, 1)
//            //border = BorderFactory.createLineBorder(Color(122, 138, 153))
//            val myBorder = BorderFactory.createCompoundBorder(
//                BorderFactory.createLineBorder(Color(122, 138, 153), 1),
//                BorderFactory.createEmptyBorder(0, 0, 0, 1)
//            )
            border = BorderFactory.createStrokeBorder(BasicStroke(0.1f))
        }
    }

    private fun createData(fieldList: ArrayList<FieldBean>): DefaultMutableTreeTableNode {
        val root = DefaultMutableTreeTableNode()
        for(item in fieldList) {
            val node = DefaultMutableTreeTableNode(item)
            root.add(node)
            defaultMutableTreeTableNodeList.add(node)
        }
        return root
    }

    override fun doOKAction() {
        if(okAction != null) {
            okAction?.invoke()
        } else {
            super.doOKAction()
        }
    }

    public var okAction: (() -> Unit)? = null
}