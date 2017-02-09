<#import "../macros/mainFrame.ftl" as main />
<#import "../macros/formMacros.ftl" as form />
<#import "/spring.ftl" as spring />

<#escape x as x?html>
<@main.frame>

<script type="text/javascript">
    $('#collapseStock').collapse('show');
    $(function () {
        if (!${user.checkAuthority(AUTHORITY.MANAGE_ORG_STOCKINGORDER)?c}) {
            window.location = "/noauthority"
        } else {
            var url = '/stockingorder/list/data?orderNumberView=' + $("#orderNumberView").val()
                    + "&shop.id=" + $("#shopId").val() + "&stockingDate=" + $("#stockingDate").val();
            showList(url);
            $('.Wdate').datepicker();
            $('.Wdate').datepicker("option",$.datepicker.regional["zh-TW"]);
            $('#stockingDate').datepicker( "setDate", $('#stockingDate').val());
        }
    });


    function showList(url) {
        $("#gridBody").jqGrid({
            url: url,
            colModel: [
                { label: '单据编号', name: 'orderNumberView', width: 100, align:"center" },
                { label: '门店', name: 'shop.name', width: 80, align:"center"},
                { label: '盘点人', name: 'erpUser.realName', width: 60, align:"center"},
                { label: '盘点日期', name: 'stockingDate', width: 50,align:"center"
                    ,
                    formatter:function(cellvalue, options, rowObject) {
                        if(cellvalue!=null){
                            return formatterDateWithSecond(cellvalue);
                        } else {
                            return "";
                        }
                    }
                },
                { label: '盘点状态', name: 'stockingStatus', width: 40, align:"center",
                    formatter: function (cellvalue, options, rowObject) {
                        if(cellvalue == 0) {
                            return "未完成";
                        } else {
                            return "已完成";
                        }
                    }
                },
                { label: '操作', name: 'id', width: 50, align:"center",
                    formatter:function(cellvalue, options, rowObject){
                        var stockingStatus = rowObject["stockingStatus"];
                        if(stockingStatus == 0){
                            return "<a onclick='toEdit("+ cellvalue +",0)' href='#' style='text-decoration:underline;color:blue'>"+"继续盘点"+"</a>"
                                   + "<a onclick='cancel("+ cellvalue +")' href='#' style='margin-left:30px;text-decoration:underline;color:blue'>"+"取消盘点"+"</a>"
                                    ;
                        } else {
                            return "<a onclick='toEdit("+ cellvalue +",1)' href='#' style='margin-left:30px;text-decoration:underline;color:blue'>"+"查看明细"+"</a>";
                        }
                    }

                }

            ],
            //multiselect:true,
            rownumbers: true
        });
    }

    function toEdit(id,doType) {
        window.open( "/stockingorder/edit?id=" + id + "&doType=" + doType);
    }

    function cancel(id){
        $.ajax( {
            url:"/stockingorder/cancel",
            data:{
                id : id
            },
            type:'get',
            success:function(data) {
                if(data.msg == true ){
                    alert("取消成功！");
                    queryList();
                }
            },
            error : function() {
                alert("操作异常！");
            }
        });
    }

    function queryList(){
        var url = '/stockingorder/list/data?orderNumberView=' + $("#orderNumberView").val()
                + "&shop.id=" + $("#shopId").val() + "&stockingDate=" + $("#stockingDate").val();
        jQuery("#gridBody").setGridParam({url:url}).trigger("reloadGrid", [{ page: 1}]);
    }

    function toStocking(){
        window.location = "/stockingorder/tostocking";
    }


</script>


<div class="row">
    <legend>库存盘点管理 -> 库存盘点查询</legend>
    <form class="" id="fm" action='<@spring.url relativeUrl = "/fixedasset/tosave"/>' method="GET">
        <div class="col-md-6">
            <label  class="control-label">盘点单号: </label>
            <input type="text" name="orderNumberView" id="orderNumberView">&nbsp;

            &nbsp;
            <label  class="control-label">所属门店: </label>
            <select name="shopId" id="shopId">
                <#list shops as shop>
                    <option value="${shop.id}">${shop.name}</option>
                </#list>
            </select>
            &nbsp;
            <label  class="control-label">盘点日期: </label>
            <input type="text" class="Wdate" name="stockingDate" id="stockingDate" readonly>&nbsp;
        </div>
        <div class="col-md-4">
            <@form.btn_search "onclick='queryList();'" "查 询"/>&nbsp;&nbsp;&nbsp;
            <@form.btn_refresh "onclick='toStocking();'" "开始盘点"/>
        </div>
    </form>
</div>
<br>
<table id="gridBody" class="scroll" cellpadding="0" cellspacing="0"></table>
<div id="toolBar"></div>

</@main.frame>
</#escape>