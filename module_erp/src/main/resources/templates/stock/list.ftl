<#import "../macros/mainFrame.ftl" as main />
<#import "../macros/formMacros.ftl" as form />
<#import "/spring.ftl" as spring />

<#escape x as x?html>
<@main.frame>

<script type="text/javascript">
    $('#collapseStock').collapse('show');
    $(function () {
        if (!${user.checkAuthority(AUTHORITY.MANAGE_ORG_STOCK)?c}) {
            window.location = "/noauthority"
        } else {
            changeRootCategory();
            var url = '/stock/list/data?name=' + $("#name").val()
                    + "&shopId=" + $("#shopId").val()
                    +"&rootCategory=" + $("#rootCategory").val();
            if($("#secondaryCategory").val() != null && $("#secondaryCategory").val() != '') {
                url += "&secondaryCategory=" + $("#secondaryCategory").val();
            }
//                    + "&isDistribution=" + $("#isDistribution").val();
            url = encodeURI(url,"UTF-8");
            showList(url);
        }
    });

    function showList(url) {
        $("#gridBody").jqGrid({
            url: url,
            colModel: [
                { label: '商品名', name: 'name', width: 100, align:"center" },
                { label: '品牌', name: 'brandName', width: 40, align:"center"},
                { label: '条形码', name: 'barCode', width: 70, align:"center"},
                { label: '顶级分类',index:'rootCategory', name: 'rootCategory', width: 50, align:"center",
                    formatter: "select", editoptions:{value:"1:机油;2:机滤;3:轮胎;4:电瓶;5:电子类产品;6:美容类产品;7:汽车用品;8:养护产品;9:耗材类产品;10:灯具类产品;" +
                "11:雨刮类产品;12:发动机配件类;13:底盘配件类;14:变速箱类;15:电气类;16:车身覆盖类;17:服务类;0:临时分类;"}},
                { label: '二级分类', name: 'secondaryCategory.name', width: 50, align:"center" },
                { label: '成本(元)', name: 'cost', width: 50, align:"center" },
                { label: '库存数量', name: 'number', width: 50, align:"center"},
                { label: '结算状态', name: 'isDistribution', width: 50 ,align:"center",
                    formatter:function(cellvalue, options, rowObject){
                        switch(cellvalue) {
                            case 0 :
                                return "月结";
                            case 1 :
                                return "铺货";
                            case 2 :
                                return "月结";
                            case 3 :
                                return "现结";
                        }
                    }
//
                },
                { label: '商品描述', name: 'description', width: 40, align:"center"}
            ],
            //multiselect:true,
            rownumbers: true
        });
    }

    function queryList(){
        var url = '/stock/list/data?name=' + $("#name").val()
                + "&shopId=" + $("#shopId").val()
                +"&rootCategory=" + $("#rootCategory").val();
        if($("#secondaryCategory").val() != null && $("#secondaryCategory").val() != '') {
            url += "&secondaryCategory=" + $("#secondaryCategory").val();
        }
//                + "&isDistribution=" + $("#isDistribution").val();
        url = encodeURI(url,"UTF-8");
        jQuery("#gridBody").setGridParam({url:url}).trigger("reloadGrid", [{ page: 1}]);
    }

    function changeRootCategory() {
        var second = $("#rootCategory").val();
        var orgId = $("#orgId").val();
        var murl = "/customstockitem/list/secondarydata?root=" + second + "&orgId=" + orgId;
        $.ajax({
            url:murl,
            dataType:"json",
            type:"GET",
            success: function(ret) {
                var obj = $("#secondaryCategory");
                obj.empty();
                obj.append("<option value=''>无</option>");
                for(var i = 0 ; i < ret.secondaryCategoryList.length;i++) {
                    var appendStr = "<option value='"+ret.secondaryCategoryList[i].id + "'";
                    appendStr = appendStr + ">" + ret.secondaryCategoryList[i].name + "</option>";
                    obj.append(appendStr);
                }
            }
        });
    }



</script>


<div class="row">
    <legend>库存管理 -> 库存查询</legend>
    <form class="" id="fm" action='<@spring.url relativeUrl = "/fixedasset/tosave"/>' method="GET">
        <input id="orgId" value="${orgId}" type="hidden">
        <div class="col-md-7">
            <label  class="control-label">关键字: </label>
            <input type="text" name="name" id="name">&nbsp;
            <#--&nbsp;-->
            <#--<label  class="control-label">顶级分类: </label>-->
            <#--<select id="rootCategory">-->
                <#--<option value="1">洗车</option>-->
                <#--<option value="2">美容</option>-->
                <#--<option value="3">保养</option>-->
                <#--<option value="4">配件</option>-->
                <#--<option value="5">精品用品</option>-->
                <#--<option value="6">维修</option>-->
                <#--<option value="7">会员</option>-->
            <#--</select>-->
            &nbsp;
            <label  class="control-label">所属门店: </label>
            <select name="shopId" id="shopId">
                <#list shops as shop>
                    <option value="${shop.id}">${shop.name}</option>
                </#list>
            </select>&nbsp;
            <label class="control-label">顶级分类： </label>
            <@form.topCategory "rootCategory" "onchange='changeRootCategory();'" />
            &nbsp;
            <label class="control-label">二级分类： </label>
            <select id="secondaryCategory">

            </select>
            <#--<label  class="control-label">结算状态: </label>-->
            <#--<select name="isDistribution" id="isDistribution">-->
                <#--<option value="0">非铺货</option>-->
                <#--<option value="1">铺货</option>-->
                <#--<option value="2">月结</option>-->
                <#--<option value="3">现结</option>-->
            <#--</select>-->
        </div>
        <div class="col-md-2">
            <@form.btn_search "onclick='queryList();'" "查 询"/>
        </div>
    </form>
</div>
<br>
<table id="gridBody" class="scroll" cellpadding="0" cellspacing="0"></table>
<div id="toolBar"></div>

</@main.frame>
</#escape>