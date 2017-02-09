<#import "../macros/mainFrame.ftl" as main />
<#import "../macros/formMacros.ftl" as form />
<#import "/spring.ftl" as spring />

<#escape x as x?html>
    <@main.frame>

    <script type="text/javascript">
        $('#collapseOrg').collapse('show');
        $().ready(function () {
            if (!${user.checkAuthority(AUTHORITY.MANAGE_ORG_ACCOUNT)?c}) {
                window.location = "/noauthority"
            } else {
                showAccountList();
            }
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
                    { label: '电话',index:'phone', name: 'phone', width: 100, align:"center" },
                    { label: '所属组织',index:'organizationName', name: 'organization.name', width: 100, align:"center" },
                    { label: '角色信息',index:'role', name: 'role.role', width: 100, align:"center" },
                    { label: '创建时间',index:'showedDate', name: 'showedDate', width: 100, align:"center" ,
                        formatter:function(cellvalue, option, rowObject){
                            return cellvalue != null ? cellvalue.substring(0,10) : "";
                        }
                    },
                    { label: '操作',  name: 'id', width: 100, align:"center",
                        formatter: function (cellvalue, options, rowObject) {
                            var modify = "<a href='####' onclick=\"changeRow(" + cellvalue + ")\" style='text-decoration:underline;color:blue'>" + "修改" + "</a>";
                            var dele = "  <a href='####'  onclick=\"deleteRow(" + cellvalue + ")\"  style='margin-left:30px;text-decoration:underline;color:blue'>" + "删除" + "</a>";
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
            if(confirm("是否确认删除")){
                window.location = "/account/delete?id=" + id;
            }

        }

    </script>

    <legend>账户管理 -> 账户信息查询</legend>

    <div class="row" style="margin-bottom: 1%">
        <div class="col-md-6">
            <label class="control-label"> 账户： </label>
            <input  type="text" id="accountName" name="accountName" value=""/>
        &nbsp;&nbsp;
            <label class="control-label">组织： </label>
            <select  type="text" id="organization" name="organization" disabled>
                <option value= "${user.organization.id}">${user.organization.name}</option>
            </select>
            &nbsp;&nbsp;
            <label class="control-label">门店： </label>
            <select  id="shop" name="shop" >
                <#if shopList?size gt 1>
                    <legend><option value="">请选择</option></legend>
                </#if>
                <#list shopList as shop>
                    <option value= "${shop.id}">${shop.name}</option>
                </#list>
            </select>
        </div>
        <div class="col-md-3">
            <@form.btn_search "onclick='reloadGrid();'" "查 询" />&nbsp;&nbsp;
            <@form.btn_add "onclick='addAccount();'" "新 增" />
        </div>
    </div>
    <div  class="row">
        <table id="accountList" class="scroll" cellpadding="0" cellspacing="0"></table>
        <div id="toolBar"></div>
    </div>
    </@main.frame>
</#escape>