function doGet(e)
{
  var ss = SpreadsheetApp.openByUrl(""); //Paste Here Your Google Sheets shareable link
  var sheet = ss.getSheetByName(""); //Specify the Sheet Number
  return insert(e,sheet); //Put Variables that you want to show on the Google Sheets
}
function doPost(e)
{
  var ss = SpreadsheetApp.openByUrl(""); //Paste Here Your Google Sheets shareable link
  var sheet = ss.getSheetByName(""); //Specify the Sheet Number
  return insert(e,sheet); //Put Variables that you want to show on the Google Sheets 
} 
function insert(e,sheet) 
{
  var scannedData = e.parameter.sdata;
  var d = new Date();
  var ctime =  d.toLocaleString();
  sheet.appendRow([scannedData,ctime]); //Change the Variables scannedData and ctime according to the declared variables in Android MainActivity.
  return ContentService
  .createTextOutput("Success")
  .setMimeType(ContentService.MimeType.JAVASCRIPT) 
  }

