<#import "../macros/mainFrame.ftl" as main />
<#import "../macros/formMacros.ftl" as form />
<#import "/spring.ftl" as spring />

<#escape x as x?html>
    <@main.frame>

    <script type="text/javascript">
        $('#collapseShop').collapse('show');
        $(function () {
            if (!${user.checkAuthority(AUTHORITY.MANAGE_ORG_EXPENSE)?c}) {
                window.location = "/noauthority"
            } else {
                var url = '/expense/list/data?year=' + $("#year").val()

                        + "&deleted=" + $("#deleted").val();
                if ($("#month").val() != "") {
                    url += "&month=" + $("#month").val();
                }
                if ($("#shopId").val() != "") {
                    url += "&shop.id=" + $("#shopId").val();
                }
                showList(url);
            }
        });

        function showList(url) {
            $("#gridBody").jqGrid({
                url: url,
                colModel: [
                    {label: '门店Id', name: 'shop.id', width: 50, align: "center", hidden: true },
                    {label: '门店', name: 'shop.name', width: 50, align: "center"},
                    {
                        label: '费用月度', name: 'month', width: 70, align: "center",
                        formatter: function (value, options, rData) {
                            return rData['year'] + "-" + rData['month'];
                        }
                    },
                    {label: '记录人', name: 'notePerson', width: 70, align: "center"},
                    {
                        label: '状态', name: 'deleted', width: 50, align: "center",
                        formatter: function (cellvalue, option, rowObject) {
                            if (cellvalue == false) {
                                return "有效";
                            } else {
                                return "无效";
                            }
                        }
                    },
                    {
                        label: '合计费用(元)', name: 'sum', width: 50, align: "center",
                        formatter: function (cellvalue, option, rowObject) {
                            return parseFloat(cellvalue).toFixed(2);
                        }
                    },
                    {
                        label: '操作日期', name: 'operateDate', width: 50, align: "center", sortable: false,
                        formatter: function (cellvalue, option, rowObject) {
                            return cellvalue != null ? formatterDate(cellvalue.substr(0, 19) + ".000Z") : "";
                        }
                    },
                    {
                        label: '操作', name: 'id', width: 50, align: "center",
                        formatter: function (cellvalue, options, rowObject) {
                            var deleted = rowObject["deleted"];
                            var shopId= rowObject["shop"]["id"];
                            var hrefString = "<a onclick='toForm(" + cellvalue + ",2,0)' href='#' style='margin-left:15px;text-decoration:underline;color:blue'>" + "查看" + "</a>"
                            if (deleted) {
                                hrefString += "<a onclick='toDelete(" + cellvalue + ")' href='#' style='margin-left:15px;text-decoration:underline;color:blue'>" + "启用" + "</a>";
                            } else {
                                hrefString += "<a onclick='toForm(" + cellvalue + ",1," + shopId + ")' href='#' style='margin-left:15px;text-decoration:underline;color:blue'>" + "修改" + "</a>";
                                hrefString += "<a onclick='toDelete(" + cellvalue + ")' href='#' style='margin-left:15px;text-decoration:underline;color:blue'>" + "作废" + "</a>";
                            }
                            return hrefString;
                        }
                    }
                ],
                rownumbers: true
            });
        }

        function toForm(id, doType, shopId) {
            window.location = "/expense/form?id=" + id + "&doType=" + doType + "&shopId=" + shopId;
        }

        function toDelete(id) {
            if (confirm("是否确定")) {
                window.location = "/expense/delete?id=" + id;
            }
        }

        function queryList() {
            var url = '/expense/list/data?year=' + $("#year").val()
                    + "&deleted=" + $("#deleted").val();
            if ($("#month").val() != "") {
                url += "&month=" + $("#month").val();
            }
            if ($("#shopId").val() != "") {
                url += "&shop.id=" + $("#shopId").val();
            }
            jQuery("#gridBody").setGridParam({url: url}).trigger("reloadGrid", [{page: 1}]);
        }


    </script>
    <div class="row">
        <legend>费用管理 -> 费用信息查询</legend>
    </div>

    <div class="row">
        <form class="" id="fm" action='<@spring.url relativeUrl = "/expense/list"/>' method="GET">
            <div class="col-md-5">
                <label class="control-label">所属门店: </label>&nbsp;
                <select name="shopId" id="shopId">
                    <#if shops?size gt 1>
                        <legend>
                            <option value="">请选择</option>
                        </legend>
                    </#if>
                    <#list shops as shop>
                        <option value="${shop.id}">${shop.name}</option>
                    </#list>
                </select>
                &nbsp;
                <label class="control-label">年度: </label>&nbsp;
                <select name="year" id="year">
                    <#list years as year>
                        <option value="${year}"
                                <#if (yearNow)?? && (yearNow?number == year?number)>selected</#if>>${year}年
                        </option>
                    </#list>
                </select>
                &nbsp;
                <label class="control-label">月度: </label>&nbsp;
                <select name="month" id="month">
                    <#if months?size gt 1>
                        <legend>
                            <option value="">请选择</option>
                        </legend>
                    </#if>
                    <#list months as month>
                        <option value="${month}"
                                <#if (monthNow)?? && (monthNow?number == month?number)>selected</#if>>${month}月
                        </option>
                    </#list>
                </select>
                &nbsp;
                <label class="control-label">状态: </label>&nbsp;
                <select name="deleted" id="deleted">
                    <option value="false">有效</option>
                    <option value="true">无效</option>
                </select>
            </div>
            <div class="col-md-3">
                <@form.btn_search "onclick='queryList();'" "搜索"/>&nbsp;
                <@form.btn_search "onclick='toForm(0,0,${SHOP.id});'" "新增"/>
            </div>
        </form>
    </div>
    <br>
    <table id="gridBody" class="scroll" cellpadding="0" cellspacing="0"></table>
    <div id="toolBar"></div>

    </@main.frame>
</#escape>