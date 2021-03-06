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
package org.sonar.api.web;

import org.sonar.api.ServerExtension;

/**
 * Adds content to HTML pages. A PageDecoration is a Rails template (html.erb file) that executes content_for blocks on predefined locations :
 * <ul>
 *   <li><code>script</code> : javascript header</li>
 *   <li><code>style</code> : CSS header</li>
 *   <li><code>header</code> : area over the black top navigation bar</li>
 *   <li><code>footer</code> : area below the main page</li>
 *   <li><code>sidebar</code> : area in the sidebar between the menu and the sonar logo</li>
 * </ul>
 *
 * <p>Example of template: </p>
<pre>
 <% content_for :script do %>
   <script>alert('page loaded')</script>
 <% end %>

 <% content_for :footer do %>
  <div>this is <b>my footer</b></div>
<% end %>
</pre>
 *
 * @since 3.3
 */
public abstract class PageDecoration extends AbstractRubyTemplate implements ServerExtension {

}
