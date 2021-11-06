package com.github.tyaathome.beanconverter.utils

import com.github.tyaathome.beanconverter.ui.bean.FieldBean
import com.github.tyaathome.beanconverter.ui.bean.FieldTypeBean
import com.intellij.openapi.ui.Messages
import com.intellij.psi.*
import com.intellij.psi.impl.source.PsiClassReferenceType
import com.intellij.psi.util.PsiElementFilter
import com.intellij.psi.util.PsiTreeUtil

/**
 * Author: tya
 * Date: 2021/09/28
 * Desc:
 */

/**
 * 通过psijava文件获取src文件
 */
fun getJavaSrc(psiFile: PsiJavaFile): PsiDirectory? {
    var psiDirectory: PsiDirectory? = null
    if(psiFile is PsiJavaFile) {
        val packageName = psiFile.packageName
        psiDirectory = psiFile.containingDirectory
        if(packageName.isEmpty()) {
            return psiDirectory
        }
        val arg = packageName.split(".")
        for(str in arg) {
            psiDirectory = psiDirectory?.parent
            if(psiDirectory == null) {
                break
            }
        }
    }
    return psiDirectory
}

fun getDirectoryByPackageName(psiFile: PsiJavaFile, packageName: String): PsiDirectory? {
    val srcDirectory = getJavaSrc(psiFile) ?: return null
    var result: PsiDirectory? = null
    val arg = packageName.split(".")
    if(arg.isEmpty()) {
        return result
    }
    result = srcDirectory
    for(item in arg) {
        result = result?.findSubdirectory(item)
        if(result == null) {
            return null
        }
    }
    return result
}

fun showErrorDialog(message: String) {
    Messages.showErrorDialog(message, "错误")
}

fun parseClass(psiClass: PsiClass): ArrayList<FieldBean> {
    val fieldList = ArrayList<FieldBean>()
    for (item in psiClass.fields) {
        if (item?.hasModifierProperty(PsiModifier.STATIC) == true) {
            continue
        }
        // 获取注释元素列表
        val comments =
            PsiTreeUtil.collectElements(
                item,
                PsiElementFilter { return@PsiElementFilter it is PsiComment })

        val commentSB = StringBuilder()
        for (commentItem in comments) {
//                if(commentItem is PsiDocComment) {
//                    comment.append(commentItem)
//                }
            if (commentItem is PsiComment) {
                commentSB.append(commentItem.text).append("\n")
            }
        }
        // 格式化注释
        val comment = commentSB.toString().replace("\\s*|\t|\r|\b", "")
            .replace(" ", "")
            .replace("*", "")
            .replace("/", "")
            .replace("\n", " ").trim()
        val type = item.type
        if (type is PsiClassReferenceType) {
            val canonicalText = type.reference.canonicalText
            val index = canonicalText.lastIndexOf(".")
            var nanoTypeText = if (index != -1) {
                canonicalText.substring(index + 1, canonicalText.length)
            } else {
                canonicalText
            }
            fieldList.add(
                FieldBean(
                    item,
                    item.name,
                    FieldTypeBean(type.reference.canonicalText, type.reference.canonicalText),
                    comment
                )
            )
        } else if (type is PsiPrimitiveType) {
            when {
                type.equalsToText(CommonClassNames.JAVA_LANG_LONG) -> {
                    println(1)
                }
            }
            var canonicalText: String
            when {
                PsiType.LONG.equals(type) -> {
                    canonicalText = CommonClassNames.JAVA_LANG_LONG
                }
                PsiType.BYTE.equals(type) -> {
                    canonicalText = CommonClassNames.JAVA_LANG_BYTE
                }
                PsiType.CHAR.equals(type) -> {
                    canonicalText = CommonClassNames.JAVA_LANG_CHARACTER
                }
                PsiType.INT.equals(type) -> {
                    canonicalText = CommonClassNames.JAVA_LANG_INTEGER
                }
                PsiType.BOOLEAN.equals(type) -> {
                    canonicalText = CommonClassNames.JAVA_LANG_BOOLEAN
                }
                PsiType.FLOAT.equals(type) -> {
                    canonicalText = CommonClassNames.JAVA_LANG_FLOAT
                }
                PsiType.SHORT.equals(type) -> {
                    canonicalText = CommonClassNames.JAVA_LANG_SHORT
                }
                else -> {
                    canonicalText = ""
                }
            }
            if(canonicalText.isNotEmpty()) {
                fieldList.add(
                    FieldBean(
                        item,
                        item.name,
                        FieldTypeBean(canonicalText, canonicalText),
                        comment
                    )
                )
            }

        }
    }
    return fieldList
}

