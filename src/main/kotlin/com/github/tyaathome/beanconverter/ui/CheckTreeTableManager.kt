package com.github.tyaathome.beanconverter.ui

import org.jdesktop.swingx.JXTreeTable
import java.awt.event.MouseAdapter
import javax.swing.event.TreeSelectionListener
import com.github.tyaathome.beanconverter.ui.CheckTreeSelectionModel
import javax.swing.JTree
import javax.swing.JCheckBox
import javax.swing.event.TreeSelectionEvent
import org.jdesktop.swingx.renderer.DefaultTreeRenderer
import com.github.tyaathome.beanconverter.ui.CheckTreeCellProvider
import java.awt.event.MouseEvent

class CheckTreeTableManager(private val treetable: JXTreeTable) : MouseAdapter(), TreeSelectionListener {
    val selectionModel: CheckTreeSelectionModel
    private val tree: JTree
    var hotspot = JCheckBox().preferredSize.width
    override fun mouseClicked(me: MouseEvent) {
        val path = tree.getPathForLocation(me.x, me.y) ?: return
        if (me.x > tree.getPathBounds(path).x + hotspot) {
            return
        }
        val selected = selectionModel.isPathSelected(path, true)
        selectionModel.removeTreeSelectionListener(this)
        try {
            if (selected) {
                selectionModel.removeSelectionPath(path)
            } else {
                selectionModel.addSelectionPath(path)
            }
        } finally {
            selectionModel.addTreeSelectionListener(this)
            treetable.repaint()
        }
    }

    override fun valueChanged(e: TreeSelectionEvent) {}

    init {
        tree = treetable.getCellRenderer(0, 0) as JTree
        selectionModel = CheckTreeSelectionModel(tree.model)
        tree.cellRenderer = DefaultTreeRenderer(CheckTreeCellProvider(selectionModel))
        treetable.addMouseListener(this)
        selectionModel.addTreeSelectionListener(this)
    }
}