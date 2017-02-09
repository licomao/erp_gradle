<#import "../macros/mainFrame.ftl" as main />
<#import "../macros/formMacros.ftl" as form />
<#import "/spring.ftl" as spring />

<#escape x as x?html>
<@main.frame>
<#--<script src="/javascripts/jquery-ui-1.9.2.min.js" type="text/javascript"></script>
<script src="/javascripts/jquery.ui.widget.js" type="text/javascript"></script>
<script src="/javascripts/cndate.js" type="text/javascript"></script>-->
<script type="text/javascript">
    $('#collapseRefundOrder').collapse('show');
    var menuStatus = "${menuStatus}";
  $(function () {
      if (!${user.checkAuthority(AUTHORITY.MANAGE_ORG_REFUNDORDER)?c} && menuStatus == "search") {
          window.location = "/noauthority";
      } else if (!${user.checkAuthority(AUTHORITY.MANAGE_ORG_REFUNDAPPROVE)?c} && menuStatus == "approve") {
          window.location = "/noauthority";
      } else {
          showList(getUrl());
          $('.Wdate').datepicker();
          $('.Wdate').datepicker("option",$.datepicker.regional["zh-TW"]);
          $('#refundDateStart').datepicker( "setDate", $('#refundDateStart').val());
          $('#refundDateEnd').datepicker( "setDate", $('#refundDateEnd').val());
      }

    });
    function resValue(id,reg){
        $("#"+id).blur(function(){

         //   var reg = new RegExp("^\-?[0-9]*\.?[0-9]*$");
            var foo = reg.test($(this).val());
            if(!reg.test($(this).val())){
                $(this).val("");
            }
        });
    }
    function showList(url) {
        $("#gridBody").jqGrid({
            url: url,
            colModel: [
                { label: '退货单号', name: 'orderNumberView', width: 50, align:"center",
                    formatter:function(cellvalue, options, rowObject){
//                        alert(cellvalue)
                        if(cellvalue != "" && cellvalue!=null){
                            return "<a hidefocus='true' href='/refundorder/page/detail/${menuStatus}/"+rowObject.id+"' ><span style='color: blue' >"+cellvalue+"</span></a>"
                        }else{
                            return "";
                        }
                    }
                },
                { label: '退货门店', name: 'refundShop.name', width: 50,align:"center"},
                { label: '供应商', name: 'supplier.name', width: 50, align:"center"},
                { label: '退货申请人', name: 'applyPerson', width: 50, align:"center"},
                { label: '退货日期', name: 'createdDate', width: 50, align:"center",
                    formatter:function(cellvalue){
                        return formatterDateWithSecond(cellvalue);
                    }},
                { label: '退货状态', name: 'orderStatus', width: 50 ,align:"center",
                    formatter:function(cellvalue, options, rowObject){
                        switch (cellvalue) {
                            case 0: return "待审批";
                            case 1: return "已退货";
                            case 2: return "未通过";
//                            case 3: return "已入库";
                        }
                    }
                },
                { label: '操作', name: 'orderStatus', width: 50 ,align:"center",
                    formatter:function(cellvalue, options, rowObject){
                        var modify = "";
                        <#if menuStatus?? && menuStatus == "search">
                            if (cellvalue == 0) {//待审批状态 可进行 修改 删除  审批操作
                                modify += "<a onclick=\"update(" + rowObject.id + ")\" href='#' style='text-decoration:underline;color:blue'>" + "修改" + "</a>&nbsp;&nbsp;&nbsp;";
                                modify += "<a onclick=\"deleteRow(" + rowObject.id + "," + options.rowId + ")\" href='#' style='text-decoration:underline;color:blue'>" + "删除" + "</a>&nbsp;&nbsp;&nbsp;";
                            }
                        <#elseif menuStatus?? && menuStatus == "approve" >
                            if (cellvalue == 0) {
                                modify += "<a onclick=\"approve(" + rowObject.id + ")\" href='#' style='text-decoration:underline;color:blue'>" + "审批" + "</a>&nbsp;&nbsp;&nbsp;";
                            }
                        <#--<#elseif menuStatus?? && menuStatus == "addstorage" >
                            if (isOrgManage == "true") {
                                modify += "<a onclick=\"deleteRow(" + rowObject.id + "," + options.rowId + ")\" href='#' style='text-decoration:underline;color:blue'>" + "删除" + "</a>&nbsp;&nbsp;&nbsp;";
                            }
                            modify += "<a onclick=\"addStorage(" + rowObject.id + ")\" href='#' style='text-decoration:underline;color:blue'>" + "入库" + "</a>";
-->
                        </#if>
                    /*if(cellvalue == 0){//待审批状态 可进行 修改 删除  审批操作
                            modify += "<a onclick=\"update("+ rowObject.id +")\" href='#' style='text-decoration:underline;color:blue'>"+"修改"+"</a>&nbsp;&nbsp;&nbsp;";
                            modify += "<a onclick=\"deleteRow("+ rowObject.id + "," + options.rowId +")\" href='#' style='text-decoration:underline;color:blue'>"+"删除"+"</a>&nbsp;&nbsp;&nbsp;";
                            modify += "<a onclick=\"approve("+ rowObject.id +")\" href='#' style='text-decoration:underline;color:blue'>"+"审批"+"</a>&nbsp;&nbsp;&nbsp;";
                        }*//*else if(cellvalue == 1){
                            if(isOrgManage){
                                modify += "<a onclick=\"deleteRow("+ rowObject.id + "," + options.rowId +")\" href='#' style='text-decoration:underline;color:blue'>"+"删除"+"</a>&nbsp;&nbsp;&nbsp;";
                            }
                            modify += "<a onclick=\"addStorage("+ rowObject.id +")\" href='#' style='text-decoration:underline;color:blue'>"+"入库"+"</a>";
                        }else if(cellvalue == 1){
                            modify = "<a onclick=\"addStorage("+ rowObject.id +")\" href='#' style='text-decoration:underline;color:blue'>"+"入库"+"</a>";
                        }*/
                        return modify;
                    }
                }
            ],
            //multiselect:true,
            rownumbers: true,
            sortorder:'desc',
            sortname:'createdDate'
        });
    }
    function getUrl(){
        var url = '/refundorder/list/data?orderNumberView=' + $("#orderNumber").val();
        var refundDateStart = $("#refundDateStart").val();
        var refundDateEnd = $("#refundDateEnd").val();
        if(refundDateStart != ""){
            url += "&refundDateStart=" + refundDateStart;
        }
        if(refundDateEnd != "" ){
            url += "&refundDateEnd=" + refundDateEnd;
        }
        if (menuStatus == "approve") {
            url += "&orderStatus=0";
        } else if (menuStatus == "search") {
            var orderStatus = $("#orderStatus").val();
           /* if (orderStatus != "-1") {*/
                url += "&orderStatus=" + orderStatus;
            /*}*/
        }
        return url;
    }
    function queryList(){
        jQuery("#gridBody").setGridParam({url:getUrl()}).trigger("reloadGrid", [{ page: 1}]);
    }

    function update(id) {
        window.location = "/refundorder/tosave?id=" + id;
    }
    //删除退货单
    function deleteRow(id,rowId) {
//        $("#gridBody").jqGrid("delRowData",rowId);
        $.get("/refundorder/delete/" + id,function(data){
            $("#gridBody").jqGrid("delRowData",rowId);
        });
    }
    //前往审批页面
    function approve(id){
        window.location = "/refundorder/page/approve/approve/" + id;
    }
    //前往入库页面
    function addStorage(id){
        window.location = "/refundorder/page/addstorage/addstorage/" + id;
    }
    function toadd() {
        $("#fm").submit();
    }
</script>

<style>
    .btn-search{
        background:url("/stylesheets/images/erp/search.jpg")no-repeat;
        width: 70px;
        height: 30px;
        text-align: right;
        color: white;
    }
</style>

<legend>退货单管理 </legend>
<div class="row">
    <form class="" id="fm" action='<@spring.url relativeUrl = "/refundorder/tosave"/>' method="GET">

        <div class="col-md-12">
            <label  class="control-label">退货单号: </label>&nbsp;
            <input type="text" name="orderNumber" id="orderNumber">&nbsp;
            <label class="control-label">退货日期: </label>&nbsp;
            <input readonly type="text" value="" class="Wdate" name="refundDateStart" id="refundDateStart">&nbsp;-&nbsp;
            <input readonly type="text" value="" class="Wdate" name="refundDateEnd" id="refundDateEnd">&nbsp;
            <#--<input type="text" value="" class="Wdate" name="useDate" id="useDate">-->
            <#if menuStatus?? && menuStatus == "search" >
                <label class="control-label">状态: </label>&nbsp;
                <select  name="orderStatus" id="orderStatus">
                    <option value="-1">--请选择--</option>
                    <option value="0">待审批</option>
                    <option value="1">已退货</option>
                </select>&nbsp;
                <@form.btn_search "onclick='queryList()'" "查 询" />&nbsp;
                <@form.btn_add "onclick='toadd()'" "申请退货" />
            </#if>
        </div>
       <#-- <div class="col-md-2">
            <@form.btn_search "onclick='queryList()'" "查 询" />
        </div>-->
    </form>
</div>
<br>
<table id="gridBody" class="scroll" cellpadding="0" cellspacing="0"></table>
<div id="toolBar"></div>
</@main.frame>
</#escape>