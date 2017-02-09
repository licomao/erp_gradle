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
            var url = "/material/list/shopdata?name=" + $("#shopName").val()
                    + "&rootCategory=" + $("#rootCategory").val()
                    + "&shopId=" +  $("#outShopId").val() ;
            showShopList(url);
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
                            "customStockItem.number":${rowData.beforeNumber},
                            "number":${rowData.number}
                        },
                        "last");
                </#list>
            </#if>
//            $('.Wdate').datepicker();
//            $('.Wdate').datepicker("option",$.datepicker.regional["zh-TW"]);
//            $('#validDate').datepicker( "setDate", $('#validDate').val());
        });


        function getShopInfo() {
            var url = "/material/list/shopdata?name=" + $("#shopName").val()
                    + "&rootCategory=" + $("#rootCategory").val()
                    + "&shopId=" +  $("#outShopId").val() ;
            url = encodeURI(url,"UTF-8");
            jQuery("#shopBody").setGridParam({url:url}).trigger("reloadGrid", [{ page: 1}]);
        }
        function showShopList(url) {
            $("#shopBody").jqGrid({
                url: url,
                colModel: [
                    {name: 'id',hidden:true},
                    {name: 'viewId',hidden:true},
                    {label: '商品名称', name: 'name', width: 80, align: "center"},
                    {label: '品牌名称', name: 'brandName', width: 50, align: "center"},
                    {label: '入库单价', name: 'cost', width: 50, align: "center"},
                    {label: '库存数量', name: 'number', width: 50, align: "center"},
                    { label: '顶级分类',index:'rootCategory', name: 'rootCategory', width: 100, align:"center",
                        formatter: "select", editoptions:{value:"1:机油;2:机滤;3:轮胎;4:电瓶;5:电子类产品;6:美容类产品;7:汽车用品;8:养护产品;9:耗材类产品;10:灯具类产品;" +
                    "11:雨刮类产品;12:发动机配件类;13:底盘配件类;14:变速箱类;15:电气类;16:车身覆盖类;17:服务类;0:临时分类"}},
                    {label: '二级分类', name: 'secondaryCategory.name', width: 60, align: "center"},
                    {
                        label: '操作', width: 35, align: "center",
                        formatter: function (cellvalue, options, rowObject) {
                            var modify = "<a onclick=\"stockSetUrl(" + rowObject.id + ")\" href='#' style='text-decoration:underline;color:blue'>" + "添 加" + "</a>";
                            return modify;
                        }
                    }
                ],
                rownumbers: true
            });
        }

        function stockSetUrl(id) {
            var rowData = $("#shopBody").jqGrid('getRowData',id);
            var obj = $("#gridBody").jqGrid("getRowData");
            var flag = true;
            jQuery(obj).each(function(){
                 if( this['customStockItem.viewId'] == rowData.viewId){
                     alert("该商品已经选择了");
                     flag = false;
                     return false;
                 }
            });
            if (flag){
                var row = $("#gridBody").getGridParam("reccount") + 1;
                $("#gridBody").addRowData(row,
                        {"customStockItem.id":"",
                            viewId:rowData.viewId,
                            "customStockItem.name":rowData.name,
                            "customStockItem.brandName":rowData.brandName,
                            "customStockItem.cost":rowData.cost,
                            "customStockItem.number":rowData.number,
                            "number":0},
                        "last");
            }
        }

        function addStock(){
            $("#gridBody").jqGrid({
                pager : '#gridpager',
                colModel: [
                    { name:'customStockItem.id'  , hidden:true },
                    { name:'viewId'  , hidden:true },
                    { label: '商品名称',name:'customStockItem.name', width: 70, align:"center" },
                    { label: '品牌名称', name:'customStockItem.brandName',width: 50, align:"center"},
                    { label: '入库单价', name:'customStockItem.cost', width: 40, align:"center"},
                    {label: '库存数量', name:'customStockItem.number', width: 40, align:"center"},
                    { label: '调拨数量',name:'number', width: 30,align:"center",
                        formatter: function (cellvalue, options, rowObject) {
                            return "<input type='text' class='input-sm form-control' size='3' value='"+cellvalue+"'onblur='validate(this,"+ rowObject['customStockItem.number'] +")' id='number_"+rowObject["viewId"] + "_"+ rowObject["customStockItem.cost"]  +"'/>";
                        }
                    },

                    { label: '操作', name:'id', width: 30, align:"center",
                        formatter: function (cellvalue, options, rowObject) {
                            return "<a href='#'style='text-decoration:underline;color:blue' onclick='deleteRow("+ options.rowId +")'>删 除</a>";
                        }
                    }
                ],
                rownumbers: true
            });
        }

        function deleteRow(rowId){
            $("#gridBody").jqGrid("delRowData",rowId);
        }
        function subForm(){
            var oneData ="";
            var obj = $("#gridBody").jqGrid("getRowData");
            jQuery(obj).each(function(){
                oneData += "" + this['viewId'] + ","+ $("#number_"+ this['viewId'] + "_" + this['customStockItem.cost']).val() + ","
                        + this['customStockItem.cost'] + ","
                        + this['customStockItem.number'] +
                        ";";
            });


            if(oneData == ""){
                alert("请至少选择一个商品！");
                return;
            }
            $("#listData").val(oneData);
            if(confirm("是否确认申请调拨!")){
                $("#fm").submit();
            }
        }

        function validate(obj,stockNum){
            var reg = new RegExp("^[0-9]*$");
            if(!reg.test(obj.value)){
                alert("请输入正整数!");
                obj.focus();
                return;
            }
            if (stockNum < obj.value){
                alert("不得超出库存数量!");
                obj.focus();
                return;
            }

        }

        function changeOutShop(shopId){
            var url = "/material/list/shopdata?name=" + $("#shopName").val()
                    + "&rootCategory=" + $("#rootCategory").val()
                    + "&shopId=" +  shopId ;
            url = encodeURI(url,"UTF-8");
            jQuery("#shopBody").setGridParam({url:url}).trigger("reloadGrid", [{ page: 1}]);
        }
    </script>

    <div class="row" style="margin-top: -80px">
        <div class="col-md-11">
            <div id="actions" class="form-action">
                <form class="" id="fm" action='<@spring.url relativeUrl = "/stocktransferorder/editsave"/>'   method="post">
                    <@form.textInput "stockTransferOrder.id" "" "hidden"/>
                    <@form.textInput "stockTransferOrder.ver" "" "hidden"/>
                    <@form.textInput "stockTransferOrder.transferStatus" "" "hidden"/>
                    <input type="hidden" name="listData" id="listData">
                        <legend>库存调拨 -> 申请调拨单编辑</legend>
                    <div class="col-md-5">
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
                                <label class="control-label"  >调出门店</label><br>
                                <select name="outShop.id" id="outShopId" onchange="changeOutShop(this.value);" >
                                    <#list outShops as os >
                                        <#if SHOP.id != os.id>
                                        <option <#if os.id == stockTransferOrder.outShop.id> selected</#if> value="${os.id}">${os.name}</option>
                                        </#if>
                                    </#list>
                                </select>
                                <br>
                                <font size="1px" color="red">ps：选择不同的调出门店可查询对应门店的库存信息。</font>
                            </div>
                            <div class="col-md-5 col-md-offset-2">
                                <@form.textInput "stockTransferOrder.erpUser.realName" "class='form-control' readonly" "text" "申请人" />
                            </div>
                        </div>
                        <div class="row">
                            <@form.textArea "stockTransferOrder.remark" "class='form-control'" "备注"/>
                        </div>
                        <br/>
                        <table id="gridBody" class="scroll" cellpadding="0" cellspacing="0"></table>
                        <br>
                        <div class="span2 text-center">
                            <@form.btn_save "onclick='subForm()'" "确认申请"/>
                        </div>
                    </div>
                    <div class="col-md-6 col-md-offset-1" >
                        <div id="actions" class="form-action">
                            <div class="row">
                                <div class="col-md-8">
                                    <label  class="control-label">商品名称: </label>
                                    <input type="text" id="shopName" value="">
                                    <label  class="control-label">顶级分类: </label>
                                    <@form.topCategory "rootCategory" "" />&nbsp;
                                </div>
                                <div class="col-md-4">
                                    <@form.btn_search "onclick='getShopInfo()'" "查 询"/>
                                </div>
                            </div>
                            <br>
                            <table id="shopBody" class="scroll" cellpadding="0" cellspacing="0"></table>
                            <div id="toolBar"></div>
                        </div>
                    </div>
                </form>
            </div>
        </div>

    </div>
    </@main.frame>

</#escape>