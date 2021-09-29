package com.github.tyaathome.beanconverter.actions

import com.github.tyaathome.beanconverter.ui.bean.FieldBean
import com.github.tyaathome.beanconverter.ui.bean.FieldTypeBean
import com.github.tyaathome.beanconverter.dialog.FieldsDialog
import com.github.tyaathome.beanconverter.utils.getDirectoryByPackageName
import com.github.tyaathome.beanconverter.utils.showErrorDialog
import com.intellij.ide.fileTemplates.JavaTemplateUtil
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.ui.Messages
import com.intellij.psi.*
import com.intellij.psi.codeStyle.JavaCodeStyleManager
import com.intellij.psi.impl.source.PsiClassReferenceType
import com.intellij.psi.javadoc.PsiDocComment
import com.intellij.psi.util.PsiElementFilter
import com.intellij.psi.util.PsiTreeUtil
import java.awt.Dimension
import java.io.File


/**
 * Author: tya
 * Date: 2021/09/24
 * Desc:
 */
class BeanConverterAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        // 数据检查
        val psiFile = e.getData(CommonDataKeys.PSI_FILE)
        if(psiFile !is PsiJavaFile) {
            showErrorDialog("不支持该文件类型！")
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
            showErrorDialog("没有找到对应的类")
            return
        }

        val project = e.project
        if(project == null) {
            showErrorDialog("project 为空")
            return
        }
        val factory = JavaPsiFacade.getElementFactory(project)
        // 获取父文件目录
        val directory = psiFile.parent
        val service = JavaDirectoryService.getInstance()
        if(directory == null) {
            showErrorDialog("父文件不是文件夹格式")
            return
        }

        val fieldList = ArrayList<FieldBean>()
        for(item in psiClass.fields) {
            if(item?.hasModifierProperty(PsiModifier.STATIC) == true) {
                continue
            }
            // 获取注释元素列表
            val comments =
                PsiTreeUtil.collectElements(
                    item,
                    PsiElementFilter { return@PsiElementFilter it is PsiComment })

            val comment = StringBuilder()
            for (commentItem in comments) {
//                if(commentItem is PsiDocComment) {
//                    comment.append(commentItem)
//                }
                if (commentItem is PsiComment) {
                    comment.append(commentItem.text).append("\n")
                }
            }
            val type = item.type
            if(type is PsiClassReferenceType) {
                val canonicalText = type.reference.canonicalText
                val index = canonicalText.lastIndexOf(".")
                var nanoTypeText = if(index != -1) {
                    canonicalText.substring(index+1, canonicalText.length)
                } else {
                    canonicalText
                }
                fieldList.add(FieldBean(item, item.name, FieldTypeBean(type.reference.canonicalText, type.reference.canonicalText), comment.toString()))

            }
        }

        var classFullName = ""
        var className = psiFile.name
        val array = className.split(".")
        if(array.isNotEmpty()) {
            className = array[0]
        }
        while(className.endsWith("Bean")) {
            className = className.substring(0, className.indexOf("Bean"))
        }
        classFullName = "${psiFile.packageName}.${className}ViewModel"

        // 显示对话框
        val dialog = FieldsDialog(classFullName, fieldList)
        dialog.okAction = action@{ classPath, extendsBean, fieldList ->
            val index = classPath.lastIndexOf(".")
            if (index == -1) {
                showErrorDialog("输入的类名称有误")
                return@action
            }
            val pageName = classPath.substring(0, index)
            val className = classPath.substring(index + 1, classPath.length)
            val directory = getDirectoryByPackageName(psiFile, pageName)
            if (directory != null) {
                val file = File(
                    directory.virtualFile.canonicalPath.plus("/$className.java")
                )
                if (file.exists()) {
                    showErrorDialog("文件已存在")
                    return@action
                }
                val viewModelClass =
                    service.createClass(directory, className, JavaTemplateUtil.INTERNAL_CLASS_TEMPLATE_NAME)
                viewModelClass.modifierList?.setModifierProperty(PsiModifier.PUBLIC, true)
                val viewModelFile = viewModelClass.containingFile as PsiJavaFile

                WriteCommandAction.runWriteCommandAction(project) {
                    val viewModelPackage = service.getPackage(directory)
                    if (viewModelPackage != null) {
                        viewModelFile.packageName = viewModelPackage.qualifiedName
                    }

                    for (item in fieldList) {
                        if(!item.selected) {
                            continue
                        }
                        val field = item.psiFiled
                        if (field.hasModifierProperty(PsiModifier.STATIC)) {
                            continue
                        }
                        println(field.name)
                        println(field.docComment)

                        val code = StringBuilder().append(item.comment)
                        val type = field.type
                        if (type is PsiClassReferenceType) {
                            code.append("public final androidx.lifecycle.MutableLiveData<${item.fieldType.typeName}> ${item.fieldName} = new androidx.lifecycle.MutableLiveData<>();\n")
                            viewModelClass.add(factory.createFieldFromText(code.toString(), viewModelClass))
                        }
                    }

                    val manager = JavaCodeStyleManager.getInstance(project)
                    manager.optimizeImports(viewModelFile)
                    manager.shortenClassReferences(viewModelClass)
                    dialog.dismiss()
                }
            }
        }

        dialog.isVisible = true
    }

}