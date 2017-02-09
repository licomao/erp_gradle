<#import "../macros/mainFrame.ftl" as main />
<#import "../macros/formMacros.ftl" as form />
<#import "/spring.ftl" as spring />
<#escape x as x?html>
    <@main.frame>
    <script type="text/javascript">
        $('#collapsePt').collapse('show');
        $(function() {
            $().ready(function () {
                changeRootCategory();
                var obj = document.getElementById("rootCategory");
                checkType(obj,1);
            });

            $("#rootCategory").change(changeRootCategory);

            function changeRootCategory() {
                var second = $("#rootCategory").val();
                var orgId = $("#orgId").val();
                var murl = "/customstockitem/list/secondarydata?root=" + second + "&orgId=" + orgId;
                $.ajax({
                    url:murl,
                    dataType:"json",
                    type:"post",
                    success: function(ret) {
                        var obj = $("#secondaryCategory");
                        obj.empty();
                        obj.append("<option value=''>无</option>");
                        for(var i = 0 ; i < ret.secondaryCategoryList.length;i++) {
                            var appendStr = "<option value='"+ret.secondaryCategoryList[i].id + "'";
                            <#if customStockItem.secondaryCategory??>
                                if (ret.secondaryCategoryList[i].id == ${customStockItem.secondaryCategory.id}) {
                                    appendStr += " selected "
                                }
                            </#if>
                            var appendStr = appendStr + ">" + ret.secondaryCategoryList[i].name + "</option>";
                            obj.append(appendStr);
                        }
                    }
                });
            }
        });

        function checkType(obj,tp) {
            var spOne = document.getElementById("spOne");
            var spTwo = document.getElementById("spTwo");
            var spThree = document.getElementById("spThree");
            if (obj.value ==17){
                spOne.style.display = "none";
                spThree.style.display = "none";
                spTwo.style.display = "block";
            } else {
                spOne.style.display = "block";
                spThree.style.display = "block";
                spTwo.style.display = "none";
            }

        }

        function subForm(){
            if (confirm("是否确认保存!")){
                $("#fm").submit();
            }
        }

        function validate(obj){
            var val = obj.value.replace("￥","");
            var reg = /^[0-9]+([.]{1}[0-9]{1,2})?$/;
            if (!reg.test(val)) {
                alert ("请输入正确金额");
                $(obj).val("0");
                obj.focus();
                return;
            }
        }

    </script>

        <#if customStockItem.name?? && customStockItem.brandName??>
        <legend>商品管理 -> 修改商品信息</legend>
        <#else>
        <legend>商品管理 -> 新增商品信息</legend>
        </#if>

    <form class="" id="fm" action='<@spring.url relativeUrl = "/customstockitem/save"/>' method="post">
        <input id="orgId" value="${orgId}" type="hidden">
        <@spring.bind "customStockItem" />
        <div class="row">
            <@form.labelAndTextInput "customStockItem.id" "class='form-control'" "hidden" ""/>
        </div>
        <div id="spTwo" style="display: none" >
            <div class="row">
                <div class="col-md-offset-1 col-md-3">
                    <@form.labelAndTextInput "customStockItem.serviceName" "class='form-control checkTp'" "text" "服务名称：" true/>
                </div>
                <div class="col-md-3">
                    <@form.labelAndTextInput "customStockItem.cost" "class='form-control' onblur='validate(this)'" "text" "服务单价：" true/>
                </div>
            </div>
        </div>
        <div id="spOne" >
            <div class="row">
                <div class="col-md-offset-1 col-md-3">
                    <@form.labelAndTextInput "customStockItem.name" "class='form-control'" "text" "商品名称：" true/>
                </div>
                <div class="col-md-3">
                    <@form.labelAndTextInput "customStockItem.brandName" "class='form-control'" "text" "品牌名称：" true/>
                </div>
            </div>
        </div>
            <div class="row" style="margin-top: 1%">
                <div class="col-md-offset-1 col-md-3">

                    <#if shopType == 0><#--组织-->
                <@form.labelAndSelect "customStockItem.rootCategory" {"1":"机油","2":"机滤","3":"轮胎","4":"电瓶","5":"电子类产品","6":"美容类产品","7":"汽车用品",
                    "8":"养护产品","9":"耗材类产品","10":"灯具类产品","11":"雨刮类产品","12":"发动机配件类",
                    "13":"底盘配件类","14":"变速箱类","15":"电气类","16":"车身覆盖类","17":"服务类","0":"临时分类"} " onchange='checkType(this,0);' class='form-control'" "顶级分类： " />
            <#else><#--门店-->
                        <@form.labelAndSelect "customStockItem.rootCategory" {"0":"临时分类","17":"服务类"} " onchange='checkType(this,0);' class='form-control' " "顶级分类： " />
                    </#if>
                </div>
                <div class="col-md-3">
                    <#--<@form.labelAndSelect "customStockItem.secondaryCategory" {"1":""} "class='form-control'" "二级分类： " />-->
                </div>
            </div>
        <div id="spThree" >
            <#--&lt;#&ndash;<div class="row" style="margin-top: 1%">&ndash;&gt;-->
                <#--&lt;#&ndash;<div class="col-md-offset-1 col-md-3">&ndash;&gt;-->
                    <#--&lt;#&ndash;<div class="col-md-4">&ndash;&gt;-->
                        <#--&lt;#&ndash;<label class="control-label" >产品供应商:</label>&ndash;&gt;-->
                    <#--&lt;#&ndash;</div>&ndash;&gt;-->
                    <#--&lt;#&ndash;<div class="col-md-8">&ndash;&gt;-->
                        <#--&lt;#&ndash;<div class="controls">&ndash;&gt;-->
                            <#--&lt;#&ndash;<select name="supplier.id" class="form-control">&ndash;&gt;-->
                                <#--&lt;#&ndash;<#list suppliers as sp>&ndash;&gt;-->
                                    <#--&lt;#&ndash;<option value="${sp.id}" <#if customStockItem.supplier??> <#if sp.id == customStockItem.supplier.id>selected</#if> </#if>>${sp.name}</option>&ndash;&gt;-->
                                <#--&lt;#&ndash;</#list>&ndash;&gt;-->
                            <#--&lt;#&ndash;</select>&ndash;&gt;-->
                        <#--&lt;#&ndash;</div>&ndash;&gt;-->
                    <#--&lt;#&ndash;</div>&ndash;&gt;-->
                <#--&lt;#&ndash;</div>&ndash;&gt;-->
                <#--&lt;#&ndash;<div class="col-md-3">&ndash;&gt;-->
                    <#--&lt;#&ndash;<@form.labelAndSelect "customStockItem.isDistribution" {"0":"非铺货","1":"铺货","2":"月结","3":"现结"} "class='form-control'" "结算状态： " />&ndash;&gt;-->
                <#--&lt;#&ndash;</div>&ndash;&gt;-->
            <#--</div>-->
            <div class="row" >
                <div class="col-md-offset-1 col-md-3" style="margin-top: 1%">
                    <@form.labelAndTextInput "customStockItem.barCode" "class='form-control' style='width:180px;'" "text" "条形码" false/>
                </div>
            </div>

        </div>

        <div class="row" style="margin-top: 1%">
            <div class="col-md-offset-1 col-md-7">
                <@form.textArea "customStockItem.description" "class='form-control' style='width:745px;height:300px'"  "商品描述" />
            </div>
        </div>

        <div class="row" style="margin-top: 1%">
            <div class="col-md-offset-2 col-md-3">
                <div class="col-md-offset-6 col-md--6">
                    <@form.btn_save "onclick='subForm()'" "保 存"/>
                </div>
            </div>
        </div>
    </form>
    </@main.frame>
</#escape>
