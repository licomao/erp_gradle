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
        var itemName = $("#shopName").val();
        var rootCategory = $("#topCategory").val();
        var isAppSale = $("#appSelect").val();

        $("#itemList").jqGrid({
            url: '/stockitem/list/data',
            mtype: 'POST',
            postData: { itemName:itemName,rootCategory:rootCategory, isAppSale:isAppSale},
            colModel: [
                { label: '商品名称', index:'name', name: 'name', width: 100, align:"center" },
                { label: '品牌名称',index:'brandName', name: 'brandName', width: 150, align:"center" },
                { label: '顶级分类',index:'rootCategory', name: 'rootCategory', width: 100, align:"center",
                    formatter: "select", editoptions:{value:"1:机油;2:机滤;3:轮胎;4:电瓶;5:电子类产品;6:美容类产品;7:汽车用品;8:养护产品;9:耗材类产品;10:灯具类产品;" +
                                                                "11:雨刮类产品;12:发动机配件类;13:底盘配件类;14:变速箱类;15:电气类;16:车身覆盖类;17:车身覆盖类;0:临时分类;"}},
                { label: '二级分类',index:'secondaryCategoryName', name: 'secondaryCategory.name', width: 100, align:"center" },
                { label: '结算状态',index:'isDistribution', name: 'isDistribution', width: 100, align:"center",
                    formatter: "select", editoptions:{value:"0:非铺货;1:铺货;2:月结;3:现结;"}},
                { label: '操作',  name: 'id', width: 100, align:"center",
                    formatter: function (cellvalue, options, rowObject) {
                        var modify = "<a onclick=\"changeRow(" + cellvalue + ")\" href='####'style='text-decoration:underline;color:blue'>" + "修改" + "</a>";
                        var dele = "  <a onclick=\"deleteRow(" + cellvalue + ")\" href='####' style='text-decoration:underline;color:blue'>" + "删除" + "</a>";
                        return modify + dele;
                    }
                }
            ]
        });
    }

    function reloadGrid() {
        var itemName = $("#shopName").val();
        var rootCategory = $("#topCategory").val();
        var isAppSale = $("#appSelect").val();
        $("#itemList").jqGrid('setGridParam',{
            postData: { itemName:itemName,rootCategory:rootCategory, isAppSale:isAppSale},
            page:1
        }).trigger("reloadGrid", [{ page: 1}]);
    }

    function addItem() {
        window.location = "/stockitem/tosave";
    }

    function changeRow(id) {
        window.location = "/stockitem/tosave?id=" + id;
    }
    function deleteRow(id) {
        window.location = " /stockitem/delete?id=" + id;
    }

</script>

    <div class="row" style="margin-bottom: 1%">
        <div class="col-md-1">
            <label class="control-label"> 商品名称： </label>
        </div>
        <div class="col-md-2">
            <input class="form-control" type="search" id="shopName" name="shopName" value=""/>
        </div>
        <div class="col-md-1">
            <label class="control-label">顶级分类： </label>
        </div>
        <div class="col-md-1">
            <@form.topCategory "topCategory" "class='form-control'" />
        </div>
        <div class="col-md-2">
            <div class="col-md-7">
                <label class="control-label">APP推广： </label>
            </div>
            <div class="col-md-5">
            <select class="form-control" type="search" id="appSelect" name="appSelect" >
                <option value="0">否</option>
                <option value="1">是</option>
            </select>
            </div>
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