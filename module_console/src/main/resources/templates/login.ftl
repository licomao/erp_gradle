<#import "/macros/generalFrame.ftl" as general />
<#import "/macros/formMacros.ftl" as form />
<#import "/spring.ftl" as spring />


<#escape x as x?html>
<@general.frame title="登录">

<script>
    $(function () {

        $('.dialog').dialog({
            autoOpen : false
        });
        $('.dialog').dialog('open');
    });

    function shopLogin(id) {
        var userid = $("#userid").val();
        window.location = "/shoplogin?shopid=" + id + "&userid=" + userid;
    }
</script>

    <div class=" " style="margin-top:100px;">
        <div class="col-md-2 col-md-offset-5">
            <#if error?? && error.isPresent()>
                <p class="text-danger"><@spring.message code="login.error"/></p>
            </#if>

            <form action='/login' method="post"  id="fm">
                <div class="form-group">
                    <label for="username" class="control-label">用户名</label>
                    <input class="form-control" type="text" name="username" id="username" autofocus>
                </div>
                <div class="form-group">
                    <label for="password" class="control-label">密码</label>
                    <input class="form-control" type="password" name="password" id="password"  onkeydown='if(event.keyCode==13){$("#fm").submit();}'>
                </div>
                <button class="btn btn-primary  btn-block" type="submit">登录</button>
            </form>
        </div>
    </div>

    <#if shops??>
        <div class="dialog" title="请选择门店">
            <div>
                <input type="hidden" name="userid" id="userid" value="${userid}"/>
                <#list shops as userShop>
                    <#if userShop??>
                    <a href="#" type="radio" onclick="shopLogin(${userShop.id})">${userShop.name}</a>
                    </#if>
                </#list>
            </div>
        </div>
    </#if>

</@general.frame>

</#escape>