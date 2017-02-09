<#import "../macros/mainFrame.ftl" as main />
<#import "../macros/formMacros.ftl" as form />
<#import "/spring.ftl" as spring />


<#escape x as x?html>
  <@main.frame>
<script>
    $('#collapsePayment').collapse('show');
    $(function () {
        showCusInfo();
        showWorkHours();
        showProject();

        <#if settleOrder.operationItemDetails?? >
            <#list settleOrder.operationItemDetails as operationItemDetails >
                var operationItemId = "${operationItemDetails.id?c}";
                var hourRow = $("#workHoursList").getGridParam("reccount") + 1;

                $("#workHoursList").addRowData(hourRow,
                        {"id":${operationItemDetails.operationItem.id?c},
                            "name":"${operationItemDetails.operationItem.name}",
                            "laborHours":${operationItemDetails.operationItem.laborHours},
                            "sum":${operationItemDetails.sum?c}
                        },"last");

            </#list>
        </#if>

        getAmount();
        <#if settleOrder.payment??>calculatePosData(${settleOrder.payment.posAmount?c})</#if>
    });

    //获取成本总和
    function getCostSum(){
        var costSim=0;
        var obj = $("#customCard").jqGrid("getRowData");

        jQuery(obj).each(function(){
//            231
//            var number = $("#count-" + $("#customCard").jqGrid('getCell',id,0)).val();
//            rowObject['suiteItem']['skuItem']['id']
            var id = this['customStockItem.id'];
            costSim += parseInt(this["cost"]) * $("#count-" + id + '-' + this['cost'].replace('.',"\\.")).val();
        });
        $("#costsum").html(costSim);
    }

    //获取合计
    function getAmount(){
        var amount=0;

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

    //点击结算，获取orderdetails，传到后台
    function getOrderGridInfo(index) {

        var obj = $("#customCard").jqGrid("getRowData");

        var saveFlag = true;

        var lines = $("#workHoursList").getGridParam("reccount");        //检测是否选择作业项目
        if(lines <= 0) {
            alert("请选择至少一个作业项目。");
            return;
        }

        var cash = document.getElementById("payment.cashAmount").value;
        var post = document.getElementById("posamount").value;
        var app = document.getElementById("payment.appAmount").value;
        if(parseFloat(cash) + parseFloat(post) + parseFloat(app) != parseFloat($("#sum").html())) {
            alert("输入金额总和与合计不符，请确认。");
            return;
        }

        jQuery(obj).each(function(){
            var reg = new RegExp("^[0-9]+([.]{1}[0-9]+){0,1}$");
            var number = $("#count-" + this['customStockItem.id'] + "-"  + this['cost'].replace('.',"\\.")).val();
            if(!reg.test(number)){
                alert("请输入数字!");
                $("#count-" + this['customStockItem.id'] + "-"  + this['cost'].replace('.',"\\.")).focus();
                saveFlag = false;
                return false;
            }
            if(parseFloat(number) > parseFloat(this['timesLeft'])) { //数量小于剩余次数
                alert("不能超过商品剩余次数，请确认。");
                $("#count-" + this['customStockItem.id'] + "-" + this['cost'].replace('.',"\\.")).focus();
                saveFlag = false;
                return false;
            }

            //施工人员
            var receiver = $("#receiver-" +  this['customStockItem.id'] + '-' + this['cost'].replace('.',"\\.")).val();
            if (receiver == null) {
                alert("没有施工人员，请先考勤。");
                return false;
            }
        });

        if(saveFlag) {
            var oneData ="";
            var obj = $("#customCard").jqGrid("getRowData");

            jQuery(obj).each(function(){
                oneData += "" + this['customStockItem.id'] + "," + $("#count-"+ this['customStockItem.id'] + "-" + this['cost'].replace('.',"\\.")).val()+ "," + $("#receiver-" + this['customStockItem.id'] + '-' + this['cost'].replace('.',"\\.")).val()+ "," + this["cost"]+ "," + this["id"];
                oneData += ";";
            });

            $("#salenotedata").val(oneData);

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
    }

    //改变现金和post机金额
    function validate(obj){
        var reg = new RegExp("^[0-9]+([.]{1}[0-9]+){0,1}$");
        if(!reg.test(obj.value)){
            alert("请输入数字!");
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
        var posData = x1<x2?x1:x2;
        $("#posamount").val(posData.toFixed(2));
    }

    //订单详情
    function showCusInfo() {

        var customSuiteId = ${customSuiteId?c};

        $("#customCard").jqGrid({
            postData: { customSuiteId:customSuiteId },
            url: '/salenote/showcardsuite',
            rowNum :40,


        colModel: [
                { label: 'ID', name: 'customStockItem.id', hidden:true},
                { label: 'ID2', name: 'id', width:10,hidden:true},
                {  name: 'zeroView', hidden:true, formatter:function(){
                    return 0;
                }},
                { label: '品名', name: 'customStockItem.name',align:"center", width: 75 },
                { label: '分类', name: 'customStockItem.rootCategory', hidden:true },
                { label: '成本价(元)',name: 'cost', align:"center", width:30 },
                { label: '总次数', name: 'times', width: 30 , align:"center",
                    formatter:function(cellvalue, options, rowObject) {
                        if (cellvalue == -1) {
                            return "无限次";
                        }
                        return cellvalue;
                    }
                },
                { label: '剩余次数', name: 'timesLeft', width: 30 , align:"center",
                    formatter:function(cellvalue, options, rowObject) {
                        if (rowObject['times'] == -1) {
                            return "无限次";
                        }
                        return cellvalue;
                    }
                },
                { label: '数量', name: 'count', align:"center" ,width: 30,
                    formatter:function(cellvalue, options, rowObject) {
                        if(cellvalue != null ) {
                            return  '<input type="currency" id="count-'+ rowObject[0] + '-' + rowObject[5] + '"  value="' + cellvalue +'" ' +
                                    'class="content" ' +
                                    'size="13" maxlength="10" style="text-align:center;" onblur="changeCount(' +options.rowId + ');getCostSum()"  />' +
                                    '<input type="hidden" id="hid_count-'+ rowObject[0] + '-' + rowObject[5] + '" value="'+ cellvalue +'" />';

                        }

                            return  '<input type="currency" id="count-'+ rowObject['customStockItem']['id'] + '-' + rowObject['cost'] + '"  value="0" ' +
                                    'class="content" ' +
                                    'size="13" maxlength="10" style="text-align:center;" onblur="changeCount(' +options.rowId + ');getCostSum()"  />' +
                                    '<input type="hidden" id="hid_count-'+ rowObject[0] + '-' + rowObject[5] + '" value="'+ cellvalue +'" />';
                    }
                },
                { label: '库存数量', name: 'stockItemNumber', width: 30 , align:"center" },
                { label: '施工人员', name: 'worker', width: 30,
                    <#--formatter:function(cellvalue, options, rowObject) {-->

                        <#--sssads-->
                        <#--return  '<select class="form-control" type="search" id="receiver-'+ rowObject'customStockItem']['id'] + '"  >' +-->
                                    <#--<#list staffList as staff>-->
                                    <#--'<option value= "${staff.id}">${staff.name}</option>' +-->
                                    <#--</#list>-->
                                    <#--'</select>';-->
                    <#--}-->
                    formatter:function(cellvalue, options, rowObject) {
                        var str="";
                        var staffid="";

                        if(cellvalue != null && cellvalue.indexOf('-') > -1) {
                            var staff=cellvalue.split("-");
                            staffid = staff[0];
                            str = "<option value=\"" + staff[0] +"\" selected>" + staff[1] + "</option>";
                        }

                        var optionStr = "";
                        <#list staffList as staff>
                            if (staffid != ${staff.id}) {
                                optionStr += '<option value= "${staff.id}">${staff.name}</option>' ;
                            };
                        </#list>
                        return  '<select class="form-control" type="search" id="receiver-'+ rowObject['customStockItem']['id'] + '-' + rowObject['cost'] +'" >' + str +
                                optionStr + '</select>';
                    }
                },
                { label: '会员卡项目', name: 'suiteName', width: 80, align:"center",
                    formatter:function() {
                        return "${customerPurchasedSuite.suite.name}";
                    }
                }
            ],
            loadComplete:function() {
                getCostSum();
                //会员卡商品明细
                <#if settleOrder.orderDetails?? >
                    <#list settleOrder.orderDetails as orderDetails >
                        var obj = $("#customCard").jqGrid("getRowData");
                        jQuery(obj).each(function() {
                            if (${orderDetails.orderedItem.id?c} == this["customStockItem.id"]  && ${orderDetails.cost?c} == this["cost"]) {
                                $("#customCard").jqGrid('setCell', this["id"], 'count', ${orderDetails.count?c});
                            }
                        });

                        <#--$("#receiver-" + ${orderDetails.orderedItem.id}).each(function(index,element) {-->
                           <#---->
                        $("#receiver-" + ${orderDetails.orderedItem.id} + '-' + (${orderDetails.cost}+"").replace('.',"\\.") ).empty();
                        $("#receiver-" + ${orderDetails.orderedItem.id} + '-' + (${orderDetails.cost}+"").replace('.',"\\.")).append( '<option value= "${orderDetails.merchandier.id}" selected>${orderDetails.merchandier.name}</option>');
                        <#list staffList as staff>
                            <#if (orderDetails.merchandier.id)?? && orderDetails.merchandier.id != staff.id >
                            $("#receiver-" + ${orderDetails.orderedItem.id} + '-' + (${orderDetails.cost}+"").replace('.',"\\.")).append( '<option value= "${staff.id}" >${staff.name}</option>');
                            </#if>
                        </#list>

                    </#list>
                </#if>
            }
        });
    };

    //返回按钮
    function returnURL() {
        window.location = "/salenote/searchcustominfo";
    }

    function changeCount(id) {
        var rowdata = $("#customCard").jqGrid("getRowData",id);

        var reg = new RegExp("^[0-9]+([.]{1}[0-9]+){0,1}$");
        var number = $("#count-" + $("#customCard").jqGrid('getCell',id,0) + "-" + $("#customCard").jqGrid('getCell',id,5).replace('.',"\\.")).val();
        var oldNumber = $("#hid_count-" + $("#customCard").jqGrid('getCell',id,0) + "-" + $("#customCard").jqGrid('getCell',id,5).replace('.',"\\.")).val();
        if(!reg.test(number)){
            alert("请输入正整数!");
            $("#count-" + $("#customCard").jqGrid('getCell',id,0) + "-" + $("#customCard").jqGrid('getCell',id,5).replace('.',"\\.")).focus();
            return;
        }
        if (oldNumber != number) {
            if (parseFloat(number) != 0){
                if(parseFloat(number) > parseFloat(rowdata['timesLeft'])) { //数量大于库存
                    alert("不能超过商品剩余次数，请确认。");
                    $("#count-" + $("#customCard").jqGrid('getCell',id,0) + "-" + $("#customCard").jqGrid('getCell',id,5).replace('.',"\\.")).focus();
                    return;
                }
                if(parseFloat(number) > parseFloat(rowdata['stockItemNumber'])) { //数量大于库存
                    alert("商品数量大于库存数量，请确认及时采购。");
                    $("#count-" + $("#customCard").jqGrid('getCell',id,0) + "-" + $("#customCard").jqGrid('getCell',id,5).replace('.',"\\.")).focus();
                    return;
                }
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
                                'size="13" maxlength="20" onblur="changeHourCost('+ options.rowId +')" />';
                    }
                },
                { label: '操作', name: 'del', width: 30, align:"center",
                    formatter:function(cellvalue, options, rowObject) {
                        return "<a onclick='deleteWorkListRow(" + options.rowId +");' href='####' style='text-decoration:underline;color:blue'>" + "删除" + "</a>";
                    }
                }
            ],
            toolbar: [false,"both"]
        });
    };

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

    function deleteWorkListRow(id) {
        var rowData = $("#workHoursList").jqGrid('getRowData',id);
        $("#workHoursList").jqGrid("delRowData", id);
        //获取合计总和
        getAmount();
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
                { name: 'space', hidden:true, formatter:function(){
                    return "";
                }},
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

    /**
     * 打印委托书
     */
    function printit() {

        //workHoursList 作业项目 工时 表
        //customList
        $("#printDiv").jqPrintDataWithoutBorder({
//            headHtml:"车辆维修委托书",//上面这种跟下面这种都可以
            headHtml:"<legend style='font-size: x-large' align='center' >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;车辆维修委托书</legend>",
            arr: [
                [
                    {text: "单位", id: "organizationName", colspan: 2 },
                    {text: "地址", id: "shopAddress" },
                    {text: "电话", id: "shopPhone" },
                    {row: 2 }
                ], [
                    {text: "工单号码", id: "saleNoView", type: "text" },
                    {text: "车主名称", id: "nameView", type: "text" },
                    {text: "送修人", id: "nameView", type: "text", colspan:2 },
                    {text: "电话", id: "phoneView", type: "text", colspan:4 },
                    {text: "车牌号码", id: "plateNumberView", type: "text" },
                    {text: "车牌颜色", },
                    {text: "车架号码", },
                    {text: "发动机号码",value:"          " },
                    {text: "车辆分类代号", value: "E类车", colspan:2 },
                    {text: "供给系统类型", value: "□ 汽油   □ 柴油   □ 电动   □ 混动", colspan:2 },
                    {text: "进厂日期",colspan:2},
                    {text: "进厂里程", id:"mileageView", colspan:2, type: "text"},
                    {row: 4, widthStyle: "90%", outBorderStyle: "topAndBottom" }
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
                        return value + "0¥";
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
                labels: ['配件名称','配件性质','数量','单价','金额'],
                names: ['customStockItem.name','customStockItem.rootCategory','count','zeroView','zeroView'],
                jqGridId: 'customCard',
//                headText: '商品列表',
                afterIndex: 4,
                outBorderStyle: "topAndBottom"
            }]
        })
    }
</script>
  <div id="printDiv" >
  <legend>销售开单 -> 会员卡开单明细</legend>

  <#if organizationInfo?? >
      <input value="${organizationInfo!''}" type="hidden" id="organizationName" >
  </#if>
  <input  id="materialAmountView" type="hidden" >
  <input  id="hoursAmountView" type="hidden" >
  <#if shopInfo?? >
      <input value="${shopInfo.address!''}" type="hidden" id="shopAddress" >
      <input value="${shopInfo.phone!''}" type="hidden" id="shopPhone" >
  </#if>

  <form id="fm" class="" action='<@spring.url relativeUrl = "/salenote/customcard/save"/>' method="post">


  <input type="hidden" id="salenotedata" name="salenotedata"/>
  <input type="hidden" id="workhoursdata" name="workhoursdata"/>
  <input type="hidden" name="nextOrSettle" id="nextOrSettle"/>
  <spring:bind path="settleOrder">
      <div class="row">
          <div class="col-md-6">
    <div class="row">
        <div class="col-md-11">
            <legend style="font-size: 17px;">客户信息</legend>
            <input class="form-control" type="hidden" name="vehicleInfo.id" id="vehicleInfoId" value="${settleOrder.vehicleInfo.id?c}">
        </div>
    </div>
    <div class="row">
        <spring:bind path="customerERPProfile">
            <div class="col-md-11">
              <div class="col-md-8">
                  <label class="control-label">姓名：</label> <span id="nameView"  ><#if customerERPProfile.realName??>${customerERPProfile.realName}</#if></span>
              </div>
              <div class="col-md-8">
                  <label class="control-label">电话:</label><span id="phoneView" > <#if customerERPProfile.customer.mobile??>${customerERPProfile.customer.mobile}</#if></span>
                  <input class="form-control" type="hidden" name="customer.mobile" id="mobile"
                         value="<#if customerERPProfile.customer.mobile??>${customerERPProfile.customer.mobile}</#if>" />
              </div>
            </div>
        </div>
        </spring:bind>
            <@form.textInput "settleOrder.id" "" "hidden" />
        <spring:bind path="settleOrder">
        <div class="row" style="margin-top: 10px;">
          <div class="col-md-11">
              <legend style="font-size: 17px;">车辆信息</legend>
          </div>
        </div>
        <div class="row">
          <div class="col-md-11">
            <div class="col-md-5">
                <label class="control-label">VIN号：</label> <#if settleOrder.vehicleInfo.vinCode??>${settleOrder.vehicleInfo.vinCode}</#if>
            </div>
            <div class="col-md-4">
                <label class="control-label">车牌号：</label> <span id="plateNumberView"><#if settleOrder.vehicleInfo.plateNumber??>${settleOrder.vehicleInfo.plateNumber}</#if></span>
            </div>
            <div class="col-md-3">
              <label class="control-label">汽车排量：</label> <#if settleOrder.vehicleInfo.engineDisplacement??>${settleOrder.vehicleInfo.engineDisplacement}</#if>
            </div>
          </div>
        </div>
        <div class="row">
        <div class="col-md-11">
            <div class="col-md-10">
                <label class="control-label">品牌：</label> ${settleOrder.vehicleInfo.model.brand}
                <label class="control-label" style="margin-left: 10px;">车型：</label> ${settleOrder.vehicleInfo.model.version}
                <label class="control-label"style="margin-left: 10px;">车系：</label> ${settleOrder.vehicleInfo.model.line}
            </div>
        </div>
        </div>
        <div class="row" style="margin-top: 1%">
          <div class="col-md-11">
              <div class="col-md-4">
                  <label class="control-label">上次保养里程：</label>
                  <input name="vehicleInfo.lastMaintenanceMileage" type="text" onblur="validate(this);" style="width:65px" class="control-text" value="<#if settleOrder.vehicleInfo.lastMaintenanceMileage??>${settleOrder.vehicleInfo.lastMaintenanceMileage?c}</#if>">
                  <#--<#if settleOrder.vehicleInfo.lastMaintenanceMileage??>${settleOrder.vehicleInfo.lastMaintenanceMileage}</#if>-->
              </div>
              <div class="col-md-4">
                  <label class="control-label">已行驶里程：</label> <span id="mileageView"><#if settleOrder.vehicleInfo.mileage??>${settleOrder.vehicleInfo.mileage?c}</#if></span>
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
          <div class="col-md-1">
              <label class="control-label">销售单号</label>
          </div>
          <div class=" col-md-2">
              <p id="saleNoView" style="color:red;"><#if settleOrder.saleNoView??>${settleOrder.saleNoView}</#if></label></p>

          </div>

          <div class="col-md-1">
              <@form.textInput "settleOrder.saleNo" "" "hidden" />
          </div>
          <div class=" col-md-2">
              <@form.textInput "settleOrder.saleNoView" "" "hidden" />
          </div>
          <div class="col-md-3">
              <@form.btn_print "onclick='printit()'" "打印委托书（销售单）" />
          </div>
          <div class="col-md-2">

          </div>
        </div>

  <!-- 中间表格 -->
  <div class="row"  style="margin-top: 1%">
      <table id="customCard" class="scroll" cellpadding="0" cellspacing="0"  border="1px">
      </table>
  </div>

  <div class="row" style="border:1px #000000 solid;">
      <div class="col-md-13">
          <div class=" col-md-2"  style="padding-top: 8px;">
              <label class="control-label">合计：</label> <label class="control-label" id="sum" name="sum"></label>
              &nbsp; &nbsp;<label class="control-label">成本合计：</label> <label class="control-label" id="costsum"></label>
          </div>
          <div class="col-md-7"  style="padding-top: 8px;">
              <label class="control-label">现金：</label> <input style="width:80px;" type="text" name="payment.cashAmount" id="payment.cashAmount" onblur="validate(this)" value="<#if settleOrder.payment??>${settleOrder.payment.cashAmount?c}</#if>">
              &nbsp; &nbsp;<label class="control-label">pos机：</label> <input style="width:80px;" type="text" name="posamount" id="posamount" onblur="calculatePos(this)">
              &nbsp;&nbsp;<label class="control-label">pos实收：</label> <input style="width:80px;" type="text" name="payment.posAmount" id="payment.posAmount" value="<#if settleOrder.payment??>${settleOrder.payment.posAmount?c}<#else>0</#if>" readonly>
              &nbsp;&nbsp;<label class="control-label">App费用：</label> <input style="width:80px;"  type="text" name="payment.appAmount" id="payment.appAmount" value="<#if settleOrder.payment??>${settleOrder.payment.appAmount?c}<#else>0</#if>" readonly="readonly" />
              &nbsp; <label class="control-label">接车人员：</label>
              <select   type="search" id="receiver" name="receiver" >
                  <#if settleOrder.receiver??>
                      <option value= "${settleOrder.receiver.id?c}" selected>${settleOrder.receiver.name}</option>
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
              <@form.btn_pages "id='settle' onclick=\"getOrderGridInfo('0');\"" "结 算" />&nbsp;
              <@form.btn_back "onclick='returnURL()'" "返 回" />
          </div>

  </spring:bind>

      </div>
  </div>
      <div class="row" style="margin-top: 2%">
          <div class=" col-md-12">
              <div clas="row">
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
                  <div class="col-md-4" >
                      <table id="projectList" class="scroll" cellpadding="0" cellspacing="0"></table>
                      <div id="projectBar"></div>
                  </div>
              </div>
          </div>
      </div>
      <input class="form-control" type="hidden" name="customSuiteId" id="customSuiteId" value="${customSuiteId?c}">
    </form>
  </div>

  </@main.frame>
</#escape>