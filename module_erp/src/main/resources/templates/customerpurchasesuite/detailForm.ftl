<#import "../macros/mainFrame.ftl" as main />
<#import "../macros/formMacros.ftl" as form />
<#import "/spring.ftl" as spring />
<#escape x as x>

    <@main.frame>

    <meta http-equiv="Windows-Target" contect="_top">
    <style type="text/css">#dialog {
        display: none;
    }</style>

    <script type="text/javascript">
        var isDotype = "update";

        $('#collapseCustomerPurchasedSuite').collapse('show');

        <#--alert(${customerPurchasedSuite.customer.id})-->
        var reg = /^[0-9]+([.]{1}[0-9]{1,2})?$/;
        var arr = {};
//        var customerERPProfileList = {};
        $(function () {

            <#--var realName = "${salePerson}";-->
//            $("#salePerson").val(realName);
           /* <#if (customerERPProfileList)??>
                <#list customerERPProfileList as obj >
                    var erpProfile = {};
                    erpProfile.id = "${obj.customer.id}";
                    erpProfile.mobile = "${obj.customer.mobile!}";
                    customerERPProfileList["${obj.customer.id}"] = erpProfile;
                </#list>

            </#if>*/

            showSkuItemList();
            <#if (customerPurchasedSuite.purchasedSuiteItems)??>
                <#list customerPurchasedSuite.purchasedSuiteItems as rowData >
//                var purchasedSuiteItems = customerPurchasedSuite.purchasedSuiteItems;
                    /*for (var i in purchasedSuiteItems) {*/
                        var row = $("#skuItemBody").getGridParam("reccount") + 1;
                        $("#skuItemBody").addRowData(row,
                                {
//                            "id": suiteItems[index].id,
                                    <#--"skuItemId": "${obj.skuItem.id}",-->

                                    <#if rowData.customStockItem??>
                                        <#if rowData.customStockItem.id != 1>
                                            "name": "${rowData.customStockItem.name!""}",
                                            "brandName": "${rowData.customStockItem.brandName!}",
                                            "description": "${rowData.customStockItem.description!}",
                                        <#else>
                                            "name": "${rowData.suiteItem.skuItem.name!""}",
                                            "brandName": "${rowData.suiteItem.skuItem.brandName!}",
                                            "description": "${rowData.suiteItem.skuItem.description!}",
                                        </#if>
                                    <#else>
                                        "name": "${rowData.suiteItem.skuItem.name!""}",
                                        "brandName": "${rowData.suiteItem.skuItem.brandName!}",
                                        "description": "${rowData.suiteItem.skuItem.description!}",
                                    </#if>

                                    "cost": "${rowData.cost!}",
                                    "allTimes": ${rowData.times},
                                    "times": ${rowData.timesLeft},
//                                    "times": (purchasedSuiteItems[i].timesLeft + purchasedSuiteItems[i].usedTimes);
                                },
                                "last");
//                    }
                </#list>
            </#if>

        });

        function back(){
            window.location = "/customerpurchasesuite/list";
        }

        function showSkuItemList(url) {
            $("#skuItemBody").jqGrid({
                colModel: [
//                    {name:'id',hidden:false},
                    {name:'skuItemId',hidden:true},
                    {label: '商品名称', name: 'name', width: 80, align: "center"},
                    {label: '品牌名称', name: 'brandName', width: 80, align: "center"},
                    {label: '商品描述', name: 'description', width: 80, align: "center"},
                    {label: '成本', name: 'cost', width: 50, align: "center"},
                    {label: '总次数', name: 'allTimes', width: 50, align: "center" ,
                        formatter: function (cellvalue, options, rowObject) {
                            if (cellvalue == -1){
                                return "无限次";
                            } else {
                                return cellvalue;
                            }
                        }
                    },
                    {label: '无限次', name: 'times', width: 40, align: "center",
                        formatter: function (cellvalue, options, rowObject) {
                            if(cellvalue != null){
                                value = cellvalue
                            }
                            var uuid = rowObject['skuItem.id'] + "_" + rowObject['cost'];
                            if (rowObject['allTimes'] == -1) {
                                return "<input type='checkbox' id='notimes_"+ uuid + "' checked disabled/>";
                            }
                            return "<input type='checkbox' id='notimes_"+ uuid + "' disabled/>";
                        }
                    },
                    {label: '可使用次数', name: 'times', width: 40, align: "center",
                        formatter: function (cellvalue, options, rowObject) {
                            if (rowObject['allTimes'] == -1) {
                                return "无限次";
                            }
                            return  cellvalue;
                        }
                    },
                ],
                rownumbers: true
            });
        }


        function printit() {
            var orgName=$("#orgName").val();
            //workHoursList 作业项目 工时 表
            $("#printDiv").jqPrintDataWithoutBorder({
                headHtml:"<legend style='font-size: x-large' align='center' >&nbsp;&nbsp;"+ orgName +" - 会员套餐明细单</legend>",
                arr: [
                    [
                        {text: "<label class='control-label'>套餐信息</label>" ,colspan:2},
                        {text: "客户姓名", id: "realName",align:'center'},
                        {text: "手机", id: "customer.mobile" },
                        {text: "套餐名称", id: "suite.name" },
                        {text: "套餐售价", id: "suite.price"},
                        {text: "销售人员",id:"staff.name"},
                        {text: "开卡门店",id:"shop.name"},
                        {text: "开卡日期",id:"createdDate"},
                        {text: "剩余天数",id:"lastDay"},
                        {text: "现金付款",id:"payment.cashAmount"},
                        {text: "pos付款",id:"payment.posAmount"},
                        {widthStyle: "90%", row: 2, outBorderStyle: "bottomHr"}
                    ]
                ]
                ,jqGridTables: [{
                    labels: ['商品名称','总次数','剩余次数'],
                    names: ['name','allTimes','times'],
                    jqGridId: 'skuItemBody',
                    headText: '套餐明细',
                    outBorderStyle: "topAndBottom",
                    indexHead: "序号",
                    afterIndex: 0
                }]
            })
        }

    </script>
    <div class="row" style="margin-top: -80px">
        <div class="col-md-11">
            <div id="actions" class="form-action">
                <form class="" id="fm" action='<@spring.url relativeUrl = "/customerpurchasesuite/save"/>'
                      method="post">
                    <@form.textInput "customerPurchasedSuite.id" "" "hidden"/>
                    <@form.textInput "customerPurchasedSuite.ver" "" "hidden"/>
                    <input id="orgName" type="hidden" value="${customerPurchasedSuite.shop.organization.name}">


                    <input type="hidden" id="testValue" >

                    <input type="hidden" name="rowDatas" id="rowDatas">

                        <legend>会员套餐管理 -> 会员售卡明细</legend>

                    <div class="row">
                        <div class="col-md-2">
                            <span style="font-size: large;font-style:oblique;font-weight: bold;text-align: center">客户资料</span>

                        </div>
                        <div class="col-md-3">
                            <@form.textInput "customerERPProfile.realName" "class='form-control',id='realName' readonly" "text" "姓名" />

                        </div>
                        <div class="col-md-3">
                            <@form.textInput "customerPurchasedSuite.customer.mobile" "class='form-control',id='mobile' readonly" "text" "手机" />

                        </div>


                    </div>
                    <br>

                    <div class="row">
                        <div class="col-md-2">
                            <span style="font-size: large;font-style:oblique;font-weight: bold;text-align: center">会员卡信息</span>
                        </div>
                        <div class="col-md-3">
                            <@form.textInput "customerPurchasedSuite.suite.name" "class='form-control' readonly" "text" "套餐名称" />

                        </div>
                        <div class="col-md-3">
                            <@form.textInput "customerPurchasedSuite.suite.price" "class='form-control' readonly" "text" "套餐售价" />
                        </div>

                    </div>
                    <div class="row">
                        <div class="col-md-3 col-md-offset-2">
                             <@form.textInput "customerPurchasedSuite.staff.name" "class='form-control' readonly" "text" "销售人员" />
                        </div>
                        <div class="col-md-3">
                            <@form.textInput "customerPurchasedSuite.shop.name" "class='form-control' readonly" "text" "开卡门店" />
                        </div>
                    </div>

                    <div class="row">
                        <div class="col-md-3 col-md-offset-2">
                            <label class="control-label">开卡日期: </label>
                            <input type="text" id="createdDate" name="createdDate" class="form-control"readonly value="${createdDate}">
                        </div>
                        <div class="col-md-3">
                            <label class="control-label">剩余天数: </label>
                            <input type="test" id="lastDay" name="lastDay"  class="form-control" readonly value="${customerPurchasedSuite.lastDay}">
                        </div>
                    </div>

                    <#if settleOrder?? >

                    <div class="row" style="margin-top:5px; ">
                        <div class="col-md-3 col-md-offset-2">
                            <@form.textInput "settleOrder.payment.cashAmount" "class='form-control' readonly" "text" "现金付款" />
                        </div>
                        <div class="col-md-3">
                            <@form.textInput "settleOrder.payment.posAmount" "class='form-control' readonly" "text" "pos付款" />
                        </div>
                    </div>
                    </#if>
                    <div class="row">
                        <div class="col-md-2">
                            <span style="font-size: large;font-style:oblique;font-weight: bold;text-align: center">备注信息</span>
                        </div>
                        <div class="col-md-3">
                            <@form.textArea "customerPurchasedSuite.remark" "class='form-control' readonly style='width:460px;height:80px;'"  "备注" />
                        </div>
                    </div>
                    <div class="row">
                    </div>
                    <div class="row">
                        <div class="col-md-3 col-md-offset-2">
                        </div>
                        <div class="col-md-3">

                        </div>

                    </div>
                    <div class="row">
                        <table id="skuItemBody" cellpadding="0" cellspacing="0"></table>
                    </div>
                    <div class="row">
                        <div class="col-md-4 col-md-offset-4">
                            <br/>
                            <@form.btn_back "onclick='back()'" "返回"/>
                            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                            <@form.btn_print "onclick='printit()'" "打印会员套餐明细" />
                        </div>

                    </div>

                </form>
            </div>
        </div>
    </div>
    </@main.frame>

</#escape>