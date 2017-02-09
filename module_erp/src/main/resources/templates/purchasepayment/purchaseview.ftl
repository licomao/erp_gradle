<#import "../macros/mainFrame.ftl" as main />
<#import "../macros/formMacros.ftl" as form />
<#import "/spring.ftl" as spring />

<#escape x as x?html>
<@main.frame>
<style type="text/css">#dialog {
    display: none;
}</style>
<#--<script src="/javascripts/jquery-ui-1.9.2.min.js" type="text/javascript"></script>
<script src="/javascripts/jquery.ui.widget.js" type="text/javascript"></script>
<script src="/javascripts/cndate.js" type="text/javascript"></script>-->
<script src="/javascripts/fingerprint-tool.js" type="text/javascript"></script>
<script type="text/javascript">
    $('#collapsePurchase').collapse('show');

    $(function () {
        addStock();
        if(${purchaseOrder.id} != 0){
        var totalPrice = 0 ;
        <#if (purchaseOrder.purchaseOrderDetailList)?? >
            <#list purchaseOrder.purchaseOrderDetailList as rowData >
                var row = $("#gridBody2").getGridParam("reccount") + 1;
                totalPrice = totalPrice + formatterPrice("${rowData.number!0}")*formatterPrice("${rowData.price!0}");
                <#--totalPrice = totalPrice + ${rowData.number}*${rowData.price};-->
                $("#gridBody2").addRowData(row,
                        {
                            "orderDetailId":${rowData.id?c},      //订单明细表ID
                            "stockNumber":"${rowData.number!0}",    //进货数量
                            "currentPrice":"${rowData.price!0}",    //当前进价
                            "totalPrice":formatterPrice("${rowData.number!0}")*formatterPrice("${rowData.price!0}"), //总价
                        "customStockItem.id":${rowData.customStockItem.id?c!0}, //商品ID
                "customStockItem.name":"${rowData.customStockItem.name!}", //商品名称
                    "customStockItem.brandName":"${rowData.customStockItem.brandName!}", //品牌
                    "lastPrice":${rowData.lastPrice!0}, //上次进货价格
                "bankNumber":${rowData.bankNumber!0}, //库存
                "customStockItem.isDistribution":"${rowData.customStockItem.isDistribution?c}",
            <#--"customStockItem.isDistribution":"${rowData.customStockItem.isDistribution}",-->
            <#--"customStockItem.cost":${rowData.customStockItem.cost},-->
            <#--"customStockItem.number":${rowData.customStockItem.number},-->
            <#--"customStockItem.cost":${rowData.customStockItem.cost}-->
            },
                "last");
            </#list>
        </#if>
        $("#totalPrice").html(totalPrice);

        <#if viewStatus?? &&  viewStatus != "addstorage" >
            $("#remark").attr("readonly",true)
        </#if >
    }

    });

    function addStock(url){
        $("#gridBody2").jqGrid({
            //                url: url,
            pager : '#gridpager',
            rowNum:99999,
            colModel: [
                { name:'orderDetailId', hidden:true},
                { name:'customStockItem.id', hidden:true},
                { label: '商品/规格',name:'customStockItem.name', width: 70, align:"center" },
                { label: '品牌', name:'customStockItem.brandName',width: 50, align:"center"},
                { label: '上次进价', name:'lastPrice', width: 40, align:"center"},
                { label: '库存数量', name:'bankNumber', width: 40, align:"center"},

                { label: '结算状态', name:'customStockItem.isDistribution', width: 40, align:"center",
                   /* formatter: function (cellvalue, options, rowObject) {
                        if(cellvalue){
                            return "是";
                        }else{
                            return "否";
                        }
                    }*/
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
                { label: '本次进价', name:'currentPrice', width: 40, align:"center"},
                { label: '进货数量',name:'stockNumber', width: 30,align:"center"},
                { label: '合计', name:'totalPrice', width: 40, align:"center"}
            ],
            rownumbers: true,
            loadComplete:function(data){
                $("#testInput").val(2);
                alert(2)
            }
        });
    }


    /*//打印页面
    function printHtml(){

        window.print();
    }
*/
    function printit(oper) {
        $("#printDiv").jqPrintDataWithoutBorder({
            headHtml:"采购单管理 -> 采购单明细",
            arr:[
                [
                    {text: "采购门店", id: "purchaseShop.name"},
                    {text: "单据编号", id: "orderNumberView"},
                    {text: "出库门店", id: "saleShop.name"},
                    {text: "供应商", id: "supplier.name"},
                    {text: "采购类型", id: "purchaseTypeName"},
                    {headText: "采购单信息", row:2, widthStyle: "auto"}
                ],
                [
                    {text: "合计", id: "totalPrice", type: "text"},{ widthStyle: "20%" }
                ]
            ],
            jqGridTables:[{
                labels:['商品/规格','品牌','上次进价','库存数量','是否铺货','本次进价','进货数量','合计'],
                names:['customStockItem.name','customStockItem.brandName','lastPrice','bankNumber','customStockItem.isDistribution','currentPrice','stockNumber','totalPrice'],
                headText: '采购单明细表',
                jqGridId: "gridBody2",
                afterIndex: 0
            }]

        });

     /*   var ips = $("#printDiv").find("input");
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

    /**
     * 申请权限
     *
     **/
    function setAuth() {
        if ($("#remark").val() == "") {
            alert("退回时备注不能为空");
            return;
        }

        initFingerprint();

        $("#pass").val("");
        $("#username").val("");

        $('#dialog').dialog({
            autoOpen: false
        });
        $('#dialog').dialog('open');
    }

    function checkAuthority() {
        $('#dialog').dialog('close');
        $.get('/customerpurchasesuite/authority/check?username=' + $("#username").val() + '&password=' + $("#pass").val()
                , function (resultMap) {
                    if (resultMap["result"]) {
                        alert("授权成功!");
                        returnAddstorage();
                    }else{
                        $("#fortst").text("");
                        alert(resultMap["message"])
                    }
                }, 'json');
    }

    /**
     * 验证指纹
     */
    function validFgp(){
        $("#fortst").text("现在可以开始验证指纹了");

        initErpFpcHandle();
    }

</script>


<SCRIPT type="text/javascript" FOR="myativx" EVENT="OnFeatureInfo(qulity)">
    // js 处理具体内容。
    //  $("#onFeatureInfoView").val(qulity);
    var str = "不合格";
    $("#fortst").text(qulity);
    if (qulity == 0) {
        str = "合格";

    }
    if (qulity == 1) {
        str = "特征点不够";
        $("#fortst").text("验证失败,请重试!");
    }



</SCRIPT>

<SCRIPT type="text/javascript" FOR="myativx" EVENT="OnCapture(ActionResult ,ATemplate)">
    var tmp = matx.GetTemplateAsString();
    //        $("#forReg").val(tmp);
    //        myString = temp;

    var Parr = [9, 0];  //alert(arr[0]);
    ret = matx.IdentificationFromStrInFPCacheDB(fpcHandle, tmp, Parr[0], Parr[1]);
    if (ret != -1) {
        $('#dialog').dialog('close');

        $.get('/salenote/authority/check?id=' + ret.toString()
                , function (resultMap) {
                    if (resultMap["result"]) {
                        alert("授权成功!");
                        returnAddstorage();
                    }else{
                        $("#fortst").text("");
                        alert(resultMap["message"])
                    }
                }, 'json');

        success();

        matx.EndEngine();

    } else {
        $("#fortst").text("验证失败,请重试!");

    }
</SCRIPT>

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
    <form class="" id="fm" action='<@spring.url relativeUrl = "/purchaseorder/save"/>' method="get">
        <@form.textInput "purchaseOrder.id" "" "hidden"/>
        <@form.textInput "purchaseOrder.ver" "" "hidden"/>

            <legend>采购单管理 -> 采购单明细</legend>

        <div class="col-md-11">
            <div class="row">
                <div class="col-md-2 col-md-offset-3">
                    <@form.textInput "purchaseOrder.purchaseShop.name" "class='form-control' readonly" "text" "采购门店" />
                    <input value="${purchaseOrder.purchaseShop.id}" name="purchaseShop.id" id="shopId" type="hidden" >
                    <input value="${purchaseOrder.id}" id="purchaseOrderId" hidden >
                </div>
                <div class="col-md-2 col-md-offset-1">
                    <@form.textInput "purchaseOrder.orderNumberView" "class='form-control' readonly" "text" "单据编号" />
                </div>
            </div>
            <div class="row">
            <#--<div class="col-md-5">
                <@form.textInput "materialOrder.erpUser.username" "class='form-control' readonly" "text" "领用人" />
            </div>-->
                <div class="col-md-2 col-md-offset-3">
                    <@form.textInput "purchaseOrder.saleShop.name" "class='form-control' readonly" "text" "出库门店" />
                </div>
                <div class="col-md-2 col-md-offset-1">
                    <@form.textInput "purchaseOrder.supplier.name" "class='form-control' readonly" "text" "供应商" />
                </div>
            </div>
            <div class="row">
                <div class="col-md-2 col-md-offset-3">
                    <@form.textInput "purchaseOrder.purchaseTypeName" "class='form-control' readonly" "text" "采购类型" />
                    <#--<div class="row">
                        <label  class="control-label" >&nbsp;&nbsp;&nbsp;&nbsp;采购类型</label>
                    </div>
                    <div class="row">
                        &nbsp;&nbsp;&nbsp;
                        <select name="purchaseType" >
                            <option value="0" <#if purchaseOrder.purchaseType?? && purchaseOrder.purchaseType == 0 >selected</#if> >常规采购</option>
                            <option value="1" <#if purchaseOrder.purchaseType?? && purchaseOrder.purchaseType == 1 >selected</#if> >临时采购</option>

                        </select>
                    </div>-->
                </div>
                <div class="col-md-2 col-md-offset-1">
                    <@form.textInput "purchaseOrder.saleNo" "class='form-control' readonly" "text" "销售单号" />
                </div>
            </div>
            <div class="row">
                <div class="col-md-5 col-md-offset-3">
                    <@form.textArea "purchaseOrder.remark" "class='form-control' resize:none  " "备注"/>

                </div>
                <#--<textarea style="" ></textarea>-->
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


                       <#-- <@form.btn_back "onclick='history.go(-1)'" "返回"/>-->
                        <#--<@form.btn_print "onclick='printit(1)'" "打印"/>-->

                    <@form.btn_back "onclick='history.go(-1);' style='margin-left:30px'"  "返回上一页"/>

                    <#--<@form.btn_back "onclick='history.go(-1)'" "返回"/>-->
            </div>
        </div>


        <div id="dialog" class="dialog"  title="验证申请授权" style="text-align: center;padding-top: 24px">
            <form id="checkForm">
                <div id="loginDiv">
                    <label>帐号:</label><input id="username" type="text"/><br>
                    <br>
                    <input type="password" style="display: none" >
                    <label>密码:</label><input id="pass" type="password"/><br>
                    <br>
                    <input type="button" value="提交" class="btn btn-primary" onclick="checkAuthority()">
                </div>
                <input type="button" value="指纹验证" id="fingerPrintDiv"  class="btn btn-primary" onclick="validFgp()" >
                <label id="fortst" ></label>
            </form>
        </div>
    </form>
</div>

<div style="display: none" >
    <object
            id="myativx"
            classid="clsid:CA69969C-2F27-41D3-954D-A48B941C3BA7"
            width=100%
            height=210
            align=middle
            hspace=0
            vspace=0
            onerror="onObjectError();">
    </object>
</div>


</@main.frame>
</#escape>