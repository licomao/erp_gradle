<#import "../macros/mainFrame.ftl" as main />
<#import "../macros/formMacros.ftl" as form />
<#import "/spring.ftl" as spring />

<#escape x as x?html>
    <@main.frame>

    <script type="text/javascript">
        $('#collapseOrg').collapse('show');
        $().ready(function () {
            if (!${user.checkAuthority(AUTHORITY.MANAGE_ORG_ROLEAUTHORIZATION)?c}) {
                window.location = "/noauthority";
            } else {
                showRoleListData();
            }
        });

        function showRoleListData() {
            var roleName = $("#roleName").val();
            var organizationid = $("#organization").val();

            $("#roleList").jqGrid({
                url: '/roleauthorization/list/data',
                postData: { roleName:roleName,organizationid:organizationid},
                colModel: [
                    { label: '角色名',index:'role', name: 'role', width: 100, align:"center"},
                    { label: '所属组织',index:'organization.name', name: 'organization.name', width: 100, align:"center" },
                    { label: '操作',  name: 'id', width: 100, align:"center",
                        formatter: function (cellvalue, options, rowObject) {
                            var modify = "<a href='####' onclick=\"changeRow(" + cellvalue + ")\"  style='text-decoration:underline;color:blue'>" + "修改" + "</a>";
                            var dele = "  <a href='####' onclick=\"deleteRow(" + cellvalue + ")\"  style='margin-left:30px;text-decoration:underline;color:blue'>" + "删除" + "</a>";
                            return modify + dele;
                        }
                    }
                ]
            });
        }

        function rate(cellvalue) {
            return (cellvalue * 100) + "%";
        }

        function reloadGrid() {
            var roleName = $("#roleName").val();
            var organization = $("#organization").val();

            $("#roleList").jqGrid('setGridParam',{
                postData: { roleName:roleName,organization:organization},
                page:1
            }).trigger("reloadGrid", [{ page: 1}]);
        }

        function addRole() {
            window.location = "/roleauthorization/tosave";
        }

        function changeRow(id) {
            $("#form").attr("action", "/roleauthorization/tosave");
            document.getElementById("rowid").value = id;
            $("#form").submit();
        }
        function deleteRow(id) {
            if(confirm("是否确认删除")){
                $("#form").attr("action", "/roleauthorization/delete");
                document.getElementById("rowid").value = id;
                $("#form").submit();
            }
        }

    </script>

    <legend>角色管理 -> 角色信息查询</legend>

    <div class="row" style="margin-bottom: 1%">

        <form  id="form" name="form" method="post">
            <input type="hidden" id="rowid" name="rowid" value=""/>
        </form>

        <div class="col-md-5">
            <label class="control-label"> 角色名： </label>
            <input  type="text" id="roleName" name="roleName" value=""/>
            &nbsp;&nbsp;
            <label class="control-label">组织： </label>
            <select  id="organization" name="organization" disabled="disabled">
                <option value= "${organization.id}">${organization.name}</option>
            </select>
        </div>
        <div class="col-md-4">
            <@form.btn_search "onclick='reloadGrid();'" "查 询" />
            &nbsp;
            <@form.btn_add "onclick='addRole();'" "新 增" />
        </div>
    </div>
    <div  class="row">
        <table id="roleList" class="scroll" cellpadding="0" cellspacing="0"></table>
        <div id="toolBar"></div>
    </div>
    </@main.frame>
</#escape>