<#import "../macros/mainFrame.ftl" as main />
<#import "../macros/formMacros.ftl" as form />
<#import "/spring.ftl" as spring />
<#escape x as x?html>
    <@main.frame>

    <script src="/javascripts/heighchat/Highcharts-4.2.3/js/highcharts.js"></script>
    <script src="/javascripts/heighchat/Highstock-4.2.3/js/highstock.js"></script>
    <script src="/javascripts/heighchat/Highmaps-4.2.3/js/highmaps.js"></script>
    <script type="text/javascript">



        $('#collapsePayment').collapse('show');
        $(function () {
            if (!${user.checkAuthority(AUTHORITY.MANAGE_ORG_CALDAYSALEPRICE)?c}) {
                window.location = "/noauthority"
            } else{
                $('.Wdate').datepicker();
                $('.Wdate').datepicker("option",$.datepicker.regional["zh-TW"]);
                var sumProfit = document.getElementById("sumProfit");
                sumProfit.innerHTML = (${sumProfit}*100).toFixed(2) + "%";
                Highcharts.getOptions().colors = Highcharts.map(Highcharts.getOptions().colors, function(color) {
                    return {
                        radialGradient: { cx: 0.5, cy: 0.3, r: 0.7 },
                        stops: [
                            [0, color],
                            [1, Highcharts.Color(color).brighten(-0.3).get('rgb')] // darken
                        ]
                    };
                });
                $('#container').highcharts({
                    chart: {
                        plotBackgroundColor: null,
                        plotBorderWidth: null,
                        plotShadow: false
                    },
                    title: {
                        text: '普通销售分析图 （不含会员套餐）',
                        style: {
                            fontWeight : 'bold',
                            fontSize : '24px'
                        }
                    },
                    tooltip: {
                        pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'
                    },
                    plotOptions: {
                        pie: {
                            allowPointSelect: true,
                            cursor: 'pointer',
                            dataLabels: {
                                enabled: true,
                                color: '#000000',
                                connectorColor: '#000000',
                                formatter: function() {
                                    return '<b>'+ this.point.name +'</b>: '+ this.percentage.toFixed(2) +' %';
                                }
                            }
                        }
                    },
                    series: [{
                        type: 'pie',
                        name: 'Browser share',
                        data: [
                            ['机油('+ ${saleForm.jiyou?c} + '件)',     (${saleForm.jiyou?c}/${saleForm.sum?c})],
                            ['轮胎('+ ${saleForm.luntai?c} + '件)',    ${saleForm.luntai?c}/${saleForm.sum?c} ],
                            ['电瓶('+ ${saleForm.dianpin?c} + '件)',     ${saleForm.dianpin?c}/${saleForm.sum?c}],
                            ['电子类产品('+ ${saleForm.dianzi?c} + '件)',     ${saleForm.dianzi?c}/${saleForm.sum?c}],
                            ['美容类产品('+ ${saleForm.meirong?c} + '件)',     ${saleForm.meirong?c}/${saleForm.sum?c}],
                            ['汽车用品('+ ${saleForm.qicheyongpin?c} + '件)',     ${saleForm.qicheyongpin?c}/${saleForm.sum?c}],
                            ['养护产品('+ ${saleForm.yanghu?c} + '件)',     ${saleForm.yanghu?c}/${saleForm.sum?c}],
                            ['耗材类产品('+ ${saleForm.haocai?c} + '件)',     ${saleForm.haocai?c}/${saleForm.sum?c}],
                            ['灯具类产品('+ ${saleForm.dengju?c} + '件)',     ${saleForm.dengju?c}/ ${saleForm.sum?c}],
                            ['雨刮类产品('+ ${saleForm.yugua?c} + '件)',     ${saleForm.yugua?c}/${saleForm.sum?c}],
                            ['发动机配件类('+ ${saleForm.fadongji?c} + '件)',     ${saleForm.fadongji?c}/${saleForm.sum?c}],
                            ['底盘配件类('+ ${saleForm.dipanpeijian?c} + '件)',     ${saleForm.dipanpeijian?c}/${saleForm.sum?c}],
                            ['变速箱类('+ ${saleForm.biansuxiang?c} + '件)',     ${saleForm.biansuxiang?c}/${saleForm.sum?c}],
                            ['电气类('+ ${saleForm.dianqi?c} + '件)',     ${saleForm.dianqi?c}/${saleForm.sum?c}],
                            ['车身覆盖类('+ ${saleForm.fugai?c} + '件)',     ${saleForm.fugai?c}/${saleForm.sum?c}],
                            ['服务类('+ ${saleForm.fuwu?c} + '次)',     ${saleForm.fuwu?c}/${saleForm.sum?c}],
                            ['临时分类('+ ${saleForm.temp?c} + '件)',     ${saleForm.temp?c}/${saleForm.sum?c}],
                        ]
                    }]
                });

            }
        });

        function subForm(){
            $("#fm").submit();
        }



    </script>
    <div class="row">
        <legend>营业额汇总分析</legend>
    </div>
    <form id="fm" action="/payment/daysettlecal" method="get">
        <div class="row">
            <div class="col-md-10 text-center">
                <label  class="control-label">所属门店: </label>&nbsp;
                <select name="shopId" id="shopId">
                    <#list shops as shop>
                        <option value="${shop.id}" <#if chooseShopId ==shop.id>selected</#if>>${shop.name}</option>
                    </#list>
                </select>&nbsp;
                <label class="control-label" id="timNow">统计日期：
                    <input type="text" name="calDateStart" id="calDateStart" class="Wdate" value="${calDateStart}"  readonly>&nbsp;
                   - &nbsp;
                    <input type="text" name="calDateEnd" id="calDateEnd" class="Wdate" value="${calDateEnd}"  readonly>
                </label>
                &nbsp;&nbsp;
            <@form.btn_pages "onclick='subForm();'" "开始计算"/>
            </div>
        </div>
    </form>
    <div class="row" style="margin-top: 20px;">
        <div class="col-md-5"  >
            <div class="row text-center">
                <legend>
                    <label>营业额分类汇总</label>
                </legend>
            </div>
            <div class="row">
                <div class="col-md-offset-1 col-md-8">
                    <label class="control-label">现金总额：</label>
                </div>
                <div class="col-md-3">
                    ${paymentToPage.cashAmount} 元
                </div>
            </div>
            <div class="row">
                <div class="col-md-offset-1 col-md-8">
                    <label class="control-label">POS总额：</label>
                </div>
                <div class="col-md-3">
                ${paymentToPage.posAmount} 元
                </div>
            </div>
            <div class="row">
                <div class="col-md-offset-1 col-md-8">
                    <label class="control-label">APP总额：</label>
                </div>
                <div class="col-md-3">
                ${paymentToPage.appAmount} 元
                </div>
            </div>
            <div class="row">
                <div class="col-md-offset-1 col-md-8">
                    <label class="control-label">第三方费用总额：</label>
                </div>
                <div class="col-md-3">
                ${paymentToPage.otherAmount} 元
                </div>
            </div>
            <div class="row">
                <div class="col-md-offset-1 col-md-8">
                    <label class="control-label">普通销售总额：</label>
                </div>
                <div class="col-md-3">
                ${allAmount} 元
                </div>
            </div>
            <div class="row">
                <div class="col-md-offset-1 col-md-8">
                    <label class="control-label">会员套餐销售总额：</label>
                </div>
                <div class="col-md-3">
                ${allPurchasedAmount} 元
                </div>
            </div>
            <div class="row">
                <div class="col-md-offset-1 col-md-8">
                    <label class="control-label">总合计：</label>
                </div>
                <div class="col-md-3">
                ${paymentToPage.amount} 元
                </div>
            </div>
            <br><br>

        </div>

        <div class="col-lg-offset-1 col-md-5" >
            <div class="row text-center">
                <legend>
                    <label>经营数据统计</label>
                </legend>
            </div>
            <div class="row">
                <div class="col-md-offset-1 col-md-8">
                    <label class="control-label">未结算施工单据数量：</label>
                </div>
                <div class="col-md-3">
                 ${notFinishedNum}
                </div>
            </div>
            <div class="row">
                <div class="col-md-offset-1 col-md-8">
                    <label class="control-label">零收入施工单数量(不包含会员套餐)：</label>
                </div>
                <div class="col-md-3">
                    ${zeroOrders}
                </div>
            </div>
            <div class="row">
                <div class="col-md-offset-1 col-md-8">
                    <label class="control-label">总成本:</label>
                </div>
                <div class="col-md-3">
               ${sumCost} 元
                </div>
            </div>
            <div class="row">
                <div class="col-md-offset-1 col-md-8">
                    <label class="control-label">总毛利: </label>
                </div>
                <div class="col-md-3">
                ${paymentToPage.amount - sumCost} 元
                </div>
            </div>
            <div class="row">
                <div class="col-md-offset-1 col-md-8">
                    <label class="control-label">总毛利率：</label>
                </div>
                <div class="col-md-3" id="sumProfit">
                </div>
            </div>
            <div class="row">
                <div class="col-md-offset-1 col-md-8">
                    <label class="control-label"><font color="red">总毛利率 = (总合计 - 总成本) ÷ 总合计</font></label>

                </div>
            </div>
            <br>
            <div class="row text-center">
            </div>
        </div>
    </div>
    <div class="row text-center" style="margin-top: 15px;">
        <div id="container" class="col-md-10" ></div>
    </div>
    <div class="row text-center" style="margin-top: 5px;">
       <label class="control-label">销售商品总数：${saleForm.sum}件</label>
    </div>
    </@main.frame>

</#escape>