<#import "../macros/mainFrame.ftl" as main />
<#import "../macros/formMacros.ftl" as form />
<#import "/spring.ftl" as spring />

<#escape x as x?html>
<@main.frame>

<script type="text/javascript">
    $('#collapseCustomerPurchasedSuite').collapse('show');
    $(function () {
        <#--<#if  ></#if>-->
        if (!${user.checkAuthority(AUTHORITY.MANAGE_ORG_CUSTOMERPURCHASESUITE)?c}) {
            window.location = "/noauthority";
        } else {
            $('.Wdate').datepicker();
            $('.Wdate').datepicker("option",$.datepicker.regional["zh-TW"]);
            var url = '/customerpurchasesuite/list/data?keyWord=';
            url += "&shopId="+$("#shopId").val();
            showList(url);
        }
    });

    function showList(url) {
    $("#gridBody").jqGrid({
        url: url,
        colModel: [
            { label: '客户姓名', name: '5', width: 30, align:"center" },
            {name: '6', hidden: true},
            { label: '套餐名称', name: '7', width: 60, align:"center"},
            { label: '套餐定价', name: '10', width: 20, align:"center"},
            { label: '手机', name: '0', width: 30, align:"center"},
            { label: '办卡日期', name: '2', width: 40, align:"center",
                formatter:function(cellvalue){
                    if(cellvalue!=null && cellvalue!=""){
                        return formatterDate(cellvalue.substr(0,19)+ ".000Z");
                    }
                }},
            { label: '剩余天数', name: '8', width: 30, align:"center",
                formatter:function(cellvalue){
                    if (cellvalue <= 0) {
                        return "已过期"
                    }

                    return cellvalue;
                }},
            { label: '有效状态', name: '3', width: 30 ,align:"center",
                formatter:function(cellvalue, options, rowObject){
                    if(cellvalue){
                        return "有效";
                    } else {
                        return "无效";
                    }
                }
            },
            { label: '开卡门店', name: '4', width: 50, align:"center"},
            { label: '售卡人员', name: '9', width: 20, align:"center"},
            { label: '实际售价', name: '11', width: 25, sortable:false, align:"center"},
            { label: '折扣授权人', name: '12', width: 25, align:"center"},
            { label:'备注', name:'13', width: 40,align:"center"},
            {
                label: '操作', name: '3', width: 30, align: "center",
                formatter: function (cellvalue, options, rowObject) {
                    var viewName = "停用";
                    var modify = "";
//                    var modify = "<a onclick=\"update("+ rowObject["6"] +")\" href='#' style='text-decoration:underline;color:blue'>"+"修改"+"</a>&nbsp;&nbsp;&nbsp;";
                    if(cellvalue){
                        viewName = "停用";
                        modify += "<a onclick=\"changeStatus("+ rowObject["6"] +",'"+ viewName +"')\" href='#' style='text-decoration:underline;color:blue'>"+viewName+"</a>&nbsp;&nbsp;";
                    } else {
                        viewName = "启用";
                        modify += "<a onclick=\"changeStatus("+ rowObject["6"] +",'"+ viewName +"')\" href='#' style='text-decoration:underline;color:blue'>"+viewName+"</a>&nbsp;&nbsp;";
                    }

                    modify += "<a onclick='toDetail(" + rowObject["6"] + ")' href='#' style='text-decoration:underline;color:blue' >详细</a>"
                    return  modify;
                }
            }
        ],
        pager:'#toolBar',
        //multiselect:true,
        rownumbers: true
    });
}

    /**
     * 启停用
     * @param id
     */
    function changeStatus(id,viewName){

        if(confirm("是否确认"+ viewName +"!!")){
    //        $("#gridBody").jqGrid(trigger("reloadGrid"));
            $.get('/customerpurchasesuite/enabled?id=' + id, function(foo){
                if(foo){
                    $("#gridBody").trigger("reloadGrid", [{ page: 1}]);
                }

            });
        }
    }

    /**
     * 明细页面
     * @param id
     */
    function toDetail(id){
        window.location = "/customerpurchasesuite/todetail?id=" + id;
    }

    function queryList(){
        var url = '/customerpurchasesuite/list/data?keyWord=' + $("#keyWord").val();
        url += "&shopId="+$("#shopId").val();
        url += "&calDateStart="+$("#calDateStart").val();
        url += "&calDateEnd="+$("#calDateEnd").val();
        url = encodeURI(url,"UTF-8");
        jQuery("#gridBody").setGridParam({url:url}).trigger("reloadGrid", [{ page: 1}]);
    }

    function showOrganizationId() {
        var ids = $('#gridBody').jqGrid('getGridParam','selarrrow');
        if (ids <= 0) {
            alert("请先选择组织");
            return;
        }
        if (!confirm("确定显示组织？"))  {
            $('#gridBody').trigger('reloadGrid');
            return;
        }
        var organizationIds = "";
        for (i=0; i<ids.length; i++) {
            var organization = $('#gridBody').jqGrid('getRowData', ids[i]);
            organizationIds += organization.id;
            if (i != ids.length-1) organizationIds += ", ";
        }
        alert("选中的ID是："+organizationIds)
    }

    function update(id) {
        window.location = "/customerpurchasesuite/tosave?id=" + id;
    }
    function toadd() {

        $.get('/customerpurchasesuite/hasCustomSuite',function(data){
            if(data){
                $("#fm").submit();
            }else{
                alert("暂无会员套餐,请先添加会员套餐。")
            }
        },'json')


    }

    function exportExcel (){
        var url = '/customerpurchasesuite/excel/export?shopId=' + $("#shopId").val()
                + "&calDateStart=" + $("#calDateStart").val()
                + "&calDateEnd=" + $("#calDateEnd").val()
                + "&keyWord=" + $("#keyWord").val();
        url = encodeURI(url,"UTF-8");
        window.location = url;
    }
    function exportExcelDetail (){
        var url = '/customerpurchasesuite/detail/excel/export?shopId=' + $("#shopId").val()
                + "&calDateStart=" + $("#calDateStart").val()
                + "&calDateEnd=" + $("#calDateEnd").val()
                + "&keyWord=" + $("#keyWord").val();
        url = encodeURI(url,"UTF-8");
        window.location = url;
    }
</script>

<legend>会员套餐销售 </legend>
<div class="row">

    <form class="" id="fm" action='<@spring.url relativeUrl = "/customerpurchasesuite/tosave"/>' method="GET">

        <div class="col-md-12">

            <label  class="control-label">关键字: </label>
                <input type="text" name="keyWord" style="width: 400px" placeholder="可按姓名，手机等关键字进行搜索" id="keyWord" >
            &nbsp; &nbsp;&nbsp; &nbsp;&nbsp; &nbsp;
            <label  class="control-label">售卡门店: </label>&nbsp;
            <select name="shopId" id="shopId" style="width:170px">
                <#list shops as shop>
                    <option value="${shop.id}">${shop.name}</option>
                </#list>
            </select>&nbsp;
            <label class="control-label" id="timNow">售卡日期：
                <input type="text" name="calDateStart" id="calDateStart" class="Wdate" value="${calDateStart}"  readonly>&nbsp;
                - &nbsp;
                <input type="text" name="calDateEnd" id="calDateEnd" class="Wdate" value="${calDateEnd}"  readonly>
            </label>
                <@form.btn_search "onclick='queryList()'" "查 询"/>&nbsp;
                <@form.btn_add "onclick='toadd()'" "会员售卡"/>
                <@form.btn_pages "onclick='exportExcel();'" "顾客导出"/>
                <@form.btn_pages "onclick='exportExcelDetail();'" "剩余次数导出"/>
        </div>
        <#--<div class="col-md-3">

        </div>-->
    </form>
</div>
<br>
<table id="gridBody" class="scroll" cellpadding="0" cellspacing="0"></table>
<div id="toolBar"></div>

</@main.frame>
</#escape>