package uk.ac.ebi.orchem.web;

import java.sql.Connection;

import java.util.ArrayList;
import java.util.List;

import uk.ac.ebi.orchem.Constants;
import uk.ac.ebi.orchem.Utils;
import uk.ac.ebi.orchem.singleton.DbAgent;


/**
 * Struts Action to run a search (substructure or similarity) in a compound database.
 * Part of mock web-application. Not core cartridge functionality.
 *
 */
public class SearchAction extends SessionAwareAction {


  public String execute() throws Exception {
    Connection conn = null;

    try {

      StringBuffer debugMsg = new StringBuffer();
      debugMsg.append("Struts action started   .." + new java.util.Date());

      //Set some default values if missing ..
      if (wsr.getTopN() == null || wsr.getTopN().equals(""))
        wsr.setTopN("35");
      if (wsr.getMinTanCoeff() == null || wsr.getMinTanCoeff().equals(""))
        wsr.setMinTanCoeff("0.70");

      List compounds = new ArrayList();
      long time = System.currentTimeMillis();

      if (wsr.getSmilesOrMol().equals("mol")) {
        if (wsr.getStructureSearchMethod().equals("sim")) {
          conn = DbAgent.DB_AGENT.getCachedConnection();
          debugMsg.append(wsr.getDebugMessage() +
                          "<br>Invoking similarity search  .." +
                          new java.util.Date());
          compounds =
              new DbSearchInvoker().similaritySearchMol(wsr.getStructure(),
                                                                   conn,
                                                                   new Float(wsr.getMinTanCoeff()).floatValue(),
                                                                   new Integer(wsr.getTopN()).intValue());
        }

        //System.out.println("substructure search\n\n"+wsr.getStructure()+"\n\n");
        if (wsr.getStructureSearchMethod().equals("sub")) {
          conn = DbAgent.DB_AGENT.getCachedConnection();
          debugMsg.append(wsr.getDebugMessage() +
                          "<br>Invoking substr search using VF2 and bitmap indices .." +
                          new java.util.Date());
          compounds =
              new DbSearchInvoker().substructureSearchMol(wsr.getStructure(),
                                                                     conn,
                                                                     new Integer(wsr.getTopN()).intValue());
        }

      } else {
        if (wsr.getStructureSearchMethod().equals("sim")) {
          conn = DbAgent.DB_AGENT.getCachedConnection();
          debugMsg.append(wsr.getDebugMessage() +
                          "<br>Invoking similarity search  .." +
                          new java.util.Date());
          compounds =
              new DbSearchInvoker().similaritySearchSmiles(wsr.getSmiles(),
                                                                      conn,
                                                                      new Float(wsr.getMinTanCoeff()).floatValue(),
                                                                      new Integer(wsr.getTopN()).intValue());
        }

        if (wsr.getStructureSearchMethod().equals("sub")) {
          conn = DbAgent.DB_AGENT.getCachedConnection();
          debugMsg.append(wsr.getDebugMessage() +
                          "<br>Invoking substr search using VF2 and bitmap indices .." +
                          new java.util.Date());
          compounds =
              new DbSearchInvoker().substructureSearchSmiles(wsr.getSmiles(),
                                                                        conn,
                                                                        new Integer(wsr.getTopN()).intValue());
        }

      }


      wsr.setDebugMessage(debugMsg.toString());
      wsr.setSearchResults(compounds);
      wsr.setPageNum(1);

      debugMsg.append(wsr.getDebugMessage() + "<br>Search completed  .." +
                      new java.util.Date());
      debugMsg.append(wsr.getDebugMessage() +
                      "<br><B>Search time in milliseconds =" +
                      (System.currentTimeMillis() - time));
      debugMsg.append(wsr.getDebugMessage() + "<br>Results : " +
                      compounds.size() + "</B>");

      this.getSession().put(Constants.SESSION_WEB_SEARCH_RESULTS, wsr);
      return "searchDone";

    } catch (Exception ex) {
      this.setExceptionMsg(Utils.getErrorString(ex));
      return "error";

    } finally {
      if (conn != null)
        DbAgent.DB_AGENT.returnCachedConnection(conn);
    }

  }


  /**
   * A 'session aware' bean to hold the search results, accessed on the JSPs.
   */
  private WebSearchResults wsr = new WebSearchResults();

  public void setWsr(WebSearchResults wsr) {
    this.wsr = wsr;
  }

  public WebSearchResults getWsr() {
    return wsr;
  }


}


