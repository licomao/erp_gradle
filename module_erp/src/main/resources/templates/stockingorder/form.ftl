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
           var url = '/stockingorder/list/stock?shopId=' + $("#shopId").val();
            showList(url);
//            setTimeout("",5000);
        });

        function showSum(){
            var obj = $("#shopBody").jqGrid("getRowData");
            var sum = 0;
                jQuery(obj).each(function(){
                    sum += this['cost'] * this['number'];
                });
            document.getElementById("beforeSum").innerHTML = sum.toFixed(2) + " 元";
            document.getElementById("afterSum").innerHTML = 0 + " 元";
        }

        function showBeforeSum(oIp){
            if (validate(oIp)){
                var obj = $("#shopBody").jqGrid("getRowData");
                var sum = 0;
                var i = 1;
                jQuery(obj).each(function(){
                    $("#pSum_"+i).val(this['cost'] * $("#pNum_"+i).val());
                    sum += this['cost'] * $("#pNum_"+i).val();
                    i++;
                });
                document.getElementById("afterSum").innerHTML = sum.toFixed(2) + " 元";
            }
        }

        function getShopInfo() {
            var url = "/stockingorder/list/stock?name=" + $("#shopName").val() + "&rootCategory=" + $("#rootCategory").val();
            jQuery("#shopBody").setGridParam({url:url}).trigger("reloadGrid", [{ page: 1}]);
        }
        function showList(url) {
            var i = 1;
            $("#shopBody").jqGrid({
                url: url,
                pager : '#gridpager',
                rowNum:99999,
                colModel: [
                    { name: 'viewId', hidden:true},
                    { label: '商品名', name: 'name', width: 100, align:"center" },
                    { label: '品牌', name: 'brandName', width: 40, align:"center"},
                    { label: '条形码', name: 'barCode', width: 50, align:"center"},
                    { label: '顶级分类', name: 'rootCategory', width: 40,align:"center", formatter: "select", editoptions:{value:"1:机油;2:机滤;3:轮胎;4:电瓶;5:电子类产品;6:美容类产品;7:汽车用品;8:养护产品;9:耗材类产品;10:灯具类产品;" +
                    "11:雨刮类产品;12:发动机配件类;13:底盘配件类;14:变速箱类;15:电气类;16:车身覆盖类;17:服务类;0:临时分类"}},
                    { label: '成本(元)', name: 'cost', width: 50, align:"center" },
                    { label: '库存数量', name: 'number', width: 40, align:"center"},
                    { label: '盘后数量', name: 'isDistribution', width: 40 ,align:"center",
                        formatter:function(cellvalue, options, rowObject){
                              return "<input type='text' class='input-sm form-control' onchange='showBeforeSum(this);checkColor(this,"+rowObject['number']+")' value=0 id='pNum_"+ i +"'/>";
                        }
                    },
                    { label: '盘后总价', name: 'isDistribution', width: 30 ,align:"center",
                        formatter:function(cellvalue, options, rowObject){
                            var str = "<input type='text' class='input-sm form-control' value='0' disabled id='pSum_"+ i +"'/>";
                            i++;
                            return str;
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
            var row = 1;
            jQuery(obj).each(function(){
                oneData += "" + this['viewId'] + "," + this['cost'] + "," + $("#pNum_"+ row).val() + "," + this['number'] + ";";
                row ++;
            });

            $("#listData").val(oneData);
            if(confirm("是否确认保存!")){
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
                <form class="" id="fm" action='<@spring.url relativeUrl = "/stockingorder/create"/>'   method="post">
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
                                <@form.textInput "stockingOrder.stockingDate" "class='form-control Wdate' readonly" "text" "盘点日期" />
                            </div>
                        </div>
                        <div class="row">
                            <strong>
                            温馨提示：由于盘点数据过多，建议先导出表格将盘点数据填写完整后,再进行系统录入！
                                </strong>
                            </div>
                        <legend>&nbsp;</legend>
                        <div class="row">
                            <table id="shopBody" class="scroll" cellpadding="0" cellspacing="0"></table>
                        </div>
                        <div class="row" style="text-align: right;padding-right: 20px;">
                            盘前总价：<label id="beforeSum"></label>&nbsp;&nbsp;&nbsp;&nbsp; 盘后总价：<label id="afterSum"></label>
                        </div>
                        <br>
                        <div class="row" style="text-align: center;position: fixed;bottom: 5px;margin-left: 40px;">
                            <@form.btn_save "onclick='subForm(0);'  " "保存盘点信息"/>
                            <@form.btn_calculator "onclick='subForm(1);' style='margin-left:70px'" "结算完成盘点"/>
                            <@form.btn_print "onclick='alert(\"已经上报\");' style='margin-left:70px;disabled:true;' disabled='disabled'; id='upToBoss'" "上报盘点信息"/>
                            <@form.btn_print "onclick='exportExcel()' align='center' style='margin-left:70px'" "数据导出" />
                        </div>
                    </div>
                </form>
            </div>
        </div>

    </div>
    </@main.frame>

</#escape>