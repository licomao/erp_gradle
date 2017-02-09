<#import "../macros/mainFrame.ftl" as main />
<#import "../macros/formMacros.ftl" as form />
<#import "/spring.ftl" as spring />

<#escape x as x?html>
<@main.frame>
<script type="text/javascript">
    $('#collapsePurchase').collapse('show');
  $(function () {
      <#--if (!${user.checkAuthority(AUTHORITY.MANAGE_ORG_REFUNDORDER)?c} && menuStatus == "search") {-->
          <#--window.location = "/noauthority";-->
      <#--} else if (!${user.checkAuthority(AUTHORITY.MANAGE_ORG_REFUNDAPPROVE)?c} && menuStatus == "approve") {-->
          <#--window.location = "/noauthority";-->
      <#--} else {-->
      getSupplier();
      showList(getUrl());
    });

    function showList(url) {
        $("#gridBody").jqGrid({
            url: url,
            colModel: [
                { label: '采购单号', name: 'purchaseOrder.orderNumberView', width: 45, align:"center",
                    formatter:function(cellvalue, options, rowObject){
                        var modify = "<a href='/purchasepayment/orderview?orderNumberView="+cellvalue+"' hidefocus='true' style='text-decoration:underline;color:blue'>"+cellvalue+"</a>";
                        return modify;
                    }
                },
                { label: '采购门店', name: 'purchaseOrder.purchaseShop.name', width: 30,align:"center"},
                { label: '采购类型', name: 'purchaseOrder.purchaseType', width: 20,align:"center",
                    formatter:function(cellvalue){
                        if(cellvalue == 0){
                            return "常规采购";
                        } else {
                            return "临时采购";
                        }
                    }
                },
                { label: '供应商', name: 'supplier.name', width: 45, align:"center"},
                { label: '付款方式', name: 'payType', width: 20, align:"center",
                    formatter:function(cellvalue){
                        switch (cellvalue) {
                            case 1 : return "现金";
                            case 2 : return "银行汇款";
                            case 3 : return "支票";
                            default : return "";
                        }
                    }
                },
                { label: '付款日期', name: 'payDate', width: 25, align:"center",
                    formatter:function(cellvalue){
                        return formatterDate(cellvalue);
                    }},
                { label: '应付金额', name: 'accountPayable', width: 25 ,align:"center",
                    formatter:function(cellvalue){
                        return parseFloat(cellvalue).toFixed(2);
                    }
                },
                { label: '本次付款金额', name: 'payment', width: 25 ,align:"center",
                    formatter:function(cellvalue){
                        return parseFloat(cellvalue).toFixed(2);
                    }
                },
                { label: '本次抵扣金额', name: 'deductionPayment', width: 25 ,align:"center",
                    formatter:function(cellvalue){
                        return parseFloat(cellvalue).toFixed(2);
                    }
                },
                { label: '是否有效', name: 'deleted', width: 20 ,align:"center",
                    formatter:function(cellvalue){
                        if (cellvalue){
                            return "已作废";
                        } else {
                            return "有效";
                        }

                    }
                },
                { label: '付款账号', name: 'payAccount', width: 50 ,align:"center"},
                { label: '付款去向', name: 'payWay', width: 80 ,align:"center"}
            ],
            //multiselect:true,
            rownumbers: true,
            sortorder:'desc',
            sortname:'createdDate'
        });
    }
    function getUrl(){
        var url = '/purchasepayment/paymentlist/data?orgId=' + $("#orgId").val();
        var supplierId = $("#supplierId").val();
        var payType = $("#payType").val();
        var orderNumber = $("#orderNumber").val();
        url += "&purchaseType=" + $("#purchaseType").val();
        if(supplierId != "" && supplierId != null){
            url += "&supplierId=" + supplierId;
        }
        if(payType != ""){
            url += "&payType=" + payType;
        }
        if(orderNumber != ""){
            url += "&orderNumber=" + orderNumber;
        }
        url += "&deleted=" + $("#deleted").val();
        return url;
    }
    function queryList(){
        jQuery("#gridBody").setGridParam({url:getUrl()}).trigger("reloadGrid", [{ page: 1}]);
    }



    function getSupplier() {
        var orgId = $("#orgId").val();
        var murl = "/purchasepayment/paymentlist/supplier?orgId=" + orgId;
        $.ajax({
            url:murl,
            dataType:"json",
            type:"GET",
            success: function(ret) {
                var obj = $("#supplierId");
                obj.empty();
                obj.append("<option value=''>无</option>");
                for(var i = 0 ; i < ret.suppliers.length;i++) {
                    var appendStr = "<option value='"+ret.suppliers[i].id + "'>" + ret.suppliers[i].name + "</option>";
                    obj.append(appendStr);
                }
            }
        });
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
<legend>平台管理 -> 付款单流水查询</legend>
<div class="row">
    <form class="" id="fm" action='<@spring.url relativeUrl = "#"/>' method="GET">
        <div class="col-md-12">
            <label  class="control-label">采购单号: </label>&nbsp;
            <input type="text" name="orderNumber" id="orderNumber">&nbsp;
            <label  class="control-label">付款组织: </label>&nbsp;
            <select name="orgId" id="orgId" onchange="getSupplier();">
                <#list organizations as org>
                    <option  value="${org.id}">${org.name}</option>
                </#list>
            </select>&nbsp;
            <label class="control-label">采购类型: </label>&nbsp;
            <select class="control-text" id="purchaseType">
                <option value="99">请选择</option>
                <option value="0">常规采购</option>
                <option value="1">临时采购</option>
            </select>&nbsp;
            <label  class="control-label">供应商: </label>&nbsp;
            <select name="supplierId" id="supplierId" >

            </select>&nbsp;
            <label  class="control-label">付款方式: </label>&nbsp;
            <select name="payType" id="payType" >
                <option value="0">请选择</option>
                <option value="1">现金</option>
                <option value="2">银行汇款</option>
                <option value="3">支票</option>
            </select>&nbsp;
            <label  class="control-label">是否作废: </label>&nbsp;
            <select name="deleted" id="deleted" >
                <option value="false">否</option>
                <option value="true">是</option>
            </select>&nbsp;

                <@form.btn_search "onclick='queryList()'" "查 询" />
        </div>
    </form>
</div>
<br>
<table id="gridBody" class="scroll" cellpadding="0" cellspacing="0"></table>
<div id="toolBar"></div>
</@main.frame>
</#escape>