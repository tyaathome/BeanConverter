package com.github.tyaathome.beanconverter.utils

import com.intellij.openapi.ui.Messages
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiJavaFile

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
        val arg = packageName.split(".")
        psiDirectory = psiFile.containingDirectory
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

