<#import "../macros/mainFrame.ftl" as main />
<#import "../macros/formMacros.ftl" as form />
<#import "/spring.ftl" as spring />
<#escape x as x?html>
<@main.frame>
<script type="text/javascript">
    $('#backstagemanagement').collapse('show');
    $(function() {
        $().ready(function () {
            changeAppSortStatus();
            changeRootCategory();
        });

        $("#isAppSale").change(changeAppSortStatus);

        $("#rootCategory").change(changeRootCategory);

        function changeAppSortStatus() {
            var test = document.getElementById("isAppSale").checked;
            if (test) {
                $("#appSort").attr('disabled', false);
                $("#appSort").attr('required', true);
            } else {
                $("#appSort").attr('disabled', true);
                $("#appSort").attr('required', false);
            }
        }

        function changeRootCategory() {
            var second = $("#rootCategory").val();
            var orgId = $("#orgId").val();
            var murl = "/stockitem/list/secondarydata?root=" + second + "&orgId=" + orgId;
            $.ajax({
                url:murl,
                dataType:"json",
                type:"post",
                success: function(ret) {
                    var obj = $("#secondaryCategory");
                    obj.empty();
                    for(var i = 0 ; i < ret.secondaryCategoryList.length;i++) {
                        obj.append("<option value='"+ret.secondaryCategoryList[i].id+"'>"+ret.secondaryCategoryList[i].name+"</option>");
                    }
                }
            });
        }
    });
</script>

<form class="" action='<@spring.url relativeUrl = "/stockitem/save"/>' method="post">
    <input id="orgId" value="${orgId}" type="hidden">
<@spring.bind "stockItem" />
    <div class="row">
    <@form.labelAndTextInput "stockItem.id" "class='form-control'" "hidden" ""/>
    </div>
<div class="row">
    <div class="col-md-offset-1 col-md-3">
        <@form.labelAndTextInput "stockItem.name" "class='form-control'" "text" "商品名称：" true/>
    </div>
    <div class="col-md-3">
        <@form.labelAndTextInput "stockItem.brandName" "class='form-control'" "text" "品牌名称：" true/>
    </div>
</div>
<div class="row" style="margin-top: 1%">
    <div class="col-md-offset-1 col-md-3">
        <@form.labelAndSelect "stockItem.rootCategory" {"1":"机油","2":"机滤","3":"轮胎","4":"电瓶","5":"电子类产品","6":"美容类产品","7":"汽车用品",
        "8":"养护产品","9":"耗材类产品","10":"灯具类产品","11":"雨刮类产品","12":"发动机配件类",
        "13":"底盘配件类","14":"变速箱类","15":"电气类","16":"车身覆盖类","17":"车身覆盖类","0":"临时分类"} "class='form-control'" "顶级分类： " />
    </div>
    <div class="col-md-3">
            <@form.labelAndSelect "stockItem.secondaryCategory" {"1":""} "class='form-control'" "二级分类： " />
    </div>
</div>
<div class="row" style="margin-top: 1%">
    <div class="col-md-offset-1 col-md-3">
        <@form.labelAndCheckbox "stockItem.isAppSale" "是否APP推广" "class='form-control'"/>
    </div>
    <div class="col-md-3">
        <@form.labelAndTextInput "stockItem.appSort" "class='form-control' disabled" "text" "商品排位序号：" false/>
    </div>
</div>
<div class="row" style="margin-top: 1%">
    <div class="col-md-offset-1 col-md-3">
        <@form.labelAndSelect "stockItem.supplier" {"1":""} "class='form-control'" "产品供应商： " />
    </div>
    <div class="col-md-3">
        <@form.labelAndSelect "stockItem.isDistribution" {"0":"月结","1":"铺货","2":"月结","3":"现结"} "class='form-control'" "结算状态： " />
    </div>
</div>
<div class="row" style="margin-top: 1%">
    <div class="col-md-offset-1 col-md-7">
        <@form.textArea "stockItem.description" "class='form-control' style='width:745px;height:300px'"  "门店描述" />
    </div>
</div>

<div class="row" style="margin-top: 1%">
    <div class="col-md-offset-3 col-md-1">
        <div class="col-md-offset-6 col-md--6">
            <button class="btn btn-primary btn_erp" onclick=""><img src="/stylesheets/images/erp/save.png">保存</button>
        </div>
    </div>
</div>
</form>
</@main.frame>
</#escape>