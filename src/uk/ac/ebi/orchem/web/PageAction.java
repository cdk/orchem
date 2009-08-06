/*  
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2008-2009  Mark Rijnbeek
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *  All we ask is that proper credit is given for our work, which includes
 *  - but is not limited to - adding the above copyright notice to the beginning
 *  of your source code files, and to any copyright notice that you may distribute
 *  with programs based on this work.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *
 */
package uk.ac.ebi.orchem.web;


import uk.ac.ebi.orchem.Utils;

/**
 * Struts Action to assist paging through a search result set.
 * Part of demo web-application. Not core cartridge functionality.
 * @author markr@ebi.ac.uk
 *
 */

public class PageAction extends SessionAwareAction { 

    public String execute() throws Exception {
        WebSearchResults wsr = (WebSearchResults)this.getSession().get(Utils.SESSION_WEB_SEARCH_RESULTS);
        wsr.setPageNum(page);
        getSession().put(Utils.SESSION_WEB_SEARCH_RESULTS, wsr);
        return "pagingDone";
    }
    
    /* Getters and setters */
    private int page;

    public void setPage(int _p) {
        this.page = _p;
    }

    public int getPage() {
        return page;
    }

}
