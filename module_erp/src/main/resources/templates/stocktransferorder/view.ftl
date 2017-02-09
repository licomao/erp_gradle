<#import "../macros/mainFrame.ftl" as main />
<#import "../macros/formMacros.ftl" as form />
<#import "/spring.ftl" as spring />
<#escape x as x?html>

    <@main.frame>
    <meta http-equiv="Windows-Target" contect="_top">
    <script src="/javascripts/jquery.iframe-transport.js" type="text/javascript"></script>
    <script src="/javascripts/jquery.fileupload.js" type="text/javascript"></script>

    <script type="text/javascript">
        $('#collapseStock').collapse('show');
        $(function(){
            addStock();
            <#if (stockTransferOrder.stockTransferOrderDetails)?? >
                <#list stockTransferOrder.stockTransferOrderDetails as rowData >
                    var customId = "${rowData.customStockItem.id}";
                    var row = $("#gridBody").getGridParam("reccount") + 1;
                $("#gridBody").addRowData(row,
                        {"customStockItem.id":${rowData.id},
                            viewId:customId,
                            "customStockItem.name":"${rowData.customStockItem.name}",
                            "customStockItem.brandName":"${rowData.customStockItem.brandName}",
                            "customStockItem.cost":${rowData.cost},
                            "number":${rowData.number}
                        },
                        "last");
                </#list>
            </#if>
//            $('.Wdate').datepicker();
//            $('.Wdate').datepicker("option",$.datepicker.regional["zh-TW"]);
//            $('#validDate').datepicker( "setDate", $('#validDate').val());
        });






        function addStock(){
            $("#gridBody").jqGrid({
                pager : '#gridpager',
                colModel: [
                    { name:'customStockItem.id'  , hidden:true },
                    { name:'viewId'  , hidden:true },
                    { label: '耗材名称',name:'customStockItem.name', width: 70, align:"center" },
                    { label: '品牌名称', name:'customStockItem.brandName',width: 50, align:"center"},
                    { label: '入库单价', name:'customStockItem.cost', width: 40, align:"center"},
                    { label: '调拨数量',name:'number', width: 50,align:"center",
                        formatter: function (cellvalue, options, rowObject) {
                            return "<input readonly type='text' class='input-sm form-control' value='"+cellvalue+"' onblur='validate(this)' id='number_"+rowObject["viewId"] + "_"+ rowObject["customStockItem.cost"]  +"'/>";
                        }
                    }
                ],
                rownumbers: true
            });
        }

        function deleteRow(rowId){
            $("#gridBody").jqGrid("delRowData",rowId);
        }
        function subForm(status){
            var msg = "";
            switch (status) {
                case 2 : msg = "是否确认调出";
                case 1 : msg = "是否确认退回";
                case 3 : msg = "是否确认入库";
            }
            if(confirm(msg)){
                $("#doType").val(status);
                $("#fm").submit();
            }
        }

        function back(){
            window.location = "/stocktransferorder/list";
        }

        function validate(obj){
            var reg = new RegExp("^[0-9]*$");
            if(!reg.test(obj.value)){
                alert("请输入正整数!");
                obj.focus();
                return;
            }

//            if(!/^[0-9]*$/.test(obj.value)){
//                alert("请输入数字!");
//            }
        }
        /**
         * 打印
         * @param oper 打印区域参数
         */
        function printit(oper) {
            var ips = $("#printDiv").find("input");
            var tes = $("#printDiv").find("textarea");
            var labels = $("#printDiv").find("label");
//            var ips = $("input");
//            var labels = $("label");
            for (var i=0;i<ips.length;i++){

                if(ips[i].type=="text"){
                    ips[i].style.border="none";
                }
            }
            for (var i=0;i<labels.length;i++){
                $(labels[i]).text($(labels[i]).text()+":")
            }
            for (var i=0;i<tes.length;i++){

                $(tes[i]).css("border","none");
                $(tes[i]).css("resize","none");
            }
            var printTable =
                    "<table style='width: 100%' >" +
                    "<tr>" +
                    "<td></td>" +
                    "<td>商品名称</td>" +
                    "<td>品牌名称</td>" +
                    "<td>入库单价</td>" +
                    "<td>调拨数量</td>" +
                    "</tr>"; /*+
                            "<tr>" +
                                "<td>测试1</td>" +
                                "<td>测试2</td>" +
                                "<td>测试3</td>" +
                                "<td>测试1</td>" +
                                "<td>测试2</td>" +
                            "</tr>" +
                        "</table>";*/

            var ids = $("#gridBody").getDataIDs();
            for(var i = 0; i < ids.length; i++){
                var obj = $("#gridBody").getRowData(ids[i]);
                var index = i+1;
                printTable += "<tr><td>" + index + "</td>";
                printTable += "<td>" + obj["customStockItem.name"] + "</td>"
                printTable += "<td>" + obj["customStockItem.brandName"] + "</td>"
                printTable += "<td>" + obj["customStockItem.cost"] + "</td>"
                printTable += "<td>" + obj["number"] + "</td></tr>"

            }
            printTable += "</table>";
            if (oper < 10){

                bdhtml=window.document.body.innerHTML;//获取当前页的html代码
                sprnstr="<!--startprint"+oper+"-->";//设置打印开始区域
                eprnstr="<!--endprint"+oper+"-->";//设置打印结束区域
                prnhtml=bdhtml.substring(bdhtml.indexOf(sprnstr)+18); //从开始代码向后取html

                prnhtml=prnhtml.substring(0,prnhtml.indexOf(eprnstr));//从结束代码向前取html
                prnhtml += printTable;
                window.document.body.innerHTML=prnhtml;
                window.print();
                window.document.body.innerHTML=bdhtml;


            } else {
                window.print();
            }
        }


    </script>

    <div class="row" style="margin-top: -80px">
        <div class="col-md-11">
            <div id="actions" class="form-action">

                <form class="" id="fm" action='<@spring.url relativeUrl = "/stocktransferorder/approve"/>'   method="post">
                    <@form.textInput "stockTransferOrder.id" "" "hidden"/>
                    <@form.textInput "stockTransferOrder.ver" "" "hidden"/>
                    <input type="hidden" name="listData" id="listData">
                    <input type="hidden" name="doType" id="doType">
                        <legend>库存调拨 -> 修改调拨单</legend>
                    <!--startprint1--><#--打印起始位置-->
                    <div class="col-md-5" id="printDiv" >
                        <div class="row">
                            <div class="col-md-5">
                                <input id="shopId" name="inShop.id"  type="hidden" value="${stockTransferOrder.inShop.id}">
                                <@form.textInput "stockTransferOrder.inShop.name" "class='form-control' readonly" "text" "调入门店" />

                            </div>
                            <div class="col-md-5 col-md-offset-2">
                                <@form.textInput "stockTransferOrder.orderNumberView" "class='form-control' readonly" "text" "调拨单号" />
                                <input type="hidden" name="orderNumber" value="${stockTransferOrder.orderNumber}">
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-5">
                                <@form.textInput "stockTransferOrder.outShop.name" "class='form-control' readonly" "text" "调出门店" />
                            </div>
                            <div class="col-md-5 col-md-offset-2">
                                <@form.textInput "stockTransferOrder.erpUser.realName" "class='form-control' readonly" "text" "申请人" />
                            </div>
                        </div>
                        <div class="row">
                            <@form.textArea "stockTransferOrder.remark" "class='form-control'" "备注"/>
                        </div>
                        <!--endprint1--><#--这段注释必须要-->
                        <br/>
                        <table id="gridBody" class="scroll" cellpadding="0" cellspacing="0"></table>
                        <br>

                        <div class="span2 text-center">
                            <#if stockTransferOrder.transferStatus == 0>
                                <#if SHOP.name == stockTransferOrder.outShop.name >
                                    <@form.btn_save "onclick='subForm(2)'" "确认调出"/>&nbsp;&nbsp;&nbsp;
                                    <@form.btn_back "onclick='subForm(1)'" "拒绝调出"/>
                                </#if>
                            </#if>
                            <#if stockTransferOrder.transferStatus == 2>
                                <#if SHOP.name == stockTransferOrder.inShop.name >
                                    <@form.btn_save "onclick='subForm(3)'" "确认入库"/>
                                </#if>
                            </#if>
                            <#if doType == 4>
                                <@form.btn_print "onclick='printit(1)'" "打印"/>
                                <@form.btn_back "onclick='back()'" "返回上一页"/>
                            </#if>
                        </div>
                    </div>
                </form>
            </div>
        </div>

    </div>
    </@main.frame>

</#escape>