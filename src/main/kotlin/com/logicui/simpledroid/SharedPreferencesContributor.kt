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
                    .withInsertHandler { context, item ->
                        val start = context.startOffset
                        val end = context.tailOffset
                        val newValue = "requireContext().getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)"
                        context.document.replaceString(start, end, newValue)
                    }
            result.addElement(element)
        }
    }

    init {
        println(Language.findLanguageByID("kotlin"))
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