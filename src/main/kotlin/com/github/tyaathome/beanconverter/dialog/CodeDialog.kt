package com.github.tyaathome.beanconverter.dialog

import com.github.tyaathome.beanconverter.dialog.CodeDialog
import com.github.tyaathome.beanconverter.ui.bean.ExtendsBean
import com.github.tyaathome.beanconverter.ui.bean.FieldBean
import java.awt.Dimension
import javax.swing.*

class CodeDialog : JFrame() {
    private lateinit var contentPane: JPanel
    private lateinit var buttonOK: JButton
    private lateinit var buttonCancel: JButton
    private lateinit var codeText: JTextArea

    init {
        title = "BeanConverter"
        createCenterPanel()
        size = Dimension(800, 350)
        setContentPane(contentPane)
        getRootPane().defaultButton = buttonOK
    }

    fun createCenterPanel(): JComponent {
        buttonOK.addActionListener {
            if(okAction != null) {
                okAction?.invoke(codeText.text)
            }
        }
        buttonCancel.addActionListener {
            dismiss()
        }
        return contentPane
    }

    fun dismiss() {
        isVisible = false
        dispose()
    }

    var okAction: ((String) -> Unit)? = null


}