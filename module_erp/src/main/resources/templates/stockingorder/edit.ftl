<#import "../macros/mainFrame.ftl" as main />
<#import "../macros/formMacros.ftl" as form />
<#import "/spring.ftl" as spring />
<#escape x as x?html>

    <@main.frame>
    <meta http-equiv="Windows-Target" contect="_top">
    <script src="/javascripts/jquery.iframe-transport.js" type="text/javascript"></script>
    <script src="/javascripts/jquery.fileupload.js" type="text/javascript"></script>

    <script type="text/javascript">
        $('#collapseStock').collapse('show');
        $(function(){
           var url = '/stockingorder/details?orderId='+$("#id").val();
           showList(url);
//           setTimeout("showSum()",100);
        });

        function showSum(){
            var obj = $("#shopBody").jqGrid("getRowData");
            var sum = 0;
            var beforeSum = 0;
            var i = 1;
            jQuery(obj).each(function(){
                sum += this['stockCost'] * this['oldNumber'];
                document.getElementById("pSum_"+  this['id']).value = this['stockCost'] * $("#pNum_"+ this['id']).val();
                beforeSum += this['stockCost'] * $("#pNum_"+ this['id']).val();
                checkColor(document.getElementById("pNum_"+  this['id']),this['oldNumber']);
            });
            document.getElementById("beforeSum").innerHTML = sum.toFixed(2) + " 元";
            document.getElementById("afterSum").innerHTML = beforeSum.toFixed(2) + " 元";
        }

        function showBeforeSum(oIp){
            if (validate(oIp)) {
                var obj = $("#shopBody").jqGrid("getRowData");
                var sum = 0;
                var i = 1;
                jQuery(obj).each(function () {
                    $("#pSum_" +  this['id']).val(this['stockCost'] * $("#pNum_" +  this['id']).val());
                    sum += this['stockCost'] * $("#pNum_" +  this['id']).val();
                });
                document.getElementById("afterSum").innerHTML = sum + " 元";
            }
        }

        function showList(url) {
            var status = "";
            if ($("#stockingStatus").val() == 1) status = "disabled";
            $("#shopBody").jqGrid({
                url: url,
                pager : '#gridpager',
                rowNum:99999,
                colModel: [
                    { name: 'id', hidden:true },
                    { label: '商品名', name: 'customStockItem.name', width: 100, align:"center" },
                    { label: '品牌', name: 'customStockItem.brandName', width: 40, align:"center"},
                    { label: '条形码', name: 'customStockItem.barCode', width: 50, align:"center"},
                    { label: '顶级分类', name: 'customStockItem.rootCategory', width: 40,align:"center", formatter: "select", editoptions:{value:"1:机油;2:机滤;3:轮胎;4:电瓶;5:电子类产品;6:美容类产品;7:汽车用品;8:养护产品;9:耗材类产品;10:灯具类产品;" +
                    "11:雨刮类产品;12:发动机配件类;13:底盘配件类;14:变速箱类;15:电气类;16:车身覆盖类;17:服务类;0:临时分类"}},
                    { label: '成本(元)', name: 'stockCost', width: 50, align:"center" },
                    { label: '库存数量', name: 'oldNumber', width: 40, align:"center"},
                    { label: '盘后数量', name: 'calculateNumber', width: 40 ,align:"center",
                        formatter:function(cellvalue, options, rowObject){
                              return "<input type='text' "+ status +" class='input-sm form-control' onchange='showBeforeSum(this);checkColor(this,"+rowObject['oldNumber']+")' value='" + cellvalue + "' id='pNum_"+ rowObject['id'] +"'/>";
                        }
//
                    },
                    { label: '盘后总价', name: 'isDistribution', width: 30 ,align:"center",
                        formatter:function(cellvalue, options, rowObject){
                            return "<input type='text' class='input-sm form-control' value='0' disabled id='pSum_"+ rowObject['id'] +"'/>";
                        }
//
                    }
                ],
                //multiselect:true,
                rownumbers: true,
                gridComplete:function() {
                    showSum();
                }
            });


        }

        function subForm(type){
            var oneData ="";
            var obj = $("#shopBody").jqGrid("getRowData");
            var i = 1;
            jQuery(obj).each(function(){
                oneData += "" + this['id'] + ","+ $("#pNum_"+ this['id']).val() + ";";
            });

            $("#listData").val(oneData);
            var msg="保存盘点";
            if(type == 1) msg = "结束盘点";
            if(confirm("是否确认 "+msg+"! ")){
                document.getElementById("stockingStatus").value = type;
                $("#fm").submit();
            }
        }

        function validate(obj){
            var reg = new RegExp("^[0-9]*$");
            if(!reg.test(obj.value)){
                alert("请输入正整数!");
                obj.value = 0 ;
                return false;
            }
            return true;
        }

        function checkColor(obj,beforeVal){
            var trObj = obj.parentNode.parentNode;
            beforeVal = parseInt(beforeVal);
            var objValue = parseInt(obj.value);
            if (objValue > beforeVal){
                trObj.style.color='blue';
            } else if(objValue < beforeVal) {
                trObj.style.color='red';
            } else {
                trObj.style.color='';
            }
        }

        function exportExcel (){
            window.location = encodeURI("/stockingorder/excel/export?orderId=" + $("#id").val() + "&type=edit" + "&shopId=" + $("#shopId").val(), "UTF-8");
        }

    </script>

    <div class="row" style="margin-top: -80px">
        <div class="col-md-11">
            <div id="actions" class="form-action">
                <form class="" id="fm" action='<@spring.url relativeUrl = "/stockingorder/edit/save"/>'   method="post">
                    <@form.textInput "stockingOrder.id" "" "hidden"/>
                    <@form.textInput "stockingOrder.ver" "" "hidden"/>
                    <@form.textInput "stockingOrder.stockingStatus" "" "hidden"/>
                    <input type="hidden" id="shopId" value="${stockingOrder.shop.id}">
                    <input type="hidden" name="listData" id="listData">
                        <legend>库存盘点管理 -> 库存盘点</legend>
                    <div class="col-md-8 col-md-offset-2">
                        <div class="row">
                            <div class="col-md-5">
                                <@form.textInput "stockingOrder.shop.name" "class='form-control' readonly" "text" "盘点门店" />
                                <@form.textInput "stockingOrder.shop.id" "class='form-control' readonly" "hidden" "" />
                            </div>
                            <div class="col-md-5 col-md-offset-2">
                                <@form.textInput "stockingOrder.orderNumberView" "class='form-control' readonly" "text" "盘点单号" />
                                <input type="hidden" name="orderNumber" value="${stockingOrder.orderNumber}">
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-5">
                                <@form.textInput "stockingOrder.erpUser.username" "class='form-control' readonly" "text" "盘点人" />
                                <@form.textInput "stockingOrder.erpUser.id" "class='form-control' readonly" "hidden" "" />
                            </div>
                            <div class="col-md-5 col-md-offset-2">
                                <@form.textInput "stockingOrder.stockingDate" "class='form-control' readonly" "text" "盘点日期" />
                            </div>
                        </div>
                        <div class="row">
                            <strong>
                                温馨提示：由于盘点数据过多，建议先导出表格将盘点数据填写完整后,再进行系统录入！
                            </strong>
                        </div>
                        <br>
                        <legend><br></legend>
                        <div class="row">
                            <table id="shopBody" class="scroll" cellpadding="0" cellspacing="0"></table>
                        </div>
                        <div class="row" style="text-align: right;padding-right: 20px;">
                            盘前总价：<label id="beforeSum"></label>&nbsp;&nbsp;&nbsp;&nbsp; 盘后总价：<label id="afterSum"></label>
                        </div>
                        <br>
                        <div class="row" style="text-align: center;position: fixed;bottom: 5px;margin-left: 40px;">

                            <#if stockingOrder.stockingStatus == 0>
                                <@form.btn_save "onclick='subForm(0);'" "保存盘点信息"/>
                                <@form.btn_calculator "onclick='subForm(1);' style='margin-left:70px'" "结算完成盘点"/>
                                <@form.btn_print "onclick='alert(\"已经上报\");' style='margin-left:70px;disabled:true;' disabled='disabled'; id='upToBoss'" "上报盘点信息"/>
                                <@form.btn_print "onclick='exportExcel()' align='center' style='margin-left:70px'" "数据导出" />
                            </#if>
                             <#if stockingOrder.stockingStatus == 1>
                            <@form.btn_save "onclick='subForm(0,'保存盘点');' disabled='disabled'" "保存盘点信息"/>
                            <@form.btn_calculator "onclick='subForm(1,'完成盘点');' style='margin-left:70px' disabled='disabled'" "结算完成盘点"/>
                            <@form.btn_print "onclick='alert(\"上报成功\");' style='margin-left:70px;'  id='upToBoss'" "上报盘点信息"/>
                            <@form.btn_print "onclick='exportExcel()' align='center' style='margin-left:70px'" "数据导出" />
                        </#if>
                        </div>
                    </div>
                </form>
            </div>
        </div>

    </div>
    </@main.frame>

</#escape>