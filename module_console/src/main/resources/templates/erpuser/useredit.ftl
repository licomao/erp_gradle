<#import "../macros/mainFrame.ftl" as main />
<#import "../macros/formMacros.ftl" as form />
<#import "/spring.ftl" as spring />
<#escape x as x?html>
    <@main.frame>

    <script type="text/javascript">
        function changePwd(){
            var oldpwd = $("#oldPwd").val();
            var newpwd = $("#newPwd").val();
            var hidpwd = $("#hidpwd").val();
            if(oldpwd == null || oldpwd == "" || oldpwd.trim() == "") {
                alert("旧密码不能为空");
                $("#oldPwd").val("");
                return;
            }
            if(newpwd == null || newpwd == "" || newpwd.trim() == "") {
                alert("新码不能为空");
                $("#newPwd").val("");
                return;
            }
            if( hidpwd == null || hidpwd.trim() != oldpwd.trim()) {
                alert("旧密码不对");
                $("#oldPwd").val("");
                return;
            }
            if(newpwd.trim() == hidpwd.trim()) {
                alert("新旧密码一样");
                $("#newPwd").val("");
                return;
            }
            $("#f1").submit();
        }

    </script>

    <div class="row">
        <div class="col-md-5 col-md-offset-2">
            <form action='/login/changepwd' id="f1" method="post">
                <div class="form-group">
                    <label for="username" class="control-label">旧密码</label>
                    <input class="form-control" type="password" name="oldPwd" id="oldPwd" autofocus>
                </div>
                <div class="form-group">
                    <label for="password" class="control-label">新密码</label>
                    <input class="form-control" type="password" name="newPwd" id="newPwd">
                </div>
                <button class="btn btn-primary  btn-block" type="button" onclick="changePwd()">保存</button>
                <br/>

                <label  class="control-label" id="mynotify">${notify}</label>
                <input class="form-control" type="hidden"  id="hidpwd" value="${pwd}">

            </form>
        </div>
    </div>
    </@main.frame>

</#escape>