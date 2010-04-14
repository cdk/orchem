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
        
       <a href='<s:url value="molSearch.action"/>'>Switch to applet search </a>
       <table border="0">
        <tr>
         <td>
          <s:form action="performSearch" method="post" cssStyle="vertical-align:middle;" enctype="UTF-8">
           <table border="0" style="vertical-align: middle; ">
            <tbody>
             <tr>
              <td>
               <br></br>
               <table border="0">
                <tr>
                 <td>SMILES and SMARTS searching</td>
                 <td>
                  <s:textfield required="true" value="%{#session.wsr.textInput}" name="wsr.textInput" size="100"
                               cssStyle="vertical-align: middle;"/>
                 </td>
                </tr>
                 
                <tr>
                 <td>SMILES/SMARTS</td>
                 <td>
                  <s:select name="wsr.inputFormat" value="%{#session.wsr.inputFormat}"
                            list="#{'SMILES':'SMILES' ,'SMARTS':'SMARTS'}" required="true" size="2"/>
                 </td>
                </tr>
                 
                <tr>
                 <td>Search type</td>
                 <td>
                  <s:select label="Search method" value="%{#session.wsr.structureSearchMethod}"
                            name="wsr.structureSearchMethod"
                            list="#{'sim':'Similarity search','sub':'Substructure search' }" required="true"/>
                 </td>
                </tr>
                 
                <tr>
                 <td>Top N results</td>
                 <td>
                  <s:textfield required="true" value="%{#session.wsr.topN}" name="wsr.topN" size="5"
                               cssStyle="vertical-align: middle;"/>
                 </td>
                </tr>
                 
                <tr>
                 <td>Similarity value</td>
                 <td>
                  <s:select name="wsr.minTanCoeff" value="%{#session.wsr.minTanCoeff}"
                            list="#{'0.95':'0.95' ,'0.90':'0.90' ,'0.85':'0.85' ,'0.80':'0.80' ,'0.75':'0.75' ,'0.60':'0.60' ,'0.50':'0.50','0.25':'0.25' }"
                            required="true" size="4"/>
                 </td>
                </tr>
                 
                <tr>
                 <td></td>
                 <td>
                  <s:submit cssStyle="vertical-align:middle;" cssClass="submit_button" name="action"
                            title="Submit this search." onclick="javascript:molFromEditor();"/>
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
      </td>
     </tr>
    </tbody>
   </table>
  </div>
 </body>
</html>