package com.github.tyaathome.beanconverter.actions

import com.github.tyaathome.beanconverter.dialog.CodeDialog
import com.github.tyaathome.beanconverter.dialog.FieldsDialog
import com.github.tyaathome.beanconverter.utils.parseClass
import com.github.tyaathome.beanconverter.utils.showErrorDialog
import com.intellij.ide.highlighter.JavaFileType
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.psi.*

/**
 * Author: tya
 * Date: 2021/11/05
 * Desc:
 */
class GenerateCodeAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        // 数据检查
        val psiFile = e.getData(CommonDataKeys.PSI_FILE)
        val editor = e.getData(CommonDataKeys.EDITOR) ?: return;
        if (psiFile !is PsiJavaFile) {
            showErrorDialog("不支持该文件类型！")
            return
        }
        val dialog = CodeDialog()
        val project = e.project
        if (project == null) {
            showErrorDialog("project 为空")
            return
        }
        dialog.okAction = action@{
            val stringBuilder = StringBuilder()
            stringBuilder.append("public class Test {\n")
            stringBuilder.append(it + "\n")
            stringBuilder.append("}")
            val tempPsiFile = PsiFileFactory.getInstance(project).createFileFromText("Test.java", JavaFileType.INSTANCE, stringBuilder.toString()) as PsiJavaFile
            val psiClass = tempPsiFile.classes[0]
            val fieldList = parseClass(psiClass)

            val fieldsDialog = FieldsDialog.instance(fieldList)
            fieldsDialog.okAction = { classPath, extendsBean, fieldList ->
                WriteCommandAction.runWriteCommandAction(project) {
                    val factory = JavaPsiFacade.getElementFactory(project)
                    for (item in fieldList) {
                        val fieldCode = item.formatCode()
                        if(fieldCode.isEmpty()) {
                            continue
                        }
                        val element = psiFile.findElementAt(editor.caretModel.offset)
                        var codeBlock = element
                        while (codeBlock !is PsiClass) {
                            codeBlock = codeBlock?.parent
                        }
                        if (element != null) {
                            val fieldElement = factory.createFieldFromText(fieldCode, element.context)
                            codeBlock.addBefore(fieldElement, element)
                        }
                    }
                    fieldsDialog.dismiss()
                    dialog.dismiss()
                }
            }
            // 显示对话框
            fieldsDialog.setLocationRelativeTo(null);
            fieldsDialog.isVisible = true
            println(psiClass)
        }
        // 显示对话框
        dialog.setLocationRelativeTo(null)
        dialog.isVisible = true
    }

}