<#import "../macros/mainFrame.ftl" as main />
<#import "../macros/formMacros.ftl" as form />
<#import "/spring.ftl" as spring />

<#escape x as x?html>
    <@main.frame>

    <script type="text/javascript">
        $('#collapsePt').collapse('show');

        $().ready(function () {
            showAgencyList();
        });

        function showAgencyList() {
            var accountName = $("#accountName").val();
            var organization = $("#organization").val();
            var shop = $("#shop").val();

            $("#agencyList").jqGrid({
                url: '/agency/list/data',
                postData: { accountName:accountName,organization:organization, shop:shop },
                colModel: [
                    { label: '账户名', index:'username', name: 'erpUser.username', width: 100, align:"center" },
                    { label: '电话',index:'phone', name: 'erpUser.phone', width: 150, align:"center" },
                    { label: '所属组织',index:'organizationName', name: 'erpUser.organization.name', width: 100, align:"center" },
                    { label: '角色信息',index:'role', name: 'erpUser.role.role', width: 100, align:"center" },
                    { label: '创建时间',index:'showedDate', name: 'erpUser.showedDate', width: 100, align:"center",
                        formatter: function(cellvalue){
//                             var browser = myBrowser();

                            if(cellvalue){
                                var date = new Date(cellvalue);
                                var y = date.getFullYear();
                                var m = date.getMonth()+1;
                                var d = date.getDate();
                                var H;
                                H = date.getHours();
                                var M = date.getMinutes();
                                var s = date.getSeconds();
                                return y+'-'+(m<10?('0'+m):m)+'-'+(d<10?('0'+d):d)+' '+(H<10?('0'+H):H)+':'+(M<10?('0'+M):M)+':'+(s<10?('0'+s):s);
                            }

                        }},
                    { label: '操作',  name: 'id', width: 100, align:"center",
                        formatter: function (cellvalue, options, rowObject) {
                            var modify = "<a onclick=\"changeRow(" + cellvalue + ")\" href='####' style='text-decoration:underline;color:blue'>" + "修改" + "</a>";
//                            modify += "  <a onclick=\"changeInfo(" + cellvalue + ")\" href='#' style='text-decoration:underline;color:blue'>" + "修改信息" + "</a>";
                           /* var dele = "  <a onclick=\"deleteRow(" + cellvalue + ")\"  style='text-decoration:underline;color:blue'>" + "删除" + "</a>";*/
                            return modify;
                        }
                    }
                ]
            });
        }

        function reloadGrid() {
            var accountName = $("#accountName").val();
            var organization = $("#organization").val();
            var shop = $("#shop").val();
            $("#agencyList").jqGrid('setGridParam',{
                postData: { accountName:accountName,organization:organization, shop:shop},
                page:1
            }).trigger("reloadGrid", [{ page: 1}]);
        }

        function addAgency() {
            window.location = "/agency/tosave";
        }

        /**
         * 修改
         * @param id
         */
        function changeRow(id) {
            window.location = "/agency/tosave?id=" + id;
        }
        function deleteRow(id) {
            window.location = " /agency/delete?id=" + id;
        }


    </script>
    <legend>代理商管理 </legend>
    <div class="row" style="margin-bottom: 1%">

        <div class="col-md-3">
            <label class="control-label"> 账户： </label>
            <input  type="text" id="accountName" name="accountName" value=""/>
        </div>

        <div class="col-md-4">

            <@form.btn_search "onclick='reloadGrid();'" "查 询" />
            &nbsp;
            <@form.btn_add "onclick='addAgency();'" "新 增" />
        </div>
    </div>
    <div  class="row">
        <table id="agencyList" class="scroll" cellpadding="0" cellspacing="0"></table>
        <div id="toolBar"></div>
    </div>
    </@main.frame>
</#escape>