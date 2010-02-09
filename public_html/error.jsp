<%@ taglib prefix="s" uri="/struts-tags" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="en">

<head>
   <title>
   OrChem search demo - error message
   </title>
   <link rel="stylesheet" href="inc/contents.css" type="text/css">
   <link rel="stylesheet" href="inc/userstyles.css" type="text/css">
   <link rel="stylesheet" href="inc/sidebars.css" type="text/css">
   <link rel="stylesheet" href="inc/chebi.css" type="text/css">
</head>

<body>
	<div class="headerdiv" id="headerdiv" style="position: absolute; z-index: 1;">
  	  <iframe src="inc/head.htm" name="head" id="head" marginwidth="0" marginheight="0" style="position: absolute; z-index: 1; height: 57px;" scrolling="no" width="100%" frameborder="0"></iframe>
	</div>
	<div class="contents" id="contents">
          <table width="90%" border="0" class="contentspane" id="contentspane" summary="The main content pane of the page">
   	    <tr>
              <td class="contentsarea" id="contentsarea">
                   <h1>OrChem search demo </h1>
                   <BR><BR>
                   <h3>Uhh sorry there was some kind of error !</H3>
                   <BR>
                     <a href="<s:url value="molSearch.action"/>">Search again with applet</a>
                     <BR>
                     <a href="<s:url value="smilesSearch.action"/>">Search again with Smiles</a>
                   <BR>
                   <BR>

                   <BR>
                   <BR>
<pre class="error">
<s:property value="exceptionMsg" />
</pre>

              </td>
              </tr>
              </table>
              </div>
</body>
</html>
             
