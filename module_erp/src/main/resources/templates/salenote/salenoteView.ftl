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
                            <#if orderDetails.receivable?? >
                                "receivable": parseInt(${orderDetails.receivable?c}).toFixed(2),
                            </#if>
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

        getAmount();

        <#if settleOrder.payment??>
            <#--calculatePosData(${settleOrder.payment.posAmount?c});-->
        </#if>
    });





    function getAmount(){
        //获取成本总和
        var costSim = 0;
        var obj = $("#customList").jqGrid("getRowData");
        jQuery(obj).each(function() {
            costSim += parseFloat(this["cost"]) * parseFloat(this["count"]);
        });
        $("#costsum").html(costSim.toFixed(2));
        //获取合计
        var amount=0;
        var obj = $("#customList").jqGrid("getRowData");
        jQuery(obj).each(function() {
            var id = this["id"];
            var receivablediscount = this["receivablediscount"];//折后售价
            var receivable = this["receivable"];//实际售价
            if(receivablediscount==null || receivablediscount=="") {
                amount +=parseFloat(receivable);
            }else {
                amount +=parseFloat(receivablediscount);
            }
        });
        $("#sum").html(amount.toFixed(2));
        //获取工时总和
        var hourAmount=0;
        var obj = $("#workHoursList").jqGrid("getRowData");
        jQuery(obj).each(function(){
            var sum = this["sum"];//总工时费用
            hourAmount += parseFloat(sum);
        });
        $("#hourssum").html(hourAmount);

        var maoli = (amount + hourAmount - costSim)/(amount + hourAmount) * 100;
        if (costSim != 0 && !isNaN(costSim)) {
            $("#maoli").html(maoli.toFixed(2) + "%");
        } else {
            $("#maoli").html(0);
        }
    }



    //订单详情
    function showCusInfo() {
        $("#customList").jqGrid({
//            url: '/salenote/showOrderInfo',
            pager : '#gridpager',
            colModel: [
                { label: 'ID', name: 'id', hidden:true},
                { label: '品名', name: 'orderedItem.name', width: 100 , align:"center"},
                { label: '成本价', name: 'cost', width:50 , align:"center"},
                { label: '加成率', name: 'additionRate', width:50 , align:"center",hidden:true},
                { label: '加成率', width:50 , align:"center",
                    formatter:function(cellvalue, options, rowObject) {
                        return (rowObject['additionRate']).toFixed(2) + "%";
                    }
                },
                { label: '单价', name: 'price', width: 50,editable:true, align:"center",
                    formatter:function(cellvalue, options, rowObject) {
                        var cost = (rowObject['cost'] * (1 + formatterPrice(rowObject['additionRate'])/100));
                        cost = cost.toFixed(2);
                        return formatterPrice(cost);
                }},
                { label: '分类', name: 'rootCategory', hidden:true , align:"center"},
                { label: '数量', name: 'count', width: 70, align:"center" },
                { label: '实际售价', name: 'receivable', width: 70, align:"center" },
                { label: '折后售价', name: 'receivablediscount', width: 70, align:"center" ,
                    formatter:function(cellvalue, options, rowObject) {
                        if(cellvalue != null && cellvalue !="") {
                            return parseFloat(cellvalue).toFixed(2);
                        }
                        return cellvalue;
                }},
                { label: '折扣率', name: 'discount', width: 80, align:"center"},
                { label: '授权人', name: 'discountGranter', width: 80, align:"center" },
                { label: '施工人员', name: 'merchandiername', width: 80, align:"center" }
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
                { label: '作业项目', name: 'name', width: 80},
                { label: '工时', name: 'laborHours', width: 80},
                { label: '单价', name: 'cost', width: 80,
                    formatter:function(id) {
                        <#if opreationPrice??>
                            return ${opreationPrice};
                        <#else>
                            return "";
                        </#if>
                    }
                },
                { label: '总工时费', name: 'sum', width: 80,
                    formatter:function(cellvalue) {
                        return cellvalue;
                    }
                }
            ],
        });
    };

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
    function backTohistory(){
        window.history.go(-1);
    }
</script>
  <div id="printDiv" >
  <legend>销售开单 -> 销售开单查看</legend>
  <form id="fm" class="" action='<@spring.url relativeUrl = "/salenote/save"/>' method="post">
  <input type="hidden" id="salenotedata" name="salenotedata"/>
  <input type="hidden" name="nextOrSettle" id="nextOrSettle"/>
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

  </div>

  <!-- 中间表格 -->
  <div class="row" style="margin-top: 1%">
      <table id="customList" class="scroll" cellpadding="0" cellspacing="0"  border="1px">
      </table>
  </div>

  <div class="row" style="border:1px #000000 solid;padding-top: 0.5%">
      <div class="col-md-4">
          <label class="control-label">成本合计：</label>&nbsp;
          <label class="control-label" id="costsum"></label>&nbsp;&nbsp;
          <label class="control-label">商品实收合计：</label>&nbsp;
          <label class="control-label" id="sum"></label>&nbsp;&nbsp;
          <label class="control-label">毛利率：</label>&nbsp;
          <label class="control-label" id="maoli"></label>
      </div>
      <div class="col-md-6">
              <label class="control-label">现金：</label>&nbsp;
              <input size="5" height="4px;" type="text" name="payment.cashAmount" id="payment.cashAmount"  value="<#if settleOrder.payment??>${settleOrder.payment.cashAmount}</#if>" readonly="readonly" >&nbsp;&nbsp;
              <label class="control-label">pos机实收：</label>&nbsp;
              <input size="5" type="text" name="payment.posAmount" id="payment.posAmount" value="<#if settleOrder.payment??>${settleOrder.payment.posAmount?c}</#if>" readonly="readonly">&nbsp;&nbsp;
              &nbsp;&nbsp;<label class="control-label">App费用：</label>
              <label class="control-label">第三方费用：</label>
              <input   type="text" size="5" name="payment.otherAmount" id="payment.otherAmount" value="<#if settleOrder.payment??>${settleOrder.payment.otherAmount}<#else>0</#if>" readonly="readonly" /> &nbsp;&nbsp;
              <input size="5" type="text" name="payment.appAmount" id="payment.appAmount" value="<#if settleOrder.payment??>${settleOrder.payment.appAmount}<#else>0</#if>" readonly="readonly" />
      </div>
      <div class="col-md-2">
          <label class="control-label">接车人员：</label>
          <span id="receiver">
              <#if settleOrder.receiver??>${settleOrder.receiver.name}</#if>
          </span>
      </div>
  </spring:bind>

    </form>
  </div>
  </div>
  <div class="row" style="padding-top: 0.5%">
      <div class="col-md-5"><strong><font color="red"> 注：毛利率 = (商品实收合计 + 工时合计 - 成本合计) ÷ (商品实收合计 + 工时合计)</font></strong>
         </div>
  </div>
<#if settleOrder.operationItemDetails?? && (settleOrder.operationItemDetails?size > 0)>
<div id="workhours" style="margin-top: 3%">
      <spring:bind path="settleOrder">
      <!-- 中间表格 -->
      <div class="row">
          <div>
              <table id="workHoursList" class="scroll" cellpadding="0" cellspacing="0"  border="1px">
              </table>
          </div>
      </div>

      <div class="row" style="border:1px #000000 solid; padding-top: 0.5%;padding-bottom: 0.5%">
          <div class="col-md-4">
                  <label class="control-label">工时合计：</label>&nbsp;
                  <label class="control-label" id="hourssum"></label>
          </div>
      </div>
      </spring:bind>
</div>
<br>
<div class="row  text-center">
    <div class="col-md-10">
        <#--<@form.btn_back "onclick='backTohistory();'" "返回上一页" />-->
    </div>
</div>
</#if>
  </@main.frame>
</#escape>