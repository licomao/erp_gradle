<#import "../macros/mainFrame.ftl" as main />
<#import "../macros/formMacros.ftl" as form />
<#import "/spring.ftl" as spring />

<#escape x as x?html>
<@main.frame>
<script src="/javascripts/jquery-ui-1.9.2.min.js" type="text/javascript"></script>
<script src="/javascripts/jquery.ui.widget.js" type="text/javascript"></script>
<script src="/javascripts/cndate.js" type="text/javascript"></script>
<script type="text/javascript">
    $('#collapseShop').collapse('show');
    $(function () {
            if (!${user.checkAuthority(AUTHORITY.MANAGE_ORG_MATERIALORDER)?c}) {
                window.location = "/noauthority";
            } else {
                var url = '/material/list/data?orderNumView=' + $("#orderNumView").val()
                        + "&useDateStart=" + $("#useDateStart").val()
                        + "&useDateEnd=" + $("#useDateEnd").val()
                        + "&deleted=" + $("#deleted").val();
                if ($("#shopId").val() != "") {
                    url += "&shop.id=" + $("#shopId").val();
                }
                showList(url);
                $('.Wdate').datepicker();
                $('.Wdate').datepicker("option",$.datepicker.regional["zh-TW"]);
                $('#useDate').datepicker( "setDate", $('#useDate').val());
            }
    });

    function showList(url) {
    $("#gridBody").jqGrid({
        url: url,
        colModel: [
            { label: '领用单号', name: 'orderNumView', width: 70, align:"center" },
            { label: '领用门店', name: 'shop.name', width: 50, align:"center"},
            { label: '领用人', name: 'erpUser.username', width: 40, align:"center"},
            { label: '领用日期', name: 'useDate', width: 30,align:"center",
                formatter:function(cellvalue, options, rowObject) {
                    if(cellvalue!=null){
                        return formatterDate(cellvalue);
                    } else {
                        return "";
                    }
                }
            },
            { label: '单据状态', name: 'deleted', width: 50, align:"center",
                formatter:function(cellvalue, options, rowObject){
                    var status = "";
                    if (!cellvalue) {
                        status = "有效";
                    } else {
                        status = "无效";
                    }
                    return status;
                }
            },
            { label: '操作', name: 'id', width: 60 ,align:"center",
                formatter:function(cellvalue, options, rowObject){
                    var deleted = rowObject["deleted"];
                    var show = "<a onclick=\"show("+ cellvalue +")\" href='#' style='text-decoration:underline;color:blue'>"+"查 看"+"</a>";
                    var modify = "";
                    if (!deleted){
                        modify = "<a onclick=\"deleted("+ cellvalue +",true)\" href='#' style='margin-left:30px;text-decoration:underline;color:blue'>"+"作 废"+"</a>";
                    } else {
                        modify = "<a onclick=\"deleted("+ cellvalue +",false)\" href='#' style='margin-left:30px;text-decoration:underline;color:blue'>"+"启 用"+"</a>";
                    }
                    return show +  modify;
                }
            }
        ],
        //multiselect:true,
        rownumbers: true
        });
    }

    function deleted(id,flag){
        $.ajax( {
            url:"/material/savedelete",
            data:{
                id : id ,
                deleted : flag
            },
            type:'get',
            success:function(data) {
                if(data.msg == true ){
                    alert("修改成功！");
                    queryList();
                }
            },
            error : function() {
                alert("操作异常！");
            }
        });
    }

    function queryList(){
        var url = '/material/list/data?orderNumView=' + $("#orderNumView").val()
                + "&useDateStart=" + $("#useDateStart").val()
                + "&useDateEnd=" + $("#useDateEnd").val()
                + "&deleted=" + $("#deleted").val();
        if ($("#shopId").val() != "") {
            url += "&shop.id=" + $("#shopId").val();
        }
        jQuery("#gridBody").setGridParam({url:url}).trigger("reloadGrid", [{ page: 1}]);
    }

    function show(id) {
        window.open ("/material/tosave?id=" + id);
    }
    function toadd() {
        $("#fm").submit();
    }
</script>

<style></style>

<legend>领用单管理 -> 耗材领用查询</legend>

<div class="row">
    <form class="" id="fm" action='<@spring.url relativeUrl = "/material/tosave"/>' method="GET">
        <div class="col-md-12">
            <label  class="control-label">领用单号: </label>&nbsp;
            <input type="text" name="orderNumView" id="orderNumView">&nbsp;
            <label class="control-label">领用日期: </label>&nbsp;
            <input type="text" value="${useDateStart}" class="Wdate" size="10" readonly name="useDateStart" id="useDateStart">&nbsp;-&nbsp;
            <input type="text" value="${useDateEnd}" class="Wdate" size="10" readonly  name="useDateEnd" id="useDateEnd">
            <label class="control-label">单据状态: </label>&nbsp;
            <select name="deleted" id="deleted">
                <option value="false">有效</option>
                <option value="true">无效</option>
            </select>&nbsp;
            <label  class="control-label">领用门店: </label>&nbsp;
            <select name="shopId" id="shopId">
                <#list shops as shop>
                    <option value="${shop.id}">${shop.name}</option>
                </#list>
            </select>&nbsp;
            <@form.btn_search "onclick='queryList()'" "查 询"/>&nbsp;
            <@form.btn_add "onclick='toadd()'" "领 用"/>
        </div>
        <#--<div class="col-md-2 ">-->
           <#---->
        <#--</div>-->
    </form>
</div>
<br>
<table id="gridBody" class="scroll" cellpadding="0" cellspacing="0"></table>
<div id="toolBar"></div>

</@main.frame>
</#escape>