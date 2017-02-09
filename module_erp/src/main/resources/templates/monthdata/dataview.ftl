<#import "../macros/mainFrame.ftl" as main />
<#import "../macros/formMacros.ftl" as form />
<#import "/spring.ftl" as spring />
<#escape x as x?html>

    <@main.frame>
    <meta http-equiv="Windows-Target" contect="_top">
    <script type="text/javascript">
        $('#collapseBase').collapse('show');
        function cal() {
            $("#fm").submit();
        }
    </script>
    <legend>决策 -> 月度数据统计</legend>
    <div class="row">
        <div class="col-md-11">
            <div id="actions" class="form-action">
                <form   id="fm" action='<@spring.url relativeUrl = "/monthdata/dataview"/>'   method="GET">
                    <div class="row">
                        <div class="col-md-6">
                            <label class="control-label">统计年份与月份：</label>
                            <select id="year" name="year">
                                <#list years as y >
                                    <option <#if year  == y >selected</#if> value="${y}">${y}年 </option>
                                </#list>
                            </select>
                            &nbsp;一&nbsp;
                            <select id="month" name="month">
                                <option <#if month  == "1" >selected</#if> value="1">1月</option>
                                <option <#if month  == "2" >selected</#if> value="2">2月</option>
                                <option <#if month  == "3" >selected</#if> value="3">3月</option>
                                <option <#if month  == "4" >selected</#if> value="4">4月</option>
                                <option <#if month  == "5" >selected</#if> value="5">5月</option>
                                <option <#if month  == "6" >selected</#if> value="6">6月</option>
                                <option <#if month  == "7" >selected</#if> value="7">7月</option>
                                <option <#if month  == "8" >selected</#if> value="8">8月</option>
                                <option <#if month  == "9" >selected</#if> value="9">9月</option>
                                <option <#if month  == "10" >selected</#if> value="10">10月</option>
                                <option <#if month  == "11" >selected</#if> value="11">11月</option>
                                <option <#if month  == "12" >selected</#if> value="12">12月</option>
                            </select>
                            &nbsp;
                            <label class="control-label">统计门店：</label>
                            <select name="shopId" id="shopId">
                                <#list shopList as shop>
                                    <option <#if shopId == shop.id>selected</#if> value="${shop.id}">${shop.name}</option>
                                </#list>
                            </select>
                        </div>
                        <div class="col-md-3">
                            <@form.btn_calculator "onclick='cal();'" "统  计" />
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-9">
                        <legend>&nbsp;</legend>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-2 text-center">
                           <label class="control-label">月度营业额汇总</label>
                        </div>
                        <div class="col-md-2 text-center">
                            <label class="control-label">月度库存汇总</label>
                        </div>
                        <div class="col-md-2 text-center">
                            <label class="control-label">月度费用汇总</label>
                        </div>
                        <div class="col-md-2 text-center">
                            <label class="control-label">固定资产汇总</label>
                        </div>
                    </div>
                    <div class="row" style="margin-top: 5px;">
                        <div class="col-md-1">
                            现金：
                        </div>
                        <div class="col-md-1 text-right">
                            <nobr>  ${payment.cashAmount} 元</nobr>
                        </div>
                        <div class="col-md-1">
                            期初总金额：
                        </div>
                        <div class="col-md-1 text-right">
                            <nobr>  ${startStockingSum} 元</nobr>
                        </div>
                        <div class="col-md-1">
                            耗材费用：
                        </div>
                        <div class="col-md-1 text-right">
                            <nobr> ${materialSum} 元</nobr>
                        </div>
                        <div class="col-md-1">
                            总计：
                        </div>
                        <div class="col-md-1 text-right">
                            <nobr>  ${fixAssetSum} 元</nobr>
                        </div>
                    </div>

                    <div class="row" style="margin-top: 5px;">
                        <div class="col-md-1">
                            POS：
                        </div>
                        <div class="col-md-1 text-right">
                            <nobr> ${payment.posAmount} 元</nobr>
                        </div>
                        <div class="col-md-1">
                            期末总金额：
                        </div>
                        <div class="col-md-1 text-right">
                            <nobr>  ${stockingCostSum} 元</nobr>
                        </div>
                        <div class="col-md-1">
                           日常费用：
                        </div>
                        <div class="col-md-1 text-right"><nobr>${expenseSum} 元</nobr>
                        </div>
                        <div class="col-md-1">
                        </div>
                        <div class="col-md-1">
                        </div>
                    </div>

                    <div class="row" style="margin-top: 5px;">
                        <div class="col-md-1">
                            APP：
                        </div>
                        <div class="col-md-1 text-right">
                            <nobr>${payment.appAmount} 元</nobr>
                        </div>
                        <div class="col-md-1">
                            盘盈/亏：
                        </div>
                        <div class="col-md-1  text-right">
                            <nobr>${panYK} 元</nobr>
                        </div>
                        <div class="col-md-1">
                            人力成本：
                        </div>
                        <div class="col-md-1 text-right">
                            <nobr> ${staffExpenseSum} 元</nobr>
                        </div>
                        <div class="col-md-1">
                        </div>
                        <div class="col-md-1">
                        </div>
                    </div>
                    <div class="row" style="margin-top: 5px;">
                        <div class="col-md-1">
                            总收入：
                        </div>
                        <div class="col-md-1 text-right">
                            <nobr> ${payment.amount} 元</nobr>
                        </div>
                        <div class="col-md-1">
                        </div>
                        <div class="col-md-1">
                        </div>
                        <div class="col-md-1">
                            总计：
                        </div>
                        <div class="col-md-1 text-right">
                            <nobr> ${expenseSum + staffExpenseSum + materialSum} 元</nobr>
                        </div>
                        <div class="col-md-1">
                        </div>
                        <div class="col-md-1">
                        </div>
                    </div>
                    <div class="row" style="margin-top: 5px;">
                        <div class="col-md-1">
                            总成本：
                        </div>
                        <div class="col-md-1 text-right">
                            <nobr>  ${sumCost} 元</nobr>
                        </div>
                        <div class="col-md-1">
                        </div>
                        <div class="col-md-1 text-right">
                        </div>
                        <div class="col-md-1">
                        </div>
                        <div class="col-md-1 text-right">
                        </div>
                        <div class="col-md-1">
                        </div>
                        <div class="col-md-1 text-right">
                        </div>
                    </div>
                    <div class="row" style="margin-top: 5px;">
                        <div class="col-md-1">
                            总毛利：
                        </div>
                        <div class="col-md-1 text-right">
                            <nobr>  ${payment.amount - sumCost} 元</nobr>
                        </div>
                        <div class="col-md-1">
                        </div>
                        <div class="col-md-1 text-right">
                        </div>
                        <div class="col-md-1">
                        </div>
                        <div class="col-md-1 text-right">
                        </div>
                        <div class="col-md-1">
                        </div>
                        <div class="col-md-1 text-right">
                        </div>
                    </div>
                    <div class="row" style="margin-top: 5px;">
                        <div class="col-md-1">
                            总毛利率：
                        </div>
                        <div class="col-md-1 text-right">
                            <nobr>  ${maoli} %</nobr>
                        </div>
                        <div class="col-md-1">
                        </div>
                        <div class="col-md-1 text-right">
                        </div>
                        <div class="col-md-1">
                        </div>
                        <div class="col-md-1 text-right">
                        </div>
                        <div class="col-md-1">
                        </div>
                        <div class="col-md-1 text-right">
                        </div>
                    </div>
                </form>
            </div>
        </div>

    </div>
    </@main.frame>

</#escape>