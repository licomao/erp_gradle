<#import "../macros/mainFrame.ftl" as main />
<#import "../macros/formMacros.ftl" as form />
<#import "/spring.ftl" as spring />
<#escape x as x?html>

    <@main.frame>

    <meta http-equiv="Windows-Target" contect="_top">
 <#--   <script src="/javascripts/jquery-ui-1.9.2.min.js" type="text/javascript"></script>
    <script src="/javascripts/cndate.js" type="text/javascript"></script>
    <script src="/javascripts/jquery.ui.widget.js" type="text/javascript"></script>
    <script src="/javascripts/jquery.iframe-transport.js" type="text/javascript"></script>
    <script src="/javascripts/jquery.fileupload.js" type="text/javascript"></script>
-->
    <script type="text/javascript">
            <#if pdCheck??>
                <#if pdCheck>
                alert("您已经很久没盘点库存了，请盘点完再来操作！");
                window.history.back();
                </#if>
            </#if>
        var isDotype = "update";

        $('#collapseRefundOrder').collapse('show');
        $(function () {
            var url = '/stock/list/data?name=' + $("#shopName").val()
                    + "&rootCategory=" + $("#rootCategory").val()
                    + "&shopId=" + $("#shopId").val();


//            var url = "/stock/list/data?shopId=" + $("#shopId").val() + "&customStockItem.name=" + $("#shopName").val();
            showSkuItemList(url);
            addStock();
            var tableSum = 0;
            if (${refundOrder.id} != 0 ){
                <#if (refundOrder.refundOrderDetails)?? >
                    <#list refundOrder.refundOrderDetails as rowData >
                        var customId = "${rowData.customStockItem.id?c}";
                        var row = $("#gridBody").getGridParam("reccount") + 1;
                        var totalPrice = formatterPrice("${rowData.number}")*formatterPrice("${rowData.cost}");
                        $("#gridBody").addRowData(row,
                            {
                                "viewId":customId,
                                "orderDetailId":${rowData.id?c},
                                "stockNumber":formatterPrice("${rowData.number}"),
                                <#--"currentPrice":"${rowData.cost}",-->
                                "totalPrice":totalPrice,
                                <#--"totalPrice":formatterPrice("${rowData.number}")*formatterPrice("${rowData.cost}"),-->

                                "customStockItem.id":${rowData.customStockItem.id?c}, //商品ID
                                <#--"customStockItem.id":${rowData.customStockItem.id}, //商品ID-->
                                "customStockItem.name":"${rowData.customStockItem.name}", //商品名称
                                "customStockItem.brandName":"${rowData.customStockItem.brandName}", //品牌
                                "customStockItem.cost":formatterPrice("${rowData.cost}"), //上次进价
                                "bankNumber":formatterPrice("${rowData.bankNumber}"), //库存
                                "customStockItem.isDistribution":"${rowData.customStockItem.isDistribution?c}",
                            }, "last" );
                        tableSum += totalPrice;
                    </#list>
                </#if>
            }
            $("#tableSum").text(tableSum.toFixed(2));
            isDotype = "add";

            //监听采购类型下拉选择框的change事件
            $("#purchaseType").change(function () {
                var value = $(this).val();
                if (value == 1) {
                    $("#saleNo").attr('readonly', false);
                } else {
                    $("#saleNo").attr('readonly', true);
                    $("#saleNo").val("");
                }
            });

        });


        function stockSetUrl(id, dotype, price, addId) {

            var foo = true;
            var obj = $("#gridBody").jqGrid("getRowData");
            jQuery(obj).each(function () {

                if (addId == this['viewId']+"_"+this['customStockItem.cost']) {
                    alert("该商品已经选择了");
                    foo = false;
                    return false;
                }
            });
            if (!foo) {
                return;
            }
            var row = $("#gridBody").getGridParam("reccount") + 1;
            var rowData = $("#skuItemBody").jqGrid('getRowData', id);
            var rowDataId = rowData.id;
//            alert(rowData.number)
            $("#gridBody").addRowData(row,
                {
                    "orderDetailId": 0,
                    "stockNumber": rowDataId,
//                    "currentPrice": rowDataId,
                    "totalPrice": rowDataId,
                    "customStockItem.id": rowData.viewId,
                    "viewId":rowData.viewId,
                    "bankNumber": rowData.number,
                    "customStockItem.name": rowData.name,
//                    "customStockItem.bankNumber": rowData.number,
                    "customStockItem.brandName": rowData.brandName,
                    "customStockItem.cost": rowData.cost,
                    "customStockItem.isDistribution": rowData.isDistribution,
//                    "customStockItem.number": rowData.number,

                }, "last");
        }

        /**
         * 添加
         */
        function addStock() {
            $("#gridBody").jqGrid({
                pager: '#gridpager',
                colModel: [
                    {name: 'viewId', hidden: true},
                    {name: 'orderDetailId', hidden: true},
                    {name: 'customStockItem.id', hidden: true},
                    {label: '商品/规格', name: 'customStockItem.name', width: 100, align: "center"},
                    {label: '品牌', name: 'customStockItem.brandName', width: 50, align: "center"},
                    {label: '成本', name: 'customStockItem.cost', width: 30, align: "center"},
                    {label: '库存数', name: 'bankNumber', width: 30, align: "center"},
                    {
                        label: '结算状态', name: 'customStockItem.isDistribution', width: 30, align: "center",
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

                    {
                        label: '退货数', name: 'stockNumber', width: 30, align: "center",
                        formatter: function (cellvalue, options, rowObject) {
                            var value = isDotype == "update" ? cellvalue : 0;
                            var price = rowObject["customStockItem.cost"];
                            var bankNumber = rowObject["bankNumber"];
                            var id = rowObject["viewId"] + "_" + price;
//                            return "<input type='text' class='input-sm form-control' value='" + value + "' onblur='validate(this," + id + ","+price+")' id='stockNumber_" + cellvalue + "'/>";
                            return "<input type='text' class='input-sm form-control' value='" + value + "' onblur='validate(this," + rowObject["viewId"] + "," + price + "," + bankNumber + ")' id='stockNumber_" + id + "'/>";
                        }
                    },
                    {
                        label: '合计', name: 'totalPrice', width: 30, align: "center",
                        formatter: function (cellvalue, options, rowObject) {
                            var price = rowObject["customStockItem.cost"];
                            var value = isDotype == "update" ? cellvalue : price;
                            var id = rowObject["viewId"] + "_" + price;
                            $("#tableSum").text((parseFloat($("#tableSum").text()) + parseFloat(value)).toFixed(2));
                            return "<input type='text' readonly='readonly' class='input-sm form-control' value='" + value + "' id='totalPrice_" + id + "'/>";
                        }
                    },
                    {
                        label: '操作', name: 'id', width: 20, align: "center",
                        formatter: function (cellvalue, options, rowObject) {
                            var price = rowObject["customStockItem.cost"];
                            var id = rowObject["viewId"] + "_" + price;
//                            alert(id)
                            return "<a href='#'style='text-decoration:underline;color:blue' onclick='deleteRow(" + options.rowId + ", \""+id+"\")'>删除</a>";
                        }
                    }
                ],
                rownumbers: true
            });
        }


        function deleteRow(rowId, uid) {
            var oldTotalPrice = $("input[id='totalPrice_" + uid + "']").val();
            var oldTableSum = parseFloat($("#tableSum").text());
//            var te = floatSub(oldTableSum,oldTotalPrice);
//            alert("aa"+te)
            $("#tableSum").text((oldTableSum - oldTotalPrice).toFixed(2));
            $("#gridBody").jqGrid("delRowData", rowId);

        }
        function subForm() {
            var oneData = "";
            var obj = $("#gridBody").jqGrid("getRowData");
            var isError = false;
            jQuery(obj).each(function () {


                var uid = this['viewId']+"_"+this['customStockItem.cost'];
                var validNumber = $("#stockNumber_"+uid).val();
                var bankNumber = this['bankNumber'];
//                if(validNumber > bankNumber){
//                    alert("退货数不能大于库存数!请确认");
//                    isError = true;
//                    return ;
//                }
                //商品ID    成本    退货数量       退货明细ID   库存
                var stockNumber = document.getElementById("stockNumber_"+uid).value;
                oneData += "" + this['viewId'] + "," + this['customStockItem.cost'] +
                        "," + stockNumber + "," + this['orderDetailId'] +
                        "," + this['bankNumber'] + ";";
            });
            if(isError)return ;
            if (oneData == "") {
                alert("请至少选择一个商品！");
                return;
            }
            $("#rowDatas").val(oneData);
            if (${refundOrder.id} != 0 ) {
                if (confirm("是否确认修改!")) {
                    $("#fm").submit();
                }
            }else {
                if (confirm("是否确认申请!")) {
                    $("#fm").submit();
                }
            }
        }

        function showSkuItemList(url) {
            $("#skuItemBody").jqGrid({
                url: url,

                colModel: [
                    {name: 'id', hidden: true},
                    {name: 'viewId',hidden:true},
                    {name: 'isDistribution',hidden:true},
                    {label: '商品/规格', name: 'name', width: 80, align: "center"},
                    {label: '品牌', name: 'brandName', width: 50, align: "center"},
                    {label: '成本', name: 'cost', width: 20, align: "center"},
                    {label: '库存数量', name: 'number', width: 20, align: "center"},
                    { label: '顶级分类',index:'rootCategory', name: 'rootCategory', width: 40, align:"center",
                        formatter: "select", editoptions:{value:"1:机油;2:机滤;3:轮胎;4:电瓶;5:电子类产品;6:美容类产品;7:汽车用品;8:养护产品;9:耗材类产品;10:灯具类产品;" +
                    "11:雨刮类产品;12:发动机配件类;13:底盘配件类;14:变速箱类;15:电气类;16:车身覆盖类;17:服务类;0:临时分类"}},
                    {label: '二级分类', name: 'secondaryCategory.name', width: 50, align: "center"},

                    {
                        label: '操作', name: '', width: 20, align: "center",
                        formatter: function (cellvalue, options, rowObject) {
                            var addId = rowObject["viewId"] + "_" + rowObject["cost"];
                            var modify = "<a onclick=\"stockSetUrl(" + rowObject.id + ",'add',"+ rowObject['cost'] + ",'" + addId + "')\" href='#' style='text-decoration:underline;color:blue'>" + "添加" + "</a>";
                            return modify;
                        }
                    }

                ],
                rownumbers: true
            });
        }

        function getShopInfo(){
            var url = '/stock/list/data?name=' + $("#shopName").val()
                    + "&rootCategory=" + $("#rootCategory").val()
                    + "&shopId=" + $("#shopId").val();
            url = encodeURI(url,"UTF-8");
            jQuery("#skuItemBody").setGridParam({url:url}).trigger("reloadGrid", [{ page: 1}]);

        }

        function validate(obj, id, price, bankNumber) {
            var uid = id + "_" + price;
            var reg = new RegExp("^[0-9]*$");
            var result = 0;
            var oldTotalPrice = $("input[id='totalPrice_" + uid + "']").val();

            if (!reg.test(obj.value)) {
                obj.focus();
                $(obj).val(0);
                $("input[id='totalPrice_" + uid + "']").val(0);
            } else {
                if(bankNumber != null){
                    if(obj.value > bankNumber){
                        alert("退货数量不能大于库存数量")
                        obj.focus();
                    }
                }
                result = obj.value * price;
                $("input[id='totalPrice_" + uid + "']").val(result);
            }
            $("#tableSum").text((parseFloat($("#tableSum").text()) + result - oldTotalPrice).toFixed(2));
        }

        function validateDouble(obj, id, price) {
            var uid = id + "_" + price;
            var reg = /^[0-9]+([.]{1}[0-9]{1,2})?$/;
            var oldTotalPrice = $("input[id='totalPrice_" + uid + "']").val();
            var result = 0;
            if (!reg.test(obj.value)) {
                $(obj).val(0);
                $("input[id='totalPrice_" + uid + "']").val(0);
                obj.focus();
            } else {
                result = obj.value * $("input[id='stockNumber_" + uid + "']").val();
                $("input[id='totalPrice_" + uid + "']").val(result);
            }
            $("#tableSum").text((parseFloat($("#tableSum").text()) + result - oldTotalPrice).toFixed(2));
        }
    </script>
    <div class="row" style="margin-top: -80px">
        <div class="col-md-12">
            <div id="actions" class="form-action">
                <form class="" id="fm" action='<@spring.url relativeUrl = "/refundorder/save"/>' method="post">
                    <@form.textInput "refundOrder.id" "" "hidden"/>
                    <@form.textInput "refundOrder.ver" "" "hidden"/>
                    <input type="hidden" name="rowDatas" id="rowDatas">

                    <#if pageContent?? && pageContent == "update">
                        <legend>退货单管理 -> 修改退货单申请</legend>
                    <#else>
                        <legend>退货单管理 -> 添加退货单申请</legend>
                    </#if>


                    <div class="col-md-6">
                        <div class="row">
                            <div class="col-md-5">
                                <@form.textInput "refundOrder.refundShop.name" "class='form-control' readonly" "text" "退货门店" />
                                <input value="${refundOrder.refundShop.id}" name="refundShop.id" id="shopId"
                                       type="hidden">

                            </div>
                            <div class="col-md-5 col-md-offset-2">
                                <@form.textInput "refundOrder.orderNumberView" "class='form-control' readonly" "text" "单据编号" />

                            </div>
                        </div>
                        <div class="row">

                            <div class="col-md-5">
                                <div class="row">
                                    <label class="control-label">&nbsp;&nbsp;&nbsp;&nbsp;供应商</label>
                                </div>
                                <div class="row">
                                    <div class="col-md-12" >
                                        <select class="form-control" name="supplier.id" id="supplierId">
                                            <#list supplierList as shop>
                                                <option value="${shop.id?c}"
                                                        <#if (refundOrder.supplier.id)?? && refundOrder.supplier.id == shop.id >selected</#if> >${shop.name}</option>
                                            </#list>
                                        </select>
                                    </div>
                                </div>

                            </div>
                            <div class="col-md-5 col-md-offset-2">
                            </div>


                        </div>

                        <br>

                        <div class="row">
                            <div class="col-md-12">
                            <@form.textArea "refundOrder.remark" "class='form-control'" "备注"/>
                                </div>
                        </div>
                        <br/>
                        <table id="gridBody" class="scroll" cellpadding="0" cellspacing="0"></table>
                        <br>
                        <div class="row">
                            <div class="col-md-9"></div>
                            <div class="col-md-3">
                                <label  style="text-align: center" >合计:<span id="tableSum" ></span> 元</label>
                            </div>
                        </div>
                        <div class="span2 text-center">
                            <#if pageContent?? && pageContent == "update">
                                    <@form.btn_save "onclick='subForm()'" "确认修改"/>
                                <#else>
                                <@form.btn_save "onclick='subForm()'" "确认申请"/>
                            </#if>
                        </div>

                    </div>
                    <div class="col-md-6">
                        <div class="col-md-1" ></div>
                        <div class="col-md-11" >
                            <div id="actions" class="form-action">
                                <div class="row">
                                    <label class="control-label">商品名称: </label>
                                    <input type="text" id="shopName" value="">
                                    <label class="control-label">顶级分类: </label>
                                    <@form.topCategory "rootCategory" "" />

                                    <@form.btn_search "onclick='getShopInfo()'" "查 询"/>
                                </div>
                                <br>
                                <div class="row">
                                    <table id="skuItemBody" class="scroll" cellpadding="0" cellspacing="0"></table>
                                </div>
                                <div id="toolBar"></div>
                            </div>
                        </div>
                    </div>

                </form>
            </div>
        </div>
    </div>
    </@main.frame>

</#escape>