
  function windowOpenWithSize(theurl, thewd, theht) {
    if(!(theht)) {
      theht = 528;
    }
    if(!(thewd)) {
      thewd = 410;
    }
    var newwin  = window.open(theurl,"labelsize","dependent=no, resizable=yes,toolbar=no,menubar=yes,scrollbars=yes,width="+thewd+",height="+theht);
//     alert(theurl);
    if(newwin!=null)
      newwin.focus();
  }
  
  function toTop() {
        self.scrollTo(0, 0)
}
function toBot() {
        self.scrollTo(0, window.screen.height)
}
function toSpot(xparam, yparam) {
        self.scrollTo(xparam, yparam)
}
function fold(elementId, more, moreName, hideName) {
    var elementToFold = document.getElementById(elementId);
    var moreLink = document.getElementById(more);
    if (navigator.userAgent.indexOf("Netscape6") != -1)	{
        if (elementToFold.style.visibility=="visible") {
            elementToFold.style.visibility="hidden";
            moreLink.firstChild.nodeValue=moreName;
        }	else {
            elementToFold.style.visibility="visible";
            moreLink.firstChild.nodeValue=hideName;
        }
    } else {
        if (elementToFold.style.display=="") {
            elementToFold.style.display="none";
            moreLink.firstChild.nodeValue=moreName;
        } else {
            elementToFold.style.display="";
            moreLink.firstChild.nodeValue=hideName;
        }
    }
}

  function errorMsg()
{
  alert("Netscape 6 or Mozilla is needed to install a sherlock plugin");
}
function addEngine(name,ext,cat,type){
  if ((typeof window.sidebar == "object") && (typeof window.sidebar.addSearchEngine == "function")){
    //cat="Web";
    //cat=prompt('In what category should this engine be installed?','Web')
    window.sidebar.addSearchEngine(
      "http://www.ebi.ac.uk/chebi/plugins/firefox/"+name+".src",
      "http://www.ebi.ac.uk/chebi/plugins/firefox/"+name+".jpg",
      name,
      cat );
  } else {
    errorMsg();
  }
}

function toggleVisibility(panelId)
{
    var panel = document.getElementById(panelId);
    var currentVisibility = panel.style.visibility;

    if (currentVisibility == 'hidden')
    {
        panel.style.visibility = 'visible';
    }
    else
    {
        panel.style.visibility = 'hidden';
    }
}

  // fake selects a field and then you can copy it to the clipboard
   var chebiPreviousHighlightedField = null;
   function chebiSelectAll(fieldToSelect, fieldToHighlight) {
  var selectedF = document.getElementById(fieldToSelect);
  var highlightedF = document.getElementById(fieldToHighlight);
  // select the actual field
  selectedF.select();
      // highlight the field if need be
  if ( chebiPreviousHighlightedField!=null && highlightedF == chebiPreviousHighlightedField){
      chebiPreviousHighlightedField = null;
      highlightedF.style.backgroundColor = "#ffffff";
  } else {
      if (chebiPreviousHighlightedField!=null)
         chebiPreviousHighlightedField.style.backgroundColor = "#ffffff";
      chebiPreviousHighlightedField = highlightedF;
      highlightedF.style.backgroundColor = "#eeeeee";
   }
}
