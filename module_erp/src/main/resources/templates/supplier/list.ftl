<#import "../macros/mainFrame.ftl" as main />
<#import "../macros/formMacros.ftl" as form />
<#import "/spring.ftl" as spring />

<#escape x as x?html>
    <@main.frame>

    <script type="text/javascript">
        $('#collapseShop').collapse('show');
        $(function () {
            if (!${user.checkAuthority(AUTHORITY.MANAGE_ORG_SUPPLIER)?c}) {
                window.location = "/noauthority"
            } else {
                $("#SelectOrgs").change(function(){selectChange();});
                $("#SelectOrgs ").val($("#hOrgid ").val());  //初始化select
                var url = "/supplier/list/data";
                url += "?name=" + $("#name").val() + "&organization.id=" + $("#orgId").val();
                url += "&deleted=" + $("#deleted").val();
                url = encodeURI(url,"UTF-8");
                showList(url);
            }
        });

        function showList(url) {
            $("#gridBody").jqGrid({
                url:url,
                colModel: [
                    { label: '供应商名称', name: 'name', width: 75,align:"center" },
                    { label: '联系方式', name: 'contactInfo', width: 60,align:"center"},
                    { label: '邮箱', name: 'email', width: 60,align:"center"},
                    { label: '传真', name: 'fax', width: 60,align:"center"},
                    { label: '状态', name: 'deleted', width: 30,align:"center",
                        formatter:function(cellvalue, options, rowObject){
                            if (cellvalue){
                                return "无效";
                            }else{
                                return "有效";
                            }
                        }
                    },
                    { label: '供应商描述', name: 'description', width: 200,align:"center"},
                    { label: '操作', name: 'id', width: 30 ,align:"center",
                        formatter:function(cellvalue, options, rowObject){
                            var modify = "<a href='####' onclick=\"editById("+ cellvalue +")\" style='text-decoration:underline;color:blue'>"+"修改"+"</a>";
                            var deleted = "";
                            if (rowObject['deleted']){
                                deleted += "<a href='####' onclick=\"deleteById("+ cellvalue +"," + options.rowId + ")\" style='margin-left:30px;text-decoration:underline;color:blue'>"+"启用"+"</a>";
                            } else {
                                deleted += "<a href='####' onclick=\"deleteById("+ cellvalue +"," + options.rowId + ")\" style='margin-left:30px;text-decoration:underline;color:blue'>"+"禁用"+"</a>";
                            }
                            return modify + deleted;
                        }
                    }
                ],
                rownumbers:true
            });
        }

        function queryList() {
            var url = "/supplier/list/data";
            url += "?name=" + $("#name").val() + "&organization.id=" + $("#orgId").val();
            url += "&deleted=" + $("#deleted").val();
            url = encodeURI(url,"UTF-8");
            jQuery("#gridBody").setGridParam({url:url}).trigger("reloadGrid", [{ page: 1}]);
        }

        function editById(id) {
            window.location = "/supplier/new?id=" + id;
        }
        function deleteById(id,rowId) {
            if (confirm("是否确认修改!")){
                $.get("/supplier/delete/" + id, function (data) {
                    alert("操作成功");
                    $("#gridBody").jqGrid("delRowData", rowId);
                });
            }
        }
        function newSupplier()  {
            window.location = "/supplier/new" ;
        }
    </script>

<legend>供应商管理 -> 供应商信息查询</legend>

<div class="row">
    <div class="col-md-6">
        <label for="tittle" class="control-label">供应商名称:</label>&nbsp;
        <input  type="text" id="name"/>&nbsp;
        <label for="publisher" class="control-label">所属组织:</label>&nbsp;
        <select name="orgId" id="orgId" >
            <#list orgs as org >
                <option value ="${org.id}"> ${org.name}</option>
            </#list>
        </select>&nbsp;
        <label  class="control-label">是否有效:</label>&nbsp;
        <select name="deleted" id="deleted" >
            <option value ="false">有效</option>
            <option value ="true">无效</option>
        </select>&nbsp;
    </div>
    <div class="col-md-5">
        <@form.btn_search "onclick='queryList()'" "查 询"/>&nbsp;&nbsp;&nbsp;&nbsp;
        <@form.btn_add "onclick='newSupplier()'" "新增供应商"/>
    </div>
</div>
<br>
<table id="gridBody" class="scroll" cellpadding="0" cellspacing="0"></table>
<div id="toolBar"></div>

    </@main.frame>
</#escape>