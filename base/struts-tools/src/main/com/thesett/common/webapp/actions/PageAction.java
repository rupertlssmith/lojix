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
package com.thesett.common.webapp.actions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.DynaActionForm;

import com.thesett.common.util.PagedList;

/**
 * PageAction is a Struts action class that facilitates dividing web pages into multiple sections with an index and
 * forward and back buttons to move through the pages. A typical example of such a control is found in search results
 * where the results are split into many pages.
 *
 * <p/>The collection to be split into pages must be placed into a {@link com.thesett.common.util.PagedList} which is a
 * list data structure that splits its contents into sub-lists. This action expects to find such an object in the
 * session scope variable named in the <tt>varName</tt> property of the page form. The page form should be set up in the
 * struts-config file as:
 *
 * <pre>
 * &lt;form-bean name="pageform" type="org.apache.struts.action.DynaActionForm"&gt;
 *   &lt;form-property name="varName" type="java.lang.String"/&gt;
 *   &lt;form-property name="number" type="int"/&gt;
 *   &lt;form-property name="index" type="int"/&gt;
 * &lt;/form-bean&gt;
 * </pre>
 *
 * <p/>This action simply updates the current page and optionally the index of the
 * {@link com.thesett.common.util.PagedList} and forwards to its success location.
 *
 * <p/>An entry should be created in the struts-config file for each page control needed as the SUCCESS location will
 * need to be configured to point back to the same page that the paging control is on in order for the web page to be
 * updated to the newly selected page. For example:
 *
 * <p/>See {@link com.thesett.common.webapp.tags.PageControlTag} for information about a useful tag for rendering a set
 * of paging controls.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Update current page or index offset of paged list
 *     <td> {@link com.thesett.common.util.PagedList}
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class PageAction extends BaseAction
{
    /** Used for logging. */
    private static final java.util.logging.Logger log = java.util.logging.Logger.getLogger(PageAction.class.getName());

    /** Holds the parameter name that the name of the session scope variable holding the paged list is passed in. */
    public static final String VAR_NAME_PARAM = "varName";

    /** Holds the parameter name that the page number to go to is passed in. */
    public static final String NUMBER_PARAM = "number";

    /** Holds the parameter name that the optional new page index is passed in. */
    public static final String INDEX_PARAM = "index";

    /** Holds the forward name of the success location. */
    private static final String SUCCESS_FORWARD = "success";

    /**
     * Updates the current page or index offset of a paged list in the session scope.
     *
     * <p/>This perform method is the same as the normal perform method but it is called from inside an error wrapper
     * that catches all throwables. The wrapper automatically generates Struts error messages for any Java throwable
     * that may fall through this method.
     *
     * <p/>Implementations should override this method to use the default error handling.
     *
     * @param  mapping  The ActionMapping used to select this instance
     * @param  form     The optional ActionForm bean for this request (if any)
     * @param  request  The HTTP request.
     * @param  response The HTTP response.
     * @param  errors   A struts errors object to write any error messages to.
     *
     * @return A struts forward to tell struts where to go after this action.
     *
     * @throws Exception Any exceptions are allowed to fall through to the top level handler.
     */
    public ActionForward executeWithErrorHandling(ActionMapping mapping, ActionForm form, HttpServletRequest request,
        HttpServletResponse response, ActionErrors errors) throws Exception
    {
        // Get a reference to the session.
        HttpSession session = request.getSession(false);

        // Extract the page form.
        DynaActionForm pageForm = (DynaActionForm) form;
        log.fine("pageForm = " + pageForm);

        // Get the paged list object from the session.
        String listingVarName = pageForm.getString(VAR_NAME_PARAM);
        log.fine("listingVarName = " + listingVarName);

        PagedList pagedList = (PagedList) session.getAttribute(listingVarName);

        // Set its current page.
        pagedList.setCurrentPage((Integer) pageForm.get(NUMBER_PARAM));

        // Set its index offset if one is specified.
        Integer index = (Integer) pageForm.get(INDEX_PARAM);

        if (index != null)
        {
            pagedList.setCurrentIndex(index);
        }

        // Forward to the success location.
        return mapping.findForward(SUCCESS_FORWARD);
    }
}
