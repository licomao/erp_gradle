<#macro frame title include="">
<!DOCTYPE html>
<html lang="zh-CN">
  <head>
    <meta charset="utf-8" />
    <meta http-equiv="X-UA-Compatible" content="IE=edge" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <meta http-equiv="Cache-Control" content="no-cache, no-store, must-revalidate" />
    <meta http-equiv="Pragma" content="no-cache" />
    <meta http-equiv="Expires" content="0" />
    <title>${title?html}</title>

    <script src="/javascripts/fingerprint-tool.js" type="text/javascript"></script>


  </head>
  <body>
  <#--<SCRIPT type="text/javascript" FOR="myativx" EVENT="OnEnroll(ActionResult) ">
      var tmp = matx.GetTemplateAsString();
      $("#fingerprint").val(tmp);
      $("#forReg").val('登记成功!')

  </SCRIPT>-->
  <div style="display: none" >
      <div id="initFinger">


          <object
                  id="myativx"
                  classid="clsid:CA69969C-2F27-41D3-954D-A48B941C3BA7"
          <#--codebase="<%=request.getContextPath()%>/ocx/TableListX.ocx#version=1,0,0,5"-->
                  width=100%
                  height=210
                  align=middle
                  hspace=0
                  vspace=0
                  onerror="onObjectError();">
          </object>
      </div>
  </div>
    <#nested/>

  </body>
</html>
</#macro>