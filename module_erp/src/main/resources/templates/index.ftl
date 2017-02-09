<#import "macros/mainFrame.ftl" as main />
<#import "/spring.ftl" as spring />

<#escape x as x?html>

    <@main.frame>
    <script type="text/javascript" >
        function blink(selector){
            $(selector).fadeOut('slow', function(){
                $(this).fadeIn('slow', function(){
                    blink(this);
                });
            });
        }
        $(document).ready(function(){
            blink('.blink');
        });
    </script>
    <div class="row">
        <div class="col-md-11">
            <div class="col-md-5 text-center">
                <legend>公司公告信息</legend>
                <div class="row">
                    <div class="col-md-10 text-left">
                        <legend style="color:grey;font-size: 18px;">
                           <#if erpAnnouncement.isNewInfo>
                               <span class="glyphicon glyphicon-volume-up"></span> ${erpAnnouncement.title} <font color="red" class="blink"> NEW!! </font>
                           <#else>
                               <span class="glyphicon glyphicon-volume-up"></span> ${erpAnnouncement.title}
                           </#if>
                        </legend>
                    </div>
                </div>
                <div class="row">
                    <div class="col-md-10 text-left col-md-offset-1">
                        <p style="font-family:'微软雅黑'">${erpAnnouncement.content}</p>
                    </div>
                </div>
                <div class="row">
                    <div class="col-md-10 text-right">
                    ${erpAnnouncement.publisher}
                    </div>
                </div>
            </div>
            <div class="col-lg-offset-1 col-md-5 text-center">
                <legend>平台公告信息</legend>
                <div class="row">
                    <div class="row">
                        <div class="col-md-10 text-left">
                            <legend style="color:grey;font-size: 18px;">  <span class="glyphicon glyphicon-cloud"></span> ${ptAnnouncement.title}</legend>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-10 text-left col-md-offset-1">
                            <p style="font-family:'微软雅黑'">${ptAnnouncement.content}</p>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-10 text-right">
                        ${ptAnnouncement.publisher}
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <div class="row text-center" style="margin-top: 50px;">
        <div class="col-md-10 text-left">
            <legend style="font-size: 15px;color:grey;">
                <span class="glyphicon glyphicon-tags"></span>
                快捷菜单</legend>
        </div>
    </div>
    <div class="row">
        <div class="col-md-12">

                <div class="col-md-2 text-center">
                    <#if AUTHORITYSTR?index_of(",fu82,") != -1>
                    <a class="btn btn-default btn-block" href="/salenote/searchcustominfo" style="height:180px;padding-top:35px;font-size: 58px;color:  rgb(247, 88, 88)">
                        <span class="glyphicon glyphicon-shopping-cart"></span>
                        <p style="font-size: 20px">销售开单</p>
                    </a>  </#if>
                </div>


            <div class="col-md-2 text-center">
            <#if AUTHORITYSTR?index_of(",fu11,") != -1>
                <a class="btn btn-default btn-block" href="/stock/list" style="height:180px;padding-top:35px;font-size: 58px;color:#F7A040">
                    <span class="glyphicon glyphicon-search"></span>
                    <p style="font-size: 20px">库存查询</p>
                </a>  </#if>
            </div>


            <div class="col-md-2 text-center">
            <#if AUTHORITYSTR?index_of(",fu51,") != -1>
                <a class="btn btn-default btn-block" href="/customerpurchasesuite/list" style="height:180px;padding-top:35px;font-size: 58px;color:rgb(64, 190, 64)">
                    <span class="glyphicon glyphicon-credit-card"></span>
                    <p style="font-size: 20px">会员套餐销售</p>
                </a>  </#if>
            </div>


            <div class="col-md-2 text-center">
                <#if AUTHORITYSTR?index_of(",fu5,") != -1>
                <a class="btn btn-default btn-block" href="/customer/list" style="height:180px;padding-top:35px;font-size: 58px;color:dodgerblue">
                    <span class="glyphicon glyphicon-user"></span>
                    <p style="font-size: 20px">顾客查询</p>
                </a>
                </#if>
            </div>
        </div>

    </div>
    <br>
    <div class="row">
        <div class="col-md-12">
            <div class="col-md-2 text-center">
                <#if AUTHORITYSTR?index_of(",fu84,") != -1>
                <a class="btn btn-default btn-block" href="/payment/daysettlecal" style="height:180px;padding-top:35px;font-size: 58px;color: rgb(220, 83, 250)">
                    <span class="glyphicon glyphicon-calendar"></span>
                    <p style="font-size: 20px">营业额汇总分析</p>
                </a>
                </#if>
            </div>

        </div>
    </div>

    </@main.frame>

</#escape>