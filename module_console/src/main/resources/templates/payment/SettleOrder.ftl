<#import "../macros/mainFrame.ftl" as main />
<#import "../macros/formMacros.ftl" as form />
<#import "/spring.ftl" as spring />
<#escape x as x?html>
    <@main.frame>

    <style type="text/css">
        .one, .two{float:left; font-size:14px;color:midnightblue; width:45%; text-align: center; padding: 10px;}
        .one{ border:1px solid #000;}
        .two{ border:1px solid #00f;}
        table.gridtable { font-family: verdana,arial,sans-serif; font-size: 13px;  color: #333333; border-width: 1px; border-color: #666666; border-collapse: collapse;margin: auto }
        table.gridtable th { border-width: 1px; padding: 8px; border-style: solid; border-color: #666666; background-color: #dedede; text-align: center;font-size: 15px; }
        table.gridtable td { border-width: 1px; padding: 8px; border-style: solid; border-color: #666666; background-color: #ffffff; font-weight:600;}
    </style>

    <script type="text/javascript">
        $(function (){
              $("#timNow").html(getNowFormatDate());
              if($("#hiIsClosed").val() == 1){
                        $("#closeShop").attr('disabled',true);
              }else{
                        $("#closeShop").attr('disabled',false);
              }
              if($("#hiIsCloseSuccess").val() == 1){
                        alert("结算成功");
                        $("#closeShop").attr('disabled',true);
              }
         });

        function getNowFormatDate() {
            var date = new Date();
            var seperator1 = "-";
            var seperator2 = ":";
            var month = date.getMonth() + 1;
            var strDate = date.getDate();
            if (month >= 1 && month <= 9) {
                month = "0" + month;
            }
            if (strDate >= 0 && strDate <= 9) {
                strDate = "0" + strDate;
            }
            var currentdate = date.getFullYear() + seperator1 + month + seperator1 + strDate;
            return currentdate;
        }

    </script>

    <div style="width: 100% ; text-align: center; "><label for="time" class="control-label" id="timNow">今日时间</label></div>
    <div style="width: 100%; text-align: center;">
        <div class="one">
            <table class="gridtable">
                <tr><th colspan ="2">当日结算汇总</th></tr>
                <tr><td>收入类型</td> <td>实收</td> </tr>
                <tr><td>POS机收入 ></td> <td>0</td> </tr>
                <tr><td>现金收入 ></td> <td>0</td> </tr>
                <tr><td>合计 ></td> <td>0</td> </tr>
            </table><br/>

            <input class="form-control" type="hidden"  id="hiIsClosed" value="${isClosed}">
            <input class="form-control" type="hidden"  id="hiIsCloseSuccess" value="${isCloseSuccess}">
            <form action='/payment/settleaccounts' method="get">
                <button class="btn btn-primary active" id="closeShop" type="submit">关店结算</button>
            </form>


        </div>
        <div class="two">
            <table class="gridtable">
                <tr><th colspan ="2">当日结算明细</th></tr>
                <tr><td>销售项目</td><td>实收</td></tr>
                <tr><td>洗车 </td><td>0</td></tr>
                <tr> <td>美容 </td><td>0</td></tr>
                <tr> <td>保养 </td><td>0</td></tr>
                <tr><td>配件 </td><td>0</td></tr>
                <tr> <td>精品用品 </td> <td>0</td></tr>
                <tr><td>维修 </td> <td>0</td></tr>
                <tr> <td>会员 </td> <td>0</td></tr>
                <tr><td>外加工 </td><td>0</td></tr>
                <tr> <td>钣金油漆 </td> <td>0</td></tr>
                <tr><td>耗材 </td><td>0</td></tr>
                <tr><td>其它 </td> <td>0</td> </tr>
                <tr> <td>合计 </td> <td>0</td>
                </tr>
            </table>
        </div>
    </div>
    </@main.frame>

</#escape>