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
     <a href="<s:url value="search.action"/>">Search again.. </a>

     <TABLE border="0">
        <TR>
        <TD wdith="10%">
       <s:if test="session.wsr.pageNum!=1">

            <s:form action="pageResults" method="post" cssStyle="border:0">
               <s:hidden name="page" value="%{#session.wsr.prevPageNum}" />
               <s:submit type="image" src="img/back.gif" />
             </s:form>
        </s:if>
       </td>
       <TD width="10%">
        <s:if test="session.wsr.lastPage==false">
             <s:form action="pageResults" method="post" cssStyle="border:0">
               <s:hidden name="page" value="%{#session.wsr.nextPageNum}" />
               <s:submit type="image" src="img/forward.gif" />
             </s:form>
        </s:if>
       </td>
       <TD width="80%">&nbsp;</td>

       </TR>
    </table>
    <table width="90%">
        <s:if test="#session.wsr.searchResults.size==0">
            <H3> No results found ....   <a href="<s:url value="search.action"/>">search again </a>
 </H3>
        </s:if>    
        <s:iterator  id="res" value="%{#session.wsr.searchResults}" status="res_stat">  
           <s:if test="#res_stat.count <= session.wsr.currDisplayEndIdx ">
              <s:if test="#res_stat.count >= session.wsr.currDisplayStartIdx ">

    
                      <s:if test="(#res_stat.count-#session.wsr.currDisplayStartIdx+1==1)||(#res_stat.count-#session.wsr.currDisplayStartIdx+1==4)||(#res_stat.count-#session.wsr.currDisplayStartIdx+1==7)"> 
                        <TR>
                      </s:if>

                        <td><s:property value="%{#res_stat.count-#session.wsr.currDisplayStartIdx+1}"/><BR>
                            <a href="lookupMolfile.action?id=<s:property value="id"/>"> <s:property value="id" /></a><BR>
                            Score=<s:property value="score" /><BR>
                            Formula=<s:property value="formula" />
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

