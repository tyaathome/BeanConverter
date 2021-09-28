package com.github.tyaathome.beanconverter.actions

import com.github.tyaathome.beanconverter.bean.FieldBean
import com.github.tyaathome.beanconverter.bean.FieldTypeBean
import com.github.tyaathome.beanconverter.dialog.FieldsDialog
import com.github.tyaathome.beanconverter.utils.getDirectoryByPackageName
import com.intellij.ide.fileTemplates.JavaTemplateUtil
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.psi.*
import com.intellij.psi.codeStyle.JavaCodeStyleManager
import com.intellij.psi.impl.source.PsiClassReferenceType
import com.intellij.psi.util.PsiElementFilter
import com.intellij.psi.util.PsiTreeUtil
import java.io.File


/**
 * Author: tya
 * Date: 2021/09/24
 * Desc:
 */
class BeanConverterAction : AnAction() {

    private val testMode = true

    fun doOkAction(e: AnActionEvent): () -> Unit {
        return {
//            // 创建viewmodel类文件
//            val viewModelClass = service.createClass(directory, "Testt", JavaTemplateUtil.INTERNAL_CLASS_TEMPLATE_NAME)
//            viewModelClass.modifierList?.setModifierProperty(PsiModifier.PUBLIC, true)
//            val viewModelFile = viewModelClass.containingFile as PsiJavaFile
//
//            WriteCommandAction.runWriteCommandAction(project) {
//                val viewModelPackage = service.getPackage(directory)
//                if (viewModelPackage != null) {
//                    viewModelFile.packageName = viewModelPackage.qualifiedName
//                }
//
//                for (item in psiClass.fields) {
//                    if (item?.hasModifierProperty(PsiModifier.STATIC) == true) {
//                        continue
//                    }
//                    println(item.name)
//                    println(item.docComment)
//                    // 获取注释元素列表
//                    val comments =
//                        PsiTreeUtil.collectElements(item, PsiElementFilter { return@PsiElementFilter it is PsiComment })
//
//                    val code = StringBuilder()
//                    for (commentItem in comments) {
//                        if (commentItem is PsiComment) {
//                            code.append(commentItem.text).append("\n")
//                        }
//                    }
//
//                    val type = item.type
//                    if (type is PsiClassReferenceType) {
//                        code.append("public final androidx.lifecycle.MutableLiveData<${type.reference.canonicalText}> ${item.name} = new androidx.lifecycle.MutableLiveData<>();\n\n\n\n")
//                        viewModelClass.add(factory.createFieldFromText(code.toString(), viewModelClass))
////                    when {
////                        type.equalsToText(CommonClassNames.JAVA_LANG_STRING) -> {
////
////                        }
////                    }
//
//                    }
//                }
//
//                val manager = JavaCodeStyleManager.getInstance(project)
//                manager.optimizeImports(viewModelFile)
//                manager.shortenClassReferences(viewModelClass)
            }
        }

    override fun actionPerformed(e: AnActionEvent) {
        // 数据检查
        val psiFile = e.getData(CommonDataKeys.PSI_FILE)
        if(psiFile !is PsiJavaFile) {
            println("不支持该文件类型！")
            return
        }
        val fileName = psiFile.name.split(".").let {
            if(it.size >= 2) {
                return@let it[0]
            } else {
                return@let ""
            }
        }
        var psiClass: PsiClass? = null
        for(item in psiFile.classes) {
            if(fileName == item.name) {
                psiClass = item
                break;
            }
        }
        if(psiClass == null) {
            println("没有找到对应的类")
            return
        }

        val project = e.project
        if(project == null) {
            println("project 为空")
            return
        }
        val factory = JavaPsiFacade.getElementFactory(project)
        // 获取父文件目录
        val directory = psiFile.parent
        val service = JavaDirectoryService.getInstance()
        if(directory == null) {
            println("父文件不是文件夹格式")
            return
        }



        val fieldList = ArrayList<FieldBean>()
        for(item in psiClass.fields) {
            if(item?.hasModifierProperty(PsiModifier.STATIC) == true) {
                continue
            }
            // TODO: 2021/9/28 加注释功能

            val type = item.type
            if(type is PsiClassReferenceType) {
                var nanoTypeText = ""
                val canonicalText = type.reference.canonicalText
                val index = canonicalText.lastIndexOf(".")
                nanoTypeText = if(index != -1) {
                    canonicalText.substring(index+1, canonicalText.length)
                } else {
                    canonicalText
                }
                fieldList.add(FieldBean(item, item.name, FieldTypeBean(nanoTypeText, type.reference.canonicalText)))

            }
        }


        // 显示对话框
        val dialog = FieldsDialog()
        dialog.okAction = doOkAction(e)
        dialog.showAndGet()












//        if(testMode) {
//            // 通过路径获取文件
//            val psiFile = e.getData(CommonDataKeys.PSI_FILE)
//            if(psiFile is PsiJavaFile) {
//                val pageName = "com.tyaathome.bean"
//                val className = "Abc"
//                val directory = getDirectoryByPackageName(psiFile, pageName)
//                if (directory != null) {
//                    val file = File(
//                        directory.virtualFile.canonicalPath.plus("/$className.java")
//                    )
//                    if(file.exists()) {
//                        println("文件已存在")
//                        return
//                    }
//                    val viewModelClass = JavaDirectoryService.getInstance()
//                        .createClass(directory, className, JavaTemplateUtil.INTERNAL_CLASS_TEMPLATE_NAME)
//                }
//            }
//
//            val dialog = FieldsDialog()
//            dialog.okAction = {
//                dialog.close(0)
//            }
//            dialog.showAndGet()
//            return
//        }

    }
}