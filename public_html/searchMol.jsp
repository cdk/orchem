<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ page import="uk.ac.ebi.orchem.singleton.DatabaseAgent"%>
<html xmlns="http://www.w3.org/1999/xhtml" lang="en">
   <head>
      <title>
         OrChem search demo on
         <%=DatabaseAgent.DB_AGENT.getDbName()%>
      </title>
      <link rel="stylesheet" href="inc/userstyles.css" type="text/css"></link>
   </head>
   <body>
      <div class="contents" id="contents">
         <table border="0" class="contentspane" id="contentspane" style="width: 100%;" cellspacing="10">
            <tbody>
               <tr>
                  <td class="contentsarea" id="contentsarea">
                     <h1>
                        OrChem search database
                        <%=DatabaseAgent.DB_AGENT.getDbName()%>
                     </h1>
                     
                     <a href='<s:url value="smilesSearch.action"/>'>Switch to SMILES/SMARTS search </a>
                     <table border="0">
                        <tr>
                           <td>
                              <s:form action="lookupMolfile" method="post" enctype="UTF-8">
                                 <s:hidden id="molfileLookedUp" name="molFile"/>
                                 Find molfile by Id in DB :
                                 <s:textfield name="id" size="5"/>
                                 &nbsp;&nbsp;
                                 <s:submit cssStyle="align:left" value="look up" name="lookup" title="Look up molfile"/>
                              </s:form>
                               
                              <br></br><br></br>
                           </td>
                        </tr>
                         
                        <tr>
                           <td>
                              <s:form action="performSearch" method="post" cssStyle="vertical-align:middle;"
                                      enctype="UTF-8">
                                 <table border="0" style="vertical-align: middle; " cellpadding="10">
                                    <tbody>
                                       <tr>
                                          <td id="appletDiv" style="vertical-align: top;">
                                             <applet code="org.openscience.jchempaint.applet.JChemPaintEditorApplet"
                                                     archive="jchempaint/jchempaint-applet-core.jar" id="editor"
                                                     name="editor" width="700" height="500">
                                                <param name="impliciths" value="true"></param>
                                                <param name="codebase_lookup" value="false"/>
                                                <param name="onLoadTarget" value="statusFrame"></param>
                                                <param name="image" value="hourglass.gif"></param>
                                                <param name="boxborder" value="false"></param>
                                                <param name="centerimage" value="true"></param>
                                             </applet>
                                          </td>
                                          <td valign="top">
                                             <br></br>
                                             <table border="0">
                                                <tr>
                                                   <td>Search type</td>
                                                   <td>
                                                      <s:select label="Search method"
                                                                value="%{#session.wsr.structureSearchMethod}"
                                                                name="wsr.structureSearchMethod"
                                                                list="#{'sim':'Similarity search','sub':'Substructure search' }"
                                                                required="true"/>
                                                   </td>
                                                </tr>
                                                 
                                                <tr>
                                                   <td>Top N results</td>
                                                   <td>
                                                      <s:textfield required="true" value="%{#session.wsr.topN}"
                                                                   name="wsr.topN" size="5"
                                                                   cssStyle="vertical-align: middle;"/>
                                                   </td>
                                                </tr>
                                                 
                                                <tr>
                                                   <td>Similarity value</td>
                                                   <td>
                                                      <s:select name="wsr.minTanCoeff"
                                                                value="%{#session.wsr.minTanCoeff}"
                                                                list="#{'0.95':'0.95' ,'0.90':'0.90' ,'0.85':'0.85' ,'0.80':'0.80' ,'0.75':'0.75' ,'0.60':'0.60' ,'0.50':'0.50','0.25':'0.25' }"
                                                                required="true" size="5"/>
                                                   </td>
                                                </tr>
                                                 
                                                <tr>
                                                   <td>Strict stereo match for substructure?</td>
                                                   <td>
                                                      <s:select name="wsr.strictStereoYN"
                                                                value="%{#session.wsr.strictStereoYN}"
                                                                list="#{'N':'N' ,'Y':'Y'}" required="true" size="1"/>
                                                   </td>
                                                </tr>
                                                 
                                                <tr>
                                                   <td></td>
                                                   <td>
                                                      <s:hidden name="wsr.structure" id="advancedSearchFT_structure"
                                                                value="%{#session.wsr.structure}"/>
                                                       
                                                      <s:hidden name="wsr.inputFormat" id="som" value="MOL"/>
                                                       
                                                      <s:submit cssStyle="vertical-align:middle;"
                                                                cssClass="submit_button" name="action"
                                                                title="Submit this search."
                                                                onclick="javascript:jcpFromEditor();"/>
                                                   </td>
                                                </tr>
                                             </table>
                                          </td>
                                       </tr>
                                    </tbody>
                                 </table>
                              </s:form>
                           </td>
                        </tr>
                     </table>
                     <script type="text/javascript">
                       function loadStructure() {
                           var struc = "";
                           struc = document.getElementById('molfileLookedUp').value;
                           if (struc == "") {
                               struc = document.getElementById('advancedSearchFT_structure').value;
                           }
                           var edi = document.getElementById("editor");
                           if (edi != null) {
                               edi.setMolFile(struc);
                           }
                       }
                       loadStructure();

                       function jcpFromEditor() {
                           document.getElementById('advancedSearchFT_structure').value = document.getElementById("editor").getMolFile();
                       }
                     </script>
                  </td>
               </tr>
            </tbody>
         </table>
      </div>
   </body>
</html>