package uk.ac.ebi.orchem.web;


import uk.ac.ebi.orchem.Constants;

/**
 * Struts Action to assist paging through a search result set.
 * Part of mock web-application. Not core cartridge functionality.
 * 
 */

public class PageAction extends SessionAwareAction { 

    public String execute() throws Exception {
        WebSearchResults wsr = (WebSearchResults)this.getSession().get(Constants.SESSION_WEB_SEARCH_RESULTS);
        wsr.setPageNum(page);
        getSession().put(Constants.SESSION_WEB_SEARCH_RESULTS, wsr);
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
