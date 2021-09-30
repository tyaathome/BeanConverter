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
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.util.PsiElementFilter
import com.intellij.psi.util.PsiTreeUtil
import java.awt.Dimension
import java.io.File
import java.util.regex.Matcher
import java.util.regex.Pattern


/**
 * Author: tya
 * Date: 2021/09/24
 * Desc:
 */
class BeanConverterAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        // 数据检查
        val psiFile = e.getData(CommonDataKeys.PSI_FILE)
        if (psiFile !is PsiJavaFile) {
            showErrorDialog("不支持该文件类型！")
            return
        }
        val fileName = psiFile.name.split(".").let {
            if (it.size >= 2) {
                return@let it[0]
            } else {
                return@let ""
            }
        }
        var psiClass: PsiClass? = null
        for (item in psiFile.classes) {
            if (fileName == item.name) {
                psiClass = item
                break;
            }
        }
        if (psiClass == null) {
            showErrorDialog("没有找到对应的类")
            return
        }

        val project = e.project
        if (project == null) {
            showErrorDialog("project 为空")
            return
        }
        val factory = JavaPsiFacade.getElementFactory(project)
        // 获取父文件目录
        val directory = psiFile.parent
        val service = JavaDirectoryService.getInstance()
        if (directory == null) {
            showErrorDialog("父文件不是文件夹格式")
            return
        }

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

            }
        }

        var classFullName = ""
        var className = psiFile.name
        val array = className.split(".")
        if (array.isNotEmpty()) {
            className = array[0]
        }

        while (className.endsWith("Bean")) {
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
                // 父文件夹文件
                val directoryPath = directory.virtualFile.canonicalPath
                if (directoryPath == null || !File(directoryPath).exists()) {
                    showErrorDialog("目标父文件夹不存在")
                    return@action
                }
                val file = File(
                    directory.virtualFile.canonicalPath.plus("/$className.java")
                )
                if (file.exists()) {
                    showErrorDialog("文件已存在")
                    return@action
                }

                WriteCommandAction.runWriteCommandAction(project) {
                    // 生成viewModel文件
                    val viewModelClass =
                        service.createClass(directory, className, JavaTemplateUtil.INTERNAL_CLASS_TEMPLATE_NAME)
                    viewModelClass.modifierList?.setModifierProperty(PsiModifier.PUBLIC, true)

                    // 添加父类继承
                    viewModelClass.extendsList?.add(
                        PsiElementFactory.getInstance(project)
                            .createReferenceFromText(extendsBean.packageName, null)
                    )

                    val viewModelFile = viewModelClass.containingFile as PsiJavaFile
                    val viewModelPackage = service.getPackage(directory)
                    if (viewModelPackage != null) {
                        viewModelFile.packageName = viewModelPackage.qualifiedName
                    }

                    for (item in fieldList) {
                        if (!item.selected) {
                            continue
                        }
                        val field = item.psiFiled
                        if (field.hasModifierProperty(PsiModifier.STATIC)) {
                            continue
                        }
                        val code = StringBuilder().append("// ${item.comment}\n")
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
            } else {
                showErrorDialog("目标父文件夹不存在")
                return@action
            }
        }
        // 显示对话框
        dialog.setLocationRelativeTo(null);
        dialog.isVisible = true
    }

}