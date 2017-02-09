<#import "../macros/mainFrame.ftl" as main />
<#import "../macros/formMacros.ftl" as form />
<#import "/spring.ftl" as spring />
<#escape x as x?html>
    <@main.frame>
    <script type="text/javascript">
        $('#backstagemanagement').collapse('show');
    </script>

    <form class="" action='<@spring.url relativeUrl = "/secondaryitem/save"/>' method="post">
        <div class="row">
            <@form.labelAndTextInput "secondaryCategory.id" "class='form-control'" "hidden" ""/>
        </div>
        <div class="row" style="margin-top: 1%">
            <div class="col-md-offset-1 col-md-3">
                <@form.labelAndSelect "secondaryCategory.rootCategory" {"1":"机油","2":"机滤","3":"轮胎","4":"电瓶","5":"电子类产品","6":"美容类产品","7":"汽车用品",
                    "8":"养护产品","9":"耗材类产品","10":"灯具类产品","11":"雨刮类产品","12":"发动机配件类",
                    "13":"底盘配件类","14":"变速箱类","15":"电气类","16":"车身覆盖类","17":"车身覆盖类","0":"临时分类"} "class='form-control'" "顶级分类： " />
            </div>
            <div class="col-md-3">
                <@form.labelAndTextInput "secondaryCategory.name" "class='form-control'" "text" "二级分类： " true/>
            </div>
        </div>
        <div class="row" style="margin-top: 1%">
            <div class="col-md-offset-1 col-md-3">
                <@form.labelAndTextInput "secondaryCategory.additionRate" "class='form-control'" "text" "加成率： "/>
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