package uk.ac.ebi.orchem.web;


import uk.ac.ebi.orchem.Constants;

/**
 * Struts Action to assist paging through a search result set.
 * Part of mock web-application. Not core cartridge functionality.
 * 
 */

public class PageAction extends SessionAwareAction { 

    public String execute() throws Exception {
        System.out.println("start PageAction");
        WebSearchResults wsr = (WebSearchResults)getSession().get(Constants.SESSION_WEB_SEARCH_RESULTS);
        System.out.println(wsr);
        wsr.setPageNum(page);
        System.out.println(page);
        getSession().put(Constants.SESSION_WEB_SEARCH_RESULTS, wsr);
        System.out.println("stored");
        return "pagingDone";
    }
    
    /* Getters and setters */
    private int page;

    public void setPage(int _p) {
        System.out.println("page setter from "+page+" to "+_p);
        this.page = _p;
    }

    public int getPage() {

        System.out.println("page getter "+page);
        return page;
    }

}
