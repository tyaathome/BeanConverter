package com.github.tyaathome.beanconverter.ui

import javax.swing.JPanel
import com.github.tyaathome.beanconverter.ui.TristateCheckBox
import javax.swing.JLabel
import org.jdesktop.swingx.renderer.CellContext
import javax.swing.JTree
import org.jdesktop.swingx.treetable.DefaultMutableTreeTableNode
import com.github.tyaathome.beanconverter.ui.bean.FieldBean
import org.jdesktop.swingx.renderer.ComponentProvider
import java.awt.BorderLayout
import java.lang.Boolean

class CheckTreeCellProvider(private val selectionModel: CheckTreeSelectionModel) : ComponentProvider<JPanel>() {
    private var _checkBox: TristateCheckBox? = null
    private var _label: JLabel? = null
    override fun format(arg0: CellContext) {
        //  从CellContext获取tree中的文字和图标
        val tree = arg0.component as JTree
        val node = arg0.value as DefaultMutableTreeTableNode
        val obj = node.userObject
        if (obj is FieldBean) {
            _label!!.text = obj.fieldName
            _checkBox!!.setSelector(obj)
        }

//        _label.setIcon(arg0.getIcon());

        //  根据selectionModel中的状态来绘制TristateCheckBox的外观
        val path = tree.getPathForRow(arg0.row)
        if (path != null) {
            if (selectionModel.isPathSelected(path, true)) {
                _checkBox!!.state = Boolean.TRUE
            } else if (selectionModel.isPartiallySelected(path)) {
                _checkBox!!.state = null //  注意“部分选中”状态的API
            } else {
                _checkBox!!.state = Boolean.FALSE
            }
        }

        //  使用BorderLayout布局，依次放置TristateCheckBox和JLabel
        rendererComponent.layout = BorderLayout()
        rendererComponent.add(_checkBox)
        rendererComponent.add(_label, BorderLayout.LINE_END)
    }

    override fun configureState(arg0: CellContext) {}

    /**
     * 初始化一个JPanel来放置TristateCheckBox和JLabel
     */
    override fun createRendererComponent(): JPanel {
        return JPanel()
    }

    init {
        _checkBox = TristateCheckBox() //  创建一个TristateCheckBox实例
        _checkBox?.setOpaque(false) //  设置TristateCheckBox不绘制背景
        _label = JLabel() //  创建一个JLabel实例
    }
}