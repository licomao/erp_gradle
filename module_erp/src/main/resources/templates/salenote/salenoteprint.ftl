<#import "../macros/mainFrame.ftl" as main />
<#import "../macros/formMacros.ftl" as form />
<#import "/spring.ftl" as spring />


<#escape x as x?html>
  <@main.frame>
  <style type="text/css">#dialog {
      display: none;
  }</style>
<script>
    $('#collapsePayment').collapse('show');
    $(function () {
//        alert(formatterDateWithSecond())
        $("#updatedDate").val((formatterDateWithSecond($("#updatedDate").val())));
        showCusInfo();
        showWorkHours();

//    alert(document.getElementById("realName"))
        //商品明细
        <#if settleOrder.orderDetails?? >
            <#list settleOrder.orderDetails as orderDetails >
                var row = $("#customList").getGridParam("reccount") + 1;
                $("#customList").addRowData(row,
                        {"id":${orderDetails.id?c},
                            "orderedItem.name":"${orderDetails.orderedItem.name}",
                            "cost":${orderDetails.cost?c},
                            "additionRate":<#if orderDetails.orderedItem.secondaryCategory??>${orderDetails.orderedItem.secondaryCategory.additionRate?c}<#else>0</#if>,
                            "count":${orderDetails.count?c},
                            "receivable":${orderDetails.receivable?c},
                            <#if orderDetails.discountGranter??>
                                "receivablediscount":${orderDetails.discountPrice?c},
                                <#--"receivablediscount":${orderDetails.receivable?c} * ${orderDetails.discount?c},-->
                                "discountGranter":"${orderDetails.discountGranter.realName}",
                                "discount":"${orderDetails.discount?c}",
                            <#else>
                            "receivablediscount":"",
                            "discount":"",
                            </#if>
                            "merchandiername":"${orderDetails.merchandier.name}"
                        },
                        "last");
            </#list>
        </#if>

        //工时明细
        <#if settleOrder.operationItemDetails??>
            <#list settleOrder.operationItemDetails as operationItemDetail>
                var row = $("#customList").getGridParam("reccount") + 1;
                $("#workHoursList").addRowData(row,
                        {   "id":"${operationItemDetail.id}",
                            "name": "${operationItemDetail.operationItem.name}",
                            "laborHours":"${operationItemDetail.operationItem.laborHours}",
                            "sum":"${operationItemDetail.sum}".replace(",","")
                        },
                        "last");
            </#list>
        </#if>

        getCostSum();getAmount();

        <#if settleOrder.payment??>
            <#--calculatePosData(${settleOrder.payment.posAmount?c});-->
        </#if>

    });

    //获取成本总和
    function getCostSum(){
        var costSim=0;
        var obj = $("#customList").jqGrid("getRowData");
        jQuery(obj).each(function() {
            costSim += parseFloat(this["cost"]) * parseFloat(this["count"]);
        });
        $("#costsum").html(costSim.toFixed(2));
    }

    //获取合计
    function getAmount(){
        var amount = 0;
        var obj = $("#customList").jqGrid("getRowData");
        jQuery(obj).each(function() {
            var id = this["id"];
            var receivablediscount = this["receivablediscount"];//折后售价
            var receivable = this["receivable"];//实际售价
            if(receivablediscount==null || receivablediscount=="" ) {
                amount +=parseFloat(receivable);
            }else {
                amount +=parseFloat(receivablediscount);
            }
        });

        $("#sum").html(amount.toFixed(2));
        $("#shopSum").val(amount.toFixed(2));
        <#--<#if settleOrder.payment?? && settleOrder.payment.amount gt 0>-->
            <#--$("#sum").html(${settleOrder.payment.amount?c});-->
        <#--</#if>-->

        var hourAmount=0;
        var obj = $("#workHoursList").jqGrid("getRowData");
        jQuery(obj).each(function(){
            var sum = this["sum"];//总工时费用
            hourAmount +=parseInt(sum);
        });
        if(isNaN(hourAmount)){
            hourAmount = 0;
        }
        $("#hourssum").html(hourAmount.toFixed(2));
        $("#hourSum").val(hourAmount);
        $("#allSum").val((amount + hourAmount).toFixed(2));
    }

    //订单详情
    function showCusInfo() {
        $("#customList").jqGrid({
//            url: '/salenote/showOrderInfo',
            pager : '#gridpager',
            colModel: [
                { label: 'ID', name: 'id', hidden:true},
                { name: 'space', hidden:true},
                { label: '品名', name: 'orderedItem.name', width: 100,align:'center'},
                { label: '成本价', name: 'cost', width: 20, align:'center' },
                { label: '加成率', name: 'additionRate',width: 20,align:'center',
                    formatter:function(cellvalue, options, rowObject) {
                        return (cellvalue).toFixed(2) + "%";
                    }
                },
                { label: '单价', name: 'price', width: 20,editable:true,align:'center',
                    formatter:function(cellvalue, options, rowObject) {
                        return (rowObject['cost'] * (1 + parseFloat(rowObject['additionRate'])/100)).toFixed(2);
                }},
                { label: '分类', name: 'rootCategory', hidden:true },
                { label: '数量', name: 'count', width: 20,align:'center' },
                { label: '实际售价', name: 'receivable', width: 30,align:'center' },
                { label: '折后售价', name: 'receivablediscount',align:'center', width: 30 ,
                    formatter:function(cellvalue, options, rowObject) {
                        if(cellvalue != null && cellvalue !="") {
                            return parseFloat(cellvalue).toFixed(2);
                        }
                        return cellvalue;
                }},
                { label: '折扣率', name: 'discount', width: 30,align:'center'},
                { label: '授权人', name: 'discountGranter', width: 40,align:'center' },
                { label: '施工人员', name: 'merchandiername', width: 40,align:'center' },
                { label: '售价', name: 'forPrint', width: 80 , hidden:true,
                    formatter:function(cellvalue, options, rowObject) {
                        var value= rowObject['receivablediscount'];
                        if (value != null && value !="") {
                            return parseFloat(value).toFixed(2);
                        } else {
                            return parseFloat(rowObject['receivable']).toFixed(2);
                        }
                    }}
//                ,
//                { label: '会员卡项目', name: 'purchased_care_suite_id', width: 80}
            ]
        });
    };

    //工时订单详情
    function showWorkHours() {
        $("#workHoursList").jqGrid({
            colModel: [
                { label: 'ID', name: 'id', hidden:true},
                { name: 'space', hidden:true},
                { label: '作业项目', name: 'name', width: 100,align :'center'},
                { label: '工时', name: 'laborHours', width: 30,align :'center'},
                { label: '单价', name: 'cost', width: 30,align :'center',
                    formatter:function(id) {
                        <#if opreationPrice??>
                            return ${opreationPrice};
                        <#else>
                            return "";
                        </#if>
                    }
                },
                { label: '总工时费', name: 'sum', width: 40,align :'center',
                    formatter:function(cellvalue) {
                        return cellvalue;
                    }
                }
            ],
        });
    };

    function printit() {

        //workHoursList 作业项目 工时 表
        $("#printDiv").jqPrintDataWithoutBorder({
            headHtml:"<legend style='font-size: x-large' align='center' >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;机动车维修结算清单</legend>",
            arr: [
                [
                    {text: "承修单位", id: "shopInfoName", colspan:3},
                    {text: "地址", id: "shopInfoAddress" },
                    {text: "电话", id: "shopInfoPhone" },
                    {text: "传真"},
                    {text: "开户银行"},
                    {text: "E-mail", colspan:2},
                    {text: "账号"},
                    {text: "网址", colspan:2},
                    {widthStyle: "90%", row: 3, outBorderStyle: "bottomHr"}
                ],
                [
                    {text: "车主名称", id: "realName"},
                    {text: "车辆类型", id: "carType"},
                    {text: "车辆分类代号", value: "E类"},
                    {text: "送修人", id: "realName"},
                    {text: "施工编号", id: "saleNoView", type: "text"},
                    {text: "进厂日期"},
                    {text: "出厂里程表示值", id: "mileage"},
                    {text: "工时定额"},
                    {text: "车牌号码",id:"carNum"},
                    {text: "结算日期",id:"updatedDate"},
                    {text: "发票类型"},
                    {text: "执行标准"},
                    {text: "合同编号"},
                    {text: "合格证号"},
                    {text: "发票号码"},
                    {text: "出厂日期"},
                    {row:4, widthStyle: "100%", outBorderStyle: "bottomHr"}
                ],[
                    {text: "序号", colon: false},
                    {text: "名称", colon: false},
                    {text: "金额", colon: false},
                    {text: "备注", colon: false},
                    {text: "1", colon: false},
                    {text: "材料费",colon: false,colspan: 3,id:"shopSum" },
                    {text: "2", colon: false},
                    {text: "工时费", colon: false, colspan: 3,id:"hourSum" },
                    {text: "3", colon: false},
                    {text: "外加工费", colon: false, colspan: 3},
                    {text: "4", colon: false},
                    {text: "施救服务费", colon: false, colspan: 3},
                    {text: "5", colon: false},
                    {text: "车辆牵引费", colon: false, colspan: 3},
                    {text: "6", colon: false},
                    {text: "其他", colon: false, colspan: 3},
                    {text: "7", colon: false},
                    {text: "合计", colon: false, colspan: 3,id:"allSum" },
                    {text: "8", colon: false},
                    {text: "销售收入", colon: false},
                    {text: "税前销售额", colon: false},
                    {text: "销项税额", colon: false},
                    {row: 4, widthStyle: "100%", outBorderStyle: "topAndBottomHr", headText: "<span >1 收费结算   单位:元</span>"}
                ],[
                    {text: "合计", id: "hourssum", type: "text"},
                    {outBorderStyle: "bottomHr", widthStyle: "auto"}
                ],[{text: "合计", id: "sum", type: "text"},{outBorderStyle: "bottomHr", widthStyle: "auto"}],
                [
                    {text: "该车按双方约定进行维修并经检验合格。维修竣工车辆实行质量保证，保证期为车辆行驶公里", colon: false},
                    {text: "或日。质量保证期中行驶里程和日期指标以先达到者为准。保证期从维修竣工出厂之日起计算。", colon: false},
                    {text: "因维修质量原因造成机动车无法正常使用，由本厂负责无偿返修，在原维修范围内修竣，交托修方。", colon: false},
                    {row:1,widthStyle:"100%",  outBorderStyle: "topAndBottomHr", headText: "4    质量保证"}
                ],[
                    {text: "5   旧件已确认, □ 由用户收回  □ 用户声明放弃", colon: false},
                    {outBorderStyle: "bottomHr"}
                ],[
                    {text: "质量检验员"},
                    {text: "结算员签名"},
                    {text: "客户签名"},
                    {text: "日期:&nbsp;&nbsp;&nbsp;年&nbsp;&nbsp;&nbsp;月&nbsp;&nbsp;&nbsp;日", colon:false}
                ]
            ],
            jqGridTables: [{
                labels: ['维修项目','结算工时','单价(元/工时)','金额(元)','备注'],
                names: ['name','laborHours','cost','sum','space'],
                jqGridId: 'workHoursList',
                headText: '2  工时费',
                outBorderStyle: "topAndBottom",
                indexHead: "序号",
                afterIndex: 2
            },{
                labels: ['配件类型','配件项目','单位','数量','单价(元)','金额(元)'],
                names: ['rootCategory','orderedItem.name','rootCategory','count','forPrint',"space"],
                jqGridId: 'customList',
                headText: '3  材料清单',
                outBorderStyle: "topAndBottom",
                indexHead: "序号",
                afterIndex: 3
            }]
        })
    }

    //根据pos费率计算原来pos价格
    function calculatePosData(value) {
        var x1 = value + parseFloat(${baseSet.posTopRate});
        var x2 = value/(1 - parseFloat(${baseSet.posRate}/100));
        if(parseFloat(${baseSet.posTopRate}) ==0) {
            x1 = x2
        }
        var posData = x1<x2?x1:x2;
        document.getElementById("payment.posAmount").value = posData.toFixed(2);
    }

</script>
  <div id="printDiv" >
  <legend>销售开单 -> 销售开单打印</legend>
  <form id="fm" class="" action='<@spring.url relativeUrl = "/salenote/save"/>' method="post">
  <input type="hidden" id="salenotedata" name="salenotedata"/>
  <input type="hidden" name="nextOrSettle" id="nextOrSettle"/>
  <input value="${shopInfo.name!""}" type="hidden" id="shopInfoName" >
  <input value="${shopInfo.address!""}" type="hidden" id="shopInfoAddress" >
  <input value="${shopInfo.phone!""}" type="hidden" id="shopInfoPhone" >
  <input value="${settleOrder.updatedDate!""}" type="hidden" id="updatedDate" >
      <input value="" type="hidden" id="shopSum" >
      <input value="" type="hidden" id="hourSum" >
      <input value="" type="hidden" id="allSum" >
  <spring:bind path="settleOrder">
  <div class="row">
    <div class="col-md-10">
     <legend style="font-size: 17px;">客户信息</legend>
        <input class="form-control" type="hidden" name="vehicleInfo.id" id="vehicleInfoId"
               value="${settleOrder.vehicleInfo.id}">
    </div>
  </div>
  <div class="row">

  <spring:bind path="customerERPProfile">
    <div class="col-md-12">
      <div class="col-md-1">
          <label class="control-label">姓名:</label>
      </div>
      <div class="col-md-1">
          <input class="form-control" type="text" name="realName" id="realName"
                 value="<#if customerERPProfile.realName??>${customerERPProfile.realName}</#if>" required  readonly="readonly" />
      </div>
      <div class="col-md-1">
          <label class="control-label">电话:</label>
      </div>
      <div class="col-md-1">
          <input class="form-control" type="text" name="customer.mobile" id="mobile"
                 value="<#if customerERPProfile.customer.mobile??>${customerERPProfile.customer.mobile}</#if>" required readonly="readonly" />
      </div>
    </div>
  </div>
  </spring:bind>

  <spring:bind path="settleOrder">
  <div class="row">
      <div class="col-md-10">
          <legend style="font-size: 17px;">车辆信息</legend>
      </div>
  </div>
  <div class="row">
      <div class="col-md-12">
        <div class="col-md-1">
            <label class="control-label">VIN号：</label>
        </div>
        <div class="col-md-1">
            <input class="form-control" type="text" name="vehicleInfo.vinCode" id="vin"
                   value="<#if settleOrder.vehicleInfo.vinCode??>${settleOrder.vehicleInfo.vinCode}</#if>" readonly="readonly" />
        </div>
        <div class="col-md-1">
          <label class="control-label">车牌号：</label>
        </div>
        <div class="col-md-1">
          <input class="form-control" type="text" name="vehicleInfo.plateNumber" id="carNum"
                 value="<#if settleOrder.vehicleInfo.plateNumber??>${settleOrder.vehicleInfo.plateNumber}</#if>" readonly="readonly" />
        </div>
        <div class="col-md-1">
            <label class="control-label">车型：</label>
        </div>
        <div class="col-md-3">
            <input class="form-control" type="text" name="vehicleInfo.model.version"
                   value="<#if settleOrder.vehicleInfo.model??&&settleOrder.vehicleInfo.model.version??>${settleOrder.vehicleInfo.model.version}</#if>" id="carType" readonly="readonly" />
        </div>
        <div class="col-md-1">
          <label class="control-label">汽车排量：</label>
        </div>
        <div class="col-md-1">
          <input class="form-control" type="text" name="vehicleInfo.engineDisplacement" id="engineDisplacement"
                 value="<#if settleOrder.vehicleInfo.engineDisplacement??>${settleOrder.vehicleInfo.engineDisplacement}</#if>" readonly="readonly" />
        </div>
      </div>
  </div>
  <div class="row" style="margin-top: 1%">
      <div class="col-md-12">
      <div class="col-md-2">
          <label class="control-label">上次保养里程：</label>
      </div>
      <div class="col-md-2">
          <input class="form-control" type="text" name="vehicleInfo.engineDisplacement"
                 id="lastMaintenanceMileage" value="<#if settleOrder.vehicleInfo.lastMaintenanceMileage??>${settleOrder.vehicleInfo.lastMaintenanceMileage}</#if>" readonly="readonly" />
      </div>
      <div class="col-md-1">
          <label class="control-label">已行驶里程：</label>
      </div>
      <div class="col-md-2">
          <input class="form-control" type="text" name="vehicleInfo.mileage" id="mileage"
                 value="<#if settleOrder.vehicleInfo.mileage??>${settleOrder.vehicleInfo.mileage?c}</#if>" readonly="readonly" />
      </div>
      </div>
      <div class="row" style="margin-top: 1%">
          <div class="col-md-10">
              <div class="col-md-1">
                  <label class="control-label">备注：</label>
              </div>
              <div class="col-md-7">
                  <#if settleOrder.remark??>${settleOrder.remark}</#if>
              </div>
          </div>
      </div>
      <span style="font-size: larger" ></span>
  </div>
  <div class="row" style="margin-top: 1%">
      <div class="col-md-1">
          <label class="control-label">销售单号:</label>
      </div>
      <div class="col-md-2">
          <p id="saleNoView" style="color:red;"><#if settleOrder.saleNoView??>${settleOrder.saleNoView}</#if></p>
      </div>
      <div class="col-md-1">
         <@form.textInput "settleOrder.saleNo" "" "hidden" />
      </div>
      <div class=" col-md-2">
          <@form.textInput "settleOrder.saleNoView" "" "hidden" />
      </div>
      <div class="col-md-3">
          <@form.btn_print "onclick='printit()'" "打印结算单" />
      </div>
  </div>

  <!-- 中间表格 -->
  <div class="row" style="margin-top: 1%">
      <table id="customList" class="scroll" width="100%" cellpadding="0" cellspacing="0"  border="1px">
      </table>
  </div>

  <div class="row" style="border:1px #000000 solid;padding-top: 0.5%">
      <div class="col-md-3">
              <label class="control-label">商品实收合计：</label>&nbsp;
              <label class="control-label" id="sum"></label>&nbsp;&nbsp;
              <label class="control-label">成本合计：</label>&nbsp;
              <label class="control-label" id="costsum"></label>
      </div>
      <div class="col-md-7">
              <label class="control-label">现金：</label>&nbsp;
              <input   type="text" size="5" name="payment.cashAmount" id="payment.cashAmount"  value="<#if settleOrder.payment??>${settleOrder.payment.cashAmount}</#if>" readonly="readonly" >
                &nbsp;&nbsp;<label class="control-label">pos机实收：</label>&nbsp;
              <input   type="text" size="5" name="payment.posAmount" id="payment.posAmount" value="<#if settleOrder.payment??>${settleOrder.payment.posAmount?c}</#if>" readonly="readonly">
                &nbsp;&nbsp;<label class="control-label">App费用：</label>
              <input   type="text" size="5" name="payment.appAmount" id="payment.appAmount" value="<#if settleOrder.payment??>${settleOrder.payment.appAmount}<#else>0</#if>" readonly="readonly" />
                <label class="control-label">第三方费用：</label>
                <input   type="text" size="5" name="payment.otherAmount" id="payment.otherAmount" value="<#if settleOrder.payment??>${settleOrder.payment.otherAmount}<#else>0</#if>" readonly="readonly" />
                &nbsp;&nbsp;<label class="control-label">接车人员：</label>
          <span id="receiver">
            <#if settleOrder.receiver??>${settleOrder.receiver.name}</#if>
          </span>

      </div>
  </div>
  </spring:bind>
  </form>
<#if settleOrder.operationItemDetails?? && (settleOrder.operationItemDetails?size > 0)>
<div id="workhours" style="margin-top: 3%">
      <spring:bind path="settleOrder">
      <!-- 中间表格 -->
      <div class="row">
          <div class="col-md-5" style="margin-left:-14px;">
              <table id="workHoursList" class="scroll" cellpadding="0" cellspacing="0"  border="1px">
              </table>
          </div>
      </div>
      <div class="row">
          <div class="col-md-5" style="border:1px #000000 solid; padding-top: 0.5%;padding-bottom: 0.5%;width:614px;">
              <label class="control-label">工时合计：</label>
              <label class="control-label" id="hourssum"></label>
          </div>
      </div>
      </spring:bind>
</div>
</#if>
  </@main.frame>
</#escape>