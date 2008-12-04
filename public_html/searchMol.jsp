<%@ taglib prefix="s" uri="/struts-tags" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="en">

<head>
   <title>
   OrChem search demo 
   </title>
   <link rel="stylesheet" href="inc/contents.css" type="text/css">
   <link rel="stylesheet" href="inc/userstyles.css" type="text/css">
   <link rel="stylesheet" href="inc/sidebars.css" type="text/css">
   <link rel="stylesheet" href="inc/chebi.css" type="text/css">

   <script src="inc/contents.js" type="text/javascript"></script><link rel="alternate" title="EBI News RSS" href="http://www.ebi.ac.uk/Information/News/rss/ebinews.xml" type="application/rss+xml"><style type="text/css">@media print { body, .contents, .header, .contentsarea, .head{ position: relative;}  } </style>
   <script type="text/javascript" src="inc/chebi.js"></script>

   <link rel="SHORTCUT ICON" href="http://www.ebi.ac.uk/chebi/images/chebi.ico">
   <link rel="search" href="http://www.ebi.ac.uk/chebi/plugins/openSearch/openSearch.xml" type="application/opensearchdescription+xml" title="ChEBI">
   <link rel="alternate" type="application/rss+xml" title="ChEBI RSS feed" href="http://sourceforge.net/export/rss2_projnews.php?group_id=125463&amp;rss_fulltext=1">
</head>

<body>
	<div class="headerdiv" id="headerdiv" style="position: absolute; z-index: 1;">
  	  <iframe src="inc/head.htm" name="head" id="head" marginwidth="0" marginheight="0" style="position: absolute; z-index: 1; height: 57px;" scrolling="no" width="100%" frameborder="0"></iframe>
	</div>
	<div class="contents" id="contents">
          <table border="0" class="contentspane" id="contentspane" summary="The main content pane of the page" style="width: 100%;">
   	    <tbody><tr>
              <td class="contentsarea" id="contentsarea">
                   <h1>OrChem search demo </h1>
                    <a href="<s:url value="smilesSearch.action"/>">Switch to Smiles search </a>
                    
                    <script type="text/javascript" src="inc/browserDetect.js"></script>
                   
                      <table border="0">
                      <tr>
                          <td>
                          <s:form action="lookupMolfile" method="post" enctype="UTF-8" >
                                <s:hidden id="molfileLookedUp" name="molFile"/>
                                    Find molfile by Id in DB : <s:textfield name="id" size="5" />
                                 &nbsp;&nbsp;<s:submit   
                                 cssStyle="align:left"
                                 value="look up"
                                 name="lookup" 
                                 title="Look up molfile" />
                           </s:form>
                           <BR><BR>
                          </td>
                      </tr>

                      <tr>
                          <td>
    
                           <s:form action="performSearch" method="post" cssStyle="vertical-align:middle;" enctype="UTF-8" >
                              <table border="0" style="vertical-align: middle; ">
                                 <tbody>
                                     <tr>
                                        <td colspan="2" id="appletDiv" style="vertical-align: top;">
                                            <applet id="editor" name="MSketch" width="400" height="350" code="JMSketch.class" codebase="applets/marvinSketch" archive="jmarvin.jar">
                                             <param name="autoscale" value="true">
                                             <param name="mol" value=" ">
                                            </applet>
                                        </td>
                                     </tr>
                                     <tr>
                                       <td>
                                          <BR>
                                          <table border="0">
                                           <tr>
                                           <td >Search type</td>
                                           <td>
                                            <s:select label="Search method"   
                                                value="%{#session.wsr.structureSearchMethod}"
                                                name="wsr.structureSearchMethod"
                                                list="#{'sim':'Similarity search','sub':'Substructure search' }"
                                                required="true"/>
                                            </td>
                                            </tr>

                                           <tr>
                                           <td >
                                            Top N results
                                           </td>
                                           <td >
                                            <s:textfield 
                                                 required="true"
                                                 value="%{#session.wsr.topN}"
                                                   
                                                 name="wsr.topN" 
                                                 size="5" 
                                                 cssStyle="vertical-align: middle;" />
                                           </td>
                                            </tr>

                                           <tr>
                                           <td >
                                            Similarity value
                                            </td>
                                           <td >
                                            <s:select 
                                                name="wsr.minTanCoeff"
                                                value="%{#session.wsr.minTanCoeff}"
                                                list="#{'0.95':'0.95' ,'0.90':'0.90' ,'0.85':'0.85' ,'0.80':'0.80' ,'0.75':'0.75' ,'0.60':'0.60' ,'0.50':'0.50','0.25':'0.25' }"
                                                required="true" size="4"	 />

                                           </td>
                                            </tr>

                                           <tr>
                                           <td ></td>
                                           <td >
                                   
                                             <s:hidden name="wsr.structure" id="advancedSearchFT_structure" value="%{#session.wsr.structure}" />
                                             <s:hidden name="wsr.smilesOrMol" id="som" value="mol" />
                                             <s:submit
                                             cssStyle="vertical-align:middle;" 
                                             cssClass="submit_button" 
                                             name="action" 
                                             title="Submit this search." 
                                             onclick="javascript:marvinFromEditor();"/>
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
        function loadStructure(){

            if (BrowserDetect.browser=='Firefox' && BrowserDetect.version=='1.5'){
                    alert("There is an incompatibility of the latest version of MarvinSketch with Firefox 1.5. Please upgrade your browser to use this page.");
            }
            else {
                    //alert ("loadStructure");
                    var struc="";
                    struc = document.getElementById('molfileLookedUp').value;
                    //alert ("structure retrieved");
                    
                    if(struc == "") {
                      //alert ("NOT FOUND "+document.getElementById('molfileLookedUp').value);
                      struc = document.getElementById('advancedSearchFT_structure').value;
                    }

                    var edi = document.getElementById("editor");
                    //alert ("edi "+edi);
                    if (edi!=null){
                            edi.setMol(struc);
                    }
            }
    }
    loadStructure();
                    
    function marvinFromEditor () {
            document.getElementById('advancedSearchFT_structure').value = document.getElementById("editor").getMol("mol");
    }

  </script>

</body>
</html>
             