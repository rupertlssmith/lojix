/*
 * Copyright The Sett Ltd, 2005 to 2009.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.thesett.common.webapp.tags;

import javax.servlet.jsp.PageContext;

/**
 * ScopeHelper sets up an interface on top of web variable scopes, abstracting them as simply 'get' and 'put'. By
 * specifying a scope name, 'page', 'request', 'session' or 'application' and a refering page context, this abstraction
 * is set up.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Read from web scopes.
 * <tr><td> Write to web scopes.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class ScopeHelper
{
    /** Holds the web scope identifier. */
    private int scope;

    /** Holds the page context that the web scopes are refered to from. */
    private PageContext pageContext;

    /**
     * Creates a scope helper on the named scope from the specified page context.
     *
     * @param scope       The name of the web scope.
     * @param pageContext The page context refering to the scope.
     */
    public ScopeHelper(String scope, PageContext pageContext)
    {
        // Keep the page context.
        this.pageContext = pageContext;

        // Set up the scope identifier.
        if (scope.equalsIgnoreCase("page"))
        {
            this.scope = PageContext.PAGE_SCOPE;
        }
        else if (scope.equalsIgnoreCase("request"))
        {
            this.scope = PageContext.REQUEST_SCOPE;
        }
        else if (scope.equalsIgnoreCase("session"))
        {
            this.scope = PageContext.SESSION_SCOPE;
        }
        else if (scope.equalsIgnoreCase("application"))
        {
            this.scope = PageContext.APPLICATION_SCOPE;
        }
        else
        {
            throw new IllegalArgumentException("Scope must be one of: page, request, session or application.");
        }
    }

    /**
     * Inserts an object into the scope.
     *
     * @param name  The name of the variable to set.
     * @param value The value to set.
     */
    public void put(String name, Object value)
    {
        pageContext.setAttribute(name, value, scope);
    }

    /**
     * Retrieves an object from the scope, or null if it cannot be found.
     *
     * @param  name The name of the variable to fetch.
     *
     * @return The variables value from the scope, or null if it cannot be found.
     */
    public Object get(String name)
    {
        return pageContext.getAttribute(name, scope);
    }
}
