<#import "../macros/mainFrame.ftl" as main />
<#import "../macros/formMacros.ftl" as form />
<#import "/spring.ftl" as spring />

<#escape x as x?html>
<@main.frame>

<script type="text/javascript">
    $('#collapseShop').collapse('show');
    $(function () {
        if (!${user.checkAuthority(AUTHORITY.MANAGE_ORG_FIXEDASSET)?c}) {
            window.location = "/noauthority";
        } else {
            var url = '/fixedasset/list/data?name=' + $("#name").val();
            if ($("#shopId").val() != "") {
                url += "&shop.id=" + $("#shopId").val();
            }
            if ($("#assetStatus").val() != "") {
                url += "&assetStatus=" + $("#assetStatus").val();
            }
            url = encodeURI(url,"UTF-8");
            showList(url);
        }
    });

    function showList(url) {
    $("#gridBody").jqGrid({
        url: url,
        colModel: [
            { label: '固定资产名称', name: 'name', width: 70, align:"center" },
            { label: '型号', name: 'model', width: 50, align:"center"},
            { label: '单价', name: 'price', width: 40, align:"center"},
            { label: '数量', name: 'number', width: 30,align:"center"},
            { label: '合计', name: 'sum', width: 30,align:"center" ,
                formatter:function(cellvalue){
                    return parseFloat(cellvalue).toFixed(2);
                }
            },
            { label: '使用状态', name: 'assetStatus', width: 50, align:"center",
                formatter:function(cellvalue, options, rowObject){
                    if(cellvalue == 0) {
                        return "在用";
                    } else {
                        return "报废";
                    }
                }
            },
            { label: '所属门店', name: 'shop.name', width: 80, align:"center"},
            { label: '更新账户', name: 'updatedBy', width: 80, align:"center"},
            { label: '操作', name: 'id', width: 75 ,align:"center",
                formatter:function(cellvalue, options, rowObject){
                    var o = options;
                    var modify = "<a onclick=\"update("+ cellvalue +")\" href='#' style='text-decoration:underline;color:blue'>"+"修改"+"</a>";
                    return modify;
                }
            }
        ],
        //multiselect:true,
        rownumbers: true
    });
}

    function queryList(){
        var url = '/fixedasset/list/data?name=' + $("#name").val();
        if ($("#shopId").val() != "") {
            url += "&shop.id=" + $("#shopId").val();
        }
        if ($("#assetStatus").val() != "") {
            url += "&assetStatus=" + $("#assetStatus").val();
        }
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
        window.location = "/fixedasset/tosave?id=" + id;
    }

    function toexport(){
        var url = '/fixedasset/excel/export?name=' + $("#name").val();
        if ($("#shopId").val() != "") {
            url += "&shop.id=" + $("#shopId").val();
        }
        if ($("#assetStatus").val() != "") {
            url += "&assetStatus=" + $("#assetStatus").val();
        }
        url = encodeURI(url,"UTF-8");
        window.location = url;
    }


    function toadd() {
        $("#fm").submit();
    }
</script>

<legend>固定资产管理 -> 固定资产查询</legend>

<div class="row">
    <form class="" id="fm" action='<@spring.url relativeUrl = "/fixedasset/tosave"/>' method="GET">
        <div class="col-md-7">
            <label  class="control-label">门店: </label>&nbsp;
            <select name="shopId" id="shopId">
                <#list shops as shop>
                    <option value="${shop.id}">${shop.name}</option>
                </#list>
            </select>
            &nbsp;
            <label class="control-label">固定资产名称: </label>&nbsp;
            <input type="text" name="name" id="name">&nbsp;
            <label class="control-label">资产状态: </label>&nbsp;
            <select name="assetStatus" id="assetStatus">
                <option value="99">请选择</option>
                <option value="0">在用</option>
                <option value="1">报废</option>
            </select>
        </div>
        <div class="col-md-4">
            <@form.btn_search "onclick='queryList()'" "查 询"/>&nbsp; &nbsp;
            <@form.btn_add "onclick='toadd()'" "新 增"/>
            <@form.btn_pages "onclick='toexport()'" "导出资产数据"/>
        </div>
    </form>
</div>
<br>
<table id="gridBody" class="scroll" cellpadding="0" cellspacing="0"></table>
<div id="toolBar"></div>

</@main.frame>
</#escape>