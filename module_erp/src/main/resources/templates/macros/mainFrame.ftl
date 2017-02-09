<#import "../macros/generalFrame.ftl" as general />
<#import "/spring.ftl" as spring />
<#import "accordionItem.ftl" as aci />
<#escape x as x?html>

<#assign title><@spring.message code="title"/></#assign>
<#macro frame>
    <@general.frame title="${title}">
        <#if !Session.user??>
            <script>
                window.location  = "/logout";
            </script>
        </#if>
        <#if Session.user.organization.id == 3>
            <nav class="navbar navbar-inverse navbar-fixed-top " style="min-height: 120px;background: url(/stylesheets/images/erp/taier_head.jpg) no-repeat ">
        <#else >
            <#if Session.user.organization.agency.id == 5><!-- 缘车园 -->
            <nav class="navbar navbar-inverse navbar-fixed-top " style="min-height: 120px;background: url(/stylesheets/images/erp/ycy_head.png) no-repeat ">
            <#elseif Session.user.organization.agency.id == 6>  <!-- 台州 -->
            <nav class="navbar navbar-inverse navbar-fixed-top " style="min-height: 120px;background: url(/stylesheets/images/erp/taizhou_head.png) no-repeat ">
            <#elseif Session.user.organization.id == 42>  <!-- 钜轩 -->
            <nav class="navbar navbar-inverse navbar-fixed-top " style="min-height: 120px;background: url(/stylesheets/images/erp/juxuan_header.jpg) no-repeat ">
            <#else>
            <nav class="navbar navbar-inverse navbar-fixed-top " style="min-height: 120px;background: url(/stylesheets/images/erp/head.jpg) no-repeat ">
            </#if>
        </#if>



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
            <div id="navbar" class="navbar-collapse collapse " style="margin-top: 50px;margin-right:10px;">
                <ul class="nav navbar-nav navbar-right">
                    <li><a style="font-weight:bold" href="/index">首页</a></li>
                    <li><a style="font-weight:bold" href="#"><span class="glyphicon glyphicon-circle-arrow-right"></span>当前登录门店：
                        <#if !SHOP??>
                            <#if !AUTHORITYSTR??>
                                <script>
                                    window.location  = "/logout";
                                </script>
                            </#if>
                        <#else >
                        ${SHOP.name}
                        </#if>
                    </a></li>
                    <li><a style="font-weight:bold" href="#"><span class="glyphicon glyphicon-user"></span>
                     <#if !Session.user??>
                         <#if !AUTHORITYSTR??>
                        <script>
                            window.location  = "/logout";
                        </script>
                         </#if>
                    <#else >
                        <#if Session.user.realName??>
                         ${Session.user.realName}
                        <#else >
                        ${Session.user.username}
                        </#if>
                    </#if>
                    </a></li>
                    <li><a style="font-weight:bold" onclick="updatePassword()" href="#"><span class="glyphicon glyphicon-wrench"></span>修改密码</a></li>
                    <li><a style="font-weight:bold" href="#"><span class="glyphicon glyphicon-question-sign"></span> 帮助</a></li>
                    <li><a style="font-weight:bold" href="/logout">
                        <span class="glyphicon glyphicon-log-out"></span> 退出</a></li>
                </ul>
            </div>
        </div>
    </nav>

    <div class="container-fluid">
        <div class="row"style="padding-top:15px ">
            <div class="col-sm-3 col-md-2 sidebar" style="margin-top:10px">
                <div class="panel-group" id="accordion" style="margin-top: 70px;">
                    <#if AUTHORITYSTR??>
                    <#if AUTHORITYSTR?index_of(",bigfu10,") != -1>
                        <@aci.accordionItem  itemId="collapseOrg" itemLabel="组织后台管理">
                            <#if AUTHORITYSTR?index_of(",fu91,") != -1>
                                <li class="list-group-item"><a href="/roleauthorization/list">角色管理</a></li>
                            </#if>
                            <#if AUTHORITYSTR?index_of(",fu92,") != -1>
                                <li class="list-group-item"><a href="/account/list">账户管理</a></li>
                            </#if>
                            <#if AUTHORITYSTR?index_of(",fu93,") != -1>
                                <li class="list-group-item"><a href="/shop/list">门店管理</a></li>
                            </#if>
                            <#if AUTHORITYSTR?index_of(",fu94,") != -1>
                                <li class="list-group-item"><a href="/secondaryitem/list">商品二级分类管理</a></li>
                            </#if>
                            <#if AUTHORITYSTR?index_of(",fu95,") != -1>
                                <li class="list-group-item"><a href="/announcement/list">ERP公告管理</a></li>
                            </#if>
                                <#--<li class="list-group-item" ><a href="/fingerprint/list">指纹机管理</a></li>-->
                        </@aci.accordionItem>
                    </#if>
                    <#if AUTHORITYSTR?index_of(",bigfu1,") != -1>
                        <@aci.accordionItem  itemId="collapseShop" itemLabel="门店后台管理">
                            <#if AUTHORITYSTR?index_of(",fu1,") != -1>
                                <li class="list-group-item"><a href="/fixedasset/list">固定资产管理</a></li>
                            </#if>
                            <#if AUTHORITYSTR?index_of(",fu2,") != -1>
                                <li class="list-group-item"><a href="/material/list">耗材领用管理</a></li>
                            </#if>
                            <#if AUTHORITYSTR?index_of(",fu3,") != -1>
                                <li class="list-group-item"><a href="/supplier/list">供应商管理</a></li>
                            </#if>
                            <#if AUTHORITYSTR?index_of(",fu4,") != -1>
                                <li class="list-group-item"><a href="/customstockitem/list">商品管理</a></li>
                            </#if>
                            <#if AUTHORITYSTR?index_of(",fu5,") != -1>
                                <li class="list-group-item"><a href="/customer/list">顾客管理</a></li>
                            </#if>
                            <#if AUTHORITYSTR?index_of(",fu6,") != -1>
                                <li class="list-group-item"><a href="/expense/list">费用管理</a></li>
                            </#if>
                        </@aci.accordionItem>
                    </#if>
                    <#if AUTHORITYSTR?index_of(",bigfu9,") != -1>
                        <@aci.accordionItem itemId="collapsePayment" itemLabel="门店收银">
                            <#if AUTHORITYSTR?index_of(",fu81,") != -1>
                                <li class="list-group-item"><a href="/payment/list">客户预约管理</a></li>
                            </#if>
                            <#if AUTHORITYSTR?index_of(",fu82,") != -1>
                                <li class="list-group-item"><a href="/salenote/searchcustominfo">销售开单</a></li>
                            </#if>
                            <#if AUTHORITYSTR?index_of(",fu83,") != -1>
                                <li class="list-group-item"><a href="/salenote/searchsettleinfo">销售开单查询</a></li>
                            </#if>
                            <#if AUTHORITYSTR?index_of(",fu85,") != -1>
                                <li class="list-group-item"><a href="/salenote/searchorderdeatial">销售开单明细查询</a></li>
                            </#if>
                            <#if AUTHORITYSTR?index_of(",fu84,") != -1>
                                <li class="list-group-item"><a href="/payment/daysettlecal">营业额汇总分析</a></li>
                            </#if>
                        </@aci.accordionItem>
                    </#if>
                    <#if AUTHORITYSTR?index_of(",bigfu2,") != -1>
                        <@aci.accordionItem  itemId="collapseStock" itemLabel="库存管理">
                            <#if AUTHORITYSTR?index_of(",fu11,") != -1>
                                <li class="list-group-item"><a href="/stock/list">库存查询</a></li>
                            </#if>
                            <#if AUTHORITYSTR?index_of(",fu12,") != -1>
                                <li class="list-group-item"><a href="/stockingorder/list">库存盘点</a></li>
                            </#if>
                            <#if AUTHORITYSTR?index_of(",fu13,") != -1>
                                <li class="list-group-item"><a href="/stocktransferorder/list">库存调拨</a></li>
                            </#if>
                        </@aci.accordionItem>
                    </#if>
                    <#if AUTHORITYSTR?index_of(",bigfu3,") != -1>
                        <@aci.accordionItem  itemId="collapseBase" itemLabel="决策">
                            <#if AUTHORITYSTR?index_of(",fu21,") != -1>
                                <li class="list-group-item"><a href="/baseset/tosave">基础数据设置</a></li>
                            </#if>
                            <#if AUTHORITYSTR?index_of(",fu22,") != -1>
                                <li class="list-group-item"><a href="/monthdata/dataview">月度数据统计</a></li>
                            </#if>

                        </@aci.accordionItem>
                    </#if>
                    <#if AUTHORITYSTR?index_of(",bigfu4,") != -1>
                        <@aci.accordionItem  itemId="collapsePurchase" itemLabel="采购单管理">
                            <#if AUTHORITYSTR?index_of(",fu31,") != -1>
                                <li class="list-group-item"><a href="/purchaseorder/search/list">采购单申请及查询</a></li>
                            </#if>
                            <#if AUTHORITYSTR?index_of(",fu32,") != -1>
                                <li class="list-group-item"><a href="/purchaseorder/approve/list">采购单审批</a></li>
                            </#if>
                            <#if AUTHORITYSTR?index_of(",fu33,") != -1>
                                <li class="list-group-item"><a href="/purchaseorder/addstorage/list">采购单入库</a></li>
                            </#if>
                            <#if AUTHORITYSTR?index_of(",fu34,") != -1>
                            <li class="list-group-item"><a href="/purchasepayment/willpaylist">未付款采购单查询</a></li>
                            </#if>
                            <#if AUTHORITYSTR?index_of(",fu35,") != -1>
                            <li class="list-group-item"><a href="/purchasepayment/paymentlist?type=0">采购付款单</a></li>
                            </#if>
                            <#if AUTHORITYSTR?index_of(",fu36,") != -1>
                                <li class="list-group-item"><a href="/purchasepayment/paymentlist?type=1">采购付款单(可作废)</a></li>
                            </#if>
                        </@aci.accordionItem>
                    </#if>
                    <#if AUTHORITYSTR?index_of(",bigfu5,") != -1>
                        <@aci.accordionItem  itemId="collapseRefundOrder" itemLabel="供应商退货管理">
                            <#if AUTHORITYSTR?index_of(",fu41,") != -1>
                                <li class="list-group-item"><a href="/refundorder/search/list">退货单管理</a></li>
                            </#if>
                            <#if AUTHORITYSTR?index_of(",fu42,") != -1>
                                <li class="list-group-item"><a href="/refundorder/approve/list">退货单审批</a></li>
                            </#if>
                        </@aci.accordionItem>
                    </#if>
                    <#if AUTHORITYSTR?index_of(",bigfu6,") != -1>
                        <@aci.accordionItem  itemId="collapseCustomerPurchasedSuite" itemLabel="会员管理">
                            <#if AUTHORITYSTR?index_of(",fu51,") != -1>
                                <li class="list-group-item"><a href="/customerpurchasesuite/list">会员套餐销售</a></li>
                            </#if>
                            <#if AUTHORITYSTR?index_of(",fu52,") != -1>
                                <li class="list-group-item"><a href="/customerpurchasesuite/remote/list">会员套餐异地消费</a></li>
                            </#if>
                            <#if AUTHORITYSTR?index_of(",fu53,") != -1>
                                <li class="list-group-item"><a href="/customerpurchasesuite/vipcard/list">设置会员套餐种类</a></li>
                            </#if>
                        </@aci.accordionItem>
                    </#if>
                    <#if AUTHORITYSTR?index_of(",bigfu7,") != -1>
                        <@aci.accordionItem  itemId="collapseStaff" itemLabel="人事管理">
                            <#if AUTHORITYSTR?index_of(",fu61,") != -1>
                                <li class="list-group-item"><a href="/staff/list">员工管理</a></li>
                            </#if>
                            <#if AUTHORITYSTR?index_of(",fu62,") != -1>
                                <li class="list-group-item"><a href="/job/list">职位设置</a></li>
                            </#if>
                            <#if AUTHORITYSTR?index_of(",fu63,") != -1>
                                <li class="list-group-item"><a href="/staffattendance/list">员工考勤查询</a></li>
                            </#if>
                            <#if AUTHORITYSTR?index_of(",fu64,") != -1>
                                <li class="list-group-item"><a href="/staffattendance/form">上下班考勤</a></li>
                            </#if>
                            <#if AUTHORITYSTR?index_of(",fu65,") != -1>
                                <li class="list-group-item"><a href="/staffattendance/formTest">上下班考勤(指纹)</a></li>
                            </#if>
                        </@aci.accordionItem>
                    </#if>
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
<#--<style type="text/css">
    #updatePassword {
        display: none;
    }
</style>-->
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