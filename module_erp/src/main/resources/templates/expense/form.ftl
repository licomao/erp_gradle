<#import "../macros/mainFrame.ftl" as main />
<#import "../macros/formMacros.ftl" as form />
<#import "/spring.ftl" as spring />
<#escape x as x?html>

    <@main.frame>

    <meta http-equiv="Windows-Target" contect="_top">
    <script src="/javascripts/jquery.ui.widget.js" type="text/javascript"></script>
    <script src="/javascripts/jquery.iframe-transport.js" type="text/javascript"></script>
    <script src="/javascripts/jquery.fileupload.js" type="text/javascript"></script>

    <script type="text/javascript">
        $('#collapseShop').collapse('show');
        $(function(){
            sum();
            if ($("#doType").val() == 2) {
                $(":input").attr("readonly", true);
                $("select").attr("disabled", true);
            }
        });

        function subForm(){
            if (confirm("是否确认保存!")){
                $("#fm").submit();
            }
        }

        function validate(obj){
            var reg = /^(([1-9][0-9]*)|(([0]\.\d{1,2}|[1-9][0-9]*\.\d{1,2})))$/;
            if (!reg.test(obj.value)) {
                alert("请输入非负金额，且小数点后最多2位!");
                obj.focus();
                return;
            } else {
                sum();
            }
        }

        function sum() {
            var sum = new Number(0);
            sum += parseFloat($("#rentExpense").val());
            sum += parseFloat($("#propertyExpense").val());
            sum += parseFloat($("#waterExpense").val());
            sum += parseFloat($("#electricExpense").val());
            sum += parseFloat($("#netPhoneExpense").val());
            sum += parseFloat($("#equipRepairsExpense").val());
            sum += parseFloat($("#staffBaseExpense").val());
            sum += parseFloat($("#staffCommissionExpense").val());
            sum += parseFloat($("#staffPerformanceExpense").val());
            sum += parseFloat($("#otherExpense").val());
            $("#allCount").html(sum.toFixed(2));
        }

        function backList(){
            window.location = "/expense/list";
        }

    </script>
    <div class="row" style="margin-top: -80px">
        <div class="col-md-5 col-md-offset-2">
            <div id="actions" class="form-action">
                <form class="" id="fm" action='<@spring.url relativeUrl = "/expense/save"/>' method="post">
                    <@form.textInput "expense.id" "" "hidden"/>
                    <br><br>
                    <input type="hidden" name="doType" id="doType" value="${doType}">
                    <@form.textInput "expense.shop.id" "class='form-control'" "hidden" "" />
                    <#if doType?? && doType?number == 2>
                        <legend>费用管理 -> 查看费用信息</legend>
                    <#elseif expense.id != 0>
                        <legend>费用管理 -> 修改费用信息</legend>
                    <#else>
                        <legend>费用管理 -> 新增费用信息</legend>
                    </#if>
                    <div class="row">
                        <div class="col-md-5">
                            <label class="control-label">年度：</label><br>
                            <select name="year" id="year">
                                <#list years as year>
                                    <option value="${year}" <#if (expense.year)?? && expense.year?replace(",","")?number == year?number >selected</#if> >${year}年</option>
                                </#list>
                            </select>
                            <#if errorMessage??><span class="text-danger">${errorMessage}</span></#if>
                        </div>
                        <div class="col-md-5 col-md-offset-2">
                            <label class="control-label">月度：</label><br>
                            <select name="month" id="month">
                                <#list months as month>
                                    <option value="${month}" <#if (expense.month)?? && expense.month?number == month?number >selected</#if> >${month}月</option>
                                </#list>
                            </select>
                        </div>
                    </div>
                    <br>
                    <div class="row">
                        <div class="col-md-5">
                            <@form.textInput "expense.rentExpense" "class='form-control' onblur='validate(this)'" "text" "月度房租费用(元)：" />
                        </div>
                        <div class="col-md-5 col-md-offset-2">
                            <@form.textInput "expense.propertyExpense" "class='form-control' onblur='validate(this)'" "text" "月度物业费用费用(元)：" />
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-5">
                            <@form.textInput "expense.waterExpense" "class='form-control' onblur='validate(this)'" "text" "水费(元)：" />
                        </div>
                        <div class="col-md-5 col-md-offset-2">
                            <@form.textInput  "expense.electricExpense" "class='form-control' onblur='validate(this)'" "text" "电费(元)："  />
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-5">
                            <@form.textInput  "expense.netPhoneExpense" "class='form-control' onblur='validate(this)'" "text" "宽带及电话费(元)："  />
                        </div>
                        <div class="col-md-5 col-md-offset-2">
                            <@form.textInput  "expense.equipRepairsExpense" "class='form-control' onblur='validate(this)'" "text" "设备维护费用(元)："  />
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-5">
                            <@form.textInput  "expense.staffBaseExpense" "class='form-control' onblur='validate(this)'" "text" "员工基本工资总费用(元)："  />
                        </div>
                        <div class="col-md-5 col-md-offset-2">
                            <@form.textInput  "expense.staffCommissionExpense" "class='form-control' onblur='validate(this)'" "text" "员工提成总费用(元)："  />
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-5">
                            <@form.textInput  "expense.staffPerformanceExpense" "class='form-control' onblur='validate(this)'" "text" "员工绩效总费用(元)："  />
                        </div>
                        <div class="col-md-5 col-md-offset-2">
                            <@form.textInput  "expense.otherExpense" "class='form-control' onblur='validate(this)'" "text" "其他费用(元)："  />
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-5">
                            总计：<span id="allCount">0</span> 元
                        </div>
                        <div class="col-md-5 col-md-offset-2">
                        </div>
                    </div>
                    <br/>
                    <#if doType?? && doType?number == 2>
                        <@form.btn_back "onclick='backList()'" "返回"/>
                    <#else>
                        <@form.btn_save "onclick='subForm()'" "确认保存"/>
                    </#if>
                </form>
            </div>
        </div>
    </div>
    </@main.frame>

</#escape>