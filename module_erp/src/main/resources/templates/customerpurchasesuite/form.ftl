<#import "../macros/mainFrame.ftl" as main />
<#import "../macros/formMacros.ftl" as form />
<#import "/spring.ftl" as spring />
<#escape x as x>

    <@main.frame>

    <meta http-equiv="Windows-Target" contect="_top">

    <style type="text/css">#dialog {
        display: none;
    }</style>

    <script src="/javascripts/fingerprint-tool.js" type="text/javascript"></script>

    <script type="text/javascript">
        var isDotype = "update";
        var myString = "";
        $('#collapseCustomerPurchasedSuite').collapse('show');
        var reg = /^[0-9]+([.]{1}[0-9]{1,2})?$/;
        var arr = {};
        var customerERPProfileList = {};
        $(function () {
            <#if (customerERPProfileList)??>
                <#list customerERPProfileList as obj >
                    var erpProfile = {};
                    erpProfile.id = "${obj.id}";
                    var customer = {};
                    customer.id = "${obj.customer.id}";
                    customer.mobile = "${obj.customer.mobile}";
                    erpProfile.customer = customer;
                    customerERPProfileList["${obj.id}"] = erpProfile;
                </#list>

            </#if>
            <#if (customSuiteList)?? >
                <#list customSuiteList as obj >
                    var customSuite = {};
                    var org = {};
                    org.id = "${obj.organization.id!}";
                    org.name = "${obj.organization.name!}";
                    customSuite.oranization = org;
                    customSuite.price = "${obj.price!}";
                    customSuite.description = "${obj.description!}";
                    customSuite.name = "${obj.name!}";
                    var arrs = [];
                    <#list obj.suiteItems as item >
                        var skuItem = {};
                        skuItem.id = "${item.skuItem.id}";
                        skuItem.name = "${item.skuItem.name!}";
                        skuItem.brandName = "${item.skuItem.brandName!}";
                        skuItem.description = "${item.skuItem.description!""}";
                        skuItem.price = "${item.skuItem.price}";
                        skuItem.rootCategory = "${item.skuItem.rootCategory!}";
                        var item = {};//套餐明细数组
                        item.id = "${item.id}";
                        item.times = "${item.times}";
                        item.cost = "${item.cost!}";
                        item.skuItem = skuItem;
                        arrs.push(item);

                    </#list>
                    customSuite.suiteItems = arrs;
                    arr["${obj.id}"] = customSuite;
                </#list>
            </#if>

            var url = "/material/list/shopdata?name=" + $("#shopName").val()
                    + "&rootCategory=" + $("#rootCategory").val()
                    + "&shopId=" +  $("#shopId").val() ;
            url = encodeURI(url,"UTF-8");
            showShopList(url);

            showSkuItemList();
            var suiteItems = arr[$("#customSuiteId").val()].suiteItems;
            for (var i in arr[$("#customSuiteId").val()].suiteItems) {
                var row = $("#skuItemBody").getGridParam("reccount") + 1;

                $("#skuItemBody").addRowData(row,
                        {
//                            "id": suiteItems[index].id,
                            "skuItemId": suiteItems[i].skuItem.id,
                            "suiteItemId": suiteItems[i].id,
                            "name": suiteItems[i].skuItem.name,
                            "brandName": suiteItems[i].skuItem.brandName,
                            "description": suiteItems[i].skuItem.description,
//                            "price": suiteItems[i].skuItem.price,
                            "times": suiteItems[i].times,
                            "cost": suiteItems[i].cost,
                            "rootCategory": suiteItems[i].skuItem.rootCategory
                        },
                        "last");
            }
            ;
            $("[id='suite.price']").val(arr[$("#customSuiteId").val()].price);

            $("#customSuiteId").change(function () {
                var suiteItems = arr[$(this).val()].suiteItems;
                $("[id='suite.price']").val(arr[$(this).val()].price);
                $("#skuItemBody").clearGridData(false);
                for (var index in suiteItems) {
                    var row = $("#skuItemBody").getGridParam("reccount") + 1;
                    $("#skuItemBody").addRowData(row,
                            {
                                "skuItemId": suiteItems[index].skuItem.id,
                                "suiteItemId": suiteItems[index].id,
                                "name": suiteItems[index].skuItem.name,
                                "brandName": suiteItems[index].skuItem.brandName,
                                "description": suiteItems[index].skuItem.description,
                                "cost": suiteItems[index].cost,
                                "rootCategory": suiteItems[index].skuItem.rootCategory,
                                "times": suiteItems[index].times
                            },
                            "last");
                }
            });


            $("#offpay").blur(function(){
                var value = $(this).val();
                if (!reg.test(value)) {
                    $(this).val("");
                    $("#offper").val("");

                } else {
                    var offper = value / formatterPrice($("[id='suite.price']").val());
                    var amount = parseFloat($("#cashAmount").val()) + parseFloat($("#posAmount").val());
                    if(amount > value){
                        $("#cashAmount").val(0);
                        $("#posAmount").val(0);
                    }
                    $("#offper").val(formatterPercent(offper) + "%");
                }
            })


        });

        /**
         * 验证指纹
         */
        function validFgp(){
            $("#fortst").text("现在可以开始验证指纹了");
            initErpFpcHandle();
        }

        /**
         * 申请权限
         *
         **/
         function setAuth() {

            var price = $("input[id='suite.price']").val();
            if(price == 0){
                alert("套餐售价已为0,无法再申请折扣!");
                return false;
            }

            initFingerprint();

            $("#pass").val("");
            $("#username").val("");

            $('#dialog').dialog({
                autoOpen: false
            });
            $('#dialog').dialog('open');
        }

        function success(){
            $("#fortst").text("验证指纹成功!");
        }

        function showSkuItemList(url) {
            $("#skuItemBody").jqGrid({
                pager : '#gridpager',
                colModel: [
                    {name:'skuItemId',hidden:true , formatter: function (cellvalue, options, rowObject) {
                        return formatterPrice(cellvalue);
                        }
                    },
                    {name:'suiteItemId',hidden:true , formatter: function (cellvalue, options, rowObject) {
                        return formatterPrice(cellvalue);
                    }
                    },
                    {label: '商品名称', name: 'name', width: 80, align: "center"},
                    {label: '品牌名称', name: 'brandName', width: 80, align: "center"},
                    {label: '商品描述', name: 'description', width: 80, align: "center"},
                    {label: '成本', name: 'cost', width: 50, align: "center"},
                    {label: '顶级分类', name: 'rootCategory', width: 50, align: "center",
                        formatter: "select", editoptions:{value:"1:机油;2:机滤;3:轮胎;4:电瓶;5:电子类产品;6:美容类产品;7:汽车用品;8:养护产品;9:耗材类产品;10:灯具类产品;" +
                    "11:雨刮类产品;12:发动机配件类;13:底盘配件类;14:变速箱类;15:电气类;16:车身覆盖类;17:服务类;0:临时分类"}},
                    {label: '无限次', name: 'times', width: 40, align: "center",
                        formatter: function (cellvalue, options, rowObject) {
                            if(cellvalue != null){
                                value = cellvalue;
                            }
                            if (cellvalue == -1) {
                                return "<input type='checkbox' id='notimes_"+ formatterPrice(rowObject['skuItemId']) + "' onclick='isnotimes(\"" + formatterPrice(rowObject['suiteItemId']) +"\")' checked />";
                            }
                            return "<input type='checkbox' id='notimes_"+ formatterPrice(rowObject['skuItemId']) + "'  onclick='isnotimes(\"" + formatterPrice(rowObject['suiteItemId']) +"\")'/>";
                        }
                    },
                    {label: '可使用次数', name: 'times', width: 40, align: "center",
                        formatter: function (cellvalue, options, rowObject) {
                            if (cellvalue == -1){
                                return  '<input type="text" id="time_' +  formatterPrice(rowObject['skuItemId']) + '" class="content" onblur="validate(this);" size="3" maxlength="3" disabled/>';
                            }
                            return  '<input type="text" id="time_' +  formatterPrice(rowObject['skuItemId']) + '" value="'+ cellvalue +'" class="content" onblur="validate(this);" size="3" maxlength="3" />';
                        }
                    },
                    {
                        label: '操作', name: 'do', width: 40, align: "center",
                        formatter: function (cellvalue, options, rowObject) {
                            return "<a href='####'style='text-decoration:underline;color:blue' onclick='deleteRow(" + options.rowId + ")'>删 除</a>";
                        }
                    }
                ],
                rownumbers: true
            });
        }

        function validate(obj) {
            var reg = new RegExp("^[0-9]*$");
            if (!reg.test(obj.value)) {
                alert("请输入正整数!");
                obj.focus();
                return;
            }
        }

        function isnotimes(id) {
            var checked = $("input[id='notimes_" + id + "']").is(':checked');
            if (checked){
                $("input[id='time_" + id + "']").val("");
                $("input[id='time_" + id + "']").attr("disabled", true);
            } else {
                $("input[id='time_" + id + "']").val("1");
                $("input[id='time_" + id + "']").attr("disabled", false);
            }
        }

        function getShopInfo() {
            var url = "/material/list/shopdata?name=" + $("#shopName").val()
                    + "&rootCategory=" + $("#rootCategory").val()
                    + "&shopId=" +  $("#shopId").val() ;
            url = encodeURI(url,"UTF-8");
            jQuery("#shopBody").setGridParam({url:url}).trigger("reloadGrid", [{ page: 1}]);
        }
        function showShopList(url) {
            $("#shopBody").jqGrid({
                url: url,
                rowNum :10,
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
                            var modify = "<a onclick=\"stockSetUrl(" + rowObject.id + ")\" href='####' style='text-decoration:underline;color:blue'>" + "添 加" + "</a>";
                            return modify;
                        }
                    }
                ],
                rownumbers: true
            });
        }

        function stockSetUrl(id) {
            var rowData = $("#shopBody").jqGrid('getRowData',id);

            var obj = $("#skuItemBody").jqGrid("getRowData");
            var flag = true;
            jQuery(obj).each(function(){
                if( this['skuItemId'] == rowData.viewId && rowData.cost == this['cost']){
                    alert("该商品已经选择了");
                    flag = false;
                    return false;
                }
            });
            if (flag){
                var row = $("#skuItemBody").getGridParam("reccount") + 1;
                $("#skuItemBody").addRowData(row,
                        {
                            "skuItemId": rowData.viewId,
                            "suiteItemId": rowData.id,
                            "name": rowData.name,
                            "brandName": rowData.brandName,
                            "description": rowData.description,
                            "cost": rowData.cost,
                            "rootCategory": rowData.rootCategory,
                            "times": 0
                        },
                        "last");
            }
        }



        function deleteRow(rowId) {
            $("#skuItemBody").jqGrid("delRowData", rowId);
        }

        function subForm() {
            var staffId = $("#staffId").val();
            var name = $("#name").val();
            if(staffId == null){
                alert("暂无上班打卡销售人员!请先去打卡。")
                return;
            }
            if(name == ""){
                alert("请先选择一名顾客!");
                return;
            }

            var oneData = "";
            var obj = $("#skuItemBody").jqGrid("getRowData");
            var j = 1;
            jQuery(obj).each(function(){
                var uid = this['viewId']+""+this['lastPrice'];
                oneData += "" + 0 + "," + formatterPrice(this['skuItemId']) + "," + $("input[id='notimes_" + this['skuItemId'] + "']").is(':checked') + ","+ formatterPrice($("#time_"+this['skuItemId']).val()) +  "," + this['cost'] +";";
            });

            $("#rowDatas").val(oneData);
            var cashAmount = parseFloat($("#cashAmount").val());
            var posAmount = parseFloat($("#posAmount").val());
            var offpay = $("#offpay").val();
            offpay = offpay == "" ? parseFloat(formatterPrice($("input[id='suite.price']").val())) : parseFloat(offpay);

            if(cashAmount + posAmount != offpay){
                alert("付款的金额有误,请重新输入");
                return;
            }

            if(confirm("是否确认售卡!")){
                var a = $("[id='suite.price']").val();
                a = a.replace(/,/g,'');
                $("[id='suite.price']").val(a);
                $("#fm").submit();
            }
        }

        function checkAuthority() {
            $('#dialog').dialog('close');
            $.get('/customerpurchasesuite/authority/check?username=' + $("#username").val() + '&password=' + $("#pass").val()
                    , function (resultMap) {
                        if (resultMap["result"]) {
                            alert("授权成功!")
                            $("#offpay").removeAttr("readonly");
                            $("#offpay").css("backgroundColor", "#FFFFFF");

                            $("#authButton").remove();
                            $("#authDiv").append("<input value='" + resultMap['erpUserRealName'] + "' type='text' readonly='readonly' style='width: 100%;height: 34px' >");
                            document.getElementById("authButtonStr").value = resultMap['erpUserRealName'];

                        }else{
                            $("#fortst").text("");
                            alert(resultMap["message"])
                        }
                    }, 'json');
        }

        function getCustomer(mobile){
            if(mobile!=""){
                $.get('/customer/find/' + mobile, function(data){
                    if(data.customerERPProfile != null){
                        $("#name").val(data.customerERPProfile.realName);
                        $("#customerId").val(data.customerERPProfile.id);
                    }else{
                        alert("未找到该用户或用户信息不全！")
                    }
                });
            }
        }

        function validAmount(obj){
            var reg = /^[0-9]+([.]{1}[0-9]{1,2})?$/;
            if(!reg.test(obj.value)){
                obj.focus();
                $(obj).val(0);
            }else{
                var amount = parseFloat($("#cashAmount").val()) + parseFloat($("#posAmount").val());
                var offpay = $("#offpay").val();
                offpay = offpay == "" ? parseFloat(formatterPrice($("[id='suite.price']").val())) : parseFloat(offpay);
                    if(amount > offpay){
                        alert("付款的金额有误,请重新输入");
                        $(obj).val(0);
                        obj.focus();
                    }
            }
        }
    </script>

    <SCRIPT type="text/javascript" FOR="myativx" EVENT="OnFeatureInfo(qulity)">
        // js 处理具体内容。
        $("#onFeatureInfoView").val(qulity);
        var str = "不合格";
        $("#fortst").text(qulity);
        if (qulity == 0) {
            str = "合格";

        }
        if (qulity == 1) {
            str = "特征点不够";
            $("#fortst").text("验证失败,请重试!");
        }

        if (matx.IsRegister) {
            if (matx.EnrollIndex != 1) {
                var t = matx.EnrollIndex - 1;
//                $("#forReg").val("登记状态：请再按 " + t.toString() + " 次指纹 ");
                $("#fortst").text("登记状态：请再按 " + t.toString() + " 次指纹 ");
            }
        }

    </SCRIPT>

    <SCRIPT type="text/javascript" FOR="myativx" EVENT="OnCapture(ActionResult ,ATemplate)">
        var tmp = matx.GetTemplateAsString();

        var Parr = [9, 0];  //alert(arr[0]);

        ret = matx.IdentificationFromStrInFPCacheDB(fpcHandle, tmp, Parr[0], Parr[1]);
        if (ret != -1) {
            $('#dialog').dialog('close');
            $.get('/customerpurchasesuite/authority/check?id=' + ret.toString()
                    , function (resultMap) {
                        if (resultMap["result"]) {
                            alert("授权成功!")
                            $("#offpay").removeAttr("readonly");
                            $("#offpay").css("backgroundColor", "#FFFFFF");

                            $("#authButton").remove();
                            $("#authDiv").append("<input value='" + resultMap['erpUserRealName'] + "' type='text' readonly='readonly' style='width: 100%;height: 34px' >");
                            document.getElementById("authButtonStr").value = resultMap['erpUserRealName'];

                        }else{
                            alert(resultMap["message"])
                        }
                    }, 'json');

            success();

            matx.EndEngine();

        } else {
            $("#fortst").text("验证失败,请重试!");

        }
    </SCRIPT>

    <div class="row" style="margin-top: -80px">
        <div class="col-md-11">
            <div id="actions" class="form-action">
                <form class="" id="fm" action="/customerpurchasesuite/save" method="post">
                    <@form.textInput "customerPurchasedSuite.id" "" "hidden"/>
                    <@form.textInput "customerPurchasedSuite.ver" "" "hidden"/>

                    <input type="hidden" id="customerId" name="customerErpProfileId" >

                    <input type="hidden" name="rowDatas" id="rowDatas">

                    <#if pageContent?? && pageContent == "update">
                        <legend>会员套餐管理 -> 会员套餐修改</legend>
                    <#else>
                        <legend>会员套餐管理 -> 会员套餐销售</legend>
                    </#if>

                    <div class="row">
                        <div class="col-md-2">
                            <span style="font-size: large;font-style:oblique;font-weight: bold;text-align: center">客户资料</span>

                        </div>
                        <div class="col-md-3">
                            <div class="row">
                                <label class="control-label">&nbsp;&nbsp;&nbsp;&nbsp;手机</label>
                            </div>
                            <div class="row">
                                <div class="col-md-12">
                                    <input class="form-control" onblur="getCustomer($(this).val());"  name="customer.mobile" id="mobile" >
                                </div>
                            </div>
                        </div>
                        <div class="col-md-3">
                            <div class="row">
                                <label class="control-label">&nbsp;&nbsp;&nbsp;&nbsp;姓名</label>
                            </div>
                            <div class="row">
                                <div class="col-md-12">
                                     <input class="form-control" readonly="readonly" id="name" >
                                </div>
                            </div>
                        </div>
                    </div>
                    <br>

                    <div class="row">
                        <div class="col-md-2">
                            <span style="font-size: large;font-style:oblique;font-weight: bold;text-align: center">会员卡信息</span>
                        </div>
                        <div class="col-md-3">
                            <div class="row">
                                <label class="control-label">&nbsp;&nbsp;&nbsp;&nbsp;套餐名称</label>
                            </div>
                            <div class="row">
                                <div class="col-md-12">
                                    <select class="form-control" name="suite.id"  id="customSuiteId">
                                        <#list customSuiteList as customSuite>
                                            <option value="${customSuite.id}"
                                                    <#if (customerPurchasedSuite.suite.id)?? && customerPurchasedSuite.suite.id == customSuite.id >selected</#if> >
                                            ${customSuite.name}
                                            </option>
                                        </#list>
                                    </select>
                                </div>
                            </div>

                        </div>
                        <div class="col-md-3">
                            <@form.textInput "customerPurchasedSuite.suite.price" "class='form-control' readonly" "text" "套餐售价" />
                        </div>

                    </div>
                    <div class="row">
                        <div class="col-md-3 col-md-offset-2">
                            <div class="row">
                                <label class="control-label">&nbsp;&nbsp;&nbsp;&nbsp;销售人员</label>
                            </div>
                            <div class="row" >
                                <div class="col-md-12">

                                    <select class="form-control" name="staff.id" id="staffId">
                                        <#list staffList as staffObj>
                                            <option value="${staffObj.id}" <#if (customerPurchasedSuite.staff.id)?? && customerPurchasedSuite.staff.id == staffObj.id >selected</#if> >
                                                ${staffObj.name}
                                            </option>
                                        </#list>
                                    </select>
                                </div>
                            </div>
                        </div>
                        <div class="col-md-3">
                            <@form.textInput "shop.name" "class='form-control' readonly" "text" "开卡门店" />
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-3 col-md-offset-2">
                            <div class="form-group" id="authDiv">
                                <label class="control-label">授权人</label>
                                <input style="width: 100%;height: 34px"  id="authButton" type="button"
                                       onclick="setAuth();" value="申请授权">
                            </div>
                            <input type="hidden" name="authButtonStr" id="authButtonStr" value="">
                        </div>
                        <div class="col-md-3">
                            <div class="form-group">
                                <label class="control-label">折后售价</label>
                                <input style="width: 100%;height: 34px; background-color: #eee" name="receivable"
                                       id="offpay" readonly="readonly" type="text">
                            </div>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-3 col-md-offset-2">
                            <div class="form-group">
                                <label class="control-label">现金付款</label>
                                <input style="width: 100%;height: 34px;" id="cashAmount" value="0" onblur="validAmount(this)"
                                       name="cashAmount"   type="text">
                            </div>
                        </div>
                        <div class="col-md-3">
                            <div class="form-group">
                                <label class="control-label">POS付款</label>
                                <input style="width: 100%;height: 34px;"  id="posAmount" value="0" onblur="validAmount(this)"
                                       name="posAmount"  type="text">
                            </div>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-3 col-md-offset-2">
                            <div class="form-group">
                                <label class="control-label">折扣率(%)</label>
                                <input style="width: 100%;height: 34px;background-color: #eee"  id="offper"
                                     name="discount"  readonly="readonly" type="text">
                            </div>
                        </div>
                        <div class="col-md-3">

                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-2">
                            <span style="font-size: large;font-style:oblique;font-weight: bold;text-align: center">备注信息</span>
                        </div>
                        <div class="col-md-3">
                            <div class="form-group ">
                                <label class="control-label" for="remark">备注</label>
                                <div class="controls">
                                    <textarea id="remark" style='width:460px;height:80px;' name="remark"></textarea>
                                    <#--<textarea> id="" name="" </textArea>-->
                                </div>
                            </div>

                            <#--<@form.textArea "customerPurchasedSuite.remark" "class='form-control' style='width:615px;height:80px;'"  "备注" true/>-->

                            <#--<div class="form-group">-->
                                <#--<label class="control-label">备注</label>-->
                                <#--<input style="width: 100%;height: 34px;"  id="remark"-->
                                       <#--name="remark"  type="text">-->
                            <#--</div>-->
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-6">
                            <table id="skuItemBody" cellpadding="0" cellspacing="0"></table>
                        </div>
                        <div class="col-md-6">
                            <div id="actions" class="form-action">
                                <div class="row">
                                    <div class="col-md-8">
                                        <label  class="control-label">商品名称: </label>
                                        <input type="text" id="shopName" value="">
                                        <label  class="control-label">顶级分类: </label>
                                        <@form.topCategory "rootCategory" "" />&nbsp;
                                        <input id="shopId" value="${shopId}" type="hidden"/>
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
                    </div>
                    <div class="row">
                        <div class="col-md-4 col-md-offset-4">
                            <br/>
                            <@form.btn_save "onclick='subForm()'" "确认售卡"/>
                        </div>
                    </div>

                    <div id="dialog" class="dialog" title="验证申请授权" style="text-align: center;padding-top: 24px">
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
        </div>
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