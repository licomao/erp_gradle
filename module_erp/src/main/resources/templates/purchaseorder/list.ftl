<#import "../macros/mainFrame.ftl" as main />
<#import "../macros/formMacros.ftl" as form />
<#import "/spring.ftl" as spring />

<#escape x as x?html>
    <@main.frame>
    <#--<script src="/javascripts/jquery-ui-1.9.2.min.js" type="text/javascript"></script>
    <script src="/javascripts/jquery.ui.widget.js" type="text/javascript"></script>
    <script src="/javascripts/cndate.js" type="text/javascript"></script>-->
    <script type="text/javascript">
        $('#collapsePurchase').collapse('show');
        var isOrgManage = "${isOrgManage}";
        var indexPage = "${indexPage}";
        $(function () {
//            alert(isOrgManage + "......." + action);
            if (!${user.checkAuthority(AUTHORITY.MANAGE_ORG_PURCHASEORDER)?c} && indexPage == "search") {
                window.location = "/noauthority";
            } else if (!${user.checkAuthority(AUTHORITY.MANAGE_ORG_PURCHASEAPPROVE)?c} && indexPage == "approve") {
                window.location = "/noauthority";
            }  else if (!${user.checkAuthority(AUTHORITY.MANAGE_ORG_ADDSTORAGE)?c} && indexPage == "addstorage") {
                window.location = "/noauthority";
            } else {
                showList(getUrl());
                $('.Wdate').datepicker();
                $('.Wdate').datepicker("option", $.datepicker.regional["zh-TW"]);
                $('#purchaseDateStart').datepicker("setDate", $('#purchaseDateStart').val());
                $('#purchaseDateEnd').datepicker("setDate", $('#purchaseDateEnd').val());
            }

        });
        function resValue(id, reg) {
            $("#" + id).blur(function () {

                //   var reg = new RegExp("^\-?[0-9]*\.?[0-9]*$");
                var foo = reg.test($(this).val());
                if (!reg.test($(this).val())) {
                    $(this).val("");
                }
            });
        }
        function toPurchasedDetail(rowId){
            var url = "/purchaseorder/page/detail/search/" + rowId ;
            window.open(url);
        }
        function showList(url) {
            $("#gridBody").jqGrid({
                url: url,
                colModel: [
                    {
                        label: '采购单号', name: 'orderNumberView', width: 45, align: "center",
                        formatter: function (cellvalue, options, rowObject) {
                            if (cellvalue != "" && cellvalue != null) {
                                return "<a  onclick='toPurchasedDetail("+ rowObject.id+");' href='####' style='margin-left:15px;text-decoration:underline;color:blue' >" + cellvalue + "</a>"
                            } else {
                                return "";
                            }
                        }
                    },
                    {label: '采购门店', name: 'purchaseShop.name', width: 30, align: "center"},
                    {label: '出库门店', name: 'saleShop.name', width: 30, align: "center"},
                    {label: '供应商', name: 'supplier.name', width: 60, align: "center"},
                    {label: '采购申请人', name: 'applyPerson', width: 20, align: "center"},
                    {
                        label: '采购单状态', name: 'orderStatus', width: 20, align: "center",
                        formatter: function (cellvalue, options, rowObject) {
                            switch (cellvalue) {
                                case 0:
                                    return "待审批";
                                case 1:
                                    return "审批通过";
                                case 2:
                                    return "未通过";
                                case 3:
                                    return "已入库";
                            }
                        }
                    },
                    {
                        label: '是否作废', name: 'deleted', width: 20, align: "center",
                        formatter: function (cellvalue, options, rowObject) {
                            if (cellvalue) {
                                return "已作废";
                            } else {
                                return "未作废";
                            }
                        }
                    },
                    {
                        label: '采购类型', name: 'purchaseType', width: 20, align: "center",
                        formatter: function (cellvalue, options, rowObject) {
                            switch (cellvalue) {
                                case 0:
                                    return "常规采购";
                                case 1:
                                    return "临时采购";
                            }
                        }
                    },
                    {
                        label: '销售单号', name: 'saleNo', width: 45, align: "center",
                        formatter: function (cellvalue, options, rowObject) {
                            if (cellvalue != "" && cellvalue != null) {
                                return "<a onclick=\"toView('"+cellvalue+"')\" href='#' style='margin-left:15px;text-decoration:underline;color:blue'>"+cellvalue+"</a>";
                            } else {
                                return "";
                            }
                        }

                    },
                    {label: '采购日期', name: 'createdDate', width: 40, align: "center",
                        formatter: function(cellvalue){
                           return formatterDateWithSecond(cellvalue);
                        }
//                        formatter:formatterDateWithSecond(cellvalue);
                    },
                    {label: '采购合计金额', name: 'costSum', width: 30, align: "center" ,
                        formatter:function(cellvalue){
                            return parseFloat(cellvalue).toFixed(2);
                        }
                    },
                    {label: '备注', name: 'remark', width: 30, align: "center"

                    },
                    {
                        label: '操作', name: 'orderStatus', width: 25, align: "center",
                        formatter: function (cellvalue, options, rowObject) {
                            if (rowObject['deleted']){
                                return "";
                            }
                            var o = options;
                            var modify = "";
                            <#if indexPage?? && indexPage == "search">
                                if (cellvalue == 0) {//待审批状态 可进行 修改 删除  审批操作
                                    modify += "<a onclick=\"update(" + rowObject.id + ")\" href='#' style='text-decoration:underline;color:blue'>" + "修改" + "</a>&nbsp;&nbsp;&nbsp;";
                                    modify += "<a onclick=\"deleteRow(" + rowObject.id + "," + options.rowId + ")\" href='#' style='text-decoration:underline;color:blue'>" + "删除" + "</a>&nbsp;&nbsp;&nbsp;";
//                                modify += "<a onclick=\"approve("+ rowObject.id +")\" href='#' style='text-decoration:underline;color:blue'>"+"审批"+"</a>&nbsp;&nbsp;&nbsp;";
                                } else if (cellvalue == 2) {
                                    modify += "<a onclick=\"update(" + rowObject.id + ")\" href='#' style='text-decoration:underline;color:blue'>" + "修改" + "</a>&nbsp;&nbsp;&nbsp;";
                                    modify += "<a onclick=\"deleteRow(" + rowObject.id + "," + options.rowId + ")\" href='#' style='text-decoration:underline;color:blue'>" + "删除" + "</a>&nbsp;&nbsp;&nbsp;";
                                }
                            <#elseif indexPage?? && indexPage == "approve" >
                                if (cellvalue == 0) {
                                    modify += "<a onclick=\"approve(" + rowObject.id + ")\" href='#' style='text-decoration:underline;color:blue'>" + "审批" + "</a>&nbsp;&nbsp;&nbsp;";

                                }
                            <#elseif indexPage?? && indexPage == "addstorage" >
                                if (isOrgManage == "true") {

                                    modify += "<a onclick=\"deleteRow(" + rowObject.id + "," + options.rowId + ")\" href='#' style='text-decoration:underline;color:blue'>" + "删除" + "</a>&nbsp;&nbsp;&nbsp;";
                                }
                                modify += "<a onclick=\"addStorage(" + rowObject.id + ")\" href='#' style='text-decoration:underline;color:blue'>" + "入库" + "</a>";

                            </#if>
                            return modify;
                        }
                    }
                ],
                //multiselect:true,
                rownumbers: true,
                sortorder:'desc'
            });
        }
        function getUrl() {
            var url = '/purchaseorder/list/data?orderNumberView=' + $("#orderNumber").val();
            var purchaseDateStart = $("#purchaseDateStart").val();
            var purchaseDateEnd = $("#purchaseDateEnd").val();
            var purchaseType = $("#purchaseType").val();
            var shopId = $("#shopId").val();
            url += "&purchaseShop.id=" + shopId;
            url += "&deleted=" + $("#deleted").val();
            if (purchaseDateStart != "") {
                url += "&purchaseDateStart=" + purchaseDateStart;
            }
            if (purchaseDateEnd != "") {
                url += "&purchaseDateEnd=" + purchaseDateEnd;
            }

            if (purchaseType != ""){
                url += "&purchaseType=" + purchaseType;
            }

            if (indexPage == "approve") {
                url += "&orderStatus=0";
            } else if (indexPage == "addstorage") {
                url += "&orderStatus=1";
            } else if (indexPage == "search") {
                var orderStatus = $("#orderStatus").val();
                /*if (orderStatus != "-1") {*/
                    url += "&orderStatus=" + orderStatus;
                /*}*/
            }

            return url;
        }
        function queryList() {
            jQuery("#gridBody").setGridParam({url: getUrl()}).trigger("reloadGrid", [{ page: 1}]);
        }

        function update(id) {
            window.location = "/purchaseorder/tosave?id=" + id;
        }
        function toView(saleNoView) {
            window.open( "/salenote/salenoteView?saleNoView="+ saleNoView);
        }
        function deleteRow(id, rowId) {
//        $("#gridBody").jqGrid("delRowData",rowId);
            if (confirm("是否确认作废?")){
                $.get("/purchaseorder/delete/" + id, function (data) {
                    $("#gridBody").jqGrid("delRowData", rowId);
                });
            }
        }
        //前往审批页面
        function approve(id) {
            window.location = "/purchaseorder/page/approve/approve/" + id;
        }
        //前往入库页面
        function addStorage(id) {
            window.location = "/purchaseorder/page/addstorage/addstorage/" + id;
        }
        function toadd() {
            $("#fm").submit();
        }


        function myBrowser(){
            var userAgent = navigator.userAgent; //取得浏览器的userAgent字符串
            var isOpera = userAgent.indexOf("Opera") > -1;
            if (isOpera) {
                return "Opera"
            }; //判断是否Opera浏览器
            if (userAgent.indexOf("Firefox") > -1) {
                return "FF";
            } //判断是否Firefox浏览器
            if (userAgent.indexOf("Chrome") > -1){
                return "Chrome";
            }
            if (userAgent.indexOf("Safari") > -1) {
                return "Safari";
            } //判断是否Safari浏览器
            if (userAgent.indexOf("compatible") > -1 && userAgent.indexOf("MSIE") > -1 && !isOpera) {
                return "IE";
            }; //判断是否IE浏览器
//            if (userAgent.indexOf(""))

        }


    </script>

    <style>
        .btn-search {
            background: url("/stylesheets/images/erp/search.jpg") no-repeat;
            width: 70px;
            height: 30px;
            text-align: right;
            color: white;
        }
    </style>

    <#if indexPage?? && indexPage == "search">
        <legend>采购单管理 -> 采购单申请及查询</legend>
    <#elseif indexPage?? && indexPage == "approve" >
        <legend>采购单管理 -> 采购单审批</legend>
    <#elseif indexPage?? && indexPage == "addstorage" >
        <legend>采购单管理 -> 采购单入库</legend>
    </#if>

    <div class="row">
        <form class="" id="fm" action='<@spring.url relativeUrl = "/purchaseorder/tosave"/>' method="GET">

            <div class="col-md-9">
                <label class="control-label">采购单号: </label>&nbsp;
                <input type="text" name="orderNumber" size="13" id="orderNumber">&nbsp;
                <label class="control-label">采购日期: </label>&nbsp;
                <input readonly type="text" value="" size="10" class="Wdate" name="purchaseDateStart" id="purchaseDateStart">&nbsp;-&nbsp;
                <input readonly type="text" value="" size="10" class="Wdate" name="purchaseDateEnd" id="purchaseDateEnd">&nbsp;
                <label class="control-label">采购类型: </label>&nbsp;
                <select class="control-text" id="purchaseType">
                    <option value="99">请选择</option>
                    <option value="0">常规采购</option>
                    <option value="1">临时采购</option>
                </select>
                <label class="control-label">是否作废: </label>&nbsp;
                <select class="control-text" id="deleted">
                    <option value="false">未作废</option>
                    <option value="true">已作废</option>
                </select>
            <#--<input type="text" value="" class="Wdate" name="useDate" id="useDate">-->
                <#if indexPage?? && indexPage == "search">
                    <label class="control-label">状态: </label>&nbsp;
                    <select name="orderStatus" id="orderStatus">
                        <option value="-1">--请选择--</option>
                        <option value="0">待审批</option>
                        <option value="1">审批通过</option>
                        <option value="2">未通过</option>
                        <option value="3">已入库</option>
                    </select>&nbsp;
                </#if>

            <label  class="control-label">采购门店: </label>
            <select name="shopId" id="shopId">
                <#list shops as shop>
                    <option value="${shop.id}">${shop.name}</option>
                </#list>
            </select>&nbsp;
            </div>
            <div class="col-md-3">
                <@form.btn_search "onclick='queryList()'" "查 询" />
                <#if indexPage?? && indexPage == "search">
                    &nbsp;&nbsp;<@form.btn_add "onclick='toadd()'" "申 请 采 购" />
                <#elseif indexPage?? && indexPage == "approve" >

                <#elseif indexPage?? && indexPage == "addstorage" >
                </#if>

                <#--<input type="button" class="btn btn-primary" onclick="$('#fm')[0].reset();" value="清 空"></input>-->

            <#--<@form.btn_add "onclick='toadd()'" "领 用"/>-->
            </div>
        </form>
    </div>
    <br>
    <table id="gridBody" class="scroll" cellpadding="0" cellspacing="0"></table>
    <div id="toolBar"></div>
    </@main.frame>
</#escape>