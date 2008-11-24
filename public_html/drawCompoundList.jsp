<%@ taglib prefix="s" uri="/struts-tags"%>


<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <title>Compounds drawn</title>
  </head>
  <body>
  
    <table border="0" width="100%" cellpadding="10" cellspacing="20">

         <tr><td> 
                  <b>1&nbsp;&nbsp;&nbsp;</b><applet id="editor1" name="MSketch0" width="300" height="350" code="JMView.class" codebase="applets/marvinSketch" archive="jmarvin.jar"><param name="autoscale" value="true"><param name="mol" value=" "></applet>
                  <b>2&nbsp;&nbsp;&nbsp;</b><applet id="editor2" name="MSketch1" width="300" height="350" code="JMView.class" codebase="applets/marvinSketch" archive="jmarvin.jar"><param name="autoscale" value="true"><param name="mol" value=" "></applet>
                  <b>3&nbsp;&nbsp;&nbsp;</b><applet id="editor3" name="MSketch2" width="300" height="350" code="JMView.class" codebase="applets/marvinSketch" archive="jmarvin.jar"><param name="autoscale" value="true"><param name="mol" value=" "></applet>
         </td></tr>

         <tr><td > 
                  <b>4&nbsp;&nbsp;&nbsp;</b><applet id="editor4" name="MSketch3" width="300" height="350" code="JMView.class" codebase="applets/marvinSketch" archive="jmarvin.jar"><param name="autoscale" value="true"><param name="mol" value=" "></applet>
                  <b>5&nbsp;&nbsp;&nbsp;</b><applet id="editor5" name="MSketch4" width="300" height="350" code="JMView.class" codebase="applets/marvinSketch" archive="jmarvin.jar"><param name="autoscale" value="true"><param name="mol" value=" "></applet>
                  <b>6&nbsp;&nbsp;&nbsp;</b><applet id="editor6" name="MSketch5" width="300" height="350" code="JMView.class" codebase="applets/marvinSketch" archive="jmarvin.jar"><param name="autoscale" value="true"><param name="mol" value=" "></applet>
         </td></tr>

         <tr><td >
                  <b>7&nbsp;&nbsp;&nbsp;</b><applet id="editor7" name="MSketch6" width="300" height="350" code="JMView.class" codebase="applets/marvinSketch" archive="jmarvin.jar"><param name="autoscale" value="true"><param name="mol" value=" "></applet>
                  <b>8&nbsp;&nbsp;&nbsp;</b><applet id="editor8" name="MSketch7" width="300" height="350" code="JMView.class" codebase="applets/marvinSketch" archive="jmarvin.jar"><param name="autoscale" value="true"><param name="mol" value=" "></applet>
                  <b>9&nbsp;&nbsp;&nbsp;</b><applet id="editor9" name="MSketch8" width="300" height="350" code="JMView.class" codebase="applets/marvinSketch" archive="jmarvin.jar"><param name="autoscale" value="true"><param name="mol" value=" "></applet>
         </td></tr>
    </table>
 
    <s:form action="" method="post" enctype="UTF-8" >
      <s:iterator  id="res" value="%{#session.wsr.searchResults}" status="res_stat">  
         <s:if test="#res_stat.count <= #session.wsr.currDisplayEndIdx ">
            <s:if test="#res_stat.count >= #session.wsr.currDisplayStartIdx ">
               <s:hidden id="%{#res_stat.count-#session.wsr.currDisplayStartIdx+1}" name="molfile"/>
            </s:if>
         </s:if>
      </s:iterator>  
    </s:form>

  <script type="text/javascript">
    function loadStructure(){
            var struc1="";
            struc1 = document.getElementById("1").value;
            var edi1 = document.getElementById("editor1");
            edi1.setMol(struc1);

                          var struc2="";
                              struc2 = 
          document.getElementById("2").value;
                            var edi2 = 
    document.getElementById("editor2");
                                edi2.setMol
                             (struc2);

                          var struc3="";
                              struc3 = 
          document.getElementById("3").value;
                            var edi3 = 
    document.getElementById("editor3");
                                edi3.setMol
                             (struc3);

                          var struc4="";
                              struc4 = 
          document.getElementById("4").value;
                            var edi4 = 
    document.getElementById("editor4");
                                edi4.setMol
                             (struc4);

                          var struc5="";
                              struc5 = 
          document.getElementById("5").value;
                            var edi5 = 
    document.getElementById("editor5");
                                edi5.setMol
                             (struc5);

                          var struc6="";
                              struc6 = 
          document.getElementById("6").value;
                            var edi6 = 
    document.getElementById("editor6");
                                edi6.setMol
                             (struc6);

                          var struc7="";
                              struc7 = 
          document.getElementById("7").value;
                            var edi7 = 
    document.getElementById("editor7");
                                edi7.setMol
                             (struc7);

                          var struc8="";
                              struc8 = 
          document.getElementById("8").value;
                            var edi8 = 
    document.getElementById("editor8");
                                edi8.setMol
                             (struc8);

                          var struc9="";
                              struc9 = 
          document.getElementById("9").value;
                            var edi9 = 
    document.getElementById("editor9");
                                edi9.setMol
                             (struc9);



    }

    loadStructure();
  </script>
 
  </body>
</html>