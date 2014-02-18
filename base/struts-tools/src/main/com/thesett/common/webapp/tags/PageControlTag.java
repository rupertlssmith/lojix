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

import java.net.MalformedURLException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import com.thesett.common.util.PagedList;

import org.apache.struts.taglib.TagUtils;

/**
 * PageControlTag renders a paging control for navigating amongst multiple pages. The information to be split into pages
 * should be held in a {@link com.thesett.common.util.PagedList} which is stored in a web session scope variable (it has
 * to be session or application scope to persist over multiple pages/requests). There is some flexibility in the way
 * that the paging control is rendered.
 *
 * <p/>It is possible to specify a maximum number of pages to display at once. If there are more pages than this then a
 * more button will be rendered to go to the next group of pages. The more button passes an index parameter to the page
 * action to tell it to update the index of the {@link com.thesett.common.util.PagedList}.
 *
 * <p/>The forward and back buttons will only be rendered if the forward and back parameters are set to true. The
 * forward and back buttons will also pass an index parameter to the paging control action if the maximum number of
 * pages is specified and if the forward or back button points to a page outside of those currently rendered. This will
 * adjust the current index similarly to the more button.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Render a paging control <td> {@link com.thesett.common.util.PagedList}
 * </table></pre>
 *
 * @author Rupert Smith
 *
 * @jsp.tag
 *      name = "pageControl"
 */
public class PageControlTag extends TagSupport
{
    /** Used for logging. */
    private static final java.util.logging.Logger log =
        java.util.logging.Logger.getLogger(PageControlTag.class.getName());

    /** Defines the default opening delimeter. */
    public static final String OPEN_DELIM = "";

    /** Defines the default opening delimeter to use for the first page button. */
    public static final String OPEN_DELIM_FIRST = "";

    /** Defines the default opening delimeter to use for the forward button. */
    public static final String OPEN_DELIM_FORWARD = "";

    /** Defines the default opening delimeter to use for the numnber buttons. */
    public static final String OPEN_DELIM_NUMBER = "";

    /** Defines the default opening delimeter to use for the current button. */
    public static final String OPEN_DELIM_CURRENT = "";

    /** Defines the default opening delimeter to use for the more button. */
    public static final String OPEN_DELIM_MORE = "";

    /** Defines the default opening delimeter to use for the back button. */
    public static final String OPEN_DELIM_BACK = "";

    /** Defines the default opening delimeter to use for the last button. */
    public static final String OPEN_DELIM_LAST = "";

    /** Defines the default closing delimeter. */
    public static final String CLOSE_DELIM = "";

    /** Defines the default first button text. */
    public static final String FIRST = "|&lt;";

    /** Defines the default back button text. */
    public static final String BACK = "previous";

    /** Defines the default more button text. */
    public static final String MORE = "more...";

    /** Defines the default forward button text. */
    public static final String FORWARD = "next";

    /** Defines the default last button text. */
    public static final String LAST = "&gt;|";

    /** Used to hold the name of the variable that holds the PagedList. */
    private String name;

    /** Used to hold the scope of the variable that holds the PagedList. */
    private String scope;

    /** Used to hold the property of the named variable to get the PagedList from. */
    private String property;

    /**
     * Used to hold the URL of the {@link com.thesett.common.webapp.actions.PageAction} action to process the paging
     * events.
     */
    private String action;

    /** Used to hold the maximum number of pages to display in the control at any one time. */
    private int maxPages;

    /** Used to indicate that a first page button should be rendered. */
    private boolean renderFirst = true;

    /** Used to indicate that a first page button should be rendered. */
    private boolean renderBack = true;

    /** Used to indicate that a first page button should be rendered. */
    private boolean renderForward = true;

    /** Used to indicate that a first page button should be rendered. */
    private boolean renderLast = true;

    /** Holds the optional opening delimeter to use for rendering the first page button. */
    private String openDelimFirst = OPEN_DELIM_FIRST;

    /** Holds the optional opening delimeter to use for rendering the forward page button. */
    private String openDelimForward = OPEN_DELIM_FORWARD;

    /** Holds the optional opening delimeter to use for rendering the number page button. */
    private String openDelimNumber = OPEN_DELIM_NUMBER;

    /** Holds the optional opening delimeter to use for rendering the current page button. */
    private String openDelimCurrent = OPEN_DELIM_CURRENT;

    /** Holds the optional opening delimeter to use for rendering the more page button. */
    private String openDelimMore = OPEN_DELIM_MORE;

    /** Holds the optional opening delimeter to use for rendering the back page button. */
    private String openDelimBack = OPEN_DELIM_BACK;

    /** Holds the optional opening delimeter to use for rendering the last page button. */
    private String openDelimLast = OPEN_DELIM_LAST;

    /** Holds the optioanl closing delimter to place after all buttons. */
    private String closeDelim = CLOSE_DELIM;

    /** Holds the optional string to render a first button. */
    private String firstText = FIRST;

    /** Holds the optional string to render a back button. */
    private String backText = BACK;

    /** Holds the optional string to render a more button. */
    private String moreText = MORE;

    /** Holds the optional string to render a forward button. */
    private String forwardText = FORWARD;

    /** Holds the optional string to render a last button. */
    private String lastText = LAST;

    /**
     * Sets the name of the variable to get the pages list from.
     *
     * @param         name The name of the variable to get the pages list from.
     *
     * @jsp:attribute required="true" rtexprvalue="true" type="java.lang.String"
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Sets the scope of the variable to get the paged list from.
     *
     * @param         scope The scope of the variable to get the paged list from.
     *
     * @jsp:attribute required="true" rtexprvalue="true" type="java.lang.String"
     */
    public void setScope(String scope)
    {
        this.scope = scope;
    }

    /**
     * Sets the (optional) property of the variable to get the paged list from.
     *
     * @param         property The property of the variable to get the paged list from.
     *
     * @jsp:attribute required="false" rtexprvalue="true" type="java.lang.String"
     */
    public void setProperty(String property)
    {
        this.property = property;
    }

    /**
     * Sets the name of the action to handle the paging requests.
     *
     * @param         action The name of the action to handle the paging requests.
     *
     * @jsp:attribute required="true" rtexprvalue="true" type="java.lang.String"
     */
    public void setAction(String action)
    {
        this.action = action;
    }

    /**
     * Setst the maximum number of pages to render in the control.
     *
     * @param         maxPages The maximum number of pages to render in the control.
     *
     * @jsp:attribute required="true" rtexprvalue="true" type="java.lang.String"
     */
    public void setMaxPages(int maxPages)
    {
        this.maxPages = maxPages;
    }

    /**
     * Determines whether of not to render the first button.
     *
     * @param         renderFirst Determines whether of not to render the first button.
     *
     * @jsp:attribute required="false" rtexprvalue="true" type="boolean"
     */
    public void setRenderFirst(boolean renderFirst)
    {
        this.renderFirst = renderFirst;
    }

    /**
     * Determines whether of not to render the back button.
     *
     * @param         renderBack Determines whether of not to render the back button.
     *
     * @jsp:attribute required="false" rtexprvalue="true" type="boolean"
     */
    public void setRenderBack(boolean renderBack)
    {
        this.renderBack = renderBack;
    }

    /**
     * Determines whether of not to render the forward button.
     *
     * @param         renderForward Determines whether of not to render the forward button.
     *
     * @jsp:attribute required="false" rtexprvalue="true" type="boolean"
     */
    public void setRenderForward(boolean renderForward)
    {
        this.renderForward = renderForward;
    }

    /**
     * Determines whether of not to render the last button.
     *
     * @param         renderLast Determines whether of not to render the last button.
     *
     * @jsp:attribute required="false" rtexprvalue="true" type="boolean"
     */
    public void setRenderLast(boolean renderLast)
    {
        this.renderLast = renderLast;
    }

    /**
     * Sets the optional openining delimeter for the first button.
     *
     * @param         openDelimFirst The optional openining delimeter for the first button.
     *
     * @jsp:attribute required="false" rtexprvalue="true" type="java.lang.String"
     */
    public void setOpenDelimFirst(String openDelimFirst)
    {
        this.openDelimFirst = openDelimFirst;
    }

    /**
     * Sets the optional openining delimeter for the forward button.
     *
     * @param         openDelimForward The optional openining delimeter for the forward button.
     *
     * @jsp:attribute required="false" rtexprvalue="true" type="java.lang.String"
     */
    public void setOpenDelimForward(String openDelimForward)
    {
        this.openDelimForward = openDelimForward;
    }

    /**
     * Sets the optional openining delimeter for the number button.
     *
     * @param         openDelimNumber The optional openining delimeter for the number button.
     *
     * @jsp:attribute required="false" rtexprvalue="true" type="java.lang.String"
     */
    public void setOpenDelimNumber(String openDelimNumber)
    {
        this.openDelimNumber = openDelimNumber;
    }

    /**
     * Sets the optional openining delimeter for the current button.
     *
     * @param         openDelimCurrent The optional openining delimeter for the current button.
     *
     * @jsp:attribute required="false" rtexprvalue="true" type="java.lang.String"
     */
    public void setOpenDelimCurrent(String openDelimCurrent)
    {
        this.openDelimCurrent = openDelimCurrent;
    }

    /**
     * Sets the optional openining delimeter for the more button.
     *
     * @param         openDelimMore The optional openining delimeter for the more button.
     *
     * @jsp:attribute required="false" rtexprvalue="true" type="java.lang.String"
     */
    public void setOpenDelimMore(String openDelimMore)
    {
        this.openDelimMore = openDelimMore;
    }

    /**
     * Sets the optional openining delimeter for the back button.
     *
     * @param         openDelimBack The optional openining delimeter for the back button.
     *
     * @jsp:attribute required="false" rtexprvalue="true" type="java.lang.String"
     */
    public void setOpenDelimBack(String openDelimBack)
    {
        this.openDelimBack = openDelimBack;
    }

    /**
     * Sets the optional openining delimeter for the last button.
     *
     * @param         openDelimLast The optional openining delimeter for the last button.
     *
     * @jsp:attribute required="false" rtexprvalue="true" type="java.lang.String"
     */
    public void setOpenDelimLast(String openDelimLast)
    {
        this.openDelimLast = openDelimLast;
    }

    /**
     * Sets the optional closing delimeter.
     *
     * @param         closeDelim The optional closing delimeter.
     *
     * @jsp:attribute required="false" rtexprvalue="true" type="java.lang.String"
     */
    public void setCloseDelim(String closeDelim)
    {
        this.closeDelim = closeDelim;
    }

    /**
     * Sets the optional text for the first button.
     *
     * @param         firstText The optional text for the first button.
     *
     * @jsp:attribute required="false" rtexprvalue="true" type="java.lang.String"
     */
    public void setFirstText(String firstText)
    {
        this.firstText = firstText;
    }

    /**
     * Sets the optional text for the back button.
     *
     * @param         backText The optional text for the back button.
     *
     * @jsp:attribute required="false" rtexprvalue="true" type="java.lang.String"
     */
    public void setBackText(String backText)
    {
        this.backText = backText;
    }

    /**
     * Sets the optional text for the more button.
     *
     * @param         moreText The optional text for the more button.
     *
     * @jsp:attribute required="false" rtexprvalue="true" type="java.lang.String"
     */
    public void setMoreText(String moreText)
    {
        this.moreText = moreText;
    }

    /**
     * Sets the optional text for the forward button.
     *
     * @param         forwardText The optional text for the forward button.
     *
     * @jsp:attribute required="false" rtexprvalue="true" type="java.lang.String"
     */
    public void setForwardText(String forwardText)
    {
        this.forwardText = forwardText;
    }

    /**
     * Sets the optional text for the last button.
     *
     * @param         lastText The optional text for the last button.
     *
     * @jsp:attribute required="false" rtexprvalue="true" type="java.lang.String"
     */
    public void setLastText(String lastText)
    {
        this.lastText = lastText;
    }

    /**
     * Renders the paging control.
     *
     * @return Always SKIP_BODY;
     *
     * @throws JspException If the named paged list variable cannot be found.
     */
    public int doStartTag() throws JspException
    {
        log.fine("public int doStartTag(): called");

        TagUtils tagUtils = TagUtils.getInstance();

        // Get a reference to the PagedList.
        PagedList list = (PagedList) tagUtils.lookup(pageContext, name, property, scope);
        log.fine("list = " + list);

        // Work out what the URL of the action to handle the paging events is.
        String url;

        try
        {
            url = tagUtils.computeURL(pageContext, null, null, null, action, null, null, null, false);
        }
        catch (MalformedURLException e)
        {
            throw new JspException("Got malformed URL exception: ", e);
        }

        // Optionally render the first page button.
        renderButton(renderFirst, 0, 0, openDelimFirst, url, firstText, list.getCurrentPage() != 0);

        // Optionally render the back button.
        renderButton(renderBack, list.getCurrentPage() - 1,
            ((list.getCurrentPage() - 1) < list.getCurrentIndex()) ? (list.getCurrentIndex() - maxPages)
                                                                   : list.getCurrentIndex(), openDelimBack, url,
            backText, (list.getCurrentPage() - 1) >= 0);

        // Render links for pages from the current index to the current index plus the maximum number of pages.
        int from = list.getCurrentIndex();
        int to = list.getCurrentIndex() + maxPages;

        for (int i = from; (i < list.size()) && (i < to); i++)
        {
            renderButton(true, i, list.getCurrentIndex(),
                (i == list.getCurrentPage()) ? openDelimCurrent : openDelimNumber, url, "" + (i + 1),
                i != list.getCurrentPage());
        }

        // Optionally render a more button. The more button should only be rendered if the current index plus
        // the maximum number of pages is less than the total number of pages so there are pages beyond those that
        // have numeric link to them already.
        renderButton((list.getCurrentIndex() + maxPages) < list.size(), list.getCurrentPage() + maxPages,
            list.getCurrentPage() + maxPages, openDelimMore, url, moreText, true);

        // Optionally render a forward button.
        renderButton(renderForward, list.getCurrentPage() + 1,
            ((list.getCurrentPage() + 1) >= (list.getCurrentIndex() + maxPages)) ? (list.getCurrentIndex() + maxPages)
                                                                                 : list.getCurrentIndex(),
            openDelimForward, url, forwardText, (list.getCurrentPage() + 1) < list.size());

        // Optionally render a last page button.
        renderButton(renderLast, list.size() - 1, (list.size() / maxPages) * maxPages, openDelimLast, url, lastText,
            list.getCurrentPage() != (list.size() - 1));

        return SKIP_BODY;
    }

    /**
     * Renders a button control as a hyperlink for the page control.
     *
     * @param  render    Whether or not to render the control.
     * @param  page      The page number that the control links to.
     * @param  index     The page secondary index that the control links to.
     * @param  openDelim The opening delimeter to put around the control. (A snippet of HTML).
     * @param  url       The url of the page action handler.
     * @param  text      The text to render the control.
     * @param  active    Whether or not the control should be made active.
     *
     * @throws JspException If an error occurs whilst writing out the response.
     */
    private void renderButton(boolean render, int page, int index, String openDelim, String url, String text,
        boolean active) throws JspException
    {
        log.fine(
            "private void renderButton(boolean render, int page, int index, String openDelim, String url, String text, boolean active): called");
        log.fine("render = " + render);
        log.fine("page = " + page);
        log.fine("index = " + index);
        log.fine("openDelim = " + openDelim);
        log.fine("url = " + url);
        log.fine("text = " + text);
        log.fine("active = " + active);

        TagUtils tagUtils = TagUtils.getInstance();

        if (render)
        {
            tagUtils.write(pageContext, openDelim);

            // Only render the button as active if the active flag is set.
            if (active)
            {
                tagUtils.write(pageContext,
                    "<a href=\"" + url + "?varName=" + name + "&number=" + page + "&index=" + index + "\">" + text +
                    "</a>");
            }

            // Render an inactive button.
            else
            {
                tagUtils.write(pageContext, text);
            }

            tagUtils.write(pageContext, closeDelim);
        }
    }
}
