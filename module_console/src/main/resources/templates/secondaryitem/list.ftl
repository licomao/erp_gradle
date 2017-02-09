<#import "../macros/mainFrame.ftl" as main />
<#import "../macros/formMacros.ftl" as form />
<#import "/spring.ftl" as spring />

<#escape x as x?html>
    <@main.frame>

    <script type="text/javascript">
        $('#backstagemanagement').collapse('show');
        $().ready(function () {
            showItemManagerList();
        });

        function showItemManagerList() {
            var rootCategory = $("#rootCategory").val();

            $("#itemList").jqGrid({
                url: '/secondaryitem/list/data',
                postData: { rootCategory:rootCategory},
                colModel: [
                    { label: '顶级分类',index:'rootCategory', name: 'rootCategory', width: 100, align:"center",
                        formatter: "select", editoptions:{value:"1:机油;2:机滤;3:轮胎;4:电瓶;5:电子类产品;6:美容类产品;7:汽车用品;8:养护产品;9:耗材类产品;10:灯具类产品;" +
                    "11:雨刮类产品;12:发动机配件类;13:底盘配件类;14:变速箱类;15:电气类;16:车身覆盖类;17:车身覆盖类;0:临时分类;"}},
                    { label: '二级分类',index:'secondaryCategoryName', name: 'name', width: 100, align:"center" },
                    { label: '加成率',index:'additionRate', name: 'additionRate', width: 100, align:"center",
                        formatter: rate},
                    { label: '操作',  name: 'id', width: 100, align:"center",
                        formatter: function (cellvalue, options, rowObject) {
                            var modify = "<a onclick=\"changeRow(" + cellvalue + ")\" href='####' style='text-decoration:underline;color:blue'>" + "修改" + "</a>";
                            var dele = "  <a onclick=\"deleteRow(" + cellvalue + ")\" href='####' style='text-decoration:underline;color:blue'>" + "删除" + "</a>";
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
            var rootCategory = $("#rootCategory").val();
            $("#itemList").jqGrid('setGridParam',{
                postData: { rootCategory:rootCategory},
                page:1
            }).trigger("reloadGrid", [{ page: 1}]);
        }

        function addItem() {
            window.location = "/secondaryitem/tosave";
        }

        function changeRow(id) {
            window.location = "/secondaryitem/tosave?id=" + id;
        }
        function deleteRow(id) {
            window.location = "/secondaryitem/delete?id=" + id;
        }

    </script>

    <div class="row" style="margin-bottom: 1%">
        <div class="col-md-1">
            <label class="control-label">顶级分类： </label>
        </div>
        <div class="col-md-2">
            <@form.topCategory "rootCategory" "class='form-control'" />
        </div>
        <div class="col-md-1">
            <@form.btn_search "onclick='reloadGrid()'" "搜 索" />
        </div>
        <div class="col-md-1">
            <@form.btn_add "onclick='addItem()'" "新 增" />
        </div>
    </div>
    <div  class="row">
        <table id="itemList" class="scroll" cellpadding="0" cellspacing="0"></table>
        <div id="toolBar"></div>
    </div>
    </@main.frame>
</#escape>