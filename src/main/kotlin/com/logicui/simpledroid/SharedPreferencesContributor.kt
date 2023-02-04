package com.logicui.simpledroid

import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.lang.Language
import com.intellij.patterns.PlatformPatterns
import com.intellij.util.ProcessingContext


class SharedPreferencesContributor : CompletionContributor() {

    private val completionProvider = object : CompletionProvider<CompletionParameters?>() {
        override fun addCompletions(parameters: CompletionParameters, processingContext: ProcessingContext,
                                    result: CompletionResultSet) {

            val element = LookupElementBuilder.create("sharedPreferences")
                    .bold()
                    .withTailText(" Generate shared pref")
                    .withLookupString("getSharedPreferences")
                    .withInsertHandler { context, _ ->
                        val start = context.startOffset
                        val end = context.tailOffset
                        try {
                            throw Exception()
//                            val offset = context.editor.caretModel.offset
//                            var element = PsiUtilBase.getElementAtOffset(context.file, offset)
//                            while (element.javaClass != KtDotQualifiedExpression::class.java
//                                    && element.javaClass != KtClass::class.java) {
//                                element = element.parent ?: throw Exception()
//                            }
//                            if (element.javaClass == KtDotQualifiedExpression::class.java) {
//                                element = element.getChildOfType<KtNameReferenceExpression>() as PsiElement
//                                val variableName = element.lastChild.text
//                                val helper = PsiSearchHelper.getInstance(context.project)
//                            } else {
//                                val className = (element as KtClass).identifyingElement?.text ?: throw Exception()
//                                println(className)
//                            }
                        } catch (_: Exception) {
                            val newValue = "val sharedPreference = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)"
                            context.document.replaceString(start, end, newValue)
                            context.editor.caretModel.currentCaret.setSelection(start + 23, start + 30)
                        }
                    }
            result.addElement(element)
        }
    }

    init {
        val kotlin = Language.findLanguageByID("kotlin")
        if (kotlin != null) {
            extend(CompletionType.BASIC,
                PlatformPatterns.psiElement().withLanguage(kotlin)
                        .andNot(PlatformPatterns.psiElement().afterLeaf(".")),
                completionProvider
            )
        }
    }
}