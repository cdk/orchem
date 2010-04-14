<%@ taglib prefix="s" uri="/struts-tags"%>
<html xmlns="http://www.w3.org/1999/xhtml" lang="en">
    <head>
        <title>OrChem search demo - error message</title>
        <link rel="stylesheet" href="inc/userstyles.css" type="text/css"/>
    </head>
    <body>
        <div class="contents" id="contents">
            <table width="90%" border="0" class="contentspane" id="contentspane" cellspacing="10">
                <tr>
                    <td class="contentsarea" id="contentsarea">
                        <h1>OrChem search demo - exception</h1>
                         
                        <br></br><br></br>
                        <h3>Uhh sorry there was some kind of error !</h3>
                        <br></br>
                        <a href='<s:url value="molSearch.action"/>'>Search again with applet</a>
                        <br></br>
                         
                        <a href='<s:url value="smilesSearch.action"/>'>Search again with SMILES/SMARTS</a>
                         
                        <br></br>
                        <br></br>
                        <br></br>
                        <br></br>
<pre style="font-size:7pt;font-family:lucida console, courier new;color:red;">
<s:property value="exceptionMsg"/>
</pre>
                    </td>
                </tr>
            </table>
        </div>
    </body>
</html>