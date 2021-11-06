package com.github.tyaathome.beanconverter.dialog

import com.google.googlejavaformat.java.Formatter
import java.awt.Dimension
import javax.swing.*

class CodeDialog : JFrame() {
    private lateinit var contentPane: JPanel
    private lateinit var buttonOK: JButton
    private lateinit var buttonCancel: JButton
    private lateinit var codeText: JTextArea
    private lateinit var formatBtn: JButton

    init {
        title = "BeanConverter"
        createCenterPanel()
        size = Dimension(800, 350)
        setContentPane(contentPane)
        getRootPane().defaultButton = buttonOK
    }

    private fun createCenterPanel(): JComponent {
        formatBtn.addActionListener action@{
            val text = "public class Test {${codeText.text}}"
            try {
                val text = Formatter().formatSource(text).trim()
                val lines = text.split("\n")
                val stringBuilder = StringBuilder()
                lines.forEachIndexed { index, s ->
                    if(s.isNotEmpty()) {
                        if (index != 0 && index < lines.size - 1 && s.length > 2) {
                            val str = s.substring(2)
                            stringBuilder.append(str + "\n")
                        }
                    }
                }
                codeText.text = stringBuilder.toString()
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
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