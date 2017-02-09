<#import "../macros/mainFrame.ftl" as main />
<#import "../macros/formMacros.ftl" as form />
<#import "/spring.ftl" as spring />
<#escape x as x?html>
    <@main.frame>
    <script type="text/javascript">
        $('#collapseOrg').collapse('show');
        $(function() {
        });

        function subForm(){
            if(confirm("是否确认保存")){
                $("#fm").submit();
            }
        }

        //改变加成率
        function validate(obj) {
            var reg = new RegExp("^[0-9]+([.]{1}[0-9]+){0,1}$");
            if(!reg.test(obj.value)){
                alert("请输入数字!");
                obj.focus();
                return;
            }
        }
    </script>

    <#if secondaryCategory.name??>
    <legend>二级分类管理 -> 修改二级分类</legend>
    <#else>
    <legend>二级分类管理 -> 新增二级分类</legend>
    </#if>

    <form class="" id="fm" action='<@spring.url relativeUrl = "/secondaryitem/save"/>' method="post">
        <div class="row">
            <@form.labelAndTextInput "secondaryCategory.id" "class='form-control'" "hidden" ""/>
        </div>
        <div class="row" style="margin-top: 1%">
            <div class="col-md-offset-1 col-md-3">
                <@form.labelAndSelect "secondaryCategory.rootCategory" {"1":"机油","2":"机滤","3":"轮胎","4":"电瓶","5":"电子类产品","6":"美容类产品","7":"汽车用品",
                "8":"养护产品","9":"耗材类产品","10":"灯具类产品","11":"雨刮类产品","12":"发动机配件类",
                "13":"底盘配件类","14":"变速箱类","15":"电气类","16":"车身覆盖类","17":"服务类","0":"临时分类"} "class='form-control'" "顶级分类： " />
            </div>
            <div class="col-md-3">
                <@form.labelAndTextInput "secondaryCategory.name" "class='form-control'" "text" "二级分类： " true/>
            </div>
        </div>
        <div class="row" style="margin-top: 1%">
            <div class="col-md-offset-1 col-md-3">
                <@form.labelAndTextInput "secondaryCategory.additionRate" "class='form-control' onblur='validate(this)'" "text" "加成率："/>
            </div>
            <div style="margin-top: 0.5%">% <strong><font color="red">公式：销售单价 = 成本 + 成本 X 加价率 </font></strong></div>
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