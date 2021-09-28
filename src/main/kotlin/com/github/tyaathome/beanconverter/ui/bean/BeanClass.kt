package com.github.tyaathome.beanconverter.ui.bean

import com.github.tyaathome.beanconverter.ui.TristateCheckBox
import com.intellij.psi.PsiField

/**
 * Author: tya
 * Date: 2021/09/28
 * Desc:
 */

/**
 * 继承类数据bean
 */
data class ExtendsBean(var name: String, var packageName: String) {
    override fun toString(): String {
        return name
    }
}

/**
 * 字段数据bean
 */
data class FieldBean(var psiFiled: PsiField, var fieldName: String, var fieldType: FieldTypeBean?, var comment: String): TristateCheckBox.Selector {
    var selected = true
    override fun setSelect(select: Boolean) {
        selected = select
    }
}

/**
 * 字段类型数据bean
 */
data class FieldTypeBean(var nanoTypeName: String, var typeName: String) {
    override fun toString(): String {
        return nanoTypeName
    }
}