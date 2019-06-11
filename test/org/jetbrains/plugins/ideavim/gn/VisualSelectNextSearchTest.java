/*
 * IdeaVim - Vim emulator for IDEs based on the IntelliJ platform
 * Copyright (C) 2003-2019 The IdeaVim authors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package org.jetbrains.plugins.ideavim.gn;

import com.maddyhome.idea.vim.VimPlugin;
import com.maddyhome.idea.vim.command.CommandFlags;
import com.maddyhome.idea.vim.command.CommandState;
import org.jetbrains.plugins.ideavim.VimTestCase;

import java.util.EnumSet;

import static com.maddyhome.idea.vim.helper.StringHelper.parseKeys;

public class VisualSelectNextSearchTest extends VimTestCase {
  public void testSearch() {
    typeTextInFile(parseKeys("*", "b", "gn"), "h<caret>ello world\nhello world hello world");

    assertOffset(16);
    assertSelection("hello");
    assertMode(CommandState.Mode.VISUAL);
  }

  public void testSearchFordAndBack() {
    typeTextInFile(parseKeys("*", "2b", "gn", "gN"), "h<caret>ello world\nhello world hello world");

    assertOffset(0);
    assertSelection("h");
    assertMode(CommandState.Mode.VISUAL);
  }

  public void testWithoutSpaces() {
    configureByText("test<caret>test");
    VimPlugin.getSearch().search(myFixture.getEditor(), "test", 1, EnumSet.noneOf(CommandFlags.class), false);
    typeText(parseKeys("gn"));

    assertOffset(7);
    assertSelection("test");
    assertMode(CommandState.Mode.VISUAL);
  }

  public void testSearchCurrentlyInOne() {
    typeTextInFile(parseKeys("*", "gn"), "h<caret>ello world\nhello world hello world");

    assertOffset(16);
    assertSelection("hello");
    assertMode(CommandState.Mode.VISUAL);
  }

  public void testSearchTwice() {
    typeTextInFile(parseKeys("*", "2gn"), "h<caret>ello world\nhello world hello, hello");

    assertOffset(28);
    assertSelection("hello");
  }

  public void testSearchTwiceInVisual() {
    typeTextInFile(parseKeys("*", "gn", "2gn"), "h<caret>ello world\nhello world hello, hello hello");

    assertOffset(35);
    assertSelection("hello world hello, hello");
  }

  public void testTwoSearchesStayInVisualMode() {
    typeTextInFile(parseKeys("*", "gn", "gn"), "h<caret>ello world\nhello world hello, hello");

    assertOffset(28);
    assertSelection("hello world hello");
    assertMode(CommandState.Mode.VISUAL);
  }

  public void testCanExitVisualMode() {
    typeTextInFile(parseKeys("*", "gn", "gn", "<Esc>"), "h<caret>ello world\nhello world hello, hello");

    assertOffset(28);
    assertSelection(null);
    assertMode(CommandState.Mode.COMMAND);
  }

  public void testNullSelectionDoesNothing() {
    typeTextInFile(parseKeys("/bye<CR>", "gn"), "h<caret>ello world\nhello world hello world");

    assertOffset(1);
    assertSelection(null);
  }

  public void testIfInLastPositionOfSearchAndInNormalModeThenSelectCurrent() {
    typeTextInFile(parseKeys("*0e", "gn"), "h<caret>ello hello");

    assertOffset(4);
    assertSelection("hello");
    assertMode(CommandState.Mode.VISUAL);
  }

  public void testIfInMiddlePositionOfSearchAndInVisualModeThenSelectCurrent() {
    typeTextInFile(parseKeys("*0llv", "gn"), "h<caret>ello hello");

    assertOffset(4);
    assertSelection("llo");
    assertMode(CommandState.Mode.VISUAL);
  }

  public void testIfInLastPositionOfSearchAndInVisualModeThenSelectNext() {
    typeTextInFile(parseKeys("*0ev", "gn"), "h<caret>ello hello");

    assertOffset(10);
    assertSelection("o hello");
    assertMode(CommandState.Mode.VISUAL);
  }

  public void testMixWithN() {
    typeTextInFile(parseKeys("*", "gn", "n", "gn"), "h<caret>ello world\nhello world hello, hello");

    assertOffset(28);
    assertSelection("hello world hello");
    assertMode(CommandState.Mode.VISUAL);
  }

  public void testMixWithPreviousSearch() {
    typeTextInFile(parseKeys("*", "gn", "gn", "gN", "gn"), "h<caret>ello world\nhello world hello, hello");

    assertOffset(28);
    assertSelection("hello world hello");
    assertMode(CommandState.Mode.VISUAL);
  }
}