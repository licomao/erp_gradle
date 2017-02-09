<#import "../macros/mainFrame.ftl" as main />
<#import "../macros/formMacros.ftl" as form />
<#import "/spring.ftl" as spring />
<#escape x as x?html>

    <@main.frame>

    <meta http-equiv="Windows-Target" contect="_top">
    <script src="/javascripts/jquery-ui-1.9.2.min.js" type="text/javascript"></script>
    <script src="/javascripts/cndate.js" type="text/javascript"></script>
    <script src="/javascripts/jquery.ui.widget.js" type="text/javascript"></script>
    <script src="/javascripts/jquery.iframe-transport.js" type="text/javascript"></script>
    <script src="/javascripts/jquery.fileupload.js" type="text/javascript"></script>
    <link href="/stylesheets/select2.min.css" rel="stylesheet" />
    <script src="/javascripts/select2js/select2.min.js"></script>
    <script type="text/javascript">
        var isDotype = "update";
        <#if pdCheck??>
            <#if pdCheck>
                alert("您已经很久没盘点库存了，请盘点完再来操作！");
            window.history.back();
            </#if>
        </#if>
        $('#collapsePurchase').collapse('show');
        $(function(){
            var url = '/stock/list/data?name=' + $("#shopName").val()
                    + "&rootCategory=" + $("#rootCategory").val()
                    + "&shopId=" + $("#shopId").val();
//            var url = "/stock/list/data?shopId=" + $("#shopId").val() + "&customStockItem.name=" + $("#shopName").val();
            showSkuItemList(url);
            addStock();
            var tableSum = 0;
            if(${purchaseOrder.id} != 0){

                <#if (purchaseOrder.purchaseOrderDetailList)?? >
                    <#list purchaseOrder.purchaseOrderDetailList as rowData >
                        var row = $("#gridBody").getGridParam("reccount") + 1;
                        var totalPrice = ${rowData.number}*formatterPrice("${rowData.price}");
                        $("#gridBody").addRowData(row,
                                {
                                    "viewId":${rowData.customStockItem.id?c},
                                    "orderDetailId":${rowData.id?c},
                                    "stockNumber":${rowData.number},
                                    <#--"currentPrice":"${rowData.price}",-->
                                    "currentPrice":formatterPrice("${rowData.price}"),
                                    "totalPrice":totalPrice,
                                    <#--"totalPrice":${rowData.number}*formatterPrice("${rowData.price}"),-->
                                    "customStockItem.id":${rowData.customStockItem.id?c}, //商品ID
                                    "customStockItem.name":"${rowData.customStockItem.name}", //商品名称
                                    <#if rowData.customStockItem.brandName??>
                                    "customStockItem.brandName":"${rowData.customStockItem.brandName}", //品牌
                                    </#if>
                                    "lastPrice":${rowData.lastPrice}, //上次进货价格
                                    "bankNumber":${rowData.bankNumber}, //库存
                                    "customStockItem.isDistribution":"${rowData.customStockItem.isDistribution?c}",
                                },"last");
                        tableSum += totalPrice;
                    </#list>
                </#if>
            }
            $("#tableSum").text(tableSum.toFixed(2));
            isDotype = "add";

            //监听采购类型下拉选择框的change事件
            $("#purchaseType").change(function(){
               var value = $(this).val();
                if(value == 1){
                    $("#saleNo").attr('readonly',false);
                }else{
                    $("#saleNo").attr('readonly',true);
                    $("#saleNo").val("");
                }
            });

            $(".select2").select2({
                placeholder: "请选择",
            });

        });


        function stockSetUrl(id, addId,rootCategory) {
            var foo = true;
            var obj = $("#gridBody").jqGrid("getRowData");
            if (rootCategory == 17){
                alert("服务类商品无需采购，请至商品管理中自行添加“服务价格”！");
                return false;
            }
            jQuery(obj).each(function(){
                if (addId == this['viewId']+"_"+this['lastPrice']) {
                    /*$("#stockNumber_" + addId).val(Number($("#stockNumber_" + addId).val()) + 1);

                    $("#totalPrice_" + addId).val($("#stockNumber_" + addId).val() * price);*/
                    alert("该商品已经选择了");
                    foo = false;
                    return false;
                }
               /* if(id == this['customStockItem.id']){
                    $("#stockNumber_"+id).val(Number($("#stockNumber_"+id).val())+1);

                    $("#totalPrice_"+id).val($("#stockNumber_"+id).val() * $("#currentPrice_"+id).val());

                    foo = false;
                }*/
            });
            if(!foo){
                return ;
            }
            var row = $("#gridBody").getGridParam("reccount") + 1;
            var rowData = $("#skuItemBody").jqGrid('getRowData',id);
            var rowDataId = rowData.id;
            $("#gridBody").addRowData(row,
                {
                    "orderDetailId":0,
                    "stockNumber":rowDataId,
                    "currentPrice":rowDataId,
                    "totalPrice":rowDataId,
                    "customStockItem.id":rowData.viewId,
                    "viewId":rowData.viewId,
                    "customStockItem.name":rowData.name,
                    "customStockItem.brandName":rowData.brandName,
                    "lastPrice":rowData.cost,
                    "customStockItem.isDistribution":rowData.isDistribution,
                    "bankNumber":rowData.number,
                },"last");
        }

        function addStock(url){
          $("#gridBody").jqGrid({
              pager : '#gridpager',
              colModel: [
                  {name: 'viewId', hidden: true},
//                  {name: 'viewId', hidden: false},
                  { name:'orderDetailId',hidden:true},
//                  { name:'orderDetailId',hidden:false},
                  { name:'customStockItem.id', hidden:true},
//                  { name:'customStockItem.id', hidden:false},
                  { label: '商品',name:'customStockItem.name', width: 100, align:"center" },
                  { label: '品牌', name:'customStockItem.brandName',width: 50, align:"center"},
                  { label: '上次进价', name:'lastPrice', width: 30, align:"center"},
                  { label: '库存数', name:'bankNumber', width: 30, align:"center"},

                  { label: '结算状态', name:'customStockItem.isDistribution', width: 30, align:"center",

                      formatter: function (cellvalue, options, rowObject) {
                          if(cellvalue == 0){
                              return "月结";
                          }else if(cellvalue == 1){
                              return "铺货";
                          }else if(cellvalue == 2){
                              return "月结";
                          }else if(cellvalue == 3){
                              return "现结";
                          }
                      }
                  },
                  { label: '本次进价', name:'currentPrice', width: 30, align:"center",
                      formatter: function (cellvalue, options, rowObject) {
                          var value = isDotype == "update" ? cellvalue : 0;
                          var price = rowObject["lastPrice"];
                          var id = rowObject["viewId"] + "_" + price;
                          return "<input type='text' class='input-sm form-control' value='"+value+"' onblur='validateDouble(this," + rowObject['viewId'] + "," + price + ")' id='currentPrice_"+ id +"'/>";
//                          return "<input type='text' class='input-sm form-control' value='"+value+"' onblur='validateDouble(this,'" + rowObject["viewId"] + "'," + price + ")' id='currentPrice_"+ id +"'/>";
                      }
                  },
                  { label: '进货数',name:'stockNumber', width: 30,align:"center",
                      formatter: function (cellvalue, options, rowObject) {
                          var value = isDotype == "update"?cellvalue:1;
//                          var id = rowObject["customStockItem.id"];
                          var price = rowObject["lastPrice"];
                          var id = rowObject["viewId"] + "_" + price;
                          return "<input type='text' class='input-sm form-control' value='"+value+"' onblur='validate(this," + rowObject['viewId'] + "," + price + ")' id='stockNumber_"+ id +"'/>";
                      }
                  },
                  { label: '合计', name:'totalPrice', width: 30, align:"center",
                      formatter: function (cellvalue, options, rowObject) {
                          var value = isDotype == "update"?cellvalue:0;
//                          var id = rowObject["customStockItem.id"];
                          var price = rowObject["lastPrice"];
                          var id = rowObject["viewId"] + "_" + price;
                          return "<input type='text' readonly='readonly' class='input-sm form-control' value='"+value+"' id='totalPrice_"+ id +"'/>";
                      }
                  },
                  { label: '操作', name:'id', width: 20, align:"center",
                      formatter: function (cellvalue, options, rowObject) {
                          var price = rowObject["lastPrice"];
                          var id = rowObject["viewId"] + "_" + price;
                          return "<a href='#'style='text-decoration:underline;color:blue' onclick='deleteRow("+ options.rowId +", \"" + id + "\")'>删除</a>";
                      }
                  }
              ],
              rownumbers: true,
//              sortorder:'desc',
//              sortname:'createdDate'
          });
        }

        function deleteRow(rowId, uid){
            var oldTotalPrice = $("input[id='totalPrice_" + uid + "']").val();
            var oldTableSum = parseFloat($("#tableSum").text());
            $("#tableSum").text((oldTableSum - oldTotalPrice).toFixed(2));
//            var te = floatSub(oldTableSum,oldTotalPrice);
//            alert("aa"+te)
            $("#gridBody").jqGrid("delRowData",rowId);
        }
        function subForm(){
            var oneData ="";
            var obj = $("#gridBody").jqGrid("getRowData");
            jQuery(obj).each(function(){
                var uid = this['viewId']+"_"+this['lastPrice'];
//                alert(uid);
                //商品ID,本次进价,进货数,明细表ID,上次进价,库存数;
                oneData += "" + this['customStockItem.id'] + ","+ $("input[id='currentPrice_"+uid+"']").val() + "," + $("input[id='stockNumber_"+uid+"']").val() + "," +  this['orderDetailId'] + "," + this['lastPrice'] + "," + this['bankNumber'] + ";";
            });
//            alert(oneData);
            if(oneData == ""){
                alert("请至少选择一个商品！");
                return;
            }
            if($("input[id='saleNo']").val() == "" && $("#purchaseType").val() == 1){
                alert("临时采购下采购单号不能为空!");
                return;
            }
            $("#rowDatas").val(oneData);

            if(${purchaseOrder.id} != 0){
                if(confirm("是否确认修改!")){
                    $("#fm").submit();
                }
            }else{
                if(confirm("是否确认申请!")){
                    $("#fm").submit();
                }
            }
        }

        function showSkuItemList(url) {
            $("#skuItemBody").jqGrid({
                url: url,
//                multiselect:true,

                colModel: [
                    {name: 'id',hidden:true},
//                    {name: 'id',hidden:false},
//                    {name: 'viewId',hidden:false},
                    {name: 'viewId',hidden:true},
                    {name: 'isDistribution',hidden:true},

                    {label: '商品', name: 'name', width: 80, align: "center"},
                    {label: '品牌', name: 'brandName', width: 50, align: "center"},
                    { label: '上次进价', name:'cost', width: 20, align:"center"},
                    { label: '库存数', name:'number', width: 20, align:"center"},
                    { label: '顶级分类',index:'rootCategory', name: 'rootCategory', width: 40, align:"center",
                        formatter: "select", editoptions:{value:"1:机油;2:机滤;3:轮胎;4:电瓶;5:电子类产品;6:美容类产品;7:汽车用品;8:养护产品;9:耗材类产品;10:灯具类产品;" +
                    "11:雨刮类产品;12:发动机配件类;13:底盘配件类;14:变速箱类;15:电气类;16:车身覆盖类;17:服务类;0:临时分类"}},
                    {label: '二级分类', name: 'secondaryCategory.name', width: 50, align: "center"},

//                    {label: '条形码', name: 'barCode', width: 50, align: "center"},
                    { label: '操作', name:'', width: 20, align:"center",
                        formatter: function (cellvalue, options, rowObject) {
                            var addId = rowObject["viewId"] + "_" + rowObject["cost"];
//                            alert(addId);
                            var modify = "<a onclick=\"stockSetUrl(" + rowObject.id + ",'" + addId + "',"+rowObject['rootCategory']+" )\" href='#' style='text-decoration:underline;color:blue'>" + "添加" + "</a>";
                            return modify;
                        }
                    }

                ],
                rownumbers: true
            });
        }

        function getShopInfo(){
            var url = '/stock/list/data?name=' + $("#shopName").val()
                    + "&rootCategory=" + $("#rootCategory").val()
                    + "&shopId=" + $("#shopId").val();
            /*
                        var url = "/material/list/shopdata?name=" + $("#shopName").val()
                                + "&rootCategory=" + $("#rootCategory").val()
                                + "&shopId=" +  $("#shopId").val() ;*/
            url = encodeURI(url,"UTF-8");
            jQuery("#skuItemBody").setGridParam({url:url}).trigger("reloadGrid", [{ page: 1}]);

        }

        function validate(obj, id, price){
            var uid = id + "_" + price;
            var reg = new RegExp("^[0-9]*$");
            var result = 0;
            var oldTotalPrice = $("input[id='totalPrice_" + uid + "']").val();
            if(!reg.test(obj.value)){
              obj.focus();
              $(obj).val(0);
//                var id1 = "totalPrice_"+uid;
              $("input[id='totalPrice_" + uid +"']").val(0);
            }else{
                result = obj.value * $("input[id='currentPrice_" + uid + "']").val();// $("#currentPrice_"+uid).val();
//              $("#totalPrice_"+uid).val(result);

                $("input[id='totalPrice_" + uid + "']").val(result);
            }
            $("#tableSum").text((parseFloat($("#tableSum").text()) + result - oldTotalPrice).toFixed(2));
        }

        function validateDouble(obj,id,price){
            var uid = id + "_" + price;
            var reg = /^[0-9]+([.]{1}[0-9]{1,2})?$/;
            var result = 0;
            var oldTotalPrice = $("input[id='totalPrice_" + uid + "']").val();
            if(!reg.test(obj.value)){
                $(obj).val(0);

                $("input[id='totalPrice_" + uid +"']").val(0);
                obj.focus();
            }else{
//                var result = obj.value * $("#stockNumber_"+uid).val();
                result = obj.value * $("input[id='stockNumber_" + uid + "']").val();


                $("input[id='totalPrice_" + uid + "']").val(result);
            }
            $("#tableSum").text((parseFloat($("#tableSum").text()) + result - oldTotalPrice).toFixed(2));
        }

    </script>
    <div class="row" style="margin-top: -80px">
        <div class="col-md-12">
            <div id="actions" class="form-action">
                <form class="" id="fm" action='<@spring.url relativeUrl = "/purchaseorder/save"/>' method="post">


                    <@form.textInput "purchaseOrder.id" "" "hidden"/>
                    <@form.textInput "purchaseOrder.ver" "" "hidden"/>
                    <input type="hidden" name="rowDatas" id="rowDatas">

                    <#if pageContent?? && pageContent == "update">
                        <legend>采购单管理 -> 修改采购单申请</legend>
                    <#else>
                        <legend>采购单管理 -> 添加采购单申请</legend>
                    </#if>


                    <div class="col-md-6">
                        <div class="row">
                            <div class="col-md-5">
                                <@form.textInput "purchaseOrder.purchaseShop.name" "class='form-control' readonly" "text" "采购门店" />
                                <input value="${purchaseOrder.purchaseShop.id}" name="purchaseShop.id" id="shopId" type="hidden" >

                            </div>
                            <div class="col-md-5 col-md-offset-2">
                                <@form.textInput "purchaseOrder.orderNumberView" "class='form-control' readonly" "text" "单据编号" />

                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-5">
                                <div class="row">
                                    <label  class="control-label" >&nbsp;&nbsp;&nbsp;&nbsp;审批门店</label>
                                </div>
                                <div class="row" >
                                    <div class="col-md-12" >
                                        <select class="form-control" name="saleShop.id" id="saleShop">
                                            <#list saleShopList as shop>
                                                <option value="${shop.id}" <#if (purchaseOrder.saleShop.id)?? && purchaseOrder.saleShop.id == shop.id >selected</#if>   >${shop.name}</option>
                                            </#list>
                                        </select>
                                    </div>
                                </div>

                            </div>
                            <div class="col-md-5 col-md-offset-2">
                                <div class="row">
                                    <label  class="control-label" >&nbsp;&nbsp;&nbsp;&nbsp;供应商</label>
                                </div>
                                <div  class="row" >
                                    <div class="col-md-12" >
                                        <select class="form-control select2" name="supplier.id" id="supplierId">
                                        <#list supplierList as shop>
                                            <option value="${shop.id?c}" <#if (purchaseOrder.supplier.id)?? && purchaseOrder.supplier.id == shop.id >selected</#if> >${shop.name}</option>
                                        </#list>
                                        </select>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <br>
                        <div class="row">
                            <div class="col-md-5">
                                <div class="row">
                                    <label  class="control-label" >&nbsp;&nbsp;&nbsp;&nbsp;采购类型</label>
                                </div>
                                <div class="row">
                                    <div class="col-md-12" >
                                        <select class="form-control" name="purchaseType" id="purchaseType" >
                                            <option value="0" <#if purchaseOrder.purchaseType?? && purchaseOrder.purchaseType == 0 >selected</#if> >常规采购</option>
                                            <option value="1" <#if purchaseOrder.purchaseType?? && purchaseOrder.purchaseType == 1 >selected</#if> >临时采购</option>
                                        </select>
                                    </div>
                                </div>
                            </div>
                            <div class="col-md-5 col-md-offset-2">
                                <@form.textInput "purchaseOrder.saleNo" "class='form-control' id='saleNo' readonly " "text" "销售单号" true />
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-12" >
                            <@form.textArea "purchaseOrder.remark" "class='form-control'" "备注"/>
                            </div>
                        </div>
                        <br/>
                        <table id="gridBody" class="scroll" cellpadding="0" cellspacing="0"></table>
                        <div class="row">
                            <div class="col-md-9"></div>
                            <div class="col-md-3">
                                <label  style="text-align: center" >合计:<span id="tableSum" ></span> 元</label>
                            </div>
                        </div>
                        <br>
                        <div class="span2 text-center">
                            <#if pageContent?? && pageContent == "update">
                                <@form.btn_save "onclick='subForm()'" "确认修改"/>
                            <#else>
                                <@form.btn_save "onclick='subForm()'" "确认申请"/>
                            </#if>
                        </div>
                    </div>
                    <#--<div class="col-md-5 col-md-offset-" >-->
                    <div class="col-md-6" >
                        <div class="col-md-1" >

                        </div>
                        <div class="col-md-11" >
                            <div id="actions" class="form-action">
                                <div class="row">
                                    <label  class="control-label">商品名称: </label>
                                    <input type="text" id="shopName" value="">
                                    <label  class="control-label">顶级分类: </label>
                                    <@form.topCategory "rootCategory" "" />
                                    <@form.btn_search "onclick='getShopInfo()'" "查 询"/>
                                </div>
                                <br>
                                <div class="row">
                                <table id="skuItemBody" class="scroll" cellpadding="0" cellspacing="0"></table>
                                </div>
                                <div id="toolBar"></div>
                            </div>
                        </div>
                    </div>

                </form>
            </div>
        </div>
    </div>
    </@main.frame>

</#escape>