<#import "macros/generalFrame.ftl" as general />
<#import "macros/formMacros.ftl" as form />
<#import "/spring.ftl" as spring />


<#escape x as x?html>
    <@general.frame title="修车么™ SAAS业务系统">
    <#--<@general.frame title="骜顺SAAS业务系统">-->
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
<body style="background-color: #cccccc;">
    <div class=" " style="margin-top:100px;width: 98%">
        <img src="">
        <div class="row text-center">
        <img src="/stylesheets/images/erp/index_logo.png"  width="250px;">
            <p style="font-size: 25px;">
                修车么™ SAAS业务系统
            </p>
        </div>

        <#--<div class="row text-center">-->
            <#--&lt;#&ndash;<img src="/stylesheets/images/erp/index_logo.png"  width="250px;">&ndash;&gt;-->
                <#--<img src="/stylesheets/images/erp/aoshun.png"  width="550px;">-->
            <#--<p style="font-size: 25px;padding-top:20px;">-->
                <#--骜顺SAAS业务系统-->
            <#--</p>-->
        <#--</div>-->
        <div class="col-md-2 col-md-offset-5">
            <#if error?? && error.isPresent()>
                <p class="text-danger"><@spring.message code="login.error"/></p>
            </#if>


            <form action='/login' method="post"  id="fm">
                <div class="form-group">
                    <label for="username" class="control-label" >用户名</label>
                    <input class="form-control" type="text" name="username" id="username" autofocus>
                </div>
                <div class="form-group">
                    <label for="password" class="control-label"  >密码</label>
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
</body>
    </@general.frame>

</#escape>