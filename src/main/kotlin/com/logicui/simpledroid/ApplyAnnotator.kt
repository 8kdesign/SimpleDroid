package com.logicui.simpledroid

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.PsiWhiteSpaceImpl
import com.intellij.ui.JBColor
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.endOffset
import org.jetbrains.kotlin.psi.psiUtil.getChildOfType
import org.jetbrains.kotlin.psi.psiUtil.startOffset

class ApplyAnnotator : Annotator {
    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        if (element is KtNamedFunction) {
            val block = element.getChildOfType<KtBlockExpression>() ?: return
            var currentChild: PsiElement? = block.firstChild
            var lastVariable = ""
            var startOffset = 0
            var endOffset = 0
            var count = 0
            while (currentChild != null) {
                if (currentChild is PsiWhiteSpaceImpl) {
                    currentChild = currentChild.nextSibling
                    continue
                }
                val referenceElement = currentChild.getChildOfType<KtNameReferenceExpression>()
                if ((currentChild is KtBinaryExpression || currentChild is KtDotQualifiedExpression)
                        && referenceElement != null) {
                    val variable = referenceElement.text
                    if (variable == lastVariable) {
                        count++
                        endOffset = currentChild.endOffset
                    } else {
                        checkHighlight(TextRange(startOffset, endOffset), count, holder)
                        count = 1
                        lastVariable = variable
                        startOffset = currentChild.startOffset
                        endOffset = currentChild.endOffset
                    }
                } else {
                    checkHighlight(TextRange(startOffset, endOffset), count, holder)
                    lastVariable = ""
                    count = 0
                }
                currentChild = currentChild.nextSibling
            }
            checkHighlight(TextRange(startOffset, endOffset), count, holder)
        }
    }

    private fun checkHighlight(range: TextRange, count: Int, holder: AnnotationHolder) {
        if (count > 2) {
            highlight(range, holder)
        }
    }
    private fun highlight(range: TextRange, holder: AnnotationHolder) {
        holder.newAnnotation(HighlightSeverity.INFORMATION, "Please use apply{}.")
                .range(range)
                .enforcedTextAttributes(TextAttributes().apply {
                    backgroundColor = if (JBColor.isBright()) {
                        JBColor.getHSBColor(0f, 0.12f, 1f)
                    } else {
                        JBColor.getHSBColor(0f, 0.41f, 0.23f)
                    }
                })
                .needsUpdateOnTyping()
                .create()
    }

}