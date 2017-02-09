<#import "../macros/mainFrame.ftl" as main />
<#import "../macros/formMacros.ftl" as form />
<#import "/spring.ftl" as spring />
<#escape x as x?html>

    <@main.frame>

    <meta http-equiv="Windows-Target" contect="_top">

  <#--  <style type="text/css">#dialog {
        display: none;
    }</style>-->

    <script type="text/javascript">
        var isDotype = "update";
        $('#collapseCustomerPurchasedSuite').collapse('show');


        var reg = /^[0-9]+([.]{1}[0-9]{1,2})?$/;    //正的double正则
        var positiveReg =  /^\+?[1-9]\d*$/;;             //正整数正则
        var arr = {};
        var customerERPProfileList = {};
        $(function () {
            var url = '/stock/list/data?name=' + $("#shopName").val()
                    + "&rootCategory=" + $("#rootCategory").val()
                    + "&shopId=" + $("#shopId").val();
            addStock();
            showSkuItemList(url);
            resValue("price",reg);
            resValue("expiation",positiveReg);

            if(${customSuite.id} != 0){
                <#if (customSuite.suiteItems)?? >
                    <#list customSuite.suiteItems as rowData >
                        var row = $("#gridBody").getGridParam("reccount") + 1;
                        $("#gridBody").addRowData(row,
                            {
                                "id":${rowData.id?c},
                                "skuItem.id":${rowData.skuItem.id?c},
                                "skuItem.name":"${rowData.skuItem.name}",
                                "skuItem.brandName":"${rowData.skuItem.brandName!""}",

                                "cost":"${rowData.cost}",
                                "times":"${rowData.times}",
                            },
                        "last");
                    </#list>
                </#if>
            }

            setNoTimes();
        });

        function setAuth() {

            $('.dialog').dialog({
                autoOpen: false
            });
            $('.dialog').dialog('open');

        }

        function setNoTimes() {
            var obj = $("#gridBody").jqGrid("getRowData");
            $(obj).each(function(){
                var uuid = this['skuItem.id'] + "_" + this['cost'];
                var checked = $("input[id='notimes_" + uuid + "']").is(':checked');
                if (checked){
                    $("input[id='times_" + uuid + "']").val("");
                    $("input[id='times_" + uuid + "']").attr("disabled", true);
                } else {
                    $("input[id='times_" + uuid + "']").attr("disabled", false);
                }
            });
        }

        function stockSetUrl(id,addId) {
            var foo = true;
            var obj = $("#gridBody").jqGrid("getRowData");
//            alert(addId);
            jQuery(obj).each(function(){
                if (addId == this['skuItem.id']+"_"+this['cost']) {
//                if (id == this['skuItem.id']) {

                    alert("该商品已经选择了");
                    foo = false;
                    return false;
                }

            });
            if(!foo){
                return ;
            }
            var row = $("#gridBody").getGridParam("reccount") + 1;
            var rowData = $("#skuItemBody").jqGrid('getRowData',id);
            var rowDataId = rowData.viewId;
            $("#gridBody").addRowData(row,
                    {
                        "id":0,
                        "skuItem.id":rowDataId,
                        "skuItem.name":rowData.name,
                        "skuItem.brandName":rowData.brandName,
                        "cost":rowData.cost

                    },"last");
        }

        function showSkuItemList(url){
           /* var url = '/stock/list/data?name=' + $("#shopName").val()
                    + "&rootCategory=" + $("#rootCategory").val()
                    + "&shopId=" + $("#shopId").val();*/

            $("#skuItemBody").jqGrid({
                url: url,
//                pager : '#toolBar',
                colModel: [
//                    {name: 'id',hidden:false},
                    {name: 'id',hidden:true},
                    {name: 'viewId',hidden:true},
//                    {name: 'viewId',hidden:false},
                    {label: '商品/规格', name: 'name', width: 80, align: "center"},
                    {label: '品牌', name: 'brandName', width: 50, align: "center"},
                    { label: '上次进价', name:'cost', width: 40, align:"center"},
                    { label: '库存数量', name:'number', width: 40, align:"center"},
                    { label: '顶级分类',index:'rootCategory', name: 'rootCategory', width: 100, align:"center",
                        formatter: "select", editoptions:{value:"1:机油;2:机滤;3:轮胎;4:电瓶;5:电子类产品;6:美容类产品;7:汽车用品;8:养护产品;9:耗材类产品;10:灯具类产品;" +
                    "11:雨刮类产品;12:发动机配件类;13:底盘配件类;14:变速箱类;15:电气类;16:车身覆盖类;17:服务类;0:临时分类"}},
                    {label: '二级分类', name: 'secondaryCategory.name', width: 60, align: "center"},

//                    {label: '条形码', name: 'barCode', width: 50, align: "center"},
                    { label: '操作', name:'', width: 50, align:"center",
                        formatter: function (cellvalue, options, rowObject) {
                            var addId = rowObject["viewId"] + "_" + rowObject["cost"];
                            var modify = "<a onclick=\"stockSetUrl(" + rowObject.id + ",'" + addId + "')\" href='#' style='text-decoration:underline;color:blue'>" + "添加" + "</a>";
                            return modify;
                        }

                    }

                ],
//                rownumbers: true

            });
        }


        function addStock(url) {

            $("#gridBody").jqGrid({
                pager : '#gridpager',
//                url: url,
//                multiselect:true,

                colModel: [
//                    {name:"id",hidden:false},
                    {name:"id",hidden:true},
                    {name:"skuItem.id",hidden:true},
//                    {name:"skuItem.id",hidden:false},
                    {label: '商品名称', name: 'skuItem.name', width: 80, align: "center"},
                    {label: '品牌名称', name: 'skuItem.brandName', width: 80, align: "center"},
                    {label: '上次进价', name: 'cost', width: 50, align: "center"},
                    {label: '无限次', name: 'times', width: 40, align: "center",
                        formatter: function (cellvalue, options, rowObject) {
                            if(cellvalue != null){
                                value = cellvalue;
                            }
                            var uuid = rowObject['skuItem.id'] + "_" + rowObject['cost'];
                            if (cellvalue == -1) {
                                return "<input type='checkbox' id='notimes_"+ uuid + "'  onclick='isnotimes(\"" + uuid +"\")' checked />";
                            }
                            return "<input type='checkbox' id='notimes_"+ uuid + "' onclick='isnotimes(\"" + uuid +"\")'/>";
                        }
                    },
                    {label: '可使用次数', name: 'times', width: 40, align: "center",
                        formatter: function (cellvalue, options, rowObject) {
                            var value = 1;
                            if (cellvalue != null){
                                value = cellvalue
                            }
                            var uuid = rowObject['skuItem.id'] + "_" + rowObject['cost'];
                            if (cellvalue == -1){
                                return "<input type='text' class='input-sm form-control' id='times_"+ uuid + "' onblur='validate(this,"+0+")' />";;
                            }

                            return "<input type='text' class='input-sm form-control' id='times_"+ uuid + "' value='"+value+"' onblur='validate(this,"+value+")' />";
                        }
                    },
                    { label: '操作', name:'', width: 50, align:"center",
                        formatter: function (cellvalue, options, rowObject) {
//                            var addId = rowObject["viewId"] + "" + rowObject["cost"];

                            var modify = "<a onclick='deleteItem(" + options.rowId +");' href='#' style='text-decoration:underline;color:blue'>" + "删除" + "</a>";
                            return modify;
                        }
                    }


                ],
//                rownumbers: true,

            });
        }

        function isnotimes(id) {
            var checked = $("input[id='notimes_" + id + "']").is(':checked');
            if (checked){
                $("input[id='times_" + id + "']").val("");
                $("input[id='times_" + id + "']").attr("disabled", true);
            } else {
                $("input[id='times_" + id + "']").val("1");
                $("input[id='times_" + id + "']").attr("disabled", false);
            }
        }

        function subForm() {
//            alert(1);
            var oneData = "";
            var obj = $("#gridBody").jqGrid("getRowData");
            $(obj).each(function(){
                var uuid = this['skuItem.id'] + "_" + this['cost'];
//               var uid = this['skuItem.id'];
                // 明细ID , 商品ID, 是否无限次 , 可使用次数 , 成本
                oneData += "" + this['id'] + "," + this['skuItem.id'] + "," + $("input[id='notimes_" + uuid + "']").is(':checked') + "," + $("input[id='times_" + uuid + "']").val() + "," + this['cost'] + ";";
            });

            if(oneData == ""){
                alert("请至少选择一个商品！");
                return;
            }
//            alert(oneData);
            $("#rowDatas").val(oneData);
            $("#fm").submit();
        }

        function validate(obj,oldValue){
            if(!reg.test(obj.value)){
                obj.focus();
                $(obj).val(oldValue);
            }
        }

        function getItemList(){
            var url = '/stock/list/data?name=' + $("#shopName").val()
                    + "&rootCategory=" + $("#rootCategory").val()
                    + "&shopId=" + $("#shopId").val();
            url = encodeURI(url,"UTF-8");
            jQuery("#skuItemBody").setGridParam({url:url}).trigger("reloadGrid", [{ page: 1}]);
        }

        function resValue(id,reg){
            $("#"+id).blur(function(){
                var foo = reg.test($(this).val());
                if(!reg.test($(this).val())){
                    if (id == "expiation") {
                        alert("有效天数必须为大于0的正整数");
                    }
                    $(this).val(1);
                }
            });
        }

        function deleteItem(rowId){
            $("#gridBody").jqGrid("delRowData",rowId);
        }

        function back(){
            window.location = "/customerpurchasesuite/vipcard/list";
        }

    </script>
    <div class="row" style="margin-top: -80px">
        <div class="col-md-12">
            <div id="actions" class="form-action">
                <form class="" id="fm" action='<@spring.url relativeUrl = "/customerpurchasesuite/vipcard/save"/>'
                      method="post">
                    <@form.textInput "customSuite.id" "" "hidden"/>

                    <@form.textInput "customSuite.ver" "" "hidden"/>
                    <input type="hidden" name="rowDatas" id="rowDatas">
                    <input type="hidden" value="${shopId}" id="shopId">

                    <#if pageContent?? && pageContent == "update">
                        <legend>设置会员套餐种类 -> 修改会员套餐</legend>
                    <#else>
                        <legend>设置会员套餐种类 -> 添加会员套餐</legend>
                    </#if>

                    <#--<div class="row">-->
                        <div class="col-md-6">
                            <div class="row">
                                <div class="col-md-4" >
                                    <@form.textInput "customSuite.name" "class='form-control'" "text" "卡名称" true/>
                                </div>
                                <div class="col-md-4 col-md-offset-2 " >
                                    <@form.textInput "customSuite.description" "class='form-control'" "text" "套餐描述" />
                                </div>
                            </div>
                            <div class="row">
                                <div class="col-md-4" >
                                    <@form.textInput "customSuite.price" "class='form-control'" "text" "价格" />
                                </div>
                                <div class="col-md-4 col-md-offset-2 " >
                                    <@form.textInput "customSuite.expiation" "class='form-control'" "text" "有效期天数" />
                                </div>
                            </div>

                            <br>
                            <div class="row">
                                <div class="col-md-4" >
                                <label>套餐项目</label>
                                    </div>
                            </div>
                            <br/>
                            <table id="gridBody" class="scroll" cellpadding="0" cellspacing="0"></table>
                            <br>
                            <div class="span2 text-center">
                            <#--<div class="row">-->
                            <#--<div class="col-md-4 col-md-offset-4">-->
                            <#--<br/>-->
                            <#--<@form.textInput "baseSet.posTopRate" "class='form-control'" "text" "pos机封顶费率" true/>-->
                                <@form.btn_save "onclick='subForm()'" "确认保存"/>
                                &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                                <@form.btn_save "onclick='back()'" "返回"/>
                            <#--</div>-->

                            <#--</div>-->
                            </div>

                        </div>
                        <div class="col-md-5 col-md-offset-1" >
                            <div id="actions" class="form-action">
                                <div class="row">
                                    <label  class="control-label">商品名称: </label>
                                    <input type="text" id="shopName" value="">
                                    <label  class="control-label">顶级分类: </label>
                                    <@form.topCategory "rootCategory" "" />&nbsp;
                                    <@form.btn_search "onclick='getItemList()'" "查 询"/>
                                </div>
                                <br>

                                <div class="row">
                                    <table id="skuItemBody" class="scroll" cellpadding="0" cellspacing="0"></table>
                                </div>
                                <div id="toolBar"></div>

                                <#--<div class="row">
                                <table id="skuItemBody" class="scroll" cellpadding="0" cellspacing="0"></table>
                                &lt;#&ndash;<div id="toolBar"></div>&ndash;&gt;
                                </div>-->
                            </div>
                        </div>


                    </div>

                </form>
            </div>
        </div>
    </div>
    </@main.frame>

</#escape>