<#import "../macros/generalFrame.ftl" as general />
<#import "/spring.ftl" as spring />
<#import "accordionItem.ftl" as aci />
<#escape x as x?html>

<#assign title><@spring.message code="title"/></#assign>
<#macro frame>
    <@general.frame title="${title}">
    <nav class="navbar navbar-inverse navbar-fixed-top " style="min-height: 120px;background: url(/stylesheets/images/erp/head.jpg) no-repeat ">
        <div class="container-fluid" >
            <div class="navbar-header">
                <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar" aria-expanded="false" aria-controls="navbar">
                    <span class="sr-only">Toggle navigation</span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                </button>
                <#--<a class="navbar-brand" href="#">${title}</a>-->
            </div>
            <div id="navbar" class="navbar-collapse collapse " style="margin-top: 30px;margin-right:10px;">
                <ul class="nav navbar-nav navbar-right">
                    <li><a style="font-weight:bold" href="#">${Session.USER_NAME}</a></li>
                    <li><a style="font-weight:bold" href="#">设置</a></li>
                    <li><a style="font-weight:bold" onclick="updatePassword()" href="#"><span class="glyphicon glyphicon-wrench"></span>修改密码</a></li>

                    <#--<li><a style="font-weight:bold" href="/login/useredit">修改密码</a></li>-->
                    <li><a style="font-weight:bold" href="#">帮助</a></li>
                    <li><a style="font-weight:bold" href="/logout">退出</a></li>
                </ul>
            </div>
        </div>
    </nav>

    <div class="container-fluid">
        <div class="row"style="padding-top:15px ">
            <div class="col-sm-3 col-md-2 sidebar" style="margin-top:10px">
                <div class="panel-group" id="accordion" style="margin-top: 70px;">
                    <#if AUTHORITYSTR?index_of(",bigFunc1,") != -1>
                        <@aci.accordionItem  itemId="collapseOrgs" itemLabel="组织管理">
                            <#if AUTHORITYSTR?index_of(",func1,") != -1>
                                <li class="list-group-item"><a href="/organizations/list">查看组织</a></li>
                            </#if>
                            <#if AUTHORITYSTR?index_of(",func2,") != -1>
                                <li class="list-group-item"><a href="/roleauthorization/list">角色管理</a></li>
                            </#if>
                        </@aci.accordionItem>
                    </#if>
                    <#if AUTHORITYSTR?index_of(",bigFunc2,") != -1>
                        <@aci.accordionItem itemId="collapsePt" itemLabel="平台管理">
                            <#if AUTHORITYSTR?index_of(",func14,") != -1>
                                <li class="list-group-item"><a href="/campaign/list">APP公告管理</a></li>
                            </#if>
                            <#if AUTHORITYSTR?index_of(",func11,") != -1>
                                <li class="list-group-item"><a href="/erpannouncement/list">平台公告管理</a></li>
                            </#if>
                            <#if AUTHORITYSTR?index_of(",func12,") != -1>
                                <li class="list-group-item"><a href="/agency/list">代理商管理</a></li>
                            </#if>
                            <#if AUTHORITYSTR?index_of(",func13,") != -1>
                                <li class="list-group-item"><a href="#">供应商管理</a></li>
                            </#if>
                            <#if AUTHORITYSTR?index_of(",func15,") != -1>
                                <li class="list-group-item"><a href="####">设置会员套餐种类</a></li>
                            </#if>
                            <#if AUTHORITYSTR?index_of(",func16,") != -1>
                                <li class="list-group-item"><a href="/saleshelf/list">APP上架管理</a></li>
                            </#if>
                            <#if AUTHORITYSTR?index_of(",func17,") != -1>
                                <li class="list-group-item"><a href="/customstockitem/list">商品管理</a></li>
                            </#if>
                            <#if AUTHORITYSTR?index_of(",func18,") != -1>
                                <li class="list-group-item" ><a href="/fingerprint/list">指纹机管理</a></li>
                            </#if>
                            <#if AUTHORITYSTR?index_of(",func19,") != -1>
                            <li class="list-group-item" ><a href="/purchasepayment/paymentlist">付款单流水查询</a></li>
                            </#if>
                        </@aci.accordionItem>
                    </#if>
                </div>
            </div>
            <div class="col-sm-9 col-sm-offset-3 col-md-10 col-md-offset-2 main"  style="margin-top:63px">
                <div id="updatePassword"  class="dialog" title="请输入原账户密码及新密码" style="display: none ;text-align: center;padding-top: 34px">
                    <form id="changePwdForm" >

                        <label style="text-indent: 2em">旧密码：</label><input type="password" style="display: none" ><input id="oldPasswordIndex" type="password"/><br>

                        <br>
                        <label style="text-indent: 2em">新密码：</label><input id="newPasswordIndex" type="password"/><br>
                        <br>
                        <label>确认新密码：</label><input id="newPasswordCheck" onchange="checkPass()" type="password"/><span id="checkSpan" ></span><br>
                        <br>

                        <br>

                        <input type="button" value="提交" class="btn btn-primary" onclick="indexPageUpdatePassword()">
                    </form>
                </div>
                <#nested/>
            </div>
        </div>
    </div>
</@general.frame>
<script>


        <#if !SHOP?? >
        window.location  = "/logout";
        </#if>



    function updatePassword(){
        $('#updatePassword').dialog({
            autoOpen: false
        });
        $('#updatePassword').dialog('open');
//        alert(1)
    }
    function checkPass(){
        var newPassword = $("#newPasswordIndex").val();
        var newPasswordCheck = $("#newPasswordCheck").val();
        if(newPassword != newPasswordCheck){
            $("#checkSpan").text("两次输入的密码不一致");
        }else{
            $("#checkSpan").text("");
        }



    }
    function indexPageUpdatePassword(){
        var oldPassword = $("#oldPasswordIndex").val();
        var newPassword = $("#newPasswordIndex").val();
        var newPasswordCheck = $("#newPasswordCheck").val();
        if(oldPassword == "" || newPassword == "" || newPasswordCheck == "" || newPassword != newPasswordCheck){
//            alert("")
            return ;
        }

        $('#updatePassword').dialog('close');
//        $("#changePwdForm").submit();
        $.post('/login/index/changepwd?oldPwd='+oldPassword+"&newPwd="+newPassword,function(data){
            if(data){

                alert("修改成功!")
                window.location  = "/logout";
            }else{
                alert("密码错误!")
            }
        });

//        alert(1)
//        if(oldPassword)
    }
</script>
</#macro>

</#escape>