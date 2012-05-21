/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2008-2012 SonarSource
 * mailto:contact AT sonarsource DOT com
 *
 * Sonar is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * Sonar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Sonar; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.core.review.workflow;

import org.fest.assertions.MapAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.core.review.workflow.condition.Condition;
import org.sonar.core.review.workflow.condition.HasProjectPropertyCondition;
import org.sonar.core.review.workflow.condition.StatusCondition;
import org.sonar.core.review.workflow.function.TransitionFunction;
import org.sonar.core.review.workflow.screen.CommentScreen;

import static org.fest.assertions.Assertions.assertThat;

public class WorkflowTest {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void addCommand() {
    Workflow workflow = new Workflow();
    assertThat(workflow.getCommands()).isEmpty();

    assertThat(workflow.addCommand("resolve")).isSameAs(workflow);
    assertThat(workflow.getCommands()).containsOnly("resolve");
    assertThat(workflow.hasCommand("resolve")).isTrue();
  }

  @Test
  public void addCommand_does_not_accept_blank() {
    thrown.expect(IllegalArgumentException.class);
    thrown.expectMessage("Empty command key");

    Workflow workflow = new Workflow();
    workflow.addCommand("");
  }

  @Test
  public void addSeveralTimesTheSameCommand() {
    Workflow workflow = new Workflow();
    workflow.addCommand("resolve");
    workflow.addCommand("resolve");
    assertThat(workflow.getCommands()).containsOnly("resolve");
    assertThat(workflow.getCommands()).hasSize(1);
  }

  @Test
  public void addCondition_fail_if_unknown_command() {
    thrown.expect(IllegalArgumentException.class);
    thrown.expectMessage("Unknown command: resolve");

    Workflow workflow = new Workflow();
    workflow.addCondition("resolve", new StatusCondition("OPEN"));
  }

  @Test
  public void addAndGetCondition() {
    Workflow workflow = new Workflow();
    Condition condition = new StatusCondition("OPEN");
    workflow.addCommand("resolve");

    workflow.addCondition("resolve", condition);

    assertThat(workflow.getConditions("resolve")).containsExactly(condition);
  }

  @Test
  public void getConditions_empty() {
    Workflow workflow = new Workflow();
    assertThat(workflow.getConditions("resolve")).isEmpty();
  }

  @Test
  public void keepCacheOfProjectPropertiesRequiredByConditions() {
    Workflow workflow = new Workflow();
    Condition condition1 = new HasProjectPropertyCondition("jira.url");
    Condition condition2 = new HasProjectPropertyCondition("jira.login");
    workflow.addCommand("create-jira-issue");
    workflow.addCondition("create-jira-issue", condition1);
    workflow.addCondition("create-jira-issue", condition2);

    assertThat(workflow.getProjectPropertyKeys()).containsExactly("jira.url", "jira.login");
  }

  @Test
  public void cacheOfProjectPropertiesIsNotNull() {
    Workflow workflow = new Workflow();

    assertThat(workflow.getProjectPropertyKeys()).isEmpty();
  }

  @Test
  public void addAndGetFunctions() {
    Workflow workflow = new Workflow();
    workflow.addCommand("resolve");

    TransitionFunction function = new TransitionFunction("OPEN", "resolved");
    workflow.addFunction("resolve", function);

    assertThat(workflow.getFunctions("resolve")).containsExactly(function);
  }

  @Test
  public void getFunctions_empty() {
    Workflow workflow = new Workflow();
    assertThat(workflow.getFunctions("resolve")).isEmpty();
  }

  @Test
  public void addFunction_fail_if_unknown_command() {
    thrown.expect(IllegalArgumentException.class);
    thrown.expectMessage("Unknown command: resolve");

    Workflow workflow = new Workflow();
    workflow.addFunction("resolve", new TransitionFunction("OPEN", "resolved"));
  }

  @Test
  public void setScreen_fail_if_unknown_command() {
    thrown.expect(IllegalArgumentException.class);
    thrown.expectMessage("Unknown command: resolve");

    Workflow workflow = new Workflow();
    workflow.setScreen("resolve", new CommentScreen());
  }

  @Test
  public void setScreen() {
    Workflow workflow = new Workflow();
    workflow.addCommand("resolve");
    CommentScreen screen = new CommentScreen();
    workflow.setScreen("resolve", screen);

    assertThat(workflow.getScreen("resolve")).isSameAs(screen);
    assertThat(workflow.getScreensByCommand()).includes(MapAssert.entry("resolve", screen));
    assertThat(workflow.getScreensByCommand()).hasSize(1);
  }
}