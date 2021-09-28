package com.github.tyaathome.beanconverter.dialog

import com.github.tyaathome.beanconverter.bean.ExtendsBean
import com.intellij.openapi.ui.DialogWrapper
import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.*

/**
 * Author: tya
 * Date: 2021/09/28
 * Desc:
 */
class FieldsDialog : DialogWrapper(true) {

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

    init {
        title = "title"
        init()
    }

    override fun createCenterPanel(): JComponent {
        contentPane.preferredSize = Dimension(900, 500)
        for(item in extendsList) {
            extentClassComboBox.addItem(item)
        }
        return contentPane
    }

    override fun doCancelAction() {
        //super.doCancelAction()
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