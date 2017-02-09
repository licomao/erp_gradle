<#import "../macros/mainFrame.ftl" as main />
<#import "../macros/formMacros.ftl" as form />
<#import "/spring.ftl" as spring />

<#escape x as x?html>
    <@main.frame>

    <script type="text/javascript">
        $().ready(function () {
            showAccountList();
        });

        function showAccountList() {
            var accountName = $("#accountName").val();
            var organization = $("#organization").val();
            var shop = $("#shop").val();

            $("#accountList").jqGrid({
                url: '/account/list/data',
                postData: { accountName:accountName,organization:organization, shop:shop },
                colModel: [
                    { label: '账户名', index:'username', name: 'username', width: 100, align:"center" },
                    { label: '电话',index:'phone', name: 'phone', width: 150, align:"center" },
                    { label: '所属组织',index:'organizationName', name: 'organization.name', width: 100, align:"center" },
                    { label: '角色信息',index:'role', name: 'role.role', width: 100, align:"center" },
                    { label: '创建时间',index:'showedDate', name: 'showedDate', width: 100, align:"center" },
                    { label: '操作',  name: 'id', width: 100, align:"center",
                        formatter: function (cellvalue, options, rowObject) {
                            var modify = "<a onclick=\"changeRow(" + cellvalue + ")\" style='text-decoration:underline;color:blue'>" + "修改" + "</a>";
                            var dele = "  <a onclick=\"deleteRow(" + cellvalue + ")\"  style='text-decoration:underline;color:blue'>" + "删除" + "</a>";
                            return modify + dele;
                        }
                    }
                ]
            });
        }

        function reloadGrid() {
            var accountName = $("#accountName").val();
            var organization = $("#organization").val();
            var shop = $("#shop").val();
            $("#accountList").jqGrid('setGridParam',{
                postData: { accountName:accountName,organization:organization, shop:shop},
                page:1
            }).trigger("reloadGrid", [{ page: 1}]);
        }

        function addAccount() {
            window.location = "/account/tosave";
        }

        function changeRow(id) {
            window.location = "/account/tosave?id=" + id;
        }
        function deleteRow(id) {
            window.location = " /account/delete?id=" + id;
        }

    </script>

    <div class="row" style="margin-bottom: 1%">
        <div class="col-md-1">
            <label class="control-label"> 账户： </label>
        </div>
        <div class="col-md-2">
            <input class="form-control" type="search" id="accountName" name="accountName" value=""/>
        </div>
        <div class="col-md-1">
            <label class="control-label">组织： </label>
        </div>
        <div class="col-md-3">
            <select class="form-control" type="search" id="organization" name="organization" disabled>
                <option value= "${user.organization.id}">${user.organization.name}</option>
            </select>
        </div>
        <div class="col-md-1">
            <label class="control-label">门店： </label>
        </div>
        <div class="col-md-2">
            <select class="form-control" type="search" id="shop" name="shop" >
                <#list shopList as shop>
                    <option value= "${shop.id}">${shop.name}</option>
                </#list>
            </select>
        </div>
        <div class="col-md-1">
            <button class="btn btn-primary" onclick="reloadGrid()">搜 索</button>
        </div>
        <div class="col-md-1">
            <button class="btn btn-primary" onclick="addAccount()" >新 增</button>
        </div>
    </div>
    <div  class="row">
        <table id="accountList" class="scroll" cellpadding="0" cellspacing="0"></table>
        <div id="toolBar"></div>
    </div>
    </@main.frame>
</#escape>