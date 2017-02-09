<#import "../macros/mainFrame.ftl" as main />
<#import "../macros/formMacros.ftl" as form />
<#import "/spring.ftl" as spring />

<#escape x as x?html>
    <@main.frame>

    <script type="text/javascript">
        var viewName = "";
        $('#collapsePt').collapse('show');
        $(function () {
            if (!${user.checkAuthority(AUTHORITY.MANAGE_ORG_CUSTOMER)?c}) {
                window.location = "/noauthority";
            } else {
                var url = '/customerpurchasesuite/vipcard/list/data?name=';
                showList(url);
            }
        });

        function showList(url) {
            $("#gridBody").jqGrid({
                url: url,
                colModel: [

                    {name: 'id', hidden: true},
                    {label: '卡名称', name: 'name', width: 50, align: "center"},
                    {label: '套餐描述', name: 'description', width: 100, align: "center"},
                    {label: '套餐价格', name: 'price', width: 30, align: "center"},
                    {label: '有效期天数', name: 'expiation', width: 30, align: "center"},
                    {
                        label: '创建时间', name: 'createdDate', width: 40, align: "center",
                        formatter: function (cellvalue) {
                            return formatterDateWithSecond(cellvalue);
                        }
                    },{
                        label: '更新时间', name: 'updatedDate', width: 40, align: "center",
                        formatter: function (cellvalue) {
                            return formatterDateWithSecond(cellvalue);
                        }
                    },
                    {
                        label: '操作', name: 'enabled', width: 45, align: "center",
                        formatter: function (cellvalue, options, rowObject) {
                                viewName = "启用";
                            modify = "<a onclick=\"update(" + rowObject["id"] + ")\" href='#' style='text-decoration:underline;color:blue'>修改</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
                            if (cellvalue)
                                viewName = "停用";
                            modify += "<a onclick='changeStatus(" + rowObject["id"] + "," + cellvalue + " )' href='#' style='text-decoration:underline;color:blue'>" + viewName + "</a>";
                            return modify;
                        }
                    }
                ],
                pager: '#toolBar',
                //multiselect:true,
                rownumbers: true
            });
        }

        /**
         * 启停用
         * @param id
         */
        function changeStatus(id,foo) {
            var text = "启用"
            if(foo){
                text = "停用"
            }
            if(confirm("是否确认"+text)){
                $.get('/customerpurchasesuite/vipcard/enabled?id=' + id, function (foo) {
                    if (foo) {
                        $("#gridBody").trigger("reloadGrid", [{ page: 1}]);
                    }

                });
            }
        }

        /**
         * 条件查询方法
         */
        function queryList() {
            var url = '/customerpurchasesuite/vipcard/list/data?name=' + $("#name").val();
//            var url = '/customerpurchasesuite/remote/list/data?keyWord=' + $("#keyWord").val() + "&shopId=" + $("#shopId").val();
            jQuery("#gridBody").setGridParam({url: url}).trigger("reloadGrid", [{ page: 1}]);
        }

        function update(id) {
            if (id != null) {
                window.location = "/customerpurchasesuite/vipcard/tosave?id=" + id;
            } else {
                window.location = "/customerpurchasesuite/vipcard/tosave?id= + 0";
            }
        }

    </script>

    <legend>设置会员套餐种类 </legend>
    <div class="row">
        <div class="col-md-12">

            <label class="control-label">卡名称: </label>
            <input type="text" name="name" style="width: 400px" id="name">
        <#-- <select id="shopId">
             <option value="">--请选择--</option>
             <#list shopList as shop >
                 <option value="${shop.id}">${shop.name}</option>
             </#list>
         </select>-->
            &nbsp; &nbsp;&nbsp; &nbsp;&nbsp; &nbsp;
            <@form.btn_search "onclick='queryList()'" "查 询"/>&nbsp;
            <@form.btn_search "onclick='update()'" "添 加"/>&nbsp;
        </div>
    </div>
    <br>
    <table id="gridBody" class="scroll" cellpadding="0" cellspacing="0"></table>
    <div id="toolBar"></div>

    </@main.frame>
</#escape>