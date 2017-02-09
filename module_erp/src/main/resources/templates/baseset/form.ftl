<#import "../macros/mainFrame.ftl" as main />
<#import "../macros/formMacros.ftl" as form />
<#import "/spring.ftl" as spring />
<#escape x as x?html>

    <@main.frame>

    <meta http-equiv="Windows-Target" contect="_top">

   <#-- <link href="/javascripts/select2.css" rel="stylesheet" />

    <script src="/javascripts/select2.js" type="text/javascript"></script>
-->
    <script type="text/javascript">
//        $('#selectTest').select2();
        $('#collapseBase').collapse('show');
        $(function(){
            if (!${user.checkAuthority(AUTHORITY.MANAGE_ORG_BASESET)?c}) {
                window.location = "/noauthority";
            } else {
                var reg = new RegExp("^\[0-9]*\.?[0-9]*$");
                resValue("posTopRate",reg);
                resValue("posRate",reg);
                resValue("operationPrice",reg);
                changeDay();
            }

        });

        function changeDay(){
            if (!$("#isCheckPd").get(0).checked){
                $("#checkDay").attr("disabled","disabled");
            } else {
                $("#checkDay").removeAttr("disabled");
            }
        }

        function validDay(obj){
            if (!isNaN(obj.value)){
                if (parseInt(obj.value) < 1 || parseInt(obj.value) > 28){
                    alert("请输入1至28日的数字");
                    obj.focus();
                }
                obj.value = parseInt(obj.value);
            } else {
                alert("请输入数字！");
                obj.focus();
            }
        }

        function resValue(id,reg){
            $("#"+id).blur(function(){

                //   var reg = new RegExp("^\-?[0-9]*\.?[0-9]*$");
                var foo = reg.test($(this).val());
                if(!reg.test($(this).val())){
                    $(this).val(0);
                }
            });
        }
        function subForm(){
            $("#fm").submit();
        }
        function changeF() {
            document.getElementById('txt').value = document.getElementById('sel').options[document.getElementById('sel').selectedIndex].value;
        }
    </script>
    <div class="row" style="margin-top: -80px">
        <div class="col-md-11">
            <div id="actions" class="form-action">
                <form class="" id="fm" action='<@spring.url relativeUrl = "/baseset/save"/>' method="post">
                    <@form.textInput "baseSet.id" "" "hidden"/>
                    <@form.textInput "baseSet.ver" "" "hidden"/>


                    <#if pageContent?? && pageContent == "update">
                        <legend>决策 -> 基础数据修改</legend>
                    <#else>
                        <legend>决策 -> 基础数据设置</legend>
                    </#if>


                    <div class="row">
                        <div class="col-md-3 col-md-offset-2">
                            <@form.textInput "baseSet.operationPrice" "class='form-control',id='operationPrice'" "text" "工时单价" true/>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-3 col-md-offset-2">
                            <@form.textInput "baseSet.posRate" "class='form-control',id='posRate'" "text" "pos机费率 (%)" true/>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-3 col-md-offset-2">
                            <@form.textInput "baseSet.posTopRate" "class='form-control',id='posTopRate'" "text" "pos机封价格 (元)" true/>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-4 col-md-offset-2">
                            <@form.labelAndCheckbox "baseSet.isCheckPd" "是否验证盘点" "onclick='changeDay();'" />
                        </div>

                    </div>
                    <div class="row">
                        <div class="col-md-offset-2 col-md-6"><font size="1pt" color="red">注：如果需要强制盘点可在设置里勾选是否盘点验证，盘点截止日默认为1日至次月1日0:00分，如果需要设置其他盘点时间，则输入1—28的任意数字，如写28，则表示本月28日至次月28日0:00分必须要有盘点单，不然系统则强制关闭所有库存的进出口功能（如：采购，销售开单等)。</font> </div>
                    </div>
                    <div class="row" style="margin-top:1%">
                        <div class="col-md-3 col-md-offset-2">
                            <@form.textInput "baseSet.checkDay" "class='form-control' id='checkDay' onblur='validDay(this);' " "text" "盘点截止日" true/>
                        </div>
                        <div class="col-md-4"> </div>
                    </div>

                    <div class="row">
                        <div class="col-md-3 col-md-offset-2">
                            <br/>
                            <@form.btn_save "onclick='subForm()'" "确认保存"/>
                        </div>
                    </div>

                </form>
            </div>
        </div>
    </div>
    <script>

    </script>
    </@main.frame>

</#escape>