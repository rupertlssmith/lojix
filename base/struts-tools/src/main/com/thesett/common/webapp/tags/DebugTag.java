/*
 * Copyright The Sett Ltd, 2005 to 2014.
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

import java.io.IOException;
import java.security.Principal;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * DebugTag defines a custom tag that can be used to print out extensive debugging information about a JSP page.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Generate debugging information for a JSP
 * </table></pre>
 *
 * @author Rupert Smith
 *
 * @jsp.tag
 *      name = "debug"
 */
public class DebugTag extends TagSupport
{
    /** Used for logging. */
    private static final java.util.logging.Logger log = java.util.logging.Logger.getLogger(DebugTag.class.getName());

    /**
     * Returns a String with all basic request information in an HTML table.
     *
     * @return A String with all basic request information in an HTML table.
     */
    public String getRequestInfo()
    {
        Map info = new TreeMap();
        HttpServletRequest req = (HttpServletRequest) pageContext.getRequest();

        info.put("authType", nullToString(req.getAuthType()));
        info.put("characterEncoding", nullToString(req.getCharacterEncoding()));
        info.put("contentLength", Integer.toString(req.getContentLength()));
        info.put("contentType", nullToString(req.getContentType()));
        info.put("contextPath", nullToString(req.getContextPath()));
        info.put("pathInfo", nullToString(req.getPathInfo()));
        info.put("protocol", nullToString(req.getProtocol()));
        info.put("queryString", nullToString(req.getQueryString()));
        info.put("remoteAddr", nullToString(req.getRemoteAddr()));
        info.put("remoteHost", nullToString(req.getRemoteHost()));
        info.put("remoteUser", nullToString(req.getRemoteUser()));
        info.put("requestURI", nullToString(req.getRequestURI()));
        info.put("scheme", nullToString(req.getScheme()));
        info.put("serverName", nullToString(req.getServerName()));
        info.put("serverPort", Integer.toString(req.getServerPort()));
        info.put("servletPath", nullToString(req.getServletPath()));

        return toHTMLTable("request properties", info);
    }

    /**
     * Returns a String with all header information as an HTML table.
     *
     * @return A String with all header information as an HTML table.
     */
    public String getHeaders()
    {
        Map info = new TreeMap();
        HttpServletRequest req = (HttpServletRequest) pageContext.getRequest();
        Enumeration names = req.getHeaderNames();

        while (names.hasMoreElements())
        {
            String name = (String) names.nextElement();
            Enumeration values = req.getHeaders(name);
            StringBuffer sb = new StringBuffer();
            boolean first = true;

            while (values.hasMoreElements())
            {
                if (!first)
                {
                    sb.append(" | ");
                }

                first = false;
                sb.append(values.nextElement());
            }

            info.put(name, sb.toString());
        }

        return toHTMLTable("headers", info);
    }

    /**
     * Returns a String with all cookie information as an HTML table.
     *
     * @return A String with all cookie information as an HTML table.
     */
    public String getCookies()
    {
        Map info = new TreeMap();
        HttpServletRequest req = (HttpServletRequest) pageContext.getRequest();
        Cookie[] cookies = req.getCookies();

        // check that cookies is not null which it may be if there are no cookies
        if (cookies != null)
        {
            for (int i = 0; i < cookies.length; i++)
            {
                Cookie cooky = cookies[i];
                info.put(cooky.getName(), cooky.getValue());
            }
        }

        return toHTMLTable("cookies", info);
    }

    /**
     * Returns a String with all request parameter information.
     *
     * @return A String with all request parameter information.
     */
    public String getParameters()
    {
        Map info = new TreeMap();
        HttpServletRequest req = (HttpServletRequest) pageContext.getRequest();
        Enumeration names = req.getParameterNames();

        while (names.hasMoreElements())
        {
            String name = (String) names.nextElement();
            String[] values = req.getParameterValues(name);
            StringBuffer sb = new StringBuffer();

            for (int i = 0; i < values.length; i++)
            {
                if (i != 0)
                {
                    sb.append(" | ");
                }

                sb.append(values[i]);
            }

            info.put(name, sb.toString());
        }

        return toHTMLTable("request parameters", info);
    }

    /**
     * Returns a String with all request scope variables.
     *
     * @return A String with all request scope variables.
     */
    public String getRequestScope()
    {
        Map info = new TreeMap();

        HttpServletRequest req = (HttpServletRequest) pageContext.getRequest();

        Enumeration names = req.getAttributeNames();

        while (names.hasMoreElements())
        {
            String name = (String) names.nextElement();

            Object value = req.getAttribute(name);

            info.put(name, toStringValue(value));
        }

        return toHTMLTable("request scope", info);
    }

    /**
     * Returns a String with all page scope variables.
     *
     * @return A String with all page scope variables.
     */
    public String getPageScope()
    {
        Map info = new TreeMap();
        Enumeration names = pageContext.getAttributeNamesInScope(PageContext.PAGE_SCOPE);

        while (names.hasMoreElements())
        {
            String name = (String) names.nextElement();
            Object value = pageContext.getAttribute(name);

            info.put(name, toStringValue(value));
        }

        return toHTMLTable("page scope", info);
    }

    /**
     * Returns a String with all session scope variables.
     *
     * @return A String with all session scope variables.
     */
    public String getSessionScope()
    {
        Map info = new TreeMap();
        HttpServletRequest req = (HttpServletRequest) pageContext.getRequest();
        HttpSession session = req.getSession();
        Enumeration names = session.getAttributeNames();

        while (names.hasMoreElements())
        {
            String name = (String) names.nextElement();
            Object value = session.getAttribute(name);

            info.put(name, toStringValue(value));
        }

        return toHTMLTable("session scope", info);
    }

    /**
     * Returns a String with all application scope variables.
     *
     * @return A String with all application scope variables.
     */
    public String getApplicationScope()
    {
        Map info = new TreeMap();
        ServletContext context = pageContext.getServletContext();
        Enumeration names = context.getAttributeNames();

        while (names.hasMoreElements())
        {
            String name = (String) names.nextElement();
            Object value = context.getAttribute(name);

            info.put(name, toStringValue(value));
        }

        return toHTMLTable("application scope", info);
    }

    /**
     * Returns the user principal name.
     *
     * @return The user principal name.
     */
    public String getUserPrincipal()
    {
        // Create a hash table to hold the results in
        Map info = new TreeMap();

        // Extract the request from the page context
        HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();

        // Get the principal from the request
        Principal principal = request.getUserPrincipal();

        // Check if there is a principal
        if (principal != null)
        {
            info.put("principal name", principal.getName());
        }
        else
        {
            info.put("principal name", "no principal");
        }

        // Convert the results to an HTML table
        return toHTMLTable("container security", info);
    }

    /**
     * Renders the debugging message.
     *
     * @return The tag return code. Always EVAL_BODY_INCLUDE.
     *
     * @throws JspException If the debug information cannot be written to the page.
     */
    public int doStartTag() throws JspException
    {
        log.fine("doStartTag: called");

        try
        {
            // Write out the beggining of the debug table
            pageContext.getResponse().getWriter().write("<table class=\"debug\" width=\"100%\" border=\"1\">");

            // Write out the debugging info for all categories
            pageContext.getResponse().getWriter().write(getRequestInfo());
            pageContext.getResponse().getWriter().write(getHeaders());
            pageContext.getResponse().getWriter().write(getCookies());
            pageContext.getResponse().getWriter().write(getParameters());
            pageContext.getResponse().getWriter().write(getRequestScope());
            pageContext.getResponse().getWriter().write(getPageScope());
            pageContext.getResponse().getWriter().write(getSessionScope());
            pageContext.getResponse().getWriter().write(getApplicationScope());
            pageContext.getResponse().getWriter().write(getUserPrincipal());

            // Write out the closing of the debug table
            pageContext.getResponse().getWriter().write("</table>");
        }
        catch (IOException e)
        {
            throw new JspException("Got an IOException whilst writing the debug tag to the page.", e);
        }

        // Continue processing the page
        return (EVAL_BODY_INCLUDE);
    }

    /**
     * Returns the String "null" if the value is null, otherwise the value itself.
     *
     * @param  value The string to check for being null.
     *
     * @return The string "null" or the specified string value.
     */
    private String nullToString(String value)
    {
        if (value == null)
        {
            return "null";
        }
        else
        {
            return value;
        }
    }

    /**
     * Returns an HTML table with all the values of the specified property.
     *
     * @param  propName The header for the table.
     * @param  values   The key/value paris to display in the table.
     *
     * @return The HTML table as a string.
     */
    private String toHTMLTable(String propName, Map values)
    {
        StringBuffer tableSB = new StringBuffer();

        tableSB.append("<tr class=\"debug\"><th class=\"debug\">").append(propName).append("</th></tr>");

        for (Iterator it = values.keySet().iterator(); it.hasNext();)
        {
            Object o = it.next();
            String key = (String) o;

            tableSB.append("<tr class=\"debug\"><td class=\"debug\">").append(key).append("</td><td>").append(
                values.get(key)).append("</td></tr>");
        }

        return tableSB.toString();
    }

    /**
     * Returns a string representation of the specified object, in a format suitable for debug output. If the object is
     * an array all its elements are extracted and displayed seperated by commas. Other objects are converted to strings
     * by their toString methods.
     *
     * @param  value The object value to display as a string.
     *
     * @return A string representation of the specified object.
     */
    private String toStringValue(Object value)
    {
        // Check if the value is null
        if (value == null)
        {
            return "null";
        }

        StringBuffer sb = new StringBuffer();

        Class type = value.getClass();

        if (type.isArray())
        {
            Class componentType = type.getComponentType();

            sb.append(componentType.getName());
            sb.append("[]: {");

            if (!componentType.isPrimitive())
            {
                Object[] arr = (Object[]) value;

                for (int i = 0; i < arr.length; i++)
                {
                    if (i != 0)
                    {
                        sb.append(", ");
                    }

                    sb.append(arr[i]);
                }
            }
            else if (componentType == Boolean.TYPE)
            {
                boolean[] arr = (boolean[]) value;

                for (int i = 0; i < arr.length; i++)
                {
                    if (i != 0)
                    {
                        sb.append(", ");
                    }

                    sb.append(arr[i]);
                }
            }
            else if (componentType == Byte.TYPE)
            {
                byte[] arr = (byte[]) value;

                for (int i = 0; i < arr.length; i++)
                {
                    if (i != 0)
                    {
                        sb.append(", ");
                    }

                    sb.append(arr[i]);
                }
            }
            else if (componentType == Character.TYPE)
            {
                char[] arr = (char[]) value;

                for (int i = 0; i < arr.length; i++)
                {
                    if (i != 0)
                    {
                        sb.append(", ");
                    }

                    sb.append(arr[i]);
                }
            }
            else if (componentType == Double.TYPE)
            {
                double[] arr = (double[]) value;

                for (int i = 0; i < arr.length; i++)
                {
                    if (i != 0)
                    {
                        sb.append(", ");
                    }

                    sb.append(arr[i]);
                }
            }
            else if (componentType == Float.TYPE)
            {
                float[] arr = (float[]) value;

                for (int i = 0; i < arr.length; i++)
                {
                    if (i != 0)
                    {
                        sb.append(", ");
                    }

                    sb.append(arr[i]);
                }
            }
            else if (componentType == Integer.TYPE)
            {
                int[] arr = (int[]) value;

                for (int i = 0; i < arr.length; i++)
                {
                    if (i != 0)
                    {
                        sb.append(", ");
                    }

                    sb.append(arr[i]);
                }
            }
            else if (componentType == Long.TYPE)
            {
                long[] arr = (long[]) value;

                for (int i = 0; i < arr.length; i++)
                {
                    if (i != 0)
                    {
                        sb.append(", ");
                    }

                    sb.append(arr[i]);
                }
            }
            else if (componentType == Short.TYPE)
            {
                short[] arr = (short[]) value;

                for (int i = 0; i < arr.length; i++)
                {
                    if (i != 0)
                    {
                        sb.append(", ");
                    }

                    sb.append(arr[i]);
                }
            }

            sb.append("}");
        }
        else
        {
            // Obtain the objects value using toString, but protect this against null pointer exceptions, to harden
            // this implementation.
            String stringValue = null;

            try
            {
                stringValue = value.toString();
            }
            catch (NullPointerException e)
            {
                stringValue = "";
            }

            sb.append(value.getClass().getName()).append(": ").append(stringValue);
        }

        return sb.toString();
    }
}
