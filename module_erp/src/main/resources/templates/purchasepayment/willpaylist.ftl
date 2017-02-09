<#import "../macros/mainFrame.ftl" as main />
<#import "../macros/formMacros.ftl" as form />
<#import "/spring.ftl" as spring />

<#escape x as x?html>
<@main.frame>
<link href="/stylesheets/select2.min.css" rel="stylesheet" />
<script src="/javascripts/select2js/select2.min.js"></script>
<script type="text/javascript">
    $('#collapsePurchase').collapse('show');

  $(function () {

          showList(getUrl());
      $(".select2").select2({
          placeholder: "请选择",
      });

    });
    function resValue(id,reg){
        $("#"+id).blur(function(){
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
                { label: '采购单号', name: 'orderNumberView', width: 50, align:"center",
                    formatter:function(cellvalue, options, rowObject){
                        var modify = "<a href='/purchasepayment/orderview?orderNumberView="+cellvalue+"' hidefocus='true' style='text-decoration:underline;color:blue'>"+cellvalue+"</a>";
                        return modify;
                    }

                },
                { label: '采购门店', name: 'purchaseShop.name', width: 50,align:"center"},
                { label: '采购类型', name: 'purchaseType', width: 30,align:"center",
                    formatter:function(cellvalue){
                        if(cellvalue == 0){
                            return "常规采购";
                        } else {
                            return "临时采购";
                        }
                    }
                },
                { label: '供应商', name: 'supplier.name', width: 50, align:"center"},
                { label: '采购申请人', name: 'applyPerson', width: 50, align:"center"},
                { label: '采购申请日期', name: 'createdDate', width: 50, align:"center",
                    formatter:function(cellvalue){
                        return formatterDateWithSecond(cellvalue);
                    }},
                { label: '应付金额', name: 'costSum', width: 35 ,align:"center",
                    formatter:function(cellvalue){
                        return parseFloat(cellvalue).toFixed(2);
                    }
                },
                { label: '未付金额', name: 'unspentCost', width: 35 ,align:"center",
                    formatter:function(cellvalue){
                        return parseFloat(cellvalue).toFixed(2);
                    }
                },
                {
                    label: '销售单号', name: 'saleNo', width: 50, align: "center",
                    formatter: function (cellvalue, options, rowObject) {
                        if (cellvalue != "" && cellvalue != null) {
                            return "<a onclick=\"toView('"+cellvalue+"')\" href='#' style='margin-left:15px;text-decoration:underline;color:blue'>"+cellvalue+"</a>";
                        } else {
                            return "";
                        }
                    }

                },
                { label: '操作', name: 'id', width: 50 ,align:"center",
                    formatter:function(cellvalue, options, rowObject){
                        var modify = "<a href='/purchasepayment/payment?purchaseId="+cellvalue+"' hidefocus='true' style='text-decoration:underline;color:blue'>付 款</a>";
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

    function toView(saleNoView) {
        window.open ("/salenote/salenoteView?saleNoView="+ saleNoView);
    }
    function getUrl(){
        var url = '/purchasepayment/willpaylist/data?purchaseShop.id=' + $("#shopId").val();
        var supplierId = $("#supplierId").val();
        var orderNumberView =  $("#orderNumber").val();
        url += "&purchaseType=" + $("#purchaseType").val();
        if(supplierId != ""){
            url += "&supplier.id=" + supplierId;
        }
        if(orderNumberView != ""){
            url += "&orderNumberView=" + orderNumberView;
        }
        return url;
    }
    function queryList(){
        jQuery("#gridBody").setGridParam({url:getUrl()}).trigger("reloadGrid", [{ page: 1}]);
    }

    //删除退货单
    function deleteRow(id,rowId) {
//        $("#gridBody").jqGrid("delRowData",rowId);
        $.get("/refundorder/delete/" + id,function(data){
            $("#gridBody").jqGrid("delRowData",rowId);
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
<legend>采购管理 -> 未付款采购单查询</legend>
<div class="row">
    <form class="" id="fm" action='<@spring.url relativeUrl = "#"/>' method="GET">
        <div class="col-md-12">
            <label  class="control-label">采购单号: </label>&nbsp;
            <input type="text" name="orderNumber" id="orderNumber">&nbsp;
            <label  class="control-label">采购门店: </label>&nbsp;
            <select name="shopId" id="shopId">
                <#list shops as shop>
                    <option value="${shop.id}">${shop.name}</option>
                </#list>
            </select>&nbsp;
            <label class="control-label">采购类型: </label>&nbsp;
            <select class="control-text" id="purchaseType">
                <option value="99">请选择</option>
                <option value="0">常规采购</option>
                <option value="1">临时采购</option>
            </select>&nbsp;
            <label  class="control-label">供应商: </label>&nbsp;
            <select name="supplierId" id="supplierId" class="select2">
                <option value="">请选择</option>
                <#list suppliers as supplier>
                    <option value="${supplier.id}">${supplier.name}</option>
                </#list>
            </select>&nbsp;
            &nbsp;
                <@form.btn_search "onclick='queryList()'" "查 询" />
        </div>
    </form>
</div>
<br>
<table id="gridBody" class="scroll" cellpadding="0" cellspacing="0"></table>
<div id="toolBar"></div>
</@main.frame>
</#escape>