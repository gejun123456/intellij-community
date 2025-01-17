// Copyright 2000-2022 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package org.jetbrains.plugins.github.pullrequest.ui.timeline

import com.intellij.collaboration.ui.codereview.CodeReviewChatItemUIUtil
import com.intellij.collaboration.ui.codereview.CodeReviewChatItemUIUtil.ComponentType.FULL
import com.intellij.collaboration.ui.codereview.CodeReviewChatItemUIUtil.TEXT_CONTENT_WIDTH
import com.intellij.collaboration.ui.codereview.CodeReviewChatItemUIUtil.build
import com.intellij.collaboration.ui.codereview.CodeReviewTimelineUIUtil
import com.intellij.collaboration.ui.codereview.timeline.StatusMessageComponentFactory
import com.intellij.collaboration.ui.codereview.timeline.StatusMessageType
import com.intellij.openapi.util.text.HtmlBuilder
import com.intellij.openapi.util.text.HtmlChunk
import com.intellij.ui.components.panels.HorizontalLayout
import com.intellij.util.text.JBDateFormat
import com.intellij.util.ui.UIUtil
import org.jetbrains.annotations.Nls
import org.jetbrains.plugins.github.api.data.GHActor
import org.jetbrains.plugins.github.ui.avatars.GHAvatarIconsProvider
import org.jetbrains.plugins.github.ui.util.HtmlEditorPane
import java.util.*
import javax.swing.JComponent
import javax.swing.JPanel

internal object GHPRTimelineItemUIUtil {

  val TIMELINE_ITEM_WIDTH = TEXT_CONTENT_WIDTH + FULL.contentLeftShift + (CodeReviewTimelineUIUtil.ITEM_HOR_PADDING * 2)

  fun createTitleTextPane(actor: GHActor, date: Date?): HtmlEditorPane {
    val titleText = HtmlBuilder()
      .appendLink(actor.url, actor.getPresentableName())
      .append(HtmlChunk.nbsp())
      .apply {
        if (date != null) {
          append(JBDateFormat.getFormatter().formatPrettyDateTime(date))
        }
      }.toString()
    val titleTextPane = HtmlEditorPane(titleText).apply {
      foreground = UIUtil.getContextHelpForeground()
    }
    return titleTextPane
  }

  fun createTitlePane(actor: GHActor, date: Date?, additionalPanel: JComponent): JComponent {
    val titleTextPane = createTitleTextPane(actor, date)
    return JPanel(HorizontalLayout(10)).apply {
      isOpaque = false
      add(titleTextPane)
      add(additionalPanel)
    }
  }

  fun buildTimelineItem(avatarIconsProvider: GHAvatarIconsProvider,
                        actor: GHActor,
                        content: JComponent,
                        init: CodeReviewChatItemUIUtil.Builder.() -> Unit): JComponent =
    build(FULL, { avatarIconsProvider.getIcon(actor.avatarUrl, it) }, content) {
      iconTooltip = actor.getPresentableName()
      init()
    }

  fun createTimelineItem(avatarIconsProvider: GHAvatarIconsProvider,
                         actor: GHActor,
                         date: Date?,
                         content: JComponent,
                         actionsPanel: JComponent? = null): JComponent =
    buildTimelineItem(avatarIconsProvider, actor, content) {
      withHeader(createTitleTextPane(actor, date), actionsPanel)
      iconTooltip = actor.getPresentableName()
    }

  //language=HTML
  fun createDescriptionComponent(text: @Nls String, type: StatusMessageType = StatusMessageType.INFO): JComponent {
    val textPane = HtmlEditorPane(text)
    return StatusMessageComponentFactory.create(textPane, type)
  }
}