<#import "../macros/mainFrame.ftl" as main />
<#import "../macros/formMacros.ftl" as form />
<#import "/spring.ftl" as spring />
<#escape x as x?html>
    <@main.frame>

    <script type="text/javascript">
        $('#collapsePayment').collapse('show');
     $(function () {
         if (!${user.checkAuthority(AUTHORITY.MANAGE_ORG_PAYMENTPRE)?c}) {
             window.location = "/noauthority"
         } else {
             var url = "/payment/list/data";
             url += "?customer.mobile=" + $("#mobile").val();
             if ($("#shopId").val() != "") {
                 url += "&shop.id=" + $("#shopId").val();
             }
             showList(url);
         }
     });

     function showList(url) {
         $("#gridBody").jqGrid({
             url: url,
             colModel: [
                 { label: 'ID', name: 'id', hidden:true },
                 { label: '顾客昵称', name: 'customerName', width: 60,align:"center" },
                 { label: '手机', name: 'customer.mobile', width: 60,align:"center"  },
                 { label: '预约门店', name: 'shop.name', width: 60,align:"center" },
                 { label: '预约时间', name: 'appointmentDate',align:"center" ,width: 50,
                     formatter: function (cellvalue, options, rowObject) {
                         return cellvalue != null ? cellvalue.substring(0,10) : "";
                     }
                 },
                 { label: '付款记录(元)', name: 'payment.appAmount',align:"center" , width: 50},
                 { label: '预约单状态', name: 'cancelled',align:"center" , width: 50,
                     formatter: function (cellvalue, options, rowObject) {
                         return cellvalue ? "无效" : "有效";
                     }
                 },
                 { label: '来源', name: 'title', width: 60,align:"center" },
                 { label: '用户描述', name: 'description', width: 150,align:"center" },
                 { label: '车牌号', name: 'vehicleInfo.plateNumber', width: 150,align:"center" },
                 { label: '操作', name: 'id', width: 70,align:"center" ,
                    formatter:function(cellvalue, options,rowObject) {
                        if (rowObject.vehicleInfo.plateNumber != null && rowObject.vehicleInfo.plateNumber != "") {
                            return "<a onclick='toEdit(\""+ rowObject.vehicleInfo.plateNumber +"\"," + rowObject.id + ")' href='#' style='text-decoration:underline;color:blue'>"+"开单"+"</a>"
                                    + "<a onclick='deletOrder("+  rowObject["id"] +")' href='#' style='margin-left:30px;text-decoration:underline;color:blue'>" + "作废" +"</a>"
                        } else {
                            return "<a onclick='toAddPlateNumber(" + rowObject.id + ")' href='#' style='text-decoration:underline;color:blue'>"+"录入车牌号"+"</a>"
                                    + "<a onclick='deletOrder("+  rowObject["id"] +")' href='#' style='margin-left:30px;text-decoration:underline;color:blue'>" + "作废" +"</a>"
                        }
                    }
                 }
             ]
             ,rownumbers: true
         });
     }

     function toEdit(plateNumber, id) {
        window.location = encodeURI("/salenote/presale/searchpresaleinfo?plateNumber=" + plateNumber + "&id=" + id, "UTF-8");
     }

     function toAddPlateNumber(id) {
        $('#dialog').dialog({
            autoOpen: false
        });
        $('#dialog').dialog('open');
        $("#preSaleId").val(id);
     }

     function updatePlateNumber(id) {
         if ($("#plateNumber").val() == "") {
             alert("请输入车牌号");
             return;
         }
         if ($("#vinCode").val() == "") {
             alert("请输入vin码");
             return;
         }
         var murl = encodeURI("/salenote/updateappvehicleinfo?id=" + $("#preSaleId").val() + '&plateNumber=' + $("#plateNumber").val() + '&vinCode=' + $("#vinCode").val(), "UTF-8");
         $.ajax({
             url:murl,
             dataType:"json",
             type:"post",
             success: function(data) {
                 if (data.result) {
                     alert("录入成功!");
                     $('#dialog').dialog('close');
                     queryOrder();
                 } else {
                     alert("录入失败!")
                 }
             }
         });
     }

     function queryOrder(){
         var url = "/payment/list/data";
         url += "?customer.mobile=" + $("#mobile").val();
         if ($("#shopId").val() != "") {
             url += "&shop.id=" + $("#shopId").val();
         }
         jQuery("#gridBody").setGridParam({url:url}).trigger("reloadGrid", [{ page: 1}]);
     }

     function deletOrder(id) {
//         var ids = $("#gridBody").getGridParam('selarrrow');
//         if (ids <= 0) {
//             alert("请先选择单子");
//             return;
//         }
         window.location = "/payment/presaledelete?id=" + id;
     }

    </script>
    <legend>客户预约管理 -> 预约信息查询</legend>
    <form  id="f1"  action='<@spring.url relativeUrl = "/payment/query"/>' method="post">
        <div class="row">
            <div class="col-md-7">
                    <label  class="control-label">手机号:</label>
                    <input  type="text" name="mobile" id="mobile">

                    <label style="padding-left:50px;" class="control-label">预约门店:</label>
                    <select name="shopId" id="shopId" >
                        <#if shops?size gt 1>
                            <legend><option value="">请选择</option></legend>
                        </#if>
                        <#list shops as shop >
                            <option value ="${shop.id}"> ${shop.name}</option>
                        </#list>
                    </select>
            </div>
                <div class="col-md-4">
                    <@form.btn_search "onclick='queryOrder()'" "查 询"/>
                </div>
            </div>
        <br>
    </form>
    <table id="gridBody" class="scroll" cellpadding="0" cellspacing="0"></table>
    <div id="toolBar"></div>

    <div id="dialog" class="dialog" title="请输入车牌号及Vin码" style="text-align: center;padding-top: 24px; display: none">
        <form i   d="checkForm">
            <label>车牌号:</label><input id="plateNumber" name="plateNumber" type="text"/><br>
            <br>
            <label>Vin码:</label><input id="vinCode" name="vinCode" type="text"/><br>
            <br>
            <input id="preSaleId" name="preSaleId" type="hidden"/>
            <input type="button" value="提交" class="btn btn-primary" onclick="updatePlateNumber()">
        </form>
    </div>

    </@main.frame>

</#escape>