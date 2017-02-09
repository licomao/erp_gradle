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

        //会员卡商品明细
        <#if settleOrder.orderDetails?? >
            <#list settleOrder.orderDetails as orderDetails >
                var row = $("#customCard").getGridParam("reccount") + 1;
                $("#customCard").addRowData(row,
                        {"id":${orderDetails.id?c},
                            "name":"${orderDetails.orderedItem.name}",
                            "cost":${orderDetails.cost?c},
                            "count":${orderDetails.count?c},
                            "worker":"${orderDetails.merchandier.name}"
                        },
                        "last");
            </#list>
        </#if>
        getCostSum();

        <#if settleOrder.payment??>
            calculatePosData(${settleOrder.payment.posAmount});
        </#if>
    });

    //获取成本总和
    function getCostSum(){
        var costSim=0;
        var obj = $("#customCard").jqGrid("getRowData");
        jQuery(obj).each(function() {
            costSim += parseFloat(this["cost"]) * parseFloat(this["count"]);
        });
        $("#costsum").html(costSim.toFixed(2));
    }

    //订单详情
    function showCusInfo() {

        $("#customCard").jqGrid({
            colModel: [
                { label: 'ID', name: 'id', hidden:true},
                { label: '品名', name: 'name', width: 75 },
                { label: '分类', name: 'rootCategory', hidden:true },
                { label: '成本价',name: 'cost', width:75 },
                { label: '数量', name: 'count', width: 80 },
                { label: '施工人员', name: 'worker', width: 80 },
                { label: '会员卡项目', name: 'suiteName', width: 80, align:"center",
                    formatter:function() {
                        return "${settleOrder.customerPurchasedSuite.suite.name!}";
                    }
                }
            ],
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

    function printit() {

        $("#printDiv").jqPrintDataWithoutBorder({
            headHtml:"销售开单 -> 会员卡开单内容",
            arr:[
                    [
                        {text: "姓名", id: "realName"},
                        {text: "电话", id: "mobile"},
                        {headText: "客户信息", widthStyle: "50%"}
                    ], [
                        {text: "VIN号", id: "vin"},
                        {text: "车牌号", id: "carNum"},
                        {text: "车型", id: "carType"},
                        {text: "汽车排量", id: "engineDisplacement"},
                        {text: "上次保养里程", id: "lastMaintenanceMileage"},
                        {text: "已行驶里程", id: "mileage"},
                        {text: "销售单号", id: "saleNoViewp", type: "text"},
                        {headText: "车辆信息", row:3, widthStyle: "80%"}

                    ], [
                    {text: "合计", id: "sum", type: "text"},
                    {text: "现金", id: "payment.cashAmount"},
                    {text: "pos机", id: "payment.posAmount"},
                    {text: "App费用", id: "payment.appAmount"},
                    {text: "接车人员", id: "receiver", type: "text"},
                    {row: 5, widthStyle: "auto" }
                    ],
                    [{text: "合计", id: "hourssum", type: "text"},{widthStyle: "auto"}]
                ],

            jqGridTables:[{
                labels: ['品名','成本价','数量','施工人员','会员卡项目'],
                names: ['name','cost','count','worker','suiteName'],
                jqGridId: 'customCard',
                headText: '商品列表',
                afterIndex: 1
            }],
            jqGridTables: [{
                labels: ['品名','单价','分类','数量','实际售价','折后售价','折扣率','授权人','施工人员'],
                names: ['orderedItem.name','price','rootCategory','count','receivable','receivablediscount','discount','discountGranter','merchandiername'],
                jqGridId: 'customList',
                headText: '商品列表',
                afterIndex: 1
            },{
                labels: ['作业项目','工时','单价','总工时费'],
                names: ['name','laborHours','cost','sum'],
                jqGridId: 'workHoursList',
                headText: '工时报价表',
                afterIndex: 2
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
  <spring:bind path="settleOrder">

      <!--startprint1--><#--打印起始位置-->

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
        <!--startprint1-1-->
      <div class="col-md-1">
          <label class="control-label">姓名:</label>
      </div>
        <!--endprint1-1-->
      <div class="col-md-1">
          <input class="form-control" type="text" name="realName" id="realName"
                 value="<#if customerERPProfile.realName??>${customerERPProfile.realName}</#if>" required  readonly="readonly" />
      </div>
        <!--startprint1-2-->
      <div class="col-md-1">
          <label class="control-label">电话:</label>
      </div>
        <!--endprint1-2-->
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
          <!--startprint1-3-->
        <div class="col-md-1">
            <label class="control-label">VIN号：</label>
        </div>
          <!--endprint1-3-->
        <div class="col-md-1">
            <input class="form-control" type="text" name="vehicleInfo.vinCode" id="vin"
                   value="<#if settleOrder.vehicleInfo.vinCode??>${settleOrder.vehicleInfo.vinCode}</#if>" readonly="readonly" />
        </div>
          <!--startprint1-4-->
        <div class="col-md-1">
          <label class="control-label">车牌号：</label>
        </div>
          <!--endprint1-4-->
        <div class="col-md-1">
          <input class="form-control" type="text" name="vehicleInfo.plateNumber" id="carNum"
                 value="<#if settleOrder.vehicleInfo.plateNumber??>${settleOrder.vehicleInfo.plateNumber}</#if>" readonly="readonly" />
        </div>
          <!--startprint1-5-->
        <div class="col-md-1">
            <label class="control-label">车型：</label>
        </div>
          <!--endprint1-5-->
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
      <div class="col-md-1">
          <label class="control-label">销售单号:</label>
      </div>
      <div class="col-md-2">
          <p id="saleNoViewp" style="color:red;"><#if settleOrder.saleNoView??>${settleOrder.saleNoView}</#if></p>
      </div>
      <div class="col-md-1">
         <@form.textInput "settleOrder.saleNo" "" "hidden" />
      </div>
      <div class=" col-md-2">
          <@form.textInput "settleOrder.saleNoView" "" "hidden" />
      </div>
      <div class="col-md-3">
          <@form.btn_print "onclick='printit([1,2,3,4,5,6])'" "打印委托书（销售单）" />
      </div>
  </div>

  <!-- 中间表格 -->
  <div class="row" style="margin-top: 1%">
      <table id="customCard" class="scroll" cellpadding="0" cellspacing="0"  border="1px">
      </table>
  </div>

  <div class="row" style="border:1px #000000 solid;padding-top: 0.5%">
      <div class="col-md-4">
          <div class=" col-md-3">
              <label class="control-label">成本合计：</label>
          </div>
          <div class=" col-md-2">
              <label class="control-label" id="costsum"></label>
          </div>
      </div>
      <div class="col-md-3">
          <!--startprint2-2--><#--打印起始位置-->
          <div class="col-md-3">
              <label class="control-label">现金：</label>
          </div>
          <!--endprint2-2--><#--这段注释必须要-->
          <div class=" col-md-3">
              <input class="form-control" type="text" name="payment.cashAmount" id="payment.cashAmount"  value="<#if settleOrder.payment??>${settleOrder.payment.cashAmount}</#if>" readonly="readonly" >
          </div>
          <!--startprint2-3--><#--打印起始位置-->
          <div class="col-md-3">
              <label class="control-label">pos机：</label>
          </div>
          <!--endprint2-3--><#--这段注释必须要-->
          <div class=" col-md-3">
              <input class="form-control" type="text" name="payment.posAmount" id="payment.posAmount" value="<#if settleOrder.payment??>${settleOrder.payment.posAmount}</#if>" readonly="readonly">
          </div>
      </div>
      <div class="col-md-5">
          <!--startprint2-4--><#--打印起始位置-->
          <div class="col-md-2">
              <label class="control-label">App费用：</label>
          </div>
          <!--endprint2-4--><#--这段注释必须要-->
          <div class=" col-md-2">
              <input class="form-control" type="text" name="payment.appAmount" id="payment.appAmount" value="<#if settleOrder.payment??>${settleOrder.payment.appAmount}<#else>0</#if>" readonly="readonly" />
          </div>

          <!--startprint2-5--><#--打印起始位置-->
          <div class="col-md-2">
              <label class="control-label">接车人员：</label>
          </div>
  </spring:bind>
          <!--endprint2-5--><#--这段注释必须要-->
          <#if settleOrder.receiver??>${settleOrder.receiver.name}</#if>
    </form>
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
                      <div class=" col-md-6">
                          <label class="control-label">合计：</label>
                      </div>
                      <div class=" col-md-6">
                          <label class="control-label" id="hourssum"></label>
                      </div>
                  </div>
              </div>
          </spring:bind>
      </div>
      </#if>

  </@main.frame>
</#escape>