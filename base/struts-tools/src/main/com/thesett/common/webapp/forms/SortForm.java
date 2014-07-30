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
package com.thesett.common.webapp.forms;

import org.apache.struts.action.ActionForm;

/**
 * Struts form used to pass information about web variables to be sorted by a
 * {@link com.thesett.common.webapp.actions.SortAction}.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Hold names of variables to locate a list to be sorted and the comparator to sort it by.
 * </table></pre>
 *
 * @author Rupert Smith
 * @todo   Replace this with a DynaActionForm.
 *
 * @struts.form
 *         name = "sortForm"
 */
public class SortForm extends ActionForm
{
    /** Used to hold the name of the variable that holds the list to be sorted. */
    private String list;

    /** Used to hold the scope in which to find the variable to be sorted. */
    private String listScope;

    /** Used to hold the name of the variable that holds the {@link java.util.Comparator} used to perform the sort. */
    private String comparator;

    /** Used to hold the scope in which to find the Comparator to use to do the sorting. */
    private String comparatorScope;

    /**
     * Used to hold the name of the page scope variable that holds information about the current sort state of the list
     * (whether it is forward or reverse sorted). This determines whether a forward or reverse sort control will be
     * rendered.
     */
    private String sortState;

    /**
     * Used to hold the name of the property, heading, field or attribute that the sort is by. See
     * {@link com.thesett.common.webapp.beans.SortStateBean} for more information.
     */
    private String sortStateProperty;

    /**
     * Gets the name of the variable holding the list being sorted.
     *
     * @return The name of the variable holding the list being sorted.
     */
    public String getList()
    {
        return list;
    }

    /**
     * Sets the name of the variable holding the list to sort.
     *
     * @param list The name of the variable holding the list to sort.
     */
    public void setList(String list)
    {
        this.list = list;
    }

    /**
     * Gets the web scope under which the list should be stored.
     *
     * @return The web scope under which the list should be stored.
     */
    public String getListScope()
    {
        return listScope;
    }

    /**
     * Sets the web scope under which the list should be stored.
     *
     * @param listScope The web scope under which the list should be stored.
     */
    public void setListScope(String listScope)
    {
        this.listScope = listScope;
    }

    /**
     * Gets the name of the variable under which the sorting comparator can be found.
     *
     * @return The name of the variable under which the sorting comparator can be found.
     */
    public String getComparator()
    {
        return comparator;
    }

    /**
     * Sets the name of the variable under which the sorting comparator can be found.
     *
     * @param comparator The name of the variable under which the sorting comparator can be found.
     */
    public void setComparator(String comparator)
    {
        this.comparator = comparator;
    }

    /**
     * Gets the web scope under which the comparator can be found.
     *
     * @return The web scope under which the comparator can be found.
     */
    public String getComparatorScope()
    {
        return comparatorScope;
    }

    /**
     * Sets the web scope under which the comparator can be found.
     *
     * @param comparatorScope The web scope under which the comparator can be found.
     */
    public void setComparatorScope(String comparatorScope)
    {
        this.comparatorScope = comparatorScope;
    }

    /**
     * Gets the name of the variable under which the sort state bean can be found.
     *
     * @return The name of the variable under which the sort state bean can be found.
     */
    public String getSortState()
    {
        return sortState;
    }

    /**
     * Sets the name of the variable under which the sort state bean can be found.
     *
     * @param sortState The name of the variable under which the sort state bean can be found.
     */
    public void setSortState(String sortState)
    {
        this.sortState = sortState;
    }

    /**
     * Gets the name of the property of the obejcts being sorted that the sort is by.
     *
     * @return The name of the property of the obejcts being sorted that the sort is by.
     */
    public String getSortStateProperty()
    {
        return sortStateProperty;
    }

    /**
     * Sets the name of the property of the obejcts being sorted that the sort is by. Used to distinguish different
     * types of sort on the same set of objects.
     *
     * @param sortStateProperty The name of the property of the obejcts being sorted that the sort is by.
     */
    public void setSortStateProperty(String sortStateProperty)
    {
        this.sortStateProperty = sortStateProperty;
    }
}
