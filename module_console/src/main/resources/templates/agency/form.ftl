<#import "../macros/mainFrame.ftl" as main />
<#import "../macros/formMacros.ftl" as form />
<#import "/spring.ftl" as spring />
<#escape x as x?html>
    <@main.frame>
    <script type="text/javascript" xmlns="http://www.w3.org/1999/html">
        $('#collapsePt').collapse('show');
        var id = ${agency.id};
        $(function() {
            $("input[id='erpUser.username']").change(function(){

                $.get('/erpuser/username/check?username='+$(this).val(),function(isExist){
                    var text = isExist ? "改用户名已存在":"";
                    $("#usernameCheckSpan").text(text);
                });
            });
        });

        function save() {
            obj = document.getElementById("checkPassword");
            if(obj){
                var checkPassword = $("#checkPassword").val();
                if(checkPassword == ""){
                    $("#checkSpan").text("不能为空");
                    return ;
                }
            }
            if(confirm("是否确认保存")){
                $("#fm").submit();
            }
        }

        function checkPass(){
            var password = $("input[id='erpUser.password']").val();
            var passwordCheck = $("#checkPassword").val();
            if(password != passwordCheck){
                $("#checkSpan").text("两次输入的密码不一致");
            }else{
                $("#checkSpan").text("");
            }
        }
    </script>

        <#if (agency.erpUser.username)?? >
            <legend>代理商管理 -> 修改代理商信息</legend>
        <#else>
            <legend>代理商管理 -> 新增代理商信息</legend>
        </#if>

    <form class="" id="fm" action='<@spring.url relativeUrl = "/agency/save"/>' method="post">
        <@form.textInput "agency.id" "class='form-control '  "  "hidden" "账户名" />
        <#if (agency.erpUser.username)?? >
            <input  name="erpUser.enable" hidden="hidden" value="false" >
        <#else >
            <input  name="erpUser.enable" hidden="hidden" value="true" >
        </#if>
            <div class="row">
            <div class="col-md-3 col-md-offset-2">
        <#if (agency.erpUser.username)?? >
            <#--修改-->
            <@form.textInput "agency.erpUser.username" "class='form-control' readonly" "text" "账户名" true/>
        <#else >
            <#--新增-->
            <@form.textInput "agency.erpUser.username" "class='form-control'  "  "text" "账户名" true/>
                    </#if>
                <span class="text-danger" id="usernameCheckSpan" ></span>
        </div>
        </div>
        <#if (agency.erpUser.username)?? >

        <#else >
            <#--不存在时进入if-->
            <div class="row">
                <div class="col-md-3 col-md-offset-2">
                    <#--<input style="display:none">-->
                    <#--<label class="control-label">密码</label>-->
                        <#--<span class="glyphicon glyphicon-eye-open"></span>-->
                    <#--<input type="text"  id="erpUser.password" name="erpUser.password"  style="width: 100%;height: 34px"   >-->
                    <@form.textInput "agency.erpUser.password" "class='form-control'" "text" "密码" true/>

                </div>
            </div>
            <div class="row">
                <div class="col-md-3 col-md-offset-2">
                    <div class="form-group ">

                        <label class="control-label">确认密码</label>
                        <input type="text" autocomplete="off" onblur="checkPass();"  id="checkPassword" style="width: 100%;height: 34px"   >
                        <span class="text-danger" id="checkSpan" ></span>

                    </div>
                    <#--<@form.textInput "agency.erpUser.password" "class='form-control'" "text" "确认密码" true/>-->
                </div>
            </div>
        </#if>
        <#--<#if !(agency.erpUser.username)?? >-->
            <div class="row">
                <div class="col-md-3 col-md-offset-2">
                    <@form.textInput "agency.erpUser.realName" "class='form-control'" "text" "代理商名称" true/>
                </div>
            </div>
            <div class="row">
                <div class="col-md-3 col-md-offset-2">
                    <@form.textInput "agency.erpUser.phone" "class='form-control'" "text" "联系电话" true/>
                </div>
            </div>
        <#--</#if>-->


        <div class="row text-center col-lg-10" style="margin-top: 1%">
            <@form.btn_save "onclick='save();'" "保 存" />
        </div>
    </form>
    </@main.frame>
</#escape>