/*  
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2008-2009  OrChem project
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

import java.util.ArrayList;
import java.util.List;

import oracle.jdbc.driver.OracleConnection;

import uk.ac.ebi.orchem.Constants;
import uk.ac.ebi.orchem.Utils;
import uk.ac.ebi.orchem.shared.DatabaseAccess;
import uk.ac.ebi.orchem.singleton.DatabaseAgent;


/**
 * Struts Action to run a search (substructure or similarity) in a compound database.
 * Part of demo web-application. Not core cartridge functionality.
 * @author markr@ebi.ac.uk
 *
 */
public class SearchAction extends SessionAwareAction {


  public String execute() throws Exception {
    OracleConnection conn = null;

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

      String queryType = null;
      String query = null;
        
        if (wsr.getSmilesOrMol().equals("mol")) {
            queryType = "MOL";
            query = wsr.getStructure();
        }
        else {
            queryType = "SMILES";
            query = wsr.getSmiles();
        }
        

      if (wsr.getStructureSearchMethod().equals("sim")) {
          
          conn = (OracleConnection)DatabaseAgent.DB_AGENT.getCachedConnection();
          debugMsg.append(wsr.getDebugMessage() +
                          "<br>Invoking similarity search  .." +
                          new java.util.Date());
          compounds =
              new DatabaseAccess().similaritySearch(   query,
                                                       queryType,
                                                       conn,
                                                       new Float(wsr.getMinTanCoeff()).floatValue(),
                                                       new Integer(wsr.getTopN()).intValue());

      } else {
          conn = (OracleConnection) DatabaseAgent.DB_AGENT.getCachedConnection();
          debugMsg.append(wsr.getDebugMessage() +
                          "<br>Invoking substr search using VF2 and bitmap indices .." +
                          new java.util.Date());
          compounds =
              new DatabaseAccess().substructureSearch(query,
                                                      queryType,
                                                      conn,
                                                      new Integer(wsr.getTopN()).intValue());
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
                DatabaseAgent.DB_AGENT.returnCachedConnection(conn);
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


