package com.github.tyaathome.beanconverter.ui

import org.jdesktop.swingx.treetable.DefaultMutableTreeTableNode
import java.util.*
import javax.swing.tree.*

class CheckTreeSelectionModel(private val model: TreeModel) : DefaultTreeSelectionModel() {
    // tests whether there is any unselected node in the subtree of given path
    fun isPartiallySelected(path: TreePath): Boolean {
        if (isPathSelected(path, true)) {
            return false
        }
        val selectionPaths = selectionPaths ?: return false
        for (j in selectionPaths.indices) {
            if (isDescendant(selectionPaths[j], path)) {
                return true
            }
        }
        return false
    }

    // tells whether given path is selected.
    // if dig is true, then a path is assumed to be selected, if
    // one of its ancestor is selected.
    fun isPathSelected(path: TreePath?, dig: Boolean): Boolean {
        var path = path
        if (!dig) {
            return super.isPathSelected(path)
        }
        while (path != null && !super.isPathSelected(path)) {
            path = path.parentPath
        }
        return path != null
    }

    // is path1 descendant of path2
    private fun isDescendant(path1: TreePath, path2: TreePath?): Boolean {
        val obj1 = path1.path
        val obj2 = path2?.path
        for (i in obj2?.indices!!) {
            if (obj1[i] !== obj2[i]) {
                return false
            }
        }
        return true
    }

    fun addPathsByNodes(selectedNodes: List<*>) {
        val num = selectedNodes.size
        val tps = arrayOfNulls<TreePath>(num)
        for (i in 0 until num) {
            val node = selectedNodes[i] as DefaultMutableTreeTableNode
            tps[i] = TreePath(getPathToRoot(node))
        }
        addSelectionPaths(tps)
    }

    override fun addSelectionPaths(paths: Array<TreePath?>) {
        // unselect all descendants of paths[]
        for (i in paths.indices) {
            val path = paths[i]
            val selectionPaths = selectionPaths ?: break
            val toBeRemoved: ArrayList<Any?> = ArrayList<Any?>()
            for (j in selectionPaths) {
                if (isDescendant(j, path)) {
                    toBeRemoved.add(j)
                }
            }
            super.removeSelectionPaths(toBeRemoved.toTypedArray() as Array<TreePath?>)
        }

        // if all siblings are selected then unselect them and select parent recursively
        // otherwize just select that path.
        for (i in paths.indices) {
            var path = paths[i]
            var temp: TreePath? = null
            while (areSiblingsSelected(path)) {
                temp = path
                if (path?.parentPath == null) {
                    break
                }
                path = path.parentPath
            }
            if (temp != null) {
                if (temp.parentPath != null) {
                    addSelectionPath(temp.parentPath)
                } else {
                    if (!isSelectionEmpty) {
                        removeSelectionPaths(selectionPaths)
                    }
                    super.addSelectionPaths(arrayOf(temp))
                }
            } else {
                super.addSelectionPaths(arrayOf(path))
            }
        }
    }

    // tells whether all siblings of given path are selected.
    private fun areSiblingsSelected(path: TreePath?): Boolean {
        val parent = path?.parentPath ?: return true
        val node = path.lastPathComponent
        val parentNode = parent.lastPathComponent
        val childCount = model.getChildCount(parentNode)
        for (i in 0 until childCount) {
            val childNode = model.getChild(parentNode, i)
            if (childNode === node) {
                continue
            }
            if (!isPathSelected(parent.pathByAddingChild(childNode))) {
                return false
            }
        }
        return true
    }

    override fun removeSelectionPaths(paths: Array<TreePath>) {
        for (i in paths.indices) {
            val path = paths[i]
            if (path.pathCount == 1) {
                super.removeSelectionPaths(arrayOf(path))
            } else {
                toggleRemoveSelection(path)
            }
        }
    }

    // if any ancestor node of given path is selected then unselect it
    //  and selection all its descendants except given path and descendants.
    // otherwise just unselect the given path
    private fun toggleRemoveSelection(path: TreePath) {
        val stack: Stack<Any?> = Stack<Any?>()
        var parent = path.parentPath
        while (parent != null && !isPathSelected(parent)) {
            stack.push(parent)
            parent = parent.parentPath
        }
        if (parent != null) {
            stack.push(parent)
        } else {
            super.removeSelectionPaths(arrayOf(path))
            return
        }
        while (!stack.isEmpty()) {
            val temp = stack.pop() as TreePath
            val peekPath = if (stack.isEmpty()) path else (stack.peek() as TreePath)
            val node = temp.lastPathComponent
            val peekNode = peekPath.lastPathComponent
            val childCount = model.getChildCount(node)
            for (i in 0 until childCount) {
                val childNode = model.getChild(node, i)
                if (childNode !== peekNode) {
                    super.addSelectionPaths(arrayOf(temp.pathByAddingChild(childNode)))
                }
            }
        }
        super.removeSelectionPaths(arrayOf(parent))
    }

    private fun getPathToRoot(aNode: TreeNode): Array<TreeNode?> {
        var aNode: TreeNode? = aNode
        val retNodes: Array<TreeNode?>
        val temp = ArrayList<TreeNode>()

        /* Check for null, in case someone passed in a null node, or
        they passed in an element that isn't rooted at root. */while (aNode != null) {
            temp.add(aNode)
            aNode = aNode.parent
        }
        val num = temp.size
        retNodes = arrayOfNulls(num)
        for (i in num - 1 downTo 0) {
            retNodes[num - 1 - i] = temp[i]
        }
        return retNodes
    }

    init {
        setSelectionMode(DISCONTIGUOUS_TREE_SELECTION)
    }
}