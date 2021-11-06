package com.github.tyaathome.beanconverter.ui.bean

import com.intellij.psi.PsiField
import com.intellij.psi.PsiModifier

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
data class FieldBean(var psiFiled: PsiField, var fieldName: String, var fieldType: FieldTypeBean, var comment: String) {
    var selected = true
    fun setSelect(select: Boolean) {
        selected = select
    }

    /**
     * 生成格式化后的字段代码
     */
    fun formatCode(): String {
        if (!selected) {
            return ""
        }
        val field = psiFiled
        if (field.hasModifierProperty(PsiModifier.STATIC)) {
            return ""
        }
        val code = StringBuilder()
        comment.also {
            if (it.isNotEmpty()) {
                code.append("// ${it}\n")
            }
        }
        code.append("public final androidx.lifecycle.MutableLiveData<${fieldType.typeName}> $fieldName = new androidx.lifecycle.MutableLiveData<>();\n")
        return code.toString()
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