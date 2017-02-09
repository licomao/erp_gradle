<#import "../macros/mainFrame.ftl" as main />
<#import "../macros/formMacros.ftl" as form />
<#import "/spring.ftl" as spring />
<#escape x as x?html>
    <@main.frame>

    <script type="text/javascript">

     $(function () {
         $("#gridBody").jqGrid({
             url: '/payment/list/data?mobile=1',
             colModel: [
                 { label: 'ID', name: 'id', hidden:true },
                 { label: '预约单号', name: 'id', width: 75 ,align:"center",
                     formatter:function(cellvalue, options, rowObject){
                     return "<a onclick=\"viewInfo("+ cellvalue +")\" style='text-decoration:underline;color:blue'>"+cellvalue+"</a>";
                     }
                 },
                 { label: '姓名', name: 'customer.realName', width: 90 },
                 { label: '手机', name: 'customer.mobile', width: 100 },
                 { label: '预约门店', name: 'shop.name', width: 80},
                 { label: '预约时间', name: 'appointmentDate', width: 80,
                     editable:false,formatter:"date",formatoptions: {srcformat:'Y-m-d H:i:s',newformat:'Y-m-d H:i:s'}},
                 { label: '付款记录', name: 'payment.appAmount', width: 110},
                 { label: '来源', name: 'source', width: 80}
             ],
             multiselect:true
         });
     });


     function queryOrder(){
         $("#f1").submit();
     }
     function deletOrder() {
         var ids = $("#gridBody").getGridParam('selarrrow');
         if (ids <= 0) {
             alert("请先选择单子");
             return;
         }
         window.location = "/payment/presaledelete?ids=" + idstr;
     }

        function viewInfo(cellvalue){
            var wstyle = "height=600, width=800,toolbar=no,top=80px, left=40px ,scrollbars=none,menubar=no";
            OpenWindow=window.open("/payment/presaledtal?id="+cellvalue , "newwin", wstyle);
        }

    </script>

    <form  id="f1" name="f1" action='<@spring.url relativeUrl = "/payment/query"/>' method="post">

        <div class="row">
            <div class="col-md-5 col-md-offset-2">
                <div class="col-md-5">
                    <label for="username" class="control-label">手机号</label>
                    <input class="form-control" type="text" name="mobile" id="mobile">
                </div>
                <div class="col-md-5 col-md-offset-2">
                    <label for="username" class="control-label">预约单号</label>
                    <input class="form-control" type="text" name="id" id="id">
                </div>
            </div>

            <div class="row">
                <div class="col-md-5 col-md-offset-2">
                    <label for="username" class="control-label">预约门店</label>
                    <select name="shops" id="shops" >
                        <option value ="all"> 全部 </option>
                        <#list shops as shop >
                            <option value ="${shop.id}"> ${shop.name}</option>
                        </#list>
                    </select>
                    <button class="btn btn-primary active" onclick="queryOrder()">查询</button>
                    <button class="btn btn-primary active" onclick="">开单</button>
                    <button class="btn btn-primary active" onclick="deletOrder()">删除</button>
                </div>
            </div>
        </div>
    </form>
    <table id="gridBody" class="scroll" cellpadding="0" cellspacing="0"></table>
    <div id="toolBar"></div>

    </@main.frame>

</#escape>