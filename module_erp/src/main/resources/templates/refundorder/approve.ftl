                                                                                                                                            <#import "../macros/mainFrame.ftl" as main />
<#import "../macros/formMacros.ftl" as form />
<#import "/spring.ftl" as spring />

<#escape x as x?html>
<@main.frame>

<script type="text/javascript">
    $('#collapseRefundOrder').collapse('show');

    $(function () {
        addStock();
        if(${refundOrder.id} != 0){
            var totalPrice = 0 ;
            <#if (refundOrder.refundOrderDetails)?? >
                <#list refundOrder.refundOrderDetails as rowData >
                    var row = $("#gridBody2").getGridParam("reccount") + 1;
                    totalPrice = totalPrice + formatterPrice("${rowData.number!0}")*formatterPrice("${rowData.cost!0}");
                    $("#gridBody2").addRowData(row,
                        {
                            "orderDetailId":${rowData.id?c},      //订单明细表ID
                            "stockNumber":"${rowData.number!0}",    //退货数量
                            "currentPrice":"${rowData.cost!0}",    //当前进价
                            "totalPrice":formatterPrice("${rowData.number!0}")*formatterPrice("${rowData.cost!0}"), //总价
                            "customStockItem.id":${rowData.customStockItem.id?c}, //商品ID
                            <#--"customStockItem.id":${rowData.customStockItem.id}, //商品ID-->
                            "customStockItem.name":"${rowData.customStockItem.name!}", //商品名称
                            "customStockItem.brandName":"${rowData.customStockItem.brandName!}", //品牌
                            <#--"customStockItem.cost":${rowData.cost}, //上次进价-->
                            "bankNumber":"${rowData.bankNumber!0}", //库存
                            "customStockItem.isDistribution":"${rowData.customStockItem.isDistribution?c}",//是否铺货
                        },
                    "last");
                </#list>
            </#if>
        $("#totalPrice").html(totalPrice);
    }

    });

    function addStock(url){
        $("#gridBody2").jqGrid({
            //                url: url,
            pager : '#gridpager',
            rowNum:99999,
            colModel: [
                { name:'customStockItem.id'  , hidden:true },
                { name:'orderDetailId', hidden:true},
//                { name:'viewId', hidden:true},
                { label: '商品/规格',name:'customStockItem.name', width: 70, align:"center" },
                { label: '品牌', name:'customStockItem.brandName',width: 50, align:"center"},
                { label: '上次进价', name:'currentPrice', width: 40, align:"center"},
                { label: '库存数量', name:'bankNumber', width: 40, align:"center"},

                { label: '结算状态', name:'customStockItem.isDistribution', width: 40, align:"center",
                    formatter: function (cellvalue, options, rowObject) {
                        if(cellvalue == 0){
                            return "月结";
                        }else if(cellvalue == 1){
                            return "铺货";
                        }else if(cellvalue == 2){
                            return "月结";
                        }else if(cellvalue == 3){
                            return "现结";
                        }
                    }
                },
//                { label: '上次进价', name:'currentPrice', width: 40, align:"center"},
                { label: '退货数量',name:'stockNumber', width: 30,align:"center"},
                { label: '合计', name:'totalPrice', width: 40, align:"center"}
            ],
            rownumbers: true
        });
    }

    //审批方法
    function passApprove(){
        window.location = "/refundorder/${indexPage}/result/1/" + $("#refundOrderId").val();
    }
    //退回方法
    function returnApprove(){
        window.location = "/refundorder/${indexPage}/result/2/" + $("#refundOrderId").val();

    }
    //入库方法
    function addStorage(){
        window.location = "/refundorder/${indexPage}/result/3/" + $("#refundOrderId").val();

    }
    //返回list页
    function goToList(){

        window.location = "/refundorder/${indexPage}/list";
    }
    function printit(oper) {

        $("#printDiv").jqPrintDataWithoutBorder({
            headHtml:"",
            arr:[[{
                text: "退货门店", id: "refundShop.name"
            },{
                text: "单据编号", id: "orderNumberView"
            },{
                text: "供应商", id: "supplier.name"
            },{
                text: "备注", id: "remark"
            },{
                row: 2, headText: "退货单信息", widthStyle:"70%"
            }],[
                {text: "合计", id: "totalPrice", type: "text"},
                {widthStyle: "20%" }
            ]],
            jqGridTables:[{
                labels:['商品/规格','品牌','上次进价','库存数量','是否铺货','进货数量','合计'],
                names:['customStockItem.name','customStockItem.brandName','currentPrice','bankNumber','customStockItem.isDistribution','stockNumber','totalPrice'],
                headText: '退货单明细表',
                jqGridId: "gridBody2",
                afterIndex: 0
            }]
        })

        /*var ips = $("#printDiv").find("input");
        var tes = $("#printDiv").find("textarea");
        var labels = $("#printDiv").find("label");
//        var ips = $("input");
//        var labels = $("label");
        for (var i=0;i<ips.length;i++){

            if(ips[i].type=="text"){
                ips[i].style.border="none";
            }
//            $(ips[i]).val($(ips[i]).val()+":")
        }
        for (var i=0;i<labels.length;i++){
            $(labels[i]).text($(labels[i]).text()+":")
        }
        for (var i=0;i<tes.length;i++){

            $(tes[i]).css("border","none");
            $(tes[i]).css("resize","none");
        }

        if (oper < 10){
            bdhtml=window.document.body.innerHTML;//获取当前页的html代码
            sprnstr="<!--startprint"+oper+"-->";//设置打印开始区域
            eprnstr="<!--endprint"+oper+"-->";//设置打印结束区域
            prnhtml=bdhtml.substring(bdhtml.indexOf(sprnstr)+18); //从开始代码向后取html

            prnhtml=prnhtml.substring(0,prnhtml.indexOf(eprnstr));//从结束代码向前取html
            window.document.body.innerHTML=prnhtml;
            window.print();
            window.document.body.innerHTML=bdhtml;


        } else {
            window.print();
        }*/
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


<div class="row" id="printDiv" style="margin-top: -80px">
    <form class="" id="fm" action='<@spring.url relativeUrl = "/refundorder/save"/>' method="get">
        <@form.textInput "refundOrder.id" "" "hidden"/>
        <@form.textInput "refundOrder.ver" "" "hidden"/>
        <#--<input type="hidden" name="rowDatas" id="rowDatas">-->
        <!--startprint1-->
        <#if viewStatus?? && viewStatus == "approve">
            <legend>供应商退货管理 -> 退货单审批</legend>
        <#elseif viewStatus?? &&  viewStatus == "detail" >
            <legend>供应商退货管理 -> 退货单明细</legend>
        <#elseif viewStatus?? &&  viewStatus == "addstorage" >
            <legend>供应商退货管理 -> 采购单入库</legend>
        </#if>

        <div class="col-md-11">
            <div class="row">
                <div class="col-md-2 col-md-offset-3">
                    <@form.textInput "refundOrder.refundShop.name" "class='form-control' readonly" "text" "退货门店" />
                    <input value="${refundOrder.refundShop.id}" name="refundShop.id" id="shopId" type="hidden" >
                    <input value="${refundOrder.id}" id="refundOrderId" hidden >
                </div>
                <div class="col-md-2 col-md-offset-1">
                    <@form.textInput "refundOrder.orderNumberView" "class='form-control' readonly" "text" "单据编号" />
                </div>
            </div>
            <div class="row">
            <#--<div class="col-md-5">
                <@form.textInput "materialOrder.erpUser.username" "class='form-control' readonly" "text" "领用人" />
            </div>-->
                <div class="col-md-2 col-md-offset-3">
                    <@form.textInput "refundOrder.supplier.name" "class='form-control' readonly" "text" "供应商" />
                    <#--<@form.textInput "refundOrder.saleShop.name" "class='form-control' readonly" "text" "出库门店" />-->
                </div>
                <div class="col-md-2 col-md-offset-1">

                </div>
            </div>
            <div class="row">
                <#--<div class="col-md-2 col-md-offset-3">
                    <@form.textInput "refundOrder.purchaseTypeName" "class='form-control' readonly" "text" "采购类型" />

                </div>-->
                <#--<div class="col-md-2 col-md-offset-1">
                    <@form.textInput "refundOrder.saleNo" "class='form-control' readonly" "text" "销售单号" />
                </div>-->
            </div>
            <div class="row">
                <div class="col-md-5 col-md-offset-3">
                    <@form.textArea "refundOrder.remark" "class='form-control' readonly " "备注"/>

                </div>
            </div>
            <br/>
                <table id="gridBody2" class="scroll" cellpadding="0" cellspacing="0"></table>
            <br>

            <div class="row">
                <div class="col-md-5 col-md-offset-10">
                    合计：  <span id="totalPrice" ></span>元
                </div>
            </div>
            <!--endprint1--><#--这段注释必须要-->
            <div class="span2 text-center">
                <#--<#if pageContent?? && pageContent == "update">-->
                    <#if viewStatus?? && viewStatus == "approve">
                        <@form.btn_save "onclick='passApprove()'" "审批通过"/>
                        <@form.btn_save "onclick='returnApprove()'" "退回"/>
                    <#--<#elseif viewStatus?? &&  viewStatus == "addstorage" >
                        <@form.btn_add "onclick='addStorage()'" "确认核实入库"/>-->
                    <#else >
                       <#-- <@form.btn_back "onclick='history.go(-1)'" "返回"/>-->
                        <@form.btn_print "onclick='printit(1)'" "打印"/>

                    </#if>
                    <@form.btn_back "onclick='goToList()'" "返回"/>

                    <#--<@form.btn_back "onclick='history.go(-1)'" "返回"/>-->
            </div>
        </div>

    </form>
</div>


</@main.frame>
</#escape>