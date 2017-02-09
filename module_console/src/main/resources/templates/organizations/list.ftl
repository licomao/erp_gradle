<#import "../macros/mainFrame.ftl" as main />
<#import "../macros/formMacros.ftl" as form />
<#import "/spring.ftl" as spring />

<#escape x as x?html>
<@main.frame>

<script type="text/javascript">
    $('#collapseOrgs').collapse('show');
$(function () {
    var url = "/organizations/list/data"
    url += "?id=" + $("#userId").val() + "&name=" + $("#orgName").val();
    showList(url);
});

    function showList(url) {
    $("#gridBody").jqGrid({
        url: url,
        colModel: [
            { label: '注册号', name: 'serialNum', align:"center",width: 70 },
            { label: '名称', name: 'name', align:"center",width: 80 },
            { label: '税号', name: 'taxNumber', align:"center",width: 70},
            { label: '银行帐号', name: 'bankAccount',align:"center", width: 100},
            { label: '开户行', name: 'bankName',align:"center", width: 80},
            { label: '联系人', name: 'contact',align:"center", width: 40},
            { label: '联系人电话', name: 'contactPhone',align:"center", width: 60},
            { label: '试用账户', name: 'tried', width: 30 ,align:"center",
                formatter:function(cellvalue, options, rowObject){
                  if(cellvalue){
                      return "是";
                  }else{
                      return "否";
                  }
                }
            },
            { label: '门店配额', name: 'shopQuota',align:"center", width: 40 },
            { label: '操作', name: 'id', width: 75 ,align:"center",
                formatter:function(cellvalue, options, rowObject){
                    var modify = "<a href='####' onclick=\"changeOrganizationInfo("+ cellvalue +")\" style='text-decoration:underline;color:blue'>"+"修改"+"</a>";
                    return modify;
                }
            }
        ],
        rownumbers:true
    });
}

    function queryList(){
        var url = "/organizations/list/data"
        url += "?id=" + $("#userId").val() + "&name=" + $("#orgName").val();
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

    function changeOrganizationInfo(id) {
        window.location = "/organizations/new?id=" + id;
    }

    function deleteOrganization(id) {
        window.location = "/organizations/delete?ids="+ id;
    }

    function addOrg() {
        window.location = "/organizations/new";
    }

</script>



<div class="row">
    <legend>组织管理 -> 组织查询</legend>
    <div class="col-md-5">
        <label for="username" class="control-label">名称:</label>
        <input type="text" name="orgName" id="orgName">
        <input type="hidden" id="userId" value="${erpUser.id}">

    </div>
    <div class="col-md-5">
        <@form.btn_search "onclick='queryList();'" "查 询"/>
            <@form.btn_add "onclick='addOrg();'" "创建组织"/>

    </div>
</div>
<br>
<table id="gridBody" class="scroll" cellpadding="0" cellspacing="0"></table>
<div id="toolBar"></div>

</@main.frame>
</#escape>