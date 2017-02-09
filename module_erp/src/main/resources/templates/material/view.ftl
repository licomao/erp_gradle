<#import "../macros/mainFrame.ftl" as main />
<#import "../macros/formMacros.ftl" as form />
<#import "/spring.ftl" as spring />
<#escape x as x?html>

    <@main.frame>
    <meta http-equiv="Windows-Target" contect="_top">
    <script src="/javascripts/jquery.iframe-transport.js" type="text/javascript"></script>
    <script src="/javascripts/jquery.fileupload.js" type="text/javascript"></script>

    <script type="text/javascript">
        $('#collapseShop').collapse('show');
        $(function(){
            var url = "/material/list/shopdata?name=" + $("#shopName").val()
                    + "&rootCategory=" + $("#rootCategory").val()
                    + "&shopId=" +  $("#shopId").val() ;
            showShopList(url);
            var mdurl = "/material/add";
            addStock(mdurl);

            <#if (materialOrder.materialOrderDetails)?? >
                <#list materialOrder.materialOrderDetails as rowData >
                    var customId = "${rowData.customStockItem.id?c}";
                    var row = $("#gridBody").getGridParam("reccount") + 1;
                    $("#gridBody").addRowData(row,
                            {"customStockItem.id":${rowData.id?c},
                                viewId:customId,
                                "customStockItem.name":"${rowData.customStockItem.name}",
                                "customStockItem.brandName":"<#if rowData.customStockItem.brandName??>${rowData.customStockItem.brandName}</#if>",
                                "customStockItem.cost":${rowData.cost?c},
                                "customStockItem.number":${rowData.customStockItem.number},
                                "number":${rowData.number?c}
                            },
                            "last");
                </#list>
                showSum();
            </#if>

//            $('.Wdate').datepicker();
//            $('.Wdate').datepicker("option",$.datepicker.regional["zh-TW"]);
//            $('#validDate').datepicker( "setDate", $('#validDate').val());
        });


        function getShopInfo() {
            var url = "/material/list/shopdata?name=" + $("#shopName").val()
                    + "&rootCategory=" + $("#rootCategory").val()
                    + "&shopId=" +  $("#shopId").val() ;
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
                        label: '操作', width: 75, align: "center",
                        formatter: function (cellvalue, options, rowObject) {
                            var modify = "<a onclick=\"stockSetUrl(" + rowObject.id + ")\" href='#' style='text-decoration:underline;color:blue'>" + "添 加" + "</a>";
                            return modify;
                        }
                    }
                ],
                rownumbers: true,
                gridComplete:function() {
                    showSum();
                }
            });
        }

        function stockSetUrl(id) {
            var rowData = $("#shopBody").jqGrid('getRowData',id);

            var obj = $("#gridBody").jqGrid("getRowData");
            var flag = true;
            jQuery(obj).each(function(){
                 if( this['customStockItem.id'] == rowData.id){
                     alert("该商品已经选择了");
                     flag = false;
                     return false;
                 }
            });
            if (flag){
                var row = $("#gridBody").getGridParam("reccount") + 1;
                $("#gridBody").addRowData(row,
                        {"customStockItem.id":rowData.id,
                            viewId:rowData.viewId,
                            "customStockItem.name":rowData.name,
                            "customStockItem.brandName":rowData.brandName,
                            "customStockItem.cost":rowData.cost,
                            "number":rowData.viewId},
                        "last");
            }
        }

        function addStock(url){
            $("#gridBody").jqGrid({
//                url: url,
                pager : '#gridpager',
                colModel: [
                    { name:'customStockItem.id'  , hidden:true },
                    { name:'viewId'  , hidden:true },
                    { label: '耗材名称',name:'customStockItem.name', width: 70, align:"center" },
                    { label: '品牌名称', name:'customStockItem.brandName',width: 50, align:"center"},
                    { label: '入库单价', name:'customStockItem.cost', width: 40, align:"center"},
                    { label: '领用数量',name:'number', width: 30,align:"center",
                        formatter: function (cellvalue, options, rowObject) {
                            return "<input type='text' class='input-sm form-control' value='"+ cellvalue +"' onblur='validate(this, " + rowObject['customStockItem.number'] +" )' id='number_"+ rowObject['viewId']  +"_"+rowObject['customStockItem.cost']+"' readonly/>";
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
                oneData += "" + this['viewId'] + ","+ $("#number_"+ this['customStockItem.id']).val() + ","+ this['customStockItem.cost'] +";";
            });

            if(oneData == ""){
                alert("请至少选择一个耗材商品！");
                return;
            }
            $("#rowDatas").val(oneData);
            if(confirm("是否确认领用!")){
                $("#fm").submit();
            }
        }

        function back(){
            window.location = "/material/list";
        }


        function showSum(){
            var obj = $("#gridBody").jqGrid("getRowData");
            var sum = 0;
            jQuery(obj).each(function(){
                sum += this['customStockItem.cost'] * $("#number_"+ this['viewId'] + "_" + this['customStockItem.cost'].replace('.',"\\.")).val() ;
            });
            document.getElementById("sum").innerHTML = sum.toFixed(2)
        }


        function validate(obj){
            var reg = new RegExp("^[0-9]*$");
            if(!reg.test(obj.value)){
                alert("请输入正整数!");
                obj.focus();
            }
//            if(!/^[0-9]*$/.test(obj.value)){
//                alert("请输入数字!");
//            }
        }
    </script>

    <div class="row" style="margin-top: -80px">
        <div class="col-md-11">
            <div id="actions" class="form-action">
                <form class="" id="fm" action='<@spring.url relativeUrl = "/material/save"/>'   method="post">
                    <@form.textInput "materialOrder.id" "" "hidden"/>
                    <@form.textInput "materialOrder.ver" "" "hidden"/>
                    <input type="hidden" name="rowDatas" id="rowDatas">
                    <legend>领用单管理 -> 耗材领用查看</legend>
                    <div class="col-md-5">
                        <div class="row">
                            <div class="col-md-5">
                                <input id="shopId" type="hidden" value="${materialOrder.shop.id}">
                                <@form.textInput "materialOrder.shop.name" "class='form-control' readonly" "text" "领用门店" />
                            </div>
                            <div class="col-md-5 col-md-offset-2">
                                <@form.textInput "materialOrder.orderNumView" "class='form-control' readonly" "text" "领用单据号" />
                                <input type="hidden" name="orderNum" value="${materialOrder.orderNum}">
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-5">
                                <@form.textInput "materialOrder.erpUser.username" "class='form-control' readonly" "text" "领用人" />
                            </div>
                            <div class="col-md-5 col-md-offset-2">
                                <@form.textInput "materialOrder.useDate" "class='form-control' readonly" "text" "领用日期" />
                            </div>
                        </div>
                        <div class="row">
                            <@form.textArea "materialOrder.remark" "class='form-control' readonly" "备注"/>
                        </div>
                        <div class="row">
                            <table id="gridBody" class="scroll" cellpadding="0" cellspacing="0"></table>
                        </div>
                        <div class="row text-right">
                            <span>合计：</span><label id="sum">0.00</label>元
                        </div>
                        <div class="span2 text-center">
                            <#--<@form.btn_back "onclick='back()'" "返回上一页"/>-->
                        </div>
                    </div>
                </form>
            </div>
        </div>

    </div>
    </@main.frame>

</#escape>