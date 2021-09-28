package com.github.tyaathome.beanconverter.ui

import com.github.tyaathome.beanconverter.ui.TristateCheckBox.TristateDecorator
import java.awt.event.*
import javax.swing.plaf.ActionMapUIResource
import javax.swing.*
import javax.swing.event.ChangeListener

class TristateCheckBox(text: String?, icon: Icon?, initial: Boolean?) : JCheckBox(text, icon) {
    private lateinit var decorator: TristateDecorator
    private var selector: Selector? = null
    fun setSelector(selector: Selector?) {
        this.selector = selector
    }

    @JvmOverloads
    constructor(text: String? = null, initial: Boolean? = true) : this(text, null, initial) {
    }

    /**
     * No one may add mouse listeners, not even Swing!
     */
    override fun addMouseListener(l: MouseListener) {}
    /**
     * Return the current state, which is determined by the
     * selection status of the model.
     */
    /**
     * Set the new state to either SELECTED, NOT_SELECTED or
     * DONT_CARE.  If state == null, it is treated as DONT_CARE.
     */
    var state: Boolean?
        get() = decorator.state
        set(state) {
            decorator.state = state
        }

    /**
     * Exactly which Design Pattern is this?  Is it an Adapter,
     * a Proxy or a Decorator?  In this case, my vote lies with the
     * Decorator, because we are extending functionality and
     * "decorating" the original model with a more powerful model.
     */
    private inner class TristateDecorator(private val other: ButtonModel) : ButtonModel {
        // normal deselected// don't care grey tick// normal black tick
        /**
         * The current state is embedded in the selection / armed
         * state of the model.
         *
         *
         * We return the SELECTED state when the checkbox is selected
         * but not armed, DONT_CARE state when the checkbox is
         * selected and armed (grey) and NOT_SELECTED when the
         * checkbox is deselected.
         */
        var state: Boolean?
            get() = if (isSelected && !isArmed) {
                // normal black tick
                java.lang.Boolean.TRUE
            } else if (isSelected && isArmed) {
                // don't care grey tick
                null
            } else {
                // normal deselected
                java.lang.Boolean.FALSE
            }
            set(state) {
                if (state === java.lang.Boolean.FALSE) {
                    other.isArmed = false
                    if (selector != null) {
                        selector!!.setSelect(false)
                    }
                    isPressed = false
                    isSelected = false
                } else if (state === java.lang.Boolean.TRUE) {
                    other.isArmed = false
                    isPressed = false
                    isSelected = true
                    if (selector != null) {
                        selector!!.setSelect(true)
                    }
                } else {
                    other.isArmed = true
                    isPressed = true
                    isSelected = true
                    if (selector != null) {
                        selector!!.setSelect(true)
                    }
                }
            }

        /**
         * We rotate between NOT_SELECTED, SELECTED and DONT_CARE.
         */
        fun nextState() {
            val current = state
            if (current === java.lang.Boolean.FALSE) {
                state = java.lang.Boolean.TRUE
            } else if (current === java.lang.Boolean.TRUE) {
                state = null
            } else if (current == null) {
                state = java.lang.Boolean.FALSE
            }
        }

        /**
         * Filter: No one may change the armed status except us.
         */
        override fun setArmed(b: Boolean) {}
        val isFocusTraversable: Boolean
            get() = isEnabled

        /**
         * We disable focusing on the component when it is not
         * enabled.
         */
        override fun setEnabled(b: Boolean) {
//            setFocusable(b);
            other.isEnabled = b
        }

        /**
         * All these methods simply delegate to the "other" model
         * that is being decorated.
         */
        override fun isArmed(): Boolean {
            return other.isArmed
        }

        override fun isSelected(): Boolean {
            return other.isSelected
        }

        override fun isEnabled(): Boolean {
            return other.isEnabled
        }

        override fun isPressed(): Boolean {
            return other.isPressed
        }

        override fun isRollover(): Boolean {
            return other.isRollover
        }

        override fun setSelected(b: Boolean) {
            other.isSelected = b
        }

        override fun setPressed(b: Boolean) {
            other.isPressed = b
        }

        override fun setRollover(b: Boolean) {
            other.isRollover = b
        }

        override fun setMnemonic(key: Int) {
            other.mnemonic = key
        }

        override fun getMnemonic(): Int {
            return other.mnemonic
        }

        override fun setActionCommand(s: String) {
            other.actionCommand = s
        }

        override fun getActionCommand(): String {
            return other.actionCommand
        }

        override fun setGroup(group: ButtonGroup) {
            other.group = group
        }

        override fun addActionListener(l: ActionListener) {
            other.addActionListener(l)
        }

        override fun removeActionListener(l: ActionListener) {
            other.removeActionListener(l)
        }

        override fun addItemListener(l: ItemListener) {
            other.addItemListener(l)
        }

        override fun removeItemListener(l: ItemListener) {
            other.removeItemListener(l)
        }

        override fun addChangeListener(l: ChangeListener) {
            other.addChangeListener(l)
        }

        override fun removeChangeListener(l: ChangeListener) {
            other.removeChangeListener(l)
        }

        override fun getSelectedObjects(): Array<Any> {
            return other.selectedObjects
        }
    }

    interface Selector {
        fun setSelect(select: Boolean)
    }

    init {
        // Add a listener for when the mouse is pressed
        super.addMouseListener(object : MouseAdapter() {
            override fun mousePressed(e: MouseEvent) {
                grabFocus()
                decorator.nextState()
            }
        })
        // Reset the keyboard action map
        val map: ActionMap = ActionMapUIResource()
        map.put("pressed", object : AbstractAction() {
            //NOI18N
            override fun actionPerformed(e: ActionEvent) {
                grabFocus()
                decorator.nextState()
            }
        })
        map.put("released", null) //NOI18N
        SwingUtilities.replaceUIActionMap(this, map)
        // set the model to the adapted model
        decorator = TristateDecorator(getModel())
        setModel(decorator)
        state = initial
    }
}