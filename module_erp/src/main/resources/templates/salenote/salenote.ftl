<#import "../macros/mainFrame.ftl" as main />
<#import "../macros/formMacros.ftl" as form />
<#import "/spring.ftl" as spring />


<#escape x as x>
<#--<#escape x as x?html>-->
  <@main.frame>

  <meta http-equiv="Windows-Target" contect="_top">

  <style type="text/css">#dialog {
      display: none;
  }</style>
  <script src="/javascripts/fingerprint-tool.js" type="text/javascript"></script>
  <script  type="text/javascript">

    $('#collapsePayment').collapse('show');
    $(function () {

        showCusInfo();
        showItemlist();
        showWorkHours();
        showProject();
//          initFingerprint();
//          initErpFpcHandle();
        <#if settleOrder.orderDetails?? >
            <#list settleOrder.orderDetails as orderDetails >
                var customId = "${orderDetails.id?c}";
                var row = $("#customList").getGridParam("reccount") + 1;
                $("#customList").addRowData(row,
                        {"viewId":${orderDetails.orderedItem.id?c},
                            "orderedItem.name":"${orderDetails.orderedItem.name}",
                            "cost":${orderDetails.cost?c},
                            "additionRate":<#if orderDetails.orderedItem.secondaryCategory??>${orderDetails.orderedItem.secondaryCategory.additionRate?c}<#else>0</#if>,
                            "price":(${orderDetails.cost?c}*(1+<#if orderDetails.orderedItem.secondaryCategory??>${orderDetails.orderedItem.secondaryCategory.additionRate?c}<#else>0</#if>/100)).toFixed(2),
                            "rootCategory":${orderDetails.orderedItem.rootCategory},
                            "discount":${orderDetails.discount?c},
                            "count":${orderDetails.count?c},
                            <#if orderDetails.discountGranter??>
                            "receivablediscount":${orderDetails.discountPrice?c},
                            <#--"receivable":(${orderDetails.cost?c}*(1+<#if orderDetails.orderedItem.secondaryCategory??>${orderDetails.orderedItem.secondaryCategory.additionRate?c}<#else>0</#if> /100)*${orderDetails.count?c}).toFixed(2),-->
                            "discountGranter": "<#if orderDetails.discountGranter.realName?? >${orderDetails.discountGranter.realName}</#if>" +"," + "${orderDetails.discountGranter.id}",
                            </#if>
                            "worker":${orderDetails.merchandier.id} + "-" +"${orderDetails.merchandier.name}",
                            "receivable":${orderDetails.receivable?c}
                        },
                        "last");
            </#list>

        <#if settleOrder.operationItemDetails?? >
            <#list settleOrder.operationItemDetails as operationItemDetails >
                var operationItemId = "${operationItemDetails.id}";
                var hourRow = $("#workHoursList").getGridParam("reccount") + 1;

                $("#workHoursList").addRowData(hourRow,
                    {"id":${operationItemDetails.operationItem.id?c},
                        "name":"${operationItemDetails.operationItem.name}",
                        "laborHours":${operationItemDetails.operationItem.laborHours},
                        "sum":${operationItemDetails.sum?c}
                    },"last");

            </#list>
        </#if>

            getCostSum();
            getAmount();
            <#if settleOrder.payment??>calculatePosData(${settleOrder.payment.posAmount?c})</#if>
        </#if>
    });

    //商品搜索
    function showItemlist() {
        var itemName = $("#itemName").val();
        var rootCategory = $("#topCategory").val();
        $("#itemlist").jqGrid({
            postData: { name:itemName,rootCategory:rootCategory},
            url: '/salenote/item/data',
            rowNum :10,
            colModel: [
                { label: 'viewId', name: 'viewId', hidden:true, align:"center"},
                { label: '商品/规格', name: 'name', width: 100 , align:"center"},
                { label: '品名', name: 'name', hidden:true , align:"center"},
                { label: '规格', name: 'type', hidden:true , align:"center"},
                { label: '成本价', name: 'cost', hidden:true , align:"center"},
                { label: '加成率', name: 'secondaryCategory.additionRate', hidden:true, align:"center",
                    formatter:function(cellvalue, options, rowObject) {
                        if (cellvalue == null || cellvalue == '' || cellvalue =='undifined') {
                            return 0;
                        } else {
                            return cellvalue;
                        }

                    }
                },
                { label: '单价', name: 'price', hidden:true , align:"center"},
                { label: '分类', name: 'rootCategory', hidden:true , align:"center"},
                { label: '库存', name: 'number', width: 30 , align:"center",
                    formatter:function(cellvalue, options, rowObject) {
                        if (rowObject['rootCategory'] == 17) {
                            return 999;
                        } else {
                            return cellvalue;
                        }

                    }
                },
                { label: '入库价(元)', name: 'cost', width: 40 , align:"center"},
                { label: '销售单价(元)', name: 'realCost', width: 45 , align:"center",
                    formatter:function(cellvalue, options, rowObject) {
                        if (rowObject['secondaryCategory'] == null) {
                            return parseFloat(rowObject['cost']).toFixed(2);
                        } else {
                            return parseFloat(rowObject['cost'] * (1 + parseFloat(rowObject['secondaryCategory']['additionRate'] / 100))).toFixed(2);
                        }

                    }
                },
                { label: '条形码', name: 'barCode', width: 70 , align:"center"},
                { label: '商品描述', name: 'description', width: 40, align:"center"},
                { label: '操作', name: 'id', width: 30, align:"center",
                    formatter:function(cellvalue, options, rowObject) {
                        return "<a onclick='addItem(" + options.rowId +");' href='####' style='text-decoration:underline;color:blue'>" + "添加" + "</a>";
                    }
                }
            ],
        });
    }

    //改变数量
    function addItem(id) {
        var row = $("#customList").getGridParam("reccount") + 1;
        var rowData = $("#itemlist").jqGrid('getRowData',id);
        var obj = $("#customList").jqGrid("getRowData");
        var flag = true;

        jQuery(obj).each(function(){
            if( this['viewId'] == rowData['viewId'] && this['cost'] == rowData['cost']){
                alert("该商品已经选择了");
                flag = false;
            }
        });
        if (flag){
            $("#customList").addRowData(row,
                    {"viewId":rowData.viewId,
                        "orderedItem.name":rowData.name,
                        "type":rowData.type,
                        "rootCategory":rowData.rootCategory,
                        "additionRate":rowData["secondaryCategory.additionRate"],
                        "cost":rowData.cost,
                        "number":rowData.number},
                    "last");

            //获取成本总和 和 合计总和
            getCostSum();
            getAmount();
        }
    }

    function reloadGrid() {
        var itemName = $("#itemName").val();
        var rootCategory = $("#topCategory").val();
        $("#itemlist").jqGrid('setGridParam',{
            postData: { name:itemName,rootCategory:rootCategory}
        }).trigger("reloadGrid");
    }

    //获取成本总和
    function getCostSum(){
        var costSim=0;
        var obj = $("#customList").jqGrid("getRowData");
        jQuery(obj).each(function() {
            costSim += parseFloat(this["cost"]) * $("#count-" + this['viewId'] + "-" + this['cost'].replace('.',"\\.")).val();
        });
        $("#costsum").html(costSim.toFixed(2));
    }

    //获取合计
    function getAmount(){
        var amount=0;
        var obj = $("#customList").jqGrid("getRowData");
        jQuery(obj).each(function() {
            var receivablediscount = $("#receivablediscount-" + this['viewId'] + "-" + this['cost'].replace('.',"\\.")).val();//折后售价
            var receivable = this["receivable"];//实际售价
            var discount = this["discount"];
            if(receivablediscount == "") {
                amount +=parseFloat(receivable);
            }else {
                amount +=parseFloat(receivablediscount);
            }
        });
        $("#materialAmountView").val(amount);
        var hoursAmount=0;
        var hoursObj = $("#workHoursList").jqGrid("getRowData");
        jQuery(hoursObj).each(function(){
            var sum = $("#sum-" + this["id"]).val();//总工时费用
            hoursAmount +=parseFloat(sum);
        });

        $("#hoursAmountView").val(hoursAmount)
        amount += hoursAmount;
        $("#sum").html(amount.toFixed(2));
    }

    //获取界面数据集，传到后台
    function getOrderGridInfo(index) {

        var obj = $("#customList").jqGrid("getRowData");

        //施工人员
        if ($("#receiver").val() == null) {
            alert("没有施工人员，请先考勤。");
            return;
        }
        var cash = document.getElementById("payment.cashAmount").value;
        if(cash == null || cash == "") {
            document.getElementById("payment.cashAmount").focus();
            alert("请输入现金额度。");
            return;
        }
        var post = document.getElementById("posamount").value;
        if(post == null || post == "") {
            document.getElementById("posamount").focus();
            alert("请输入POS费用。");
            return;
        }

        var otherAmount = document.getElementById("payment.otherAmount").value;
        if(otherAmount == null || otherAmount == "") {
            document.getElementById("payment.otherAmount").focus();
            alert("请输入第三方费用。");
            return;
        }
        var app = document.getElementById("payment.appAmount").value;
        if(parseFloat(cash) + parseFloat(post) + parseFloat(app) + parseFloat(otherAmount) != parseFloat($("#sum").html())) {
            alert("输入金额总和与合计不符，请确认。");
            return;
        }

        var lines = $("#workHoursList").getGridParam("reccount");        //检测是否选择作业项目
        if(lines <= 0) {
            alert("请选择至少一个作业项目。");
            return;
        }

        var orderDetailData ="";
        jQuery(obj).each(function() {
            orderDetailData += "" + this['viewId'] + "," + this['orderedItem.name'] + "," + $("#count-" + this['viewId'] + "-" + this['cost'].replace('.',"\\.")).val() + ","
                    + this['discount'] + ",";

              //折后售价
            orderDetailData += $("#receivablediscount-" + this['viewId'] + "-" + this['cost'].replace('.',"\\.")).val();
            //实际售价
            orderDetailData += "," + this['receivable'];

            orderDetailData += "," + $("#receiver-" + this['viewId'] + "-" + this['cost'].replace('.',"\\.")).val() + "," + this['cost'];
            if($("#erpUserId-" + this['viewId'] + "-" + this['cost'].replace('.',"\\.")).val() != null ) {
                orderDetailData += "," + $("#erpUserId-" + this['viewId'] + "-" + this['cost'].replace('.',"\\.")).val();
            }
            orderDetailData += ";";
        });
        $("#salenotedata").val(orderDetailData);

        var hoursData = "";
        var hoursObj = $("#workHoursList").jqGrid("getRowData");
        jQuery(hoursObj).each(function() {
            hoursData += "" + this['id'] + "," + $("#sum-" + this['id']).val();
            hoursData += ";";
        });

        $("#workhoursdata").val(hoursData);

        $("#nextOrSettle").val(index);
        if(index == 1) {
            if(confirm("是否确认保存?")){
                $("#fm").submit();
            }
        }else {
            if(confirm("是否确认结算?")){
                $("#fm").submit();
            }
        }
    }


    function addShop() {
        window.location = "/customstockitem/tosave";
    }

    //订单详情
    function showCusInfo() {
        $("#customList").jqGrid({
//            url: '/salenote/showOrderInfo',
            pager : '#gridpager',
            rowNum: 40,
            colModel: [
                { label: 'ID', name: 'viewId', hidden:true},
                { label: '品名', name: 'orderedItem.name', width: 120, align:"center" },
                { label: '成本价(元)', name: 'cost', width: 50, align:"center" },
                { label: '加成率', name: 'additionRate', hidden:true},
                { label: '单价(元)', name: 'price', width: 50,editable:true, align:"center" ,
                    formatter:function(cellvalue, options, rowObject) {
                        var rate;
                        if(rowObject['additionRate'] == null) {
                            rate = 0;
                        }else {
                            rate = parseFloat(rowObject['additionRate'] / 100);
                        }
                        return (parseFloat(rowObject['cost']) * (1 + parseFloat(rate))).toFixed(2);
                }},
                { label: '分类', name: 'rootCategory', hidden:true },
                { label: '库存', name: 'number', width: 50,hidden:true },
                { label: '数量', name: 'count', width: 40, align:"center",
                    formatter:function(cellvalue, options, rowObject) {
                        if(cellvalue != null && cellvalue!="") {
                            return '<input type="currency" id="count-' + rowObject['viewId'] + '-' + rowObject['cost'] + '"  value="'+cellvalue + '" ' +
                            'class="content" ' +
                            'size="9" maxlength="10" style="text-align:center;" onblur="changeReceivable('+options.rowId + ')"  />';
                        }
                        return  '<input type="currency" id="count-' + rowObject['viewId'] + '-' + rowObject['cost'] + '"  value="1.00" ' +
                                'class="content" ' +
                                'size="9" maxlength="10" style="text-align:center;" onblur="changeReceivable('+options.rowId + ')"  />';
                    }
                },
                { label: '实际售价(元)', name: 'receivable', width: 50,editable:true, align:"center",
                    formatter:function(cellvalue, options, rowObject) {
                        if(cellvalue == null ) {
                        return parseFloat(rowObject['cost'] * (1 + parseFloat(rowObject['additionRate'] / 100))).toFixed(2);
                        } else {
                            return parseFloat(cellvalue).toFixed(2);
                        }

                    }},
                { label: '折后售价(元)', name: 'receivablediscount', width: 70, align:"center",
                    formatter:function(cellvalue, options, rowObject) {
                        var discount = rowObject['discount'];
                        var cell = formatterPrice(cellvalue);
                        if(!isNaN(discount) && !isNaN(cell)) {
                            return  '<input type="text" id="receivablediscount-' + rowObject['viewId'] + '-' + rowObject['cost'] + '" value="'+cellvalue+'" class="content"' +
                                    ' ' +
                                    'size="13" maxlength="10" style="text-align:center;" onblur="changeDiscount('+options.rowId + ')" disabled="disabled" />';
                        }
                        return  '<input type="text" id="receivablediscount-' + rowObject['viewId'] + '-' + rowObject['cost'] + '" value="" class="content"' +
                                ' ' +
                                'size="13" maxlength="10"  style="text-align:center;" onblur="changeDiscount('+options.rowId + ')" disabled="disabled" />';},
                },
                { label: '折扣率', name: 'discount', width: 60, align:"center"},
                { label: '授权人', name: 'discountGranter', width: 60, align:"center",
                    formatter:function(cellvalue, options, rowObject) {
                        if(cellvalue != null && cellvalue != "") {
                            var cells = cellvalue.split(',');
                            return "<div id='authdiv-" + rowObject['viewId'] + '-' + rowObject['cost'] + "'><input id='authBtn-" + rowObject['viewId'] + '-' + rowObject['cost'] + "' type='button' style='float:left;' onclick='setAuth(\"" + rowObject['viewId'] + '-' + rowObject['cost'] + '-' + rowObject['rootCategory'] + "\")' value='申请授权'>" +
                                    "<input value='" + cells[0] + "' type='text' style='margin-left:4%; width:50%' readonly='readonly'/>" +
                                    "<input id=erpUserId-" + rowObject['viewId'] + '-' + rowObject['cost'] + "  value='" + cells[1] + "' type='hidden' />" + "</div>";
                        }
                        return "<div id='authdiv-" + rowObject['viewId'] + '-' + rowObject['cost'] + "'><input id='authBtn-" + rowObject['viewId'] + '-' + rowObject['cost'] + "' type='button' onclick='setAuth(\"" +  rowObject['viewId'] + '-' + rowObject['cost'] + '-' + rowObject['rootCategory']  + "\")' value='申请授权'></div>";
                }},
                { label: '施工人员', name: 'worker', width: 60, align:"center",
                    formatter:function(cellvalue, options, rowObject) {
                        var str="";
                        var staffid="";

                        if(cellvalue != null && cellvalue.indexOf('-') > -1) {
                            var staff=cellvalue.split("-");
                            staffid = staff[0];
                            str = "<option value=\"" + staff[0] +"\" selected>" + staff[1] + "</option>";
                        }

                        var optionStr = "";
                        <#if staffList ??>
                        <#list staffList as staff>
                            if (staffid != ${staff.id?c}) {
                                optionStr += '<option value= "${staff.id?c}">${staff.name}</option>' ;
                            };
                        </#list>
                        </#if>
                        return  '<select class="form-control" type="search" id="receiver-'+rowObject['viewId'] + '-' + rowObject['cost'] +'" >' + str +
                                optionStr + '</select>';
                    }
                },
                { label: '操作', name: 'id', width: 30, align:"center",
                    formatter:function(cellvalue, options, rowObject) {
                        return "<a onclick='deleteCustomListRow(" + options.rowId +");' href='#' style='text-decoration:underline;color:blue'>" + "删除" + "</a>";
                    }
                }
            ],
//            ondblClickRow: function(id){//双击行
//                $("#customList").jqGrid("delRowData", id);
//                //获取成本总和 和 合计总和
//                getCostSum();
//                getAmount();
//            }
        });
    };


    function deleteCustomListRow(id) {
        $("#customList").jqGrid("delRowData", id);
        //获取成本总和 和 合计总和
        getCostSum();
        getAmount();
    }

    //改变数量
    function changeReceivable(id) {
        var rowdata = $("#customList").jqGrid("getRowData",id);

        var reg = new RegExp("^[0-9]+([.]{1}[0-9]+){0,1}$");
        var number = $("#count-" + rowdata['viewId'] + "-" + rowdata['cost'].replace('.',"\\.")).val();
        if(!reg.test(number)){
            alert("请输入大于0的数字!");
            $("#count-" + rowdata['viewId'] + "-" + rowdata['cost'].replace('.',"\\.")).focus();
            $("#count-" + rowdata['viewId'] + "-" + rowdata['cost'].replace('.',"\\.")).val(1);
            return;
        }

        if(parseFloat(number) > parseFloat(rowdata['number']) && rowdata['rootCategory'] != 17) { //数量大于库存
            alert("库存不够，请采购此商品");
            $("#count-" + rowdata['viewId'] + "-" + rowdata['cost'].replace('.',"\\.")).focus();
            return;
        }

        var cost = parseFloat(rowdata['price']) * parseFloat($("#count-" + rowdata['viewId'] + "-" + rowdata['cost'].replace('.',"\\.")).val());//保留两位小数
        $("#customList").jqGrid('setCell', id, 'receivable', cost.toFixed(2));
        getAmount();getCostSum();
    }

    //改变折扣售价
    function changeDiscount(id) {
        var rowdata = $("#customList").jqGrid("getRowData",id);

        var reg = new RegExp("^[0-9]+([.]{1}[0-9]+){0,1}$");
        if(!reg.test($("#receivablediscount-" + rowdata['viewId'] + "-" + rowdata['cost'].replace('.',"\\.")).val())){
            alert("请输入数字!");
            $("#count-" + id).focus();
            return;
        }

        var cost = parseFloat($("#receivablediscount-" + rowdata['viewId'] + "-" + rowdata['cost'].replace('.',"\\.")).val()) / parseFloat(rowdata['receivable']);
        $("#customList").jqGrid('setCell', id, 'discount', cost.toFixed(2));
        getAmount();
    }

    //改变现金和post机金额
    function validate(obj){
        var reg = new RegExp("^[0-9]+([.]{1}[0-9]+){0,1}$");
        if(!reg.test(obj.value)){
            alert("请输入数字!");
            obj.value = 0;
            obj.focus();
            return;
        }
    }

    //改变pos机金额
    function calculatePos(obj) {
        var reg = new RegExp("^[0-9]+([.]{1}[0-9]+){0,1}$");
        if(!reg.test(obj.value)){
            alert("请输入数字!");
            obj.focus();
            return;
        }

        var pos = parseFloat(${baseSet.posRate}) / 100 * parseFloat(obj.value);//费率
        if(pos > parseFloat(${baseSet.posTopRate}) && parseFloat(${baseSet.posTopRate}) !=0) {
            document.getElementById("payment.posAmount").value = (parseFloat(obj.value) - parseFloat(${baseSet.posTopRate})).toFixed(2);
        }else {
            document.getElementById("payment.posAmount").value = (parseFloat(obj.value) - pos).toFixed(2);
        }
    }

    //根据pos费率计算原来pos价格
    function calculatePosData(value) {
        var x1 = value + parseFloat(${baseSet.posTopRate});
        var x2 = value/(1 - parseFloat(${baseSet.posRate}/100));

        if(parseFloat(${baseSet.posTopRate}) ==0) {
            x1 = x2
        }
        var posData = x1<x2?x1:x2;
        $("#posamount").val(posData.toFixed(2));
    }

    var selectedid = 0;
    function setAuth(id) {

//        alert(id)

        var str=id.split("-");
        selectedid = str[0] + "-" + str[1];
        $("#fortst").text("");


        //成本价为0，且不为服务类
        if (str[1] == 0) {
            alert("价格已为0，无法打折");
            return;
        }
        initFingerprint();

        $("#pass").val("");
        $("#username").val("");

        /*INSERT INTO `garageman`.`finger_print_scanner` (`id`, `created_by`, `created_date`, `deleted`, `uid`, `updated_by`, `updated_date`, `ver`, `pid`, `sensorsn`, `usb_sn`, `vid`, `organization_id`, `shop_id`) VALUES ('12', '1-administrator', '2016-05-31 09:54:15', '\0', '079924ab-ae79-4023-b12f-77596d1ebcc4', '1-administrator', '2016-05-31 09:54:15', '0', NULL, '{12F11BFA-0ACB-45FF-961B-8C75453EDC60}', NULL, NULL, NULL, NULL);
*/

        $('#dialog').dialog({
            autoOpen: false
        });
        $('#dialog').dialog('open');
    }

    //验证授权人
    function checkAuthority() {
        $('#dialog').dialog('close');

        $.get('/salenote/authority/check?username=' + $("#username").val() + '&password=' + $("#pass").val()
                , function (resultMap) {
                    if (resultMap["result"]) {
                        alert("授权成功!");
                        $("[id=receivablediscount-" + selectedid.replace('.',"\\.") +"]").removeAttr("disabled");

                        var name = resultMap['erpUserRealName'] == null ? "" : resultMap['erpUserRealName'];
                        var erpUserId =  resultMap['erpUserId'];
                        $("#authdiv-" + selectedid.replace('.',"\\.")).empty();
                        $("#authdiv-" + selectedid.replace('.',"\\.")).append("<input value='" + name + "' type='text' readonly='readonly'/>" +
                                "<input id=erpUserId-" + selectedid + " value='" + erpUserId + "' type='hidden' />");
                        getAmount();
                    }else{
                        $("#fortst").text("");
                        alert("账号或密码错误,授权失败!")
                    }
                }, 'json');
    }

    //返回按钮
    function returnURL() {
        window.location = "/salenote/searchcustominfo";
    }

    function deleteRows() {
        var selectedIds = $("#customList").jqGrid("getGridParam", "selarrrow");

        if(eval(selectedIds) == 0) {
            alert("请选择行。")
        }else {
            for (i=selectedIds.length; i > 0; i--) {
                $("#customList").jqGrid("delRowData", selectedIds[i-1]);
            }
        }
    }

    //订单详情
    function showWorkHours() {
        $("#workHoursList").jqGrid({
            pager : '#gridpager',
            colModel: [
                { label: 'ID', name: 'id', hidden:true,align:"center"},
                { name: 'space', hidden:true,align:"center",
                    formatter:function(cellvalue,options,rowObject){
                        return "";
                    }
                },
                { label: '作业项目', name: 'name', width: 90,align:"center"},
                { label: '工时', name: 'laborHours', width: 25,align:"center"},
                { label: '单价', name: 'cost', width: 25,align:"center",
                    formatter:function(id) {
                        return ${baseSet.operationPrice?c};
                    }
                },
                { label: '总工时费', name: 'sum', width: 40,
                    formatter:function(cellvalue, options, rowObject) {
                        var costs = 0;
                        if(cellvalue == null) {
                            costs = rowObject["laborHours"] * ${baseSet.operationPrice?c};
                        }else {
                            costs = cellvalue;
                        }

                        return  '<input type="text" id="sum-'+rowObject['id'] +'" value="'+ costs +'" class="content"' +
                                ' ' +
                                'size="13" maxlength="15" onblur="changeHourCost('+ options.rowId +')" />';}
                },
                { label: '操作', name: 'del', width: 30, align:"center",
                    formatter:function(cellvalue, options, rowObject) {
                        return "<a onclick='deleteWorkListRow(" + options.rowId +");' href='#' style='text-decoration:underline;color:blue'>" + "删除" + "</a>";
                    }
                }
            ],
            toolbar: [false,"both"]
        });
    };

    function deleteWorkListRow(id) {
        var rowData = $("#workHoursList").jqGrid('getRowData',id);
        $("#workHoursList").jqGrid("delRowData", id);
        //获取合计总和
        getAmount();
    }

    //改变数量
    function changeHourCost(id) {
        var rowdata = $("#workHoursList").jqGrid("getRowData",id);
        var reg = /^[0-9]+([.]{1}[0-9]{1,2})?$/;
        var number = $("#sum-" + rowdata['id']).val();
        if(!reg.test(number)){
            alert("请输入大于0的数字!");
            $("#sum-" + rowdata['id']).focus();
            $("#sum-" + rowdata['id']).val(1);
            return;
        }

        getAmount();
    }

    /**
     * 验证指纹
     */
    function validFgp(){
//        initFingerprint();
        $("#fortst").text("现在可以开始验证指纹了");
        initErpFpcHandle();
    }

    function showProject() {
        var name = $("#name").val();
        var engineDisplacement = $("#engineDisplacement").val();
        var operationType = $("#operationType").val();
        $("#projectList").jqGrid({
            postData: { name: name, operationType: operationType, engineDisplacement: engineDisplacement},
            url: '/salenote/project',
            pager : '#projectBar',
            rowNum :10,
            colModel: [
                { label: 'ID', name: 'id', hidden:true},
                { label: '作业项目', name: 'name', width: 60 ,align:"center"},
                { label: '工时(H)', name: 'laborHours', width: 25 ,align:"center"},
                { label: '排量', name: 'carLevel', width: 20 ,align:"center"},
                { label: '分类', name: 'strType', width: 50 ,align:"center"},
                { label: '操作', name: 'del', width: 30, align:"center",
                    formatter:function(cellvalue, options, rowObject) {
                        return "<a onclick='addProject(" + options.rowId +");' href='####' style='text-decoration:underline;color:blue'>" + "添加" + "</a>";
                    }
                }
            ],
        });
    }

    function addProject(id) {
        var row = $("#workHoursList").getGridParam("reccount") + 1;
        var rowData = $("#projectList").jqGrid('getRowData',id);
        var obj = $("#workHoursList").jqGrid("getRowData");
        var flag = true;

        jQuery(obj).each(function(){
            if( this['id'] == rowData['id'] ){
                alert("该作业项目已经选择了");
                flag = false;
            }
        });
        if (flag) {
            $("#workHoursList").addRowData(row,
                    {
                        "id": rowData.id,
                        "name": rowData.name,
                        "laborHours": rowData.laborHours
                    },
                    "last");
            //获取合计总和
            getAmount();
        }
    }

    function reloadProjectGrid() {
        var name = $("#name").val();
        var operationType = $("#operationType").val();
        var engineDisplacement = $("#engineDisplacement").val();
        $("#projectList").jqGrid('setGridParam',{
            postData: { name: name, operationType: operationType, engineDisplacement: engineDisplacement}
        }).trigger("reloadGrid");
    }

    function checkByFinger(){

      var tmp = matx.GetTemplateAsString();
      var Parr = [9, 0];
      ret = matx.IdentificationFromStrInFPCacheDB(fpcHandle, tmp, Parr[0], Parr[1]);

      if (ret != -1) {
          $('#dialog').dialog('close');

          $.get('/salenote/authority/check?id=' + ret.toString()
                  , function (resultMap) {
                      if (resultMap["result"]) {
                          alert("授权成功!");
                          $("[id=receivablediscount-" + selectedid.replace('.',"\\.") +"]").removeAttr("disabled");

                          var name = resultMap['erpUserRealName'] == null ? "" : resultMap['erpUserRealName'];
                          var erpUserId =  resultMap['erpUserId'];
                          $("#authdiv-" + selectedid.replace('.',"\\.")).empty();
                          $("#authdiv-" + selectedid.replace('.',"\\.")).append("<input value='" + name + "' type='text' readonly='readonly'/>" +
                                  "<input id=erpUserId-" + selectedid + " value='" + erpUserId + "' type='hidden' />");
                          getAmount();
                      }else{
                          $("#fortst").text("");
                          alert("账号或密码错误,授权失败!")
                      }
                  }, 'json');

          success();

          matx.EndEngine();

        } else {
          $("#fortst").text("沒有匹配的指纹，请确认  指纹已录入系统!");
//          $("#fortst").text("验证失败,请重试!");
      }
    }
    /**
     * 打印委托书
     */
    function printit() {

        //customList
        $("#printDiv").jqPrintDataWithoutBorder({
//            headHtml:"车辆维修委托书",//上面这种跟下面这种都可以
            headHtml:"<legend style='font-size: x-large;' >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;车辆维修委托书</legend>",
            arr: [
                    [
                    {text: "单位", id: "organizationName", colspan: 2 },
                    {text: "地址", id: "shopAddress" },
                    {text: "电话", id: "shopPhone" },
                    {row: 2 }
                ], [
                    {text: "工单号码", id: "saleNoView", type: "text",colspan:2 },
                    {text: "车主名称", id: "nameView", type: "text",colspan:2 },
                    {text: "送修人", id: "nameView", type: "text", colspan:2 },
                    {text: "电话", id: "phoneView", type: "text", colspan:2 },
                    {text: "车牌号码", id: "plateNumberView", type: "text" },
                    {text: "车牌颜色", },
                    {text: "车架号码", },
                    {text: "发动机号码", },
                    {text: "车辆分类代号", value: "E类车",colspan:2 },
                    {text: "供给系统类型", value: "□ 汽油   □ 柴油   □ 电动   □ 混动", colspan:2 },
                    {text: "进厂日期",colspan:2},
                    {text: "进厂里程", id:"mileageView", colspan:2, type: "text"},
                    {row: 4, widthStyle: "auto", outBorderStyle: "topAndBottomHr" }
//                    {row: 3, widthStyle: "100%", outBorderStyle: "topAndBottom" }
                ], [
                    {text: "用户需求描述(含变更)"},
                    {text: "", colon: false },
                    {text: "", colon: false },
                    {text: "", colon: false },
                    {text: "", colon: false },
                    {row: 1, widthStyle: "100%", outBorderStyle: "bottom" }
                ], [
                    {text: "维修建议(含变更)" },
                    {text: "", colon: false },
                    {text: "", colon: false },
                    {text: "", colon: false },
                    {row: 1, widthStyle: "100%", outBorderStyle: "bottom" }
                ], [
                    {text: "工时费(预计)合计", id: "hoursAmountView", formatter:function(value){
                        return value + "¥";
                    }},
                    {text: "工时费=结算工时x单价", colon: false },
                    {widthStyle: "auto"}
                ], [
                    {text: "材料费(预计)", id: "materialAmountView", formatter:function(value){
                        return value + "¥";
                    }},
                    {widthStyle: "auto"}
                ], [
                    {text: "维修费用=材料费+工时费维修费用(预计)", id: "sum", type: "text", formatter:function(value){
                        return value + "¥";
                    }},
                    {text: "金额大写",id: "sum", type: "text", formatter:function(cellvalue){
                        return numberUpper(cellvalue);
                    }},
                    {text: "注:在车辆维修过程中,因车辆内在原因,需增加维修项目或扩大维修范围时,应当征得托修方", colspan:2 ,colon:false},
                    {widthStyle:"auto",row:2}
                ]
            ],
            jqGridTables: [{
                labels: ['维修项目','结算工时','单价(元/工时)','工时费(元 )','备注'],
                names: ['name','laborHours','cost','laborHours','space'],
                jqGridId: 'workHoursList',
                headText: '经双方协商，确定维修项目及所需配件如下',
                afterIndex: 3,
                outBorderStyle: "topAndBottom"
            },{
                labels: ['配件名称','配件性质','数量','单价','实际售价','折后售价'],
                names: ['orderedItem.name','rootCategory','count','price','receivable','receivablediscount'],
                jqGridId: 'customList',
                afterIndex: 4,
                outBorderStyle: "topAndBottom"
            }]
        })
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
      $("#fortst").text("指纹收集失败，请调整采集姿势!");
//      $("#fortst").text("验证失败,请重试!");
  }


</SCRIPT>

    <SCRIPT type="text/javascript" FOR="myativx" EVENT="OnCapture(ActionResult ,ATemplate)">
        checkByFinger();
        /*var tmp = matx.GetTemplateAsString();
        var Parr = [9, 0];
        ret = matx.IdentificationFromStrInFPCacheDB(fpcHandle, tmp, Parr[0], Parr[1]);

        if (ret != -1) {
            $('#dialog').dialog('close');

            $.get('/salenote/authority/check?id=' + ret.toString()
                    , function (resultMap) {
                        if (resultMap["result"]) {
                            alert("授权成功!");
                            $("[id=receivablediscount-" + selectedid.replace('.',"\\.") +"]").removeAttr("disabled");

                            var name = resultMap['erpUserRealName'] == null ? "" : resultMap['erpUserRealName'];
                            var erpUserId =  resultMap['erpUserId'];
                            $("#authdiv-" + selectedid.replace('.',"\\.")).empty();
                            $("#authdiv-" + selectedid.replace('.',"\\.")).append("<input value='" + name + "' type='text' readonly='readonly'/>" +
                                    "<input id=erpUserId-" + selectedid) + " value='" + erpUserId + "' type='hidden' />");

                        }else{
                            $("#fortst").text("");
                            alert("账号或密码错误,授权失败!")
                        }
                    }, 'json');

            success();

            matx.EndEngine();*/
    </SCRIPT>


  <div id="printDiv" >
  <legend>销售开单 -> 销售开单明细</legend>
      <#if organizationInfo?? >
        <input value="${organizationInfo!''}" type="hidden" id="organizationName" >
      </#if>
      <input  id="materialAmountView" type="hidden" >
      <input  id="hoursAmountView" type="hidden" >
      <#if shopInfo?? >
        <input value="${shopInfo.address!''}" type="hidden" id="shopAddress" >
        <input value="${shopInfo.phone!''}" type="hidden" id="shopPhone" >
      </#if>
  <form id="fm" class="" action='<@spring.url relativeUrl = "/salenote/save"/>' method="post">
  <input type="hidden" id="salenotedata" name="salenotedata"/>
  <input type="hidden" id="workhoursdata" name="workhoursdata"/>
  <input type="hidden" name="nextOrSettle" id="nextOrSettle"/>
  <spring:bind path="settleOrder">
      <div class="row">
          <div class="col-md-6">
              <div class="row">
                <div class="col-md-10">
                 <legend style="font-size: 17px;">客户信息</legend>
                    <input class="form-control" type="hidden" name="vehicleInfo.id" id="vehicleInfoId"
                           value="${settleOrder.vehicleInfo.id?c}">
                </div>
              </div>
              <div class="row">
              <spring:bind path="customerERPProfile">
                <div class="col-md-12">
                  <div class="col-md-6">
                      <label class="control-label">姓名:  </label> <span id="nameView"  ><#if customerERPProfile.realName??>${customerERPProfile.realName}</#if></span>
                  </div>
                  <div class="col-md-6">
                      <label  class="control-label">电话: </label> <span id="phoneView" ><#if customerERPProfile.customer.mobile??>${customerERPProfile.customer.mobile}</#if></span>
                      <input class="form-control" type="hidden" name="customer.mobile" id="mobile"
                             value="<#if customerERPProfile.customer.mobile??>${customerERPProfile.customer.mobile}</#if>" />
                  </div>
                </div>
              </div>
              </spring:bind>
                <@form.textInput "settleOrder.id" "" "hidden" />
                <spring:bind path="settleOrder">
              <div class="row">
                  <div class="col-md-10">
                      <legend style="font-size: 17px;">车辆信息</legend>
                  </div>
              </div>
             <div class="row">
                <div class="col-md-10">
                    <div class="col-md-6">
                    <label class="control-label">VIN号：</label> <#if settleOrder.vehicleInfo.vinCode??>${settleOrder.vehicleInfo.vinCode}</#if></div>
                        <div class="col-md-6"><label class="control-label">车牌号：</label> <span id="plateNumberView"><#if settleOrder.vehicleInfo.plateNumber??>${settleOrder.vehicleInfo.plateNumber}</#if></span>
                        </div>
                </div>
            </div>
              <div class="row" style="margin-top: 1%">
                  <div class="col-md-10">
                      <label class="control-label">品牌：</label> <#if settleOrder.vehicleInfo.model??&&settleOrder.vehicleInfo.model.version??>
                  ${settleOrder.vehicleInfo.model.brand}
                      <label class="control-label">车系：</label> ${settleOrder.vehicleInfo.model.line}
                      <label class="control-label">车型：</label> ${settleOrder.vehicleInfo.model.version}
                  ${settleOrder.vehicleInfo.model.version}</#if>
                  </div>
              </div>
              <div class="row" style="margin-top: 1%">
                  <div class="col-md-10">
                      <div class="col-md-2" style="padding-left:0px;">
                        <label class="control-label">汽车排量：</label> <#if settleOrder.vehicleInfo.engineDisplacement??>${settleOrder.vehicleInfo.engineDisplacement}</#if>
                      </div>
                  <div class="col-md-4">
                      <label class="control-label">上次保养里程：</label>
                      <input name="vehicleInfo.lastMaintenanceMileage" type="text" onblur="validate(this);" style="width:65px" class="control-text" value="<#if settleOrder.vehicleInfo.lastMaintenanceMileage??>${settleOrder.vehicleInfo.lastMaintenanceMileage?c}</#if>">
                  </div>
                  <div class="col-md-4">
                      <label  class="control-label">已行驶里程：</label><span id="mileageView"><#if settleOrder.vehicleInfo.mileage??>${settleOrder.vehicleInfo.mileage?c}</#if></span>
                  </div>
                  </div>
              </div>
                <div class="row" style="margin-top: 1%">
                    <div class="col-md-10">
                        <@form.labelAndtextArea "settleOrder.remark" "style='width:300px;'" "备注："/>
                    </div>
                </div>
        </div>
        <div class="col-md-5"><table id="workHoursList" class="scroll" cellpadding="0" cellspacing="0"  border="1px"></table></div>
  </div>
    <div class="row" style="margin-top: 1%">
      <div class="col-md-3">
          <label class="control-label">销售单号:</label> <span id="saleNoView" style="color:red;"><#if settleOrder.saleNoView??>${settleOrder.saleNoView}</#if></span>
      </div>
        <div class="col-md-3">
            <@form.btn_print "onclick='printit()'" "打印委托书（销售单）" />
        </div>
      <div class="col-md-1">
         <@form.textInput "settleOrder.saleNo" "" "hidden" />
      </div>
      <div class="col-md-1">
          <@form.textInput "settleOrder.saleNoView" "" "hidden" />
      </div>
      <div class="col-md-offset-5 col-md-2">
         &nbsp;
      </div>
  </div>

  <!-- 中间表格 -->
  <div class="row" style="margin-top: 1%"> <div class="col-md-12" style="padding-left:0px;">
      <table id="customList" class="scroll" cellpadding="0" cellspacing="0"  border="1px">
      </table>
      </div>
  </div>

  <div class="row" style="border:1px #000000 solid;padding-top: 0.5%">
      <div class="col-md-13">
      <div class="col-md-9">
          <label class="control-label">合计：</label> <label class="control-label" id="sum" name="sum"></label>
          &nbsp; &nbsp;<label class="control-label">成本合计：</label> <label class="control-label" id="costsum"></label>
          &nbsp; &nbsp; <label class="control-label">现金：</label> <input style="width:60px;" type="text" name="payment.cashAmount" id="payment.cashAmount" onblur="validate(this)" value="<#if settleOrder.payment??>${settleOrder.payment.cashAmount?c}<#else>0</#if>">
          &nbsp; &nbsp;<label class="control-label">pos机：</label> <input style="width:60px;" type="text" name="posamount" id="posamount" onblur="calculatePos(this)">
          &nbsp;&nbsp;<label class="control-label">pos实收：</label> <input style="width:60px;" type="text" name="payment.posAmount" id="payment.posAmount" value="<#if settleOrder.payment??>${settleOrder.payment.posAmount?c}<#else>0</#if>" readonly>
          &nbsp;&nbsp;<label class="control-label">App费用：</label> <input style="width:60px;"  type="text" name="payment.appAmount" id="payment.appAmount" value="<#if settleOrder.payment??>${settleOrder.payment.appAmount?c}<#else>0</#if>" readonly="readonly" />
          &nbsp;&nbsp;<label class="control-label">第三方费用：</label> <input style="width:60px;"  type="text" name="payment.otherAmount" id="payment.otherAmount" value="<#if settleOrder.payment??>${settleOrder.payment.otherAmount?c}<#else>0</#if>" />
          &nbsp;&nbsp; <label class="control-label">接车人员：</label>
          <select   type="search" id="receiver" name="receiver" >
              <#if settleOrder.receiver??>
                  <option value= "${settleOrder.receiver.id}" selected>${settleOrder.receiver.name}</option>
              </#if>
              <#list staffList as staff>
                  <#if (settleOrder.receiver.id)?? && settleOrder.receiver.id != staff.id >
                      <option value= "${staff.id}" >${staff.name}</option>
                  <#elseif !settleOrder.receiver??>
                      <option value= "${staff.id}">${staff.name}</option>
                  </#if>
              </#list>
          </select>
      </div>

          <div class=" col-md-3">
              <@form.btn_save "id='settleSave' onclick=\"getOrderGridInfo('1');\"" "保存(挂单)" />
              <@form.btn_pages "id='settle' onclick=\"getOrderGridInfo('0');\"" "结 算" />
               <@form.btn_back "onclick='returnURL()'" "返 回" />
          </div>

          <#--<div class=" col-md-1">-->
              <#--<button class="btn btn-primary active" onclick="">结 算</button>-->
          <#--</div>-->
          <#--<div class=" col-md-1">-->
              <#--<button class="btn btn-primary active" onclick="deleteRows()">删 除</button>-->
          <#--</div>-->
      </div>

  </div>


  <div class="row" style="margin-top: 2%">
    <div class=" col-md-12">
        <div clas="row">
            <div class="col-md-4">
                <label class="control-label">商品关键字：</label>
                <input style="width:150px;" type="text" name="itemName" id="itemName">
                <label class="control-label">分类：</label>
                <@form.topCategory "topCategory" "style='width:180px;'" />
            </div>
            <div class="col-md-3">
                <@form.btn_search "onclick='reloadGrid()'" "搜 索" />&nbsp;
                <#--<@form.btn_add "onclick='addShop()'" "新增产品信息" />-->
            </div>
            <div class="col-md-5">
                <label class="control-label">作业项目：</label>
                <input style="width:120px;" type="text" name="name" id="name">
                &nbsp;  <label  class="control-label">分类: </label>&nbsp;
                <select name="operationType" id="operationType"  style="120px;">
                    <option value="0">请选择</option>
                    <option value="1">维护</option>
                    <option value="2">大修和全车喷漆</option>
                    <option value="3">发动机机械</option>
                    <option value="4">发动机电气</option>
                    <option value="5">变速箱</option>
                    <option value="6">转向系统</option>
                    <option value="7">悬挂系统</option>
                    <option value="8">驱动桥</option>
                    <option value="9">制动系统</option>
                    <option value="10">电气</option>
                    <option value="11">空调</option>
                    <option value="12">钣金</option>
                    <option value="13">喷漆</option>
                </select>
                <input type="hidden" name="engineDisplacement" id="engineDisplacement" value="${engineDisplacement}" />
                &nbsp;
                <@form.btn_search "onclick='reloadProjectGrid()'" "搜 索" />
            </div>
        </div>
        <div class="row"  style="margin-top: 3%">
            <div class="col-md-6">
                <table id="itemlist" class="scroll" cellpadding="0" cellspacing="0"></table>
                 <div id="toolBar"></div>
            </div>
            <div class="col-md-4 col-md-offset-1" >
                <table id="projectList" class="scroll" cellpadding="0" cellspacing="0"></table>
                    <div id="projectBar"></div>
                </div>
            </div>
        </div>
    </div>
  </div>
    </form>

  <#--title="请输入授权账户密码"-->
  <div id="dialog" class="dialog" title="验证申请授权"
       style="text-align: center;padding-top: 24px">
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