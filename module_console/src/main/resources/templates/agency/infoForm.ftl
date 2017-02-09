<#import "../macros/mainFrame.ftl" as main />
<#import "../macros/formMacros.ftl" as form />
<#import "/spring.ftl" as spring />
<#escape x as x?html>
    <@main.frame>
    <script type="text/javascript" xmlns="http://www.w3.org/1999/html">
        $('#collapsePt').collapse('show');
        $(function() {
            $("input[id='erpUser.username']").change(function(){
                $.get('/erpuser/username/check?username='+$(this).val(),function(isExist){
                    var text = isExist ? "改用户名已存在":"";
                    $("#usernameCheckSpan").text(text);
                });
            });
        });

        function save() {
            isUpdate = document.getElementById("oldPassword");
            var password = $("[id='erpUser.password']").val();
            var passwordCheck = $("#checkPassword").val();
            if(isUpdate){
                //修改
//                alert("更新")
                var oldPassword = $("#oldPassword").val();
                if(oldPassword == "" || password == "" || passwordCheck == "" || password != passwordCheck){
                    return ;
                }

            }else{
                //新增
//                alert("新增")
                if(password == "" || passwordCheck == "" || password != passwordCheck){
                    return ;
                }

            }

            if(confirm("是否确认保存")){
                $("#fm").submit();
            }
        }

        function checkPass(){
            var password = $("[id='erpUser.password']").val();
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
        <div class="row">
            <@form.labelAndTextInput "agency.id" "class='form-control'" "hidden" ""/>
            <#--<@form.labelAndTextInput "erpUser.organization.id" "class='form-control'" "hidden" ""/>-->
        </div>
        <div class="row">
            <div class="col-md-3 col-md-offset-2">
                <#assign id="${spring.status.expression?replace('[','')?replace(']','')}">
                <label class="control-label">账户名</label>
                    <input type="text" name="erpUser.username" value="${agency.erpUser.username}"
                           <#if (agency.erpUser.username)?? >readonly="readonly"</#if> id="erpUser.username"
                           style="width: 100%;height: 34px" >
                <#if error?has_content><span class="text-danger">${spring.status.errorMessages?first}</span></#if>
                    <span class="text-danger" id="usernameCheckSpan" ></span>
               <#-- <#if (agency.erpUser.username)?? >

                    <input type="password" name="agency.erpUser.username" <#if (agency.erpUser.username)?? >readonly="readonly"</#if> id="erpUser.username" style="width: 100%;height: 34px" >
                <#else >
                    <input type="password" name="agency.erpUser.username" readonly="readonly" id="erpUser.username" style="width: 100%;height: 34px" >
                </#if>-->

                <br>
            </div>

        </div>
        <#if (agency.erpUser.username)?? >
            <div class="row">
                <div class="col-md-3 col-md-offset-2">
                    <label class="control-label">原密码</label>
                    <input type="password" autocomplete="off"  id="oldPassword" style="width: 100%;height: 34px"   >
                    <#--<span class="text-danger" id="checkSpan" ></span>-->
                </div>
            </div>
        </#if>
        <div class="row">
            <div class="col-md-3 col-md-offset-2">
                <input style="display:none">
                <label class="control-label">密码</label>
                <input type="password"  id="erpUser.password" name="erpUser.password" style="width: 100%;height: 34px"   >
                <#--<@form.textInput "agency.erpUser.password" "class='form-control'" "password" "密码" true/>-->
            </div>
        </div>
        <div class="row">
            <div class="col-md-3 col-md-offset-2">
                <div class="form-group ">

                    <label class="control-label">确认密码</label>
                    <input type="password" autocomplete="off" onchange="checkPass();" id="checkPassword" style="width: 100%;height: 34px"   >
                    <span class="text-danger" id="checkSpan" ></span>

                </div>
                <#--<@form.textInput "agency.erpUser.password" "class='form-control'" "text" "确认密码" true/>-->
            </div>
        </div>
        <div class="row">
            <div class="col-md-3 col-md-offset-2">
                <@form.textInput "agency.erpUser.phone" "class='form-control'" "text" "联系电话" true/>
            </div>
        </div>

        <div class="row text-center col-lg-10" style="margin-top: 1%">
            <@form.btn_save "onclick='save();'" "保 存" />
        </div>
    </form>
    </@main.frame>
</#escape>