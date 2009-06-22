<%@ taglib prefix="s" uri="/struts-tags"%>

<script LANGUAGE="JavaScript">
 function openURL(url)
  {
    target = window.open(url, "", "toolbar=yes,location=yes,resizable=yes,status=no,scrollbars=yes,menubar=no,height=500,left=0,top=500,width="+(screen.width) )
  }
</script>

<head>
   <title>
   Search results
   </title>
   <link rel="stylesheet" href="inc/tablestyles.css" type="text/css">
   <link rel="stylesheet" href="inc/chebi.css" type="text/css">
</head>

<html>
    <body>
     <a href="<s:url value="molSearch.action"/>">Search again with Marvin applet</a>
     <BR>
     <a href="<s:url value="smilesSearch.action"/>">Search again with Smiles</a>

     <TABLE border="0" style="border:0px; margin-top:0; margin-bottom:0">
        <TR>
        <TD width="10%">
           &nbsp;
                <s:form action="pageResults" method="post" cssStyle="border:0">
                <s:if test="session.wsr.pageNum!=1">
                   <s:hidden name="page" value="%{#session.wsr.prevPageNum}" />
                   <s:submit type="image" src="img/back.gif" />
                 </s:if>
                 </s:form>
       </td>
       <TD width="80%">&nbsp;</td>
       <TD width="10%">
        &nbsp;
             <s:form action="pageResults" method="post" cssStyle="border:0">
             <s:if test="session.wsr.lastPage==false">
               <s:hidden name="page" value="%{#session.wsr.nextPageNum}" />
               <s:submit type="image" src="img/forward.gif" />
             </s:if>
             </s:form>
       </td>

       </TR>
    </table>


    <table width="90%">
        <s:if test="#session.wsr.searchResults.size==0">
            <H3> No results found .... </H3>
        </s:if>
        <h4>Result set size = <s:property value="%{#session.wsr.searchResults.size}"/> </h4>

        <s:iterator  id="res" value="%{#session.wsr.searchResults}" status="res_stat">  
           <s:if test="#res_stat.count <= session.wsr.currDisplayEndIdx ">
              <s:if test="#res_stat.count >= session.wsr.currDisplayStartIdx ">

    
                      <s:if test="(#res_stat.count-#session.wsr.currDisplayStartIdx+1==1)||(#res_stat.count-#session.wsr.currDisplayStartIdx+1==4)||(#res_stat.count-#session.wsr.currDisplayStartIdx+1==7)"> 
                        <TR>
                      </s:if>

                        <td><s:property value="%{#res_stat.count-#session.wsr.currDisplayStartIdx+1}"/><BR>
                            <s:form action="lookupMolfile" method="post" cssStyle="border:0">
                              <a href="#" onclick="document.forms[<s:property value="#res_stat.count-#session.wsr.currDisplayStartIdx+2"/>].submit()"" > <s:property value="id" /></a><BR>
                              <s:hidden name="id" value="%{#res.id}" />
                            </s:form>
                            <!--a href="http://www.ebi.ac.uk/chebi/searchId.do?chebiId=<s:property value="id"/>"> Chebi <s:property value="id" /></a><BR-->
                            <s:if test="score!=0 ">
                               Score=<s:property value="score" /><BR>
                            </s:if>
                            <img src="dynimages?id=<s:property value="id"/>" />
                            
                        </td>

                      <s:if test="(#res_stat.count-#session.wsr.currDisplayStartIdx+1==3)||(#res_stat.count-#session.wsr.currDisplayStartIdx+1==6)||(#res_stat.count-#session.wsr.currDisplayStartIdx+1==9)"> 
                        </TR>
                      </s:if>


              </s:if>
           </s:if>
        </s:iterator>  

    </table>

    



    </body>
</html>

