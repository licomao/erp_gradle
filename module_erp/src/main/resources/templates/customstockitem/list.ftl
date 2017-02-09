<#import "../macros/mainFrame.ftl" as main />
<#import "../macros/formMacros.ftl" as form />
<#import "/spring.ftl" as spring />

<#escape x as x?html>
    <@main.frame>

    <script type="text/javascript">
        $('#collapseShop').collapse('show');
        $().ready(function () {
            if (!${user.checkAuthority(AUTHORITY.MANAGE_ORG_CUSTOMSTOCKITEM)?c}) {
                window.location = "/noauthority";
            } else {
                showItemManagerList();

            }
        });

        function showItemManagerList() {
            var itemName = $("#shopName").val();
            var rootCategory = $("#topCategory").val();
            var orgId = $("#orgId").val();
            $("#itemList").jqGrid({
                url: '/customstockitem/list/data',
                mtype: 'POST',
                postData: { itemName:itemName,rootCategory:rootCategory,orgId:orgId},
                colModel: [
                    { label: '商品名称', index:'name', name: 'name', width: 100, align:"center" },
                    { label: '品牌名称',index:'brandName', name: 'brandName', width: 150, align:"center" },
                    { label: '顶级分类',index:'rootCategory', name: 'rootCategory', width: 70, align:"center",
                        formatter: "select", editoptions:{value:"1:机油;2:机滤;3:轮胎;4:电瓶;5:电子类产品;6:美容类产品;7:汽车用品;8:养护产品;9:耗材类产品;10:灯具类产品;" +
                    "11:雨刮类产品;12:发动机配件类;13:底盘配件类;14:变速箱类;15:电气类;16:车身覆盖类;17:服务类;0:临时分类"}},
                    { label: '二级分类',index:'secondaryCategoryName', name: 'secondaryCategory.name', width: 70, align:"center" },
                    { label: '条形码',index:'barCode', name: 'barCode', width: 70, align:"center" },
                    { label: '供应商',index:'supplierName', name: 'supplier.name', width: 100, align:"center" },
                    { label: '结算状态',index:'isDistribution', name: 'isDistribution', width: 100, align:"center",
                        formatter: "select", editoptions:{value:"0:月结;1:铺货;2:月结;3:现结;"}}
                    <#if shopType == 0><#--组织-->
                        ,{ label: '操作',  name: 'id', width: 100, align:"center",
                        formatter: function (cellvalue, options, rowObject) {

                            var modify = "<a onclick=\"changeRow(" + cellvalue + ")\" href='####' style='text-decoration:underline;color:blue'>" + "修改" + "</a>";
                            var dele = "  <a onclick=\"deleteRow(" + cellvalue + ")\" href='####' style='margin-left:30px;text-decoration:underline;color:blue'>" + "删除" + "</a>";
                            return modify + dele;
                        }
                    }
                    <#else>
                        ,{ label: '操作', width: 100,name: 'id', align:"center" ,
                            formatter: function (cellvalue, options, rowObject) {
                                if (rowObject["rootCategory"] == 17 || rowObject["rootCategory"] == 0){
                                    var modify = "<a onclick=\"changeRow(" + cellvalue + ")\" href='####' style='text-decoration:underline;color:blue'>" + "修改" + "</a>";
                                    var dele = "  <a onclick=\"deleteRow(" + cellvalue + ")\" href='####' style='margin-left:30px;text-decoration:underline;color:blue'>" + "删除" + "</a>";
                                    return modify + dele;
                                } else {
                                    return "";
                                }
                            }
                        }
                    </#if>
                ],
                gridComplete:function() {
                    $(".ui-pg-input").blur(function(){
                        var reg = new RegExp("^[0-9]+([.]{1}[0-9]+){0,1}$");
                        var pgInput = $(".ui-pg-input").val();
                        if(!reg.test(pgInput)){
                            alert("请输入数字!");
                            $(".ui-pg-input").focus();
                            return ;
                        }
                        var itemName = $("#shopName").val();
                        var rootCategory = $("#topCategory").val();
                        var orgId = $("#orgId").val();
                        $("#itemList").jqGrid('setGridParam',{
                            postData: { itemName:itemName,rootCategory:rootCategory,orgId:orgId}
                        }).trigger("reloadGrid", [{ page: pgInput}]);
                    });
                }
            });
        }


        function reloadGrid() {
            var itemName = $("#shopName").val();
            var rootCategory = $("#topCategory").val();
            var orgId = $("#orgId").val();
            $("#itemList").jqGrid('setGridParam',{
                postData: { itemName:itemName,rootCategory:rootCategory,orgId:orgId},
                page:1
            }).trigger("reloadGrid", [{ page: 1}]);
        }

        function addItem() {
            window.location = "/customstockitem/tosave";
        }

        function changeRow(id) {
            window.location = "/customstockitem/tosave?id=" + id;
        }
        function deleteRow(id) {
            if(confirm("是否确认删除")){
                window.location = "/customstockitem/delete?id=" + id;
            }
        }

        function toImport(){
            window.location ="/customstockitem/toimport";
        }

    </script>

    <legend>商品管理 -> 查询商品信息</legend>

    <div class="row">
        <div class="col-md-7">
            <label class="control-label"> 商品名称： </label>&nbsp;
            <input type="text" id="shopName" name="shopName" value=""/>&nbsp;
            <label class="control-label">顶级分类： </label>&nbsp;
            <@form.topCategory "topCategory" "" />&nbsp;
        </div>
        <div class="col-md-3">
            <@form.btn_search "onclick='reloadGrid()'" "搜 索" />
            <@form.btn_add "onclick='addItem()'" "新 增" />

            <#if shopType == 0>
                <@form.btn_save "onclick='toImport();'" "导入商品"/>
            </#if>

        </div>
    </div>
    <br>
    <div  class="row">
        <table id="itemList" class="scroll" cellpadding="0" cellspacing="0"></table>
        <div id="toolBar"></div>
    </div>
    <input  type="hidden"  id="orgId" value="${orgId}">
    </@main.frame>
</#escape>
