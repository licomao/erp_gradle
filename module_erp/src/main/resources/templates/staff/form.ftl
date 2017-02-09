<#import "../macros/mainFrame.ftl" as main />
<#import "../macros/formMacros.ftl" as form />
<#--<#import "../macros/fingerPrint.ftl" as fingerprint />-->
<#import "/spring.ftl" as spring />
<#escape x as x?html>

    <@main.frame>

    <meta http-equiv="Windows-Target" contect="_top">
    <script src="/javascripts/jquery.ui.widget.js" type="text/javascript"></script>
    <script src="/javascripts/jquery.iframe-transport.js" type="text/javascript"></script>
    <script src="/javascripts/jquery.fileupload.js" type="text/javascript"></script>
    <script src="/javascripts/fingerprint-tool.js" type="text/javascript"></script>
    <script type="text/javascript">
//        var matx;  //控件
//        var fpcHandle;  //指纹缓冲区
        var myString = "";
        var beepindex = 0;

        /*/!**
         * 初始化指纹机
         *!/
        function initFingerprint() {
            try {
                matx = document.getElementById("myativx");
                matx.FPEngineVersion = "9";
                matx.SensorIndex = 0;
                matx.EnrollCount = 2;   //登记指纹的次数
                if (matx.InitEngine() == 0) {
                    $.get("/fingerprint/checkauthority?sensorSN=" + matx.SensorSN, function(data){
                        if(!data){
                            $("#initFinger").hide();
                            $("#fingerButton").hide();
                        }
                    },'json')

                } else {
                    $("#initFinger").hide();
                    $("#fingerButton").hide();
                }
            }catch (e) {
                $("#initFinger").hide();
                $("#fingerButton").hide();
            }//alert("成功");
        }*/

        $('#collapseStaff').collapse('show');
        $(function(){
            $('.Wdate').datepicker();
            $('.Wdate').datepicker("option",$.datepicker.regional["zh-TW"])
            initFingerprint();
            changeDimission();
            if ($("#doType").val() == 2) {
                $(":input").attr("readonly", true);
                $("select").attr("disabled", true);
                $("#entryDate").attr("disabled", true);
                $("#dimissionDate").attr("disabled", true);
            }
        });

        function subForm(){
            if (confirm("是否确认保存!")){

                var job = $("[id='job.id']").val();
                if (job == null) {
                    alert("请选择一项职位");
                    return;
                }
                $("#fm").submit();
            }
        }


        function validate(obj){
            var reg = /^[0-9]+([.]{1}[0-9]{1,2})?$/;
            if (!reg.test(obj.value)) {
                $(obj).val("0");
                obj.focus();
                return;
            }
        }

        function changeDimission() {
            if ($("#doType").val() != 2 && $("#status").val() == 3) {
                $("#dimissionDate").attr("disabled", false);
            } else if ($("#doType").val() != 2) {
                $("#dimissionDate").val(null);
                $("#dimissionDate").attr("disabled", true);
            }
        }

        function backList(){
            window.location = "/staff/list";
        }

        /**
         * 登记指纹
         */
        function    pRegist() {
            alert("登记指纹开始啦");
            $("#forReg").val("请按指纹");
            try {
                matx.BeginEnroll();
            } catch (e) {
                alert(e.message);
            }
        }



    </script>
    <SCRIPT type="text/javascript" FOR="myativx" EVENT="OnImageReceived(istrue)">

    </SCRIPT>

    <#--取得指纹初始特征, Quality表示该指纹特征的质量, 有如下可能值
        0: 好的指纹特征
        1: 特征点不够
        2: 其他原因导致不能取得指纹特征
    -->
    <SCRIPT type="text/javascript" FOR="myativx" EVENT="OnFeatureInfo(qulity)">
        // js 处理具体内容。
        $("#onFeatureInfoView").val(qulity);
        var str = "不合格";
        if (qulity == 0) {
            str = "合格";

        }
        if (qulity == 1) {
            str = "特征点不够";
        }

        $("#forReg").val("指纹质量:" + str);
        if (matx.IsRegister) {
            if (matx.EnrollIndex != 1) {
                var t = matx.EnrollIndex - 1;
                $("#forReg").val("登记状态：请再按 " + t.toString() + " 次指纹 ");
            }
        }


    </SCRIPT>

    <#--用户登记指纹结束时调用该事件, actionResult = true 表示登记成功, 用pTemplate属性可取得指纹特征模板, false表示失败-->
    <SCRIPT type="text/javascript" FOR="myativx" EVENT="OnEnroll(ActionResult) ">
        var tmp = matx.GetTemplateAsString();
        $("#fingerprint").val(tmp);
        $("#forReg").val('登记成功!')

    </SCRIPT>

    <div class="row" style="margin-top: -80px">
        <div class="col-md-5 col-md-offset-2">
            <div id="actions" class="form-action">
                <form class="" id="fm" action='<@spring.url relativeUrl = "/staff/save"/>' method="post">
                    <@form.textInput "staff.id" "" "hidden"/>
                    <br><br>
                    <input type="hidden" name="doType" id="doType" value="${doType}">

                    <#if doType?? && doType == 0>
                        <legend>员工管理 -> 新增员工信息</legend>
                    <#elseif doType?? && doType == 1>
                        <legend>员工管理 -> 修改员工信息</legend>
                    <#else>
                        <legend>员工管理 -> 查看员工信息</legend>
                    </#if>

                    <div class="row">
                        <div class="col-md-5">
                            <@form.textInput "staff.name" "class='form-control'" "text" "员工名称：" true/>
                        </div>
                        <div class="col-md-5 col-md-offset-2">
                            <@form.textInput "staff.identityCard" "class='form-control'" "text" "身份证：" true/>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-5">
                            <@form.textInput "staff.phone" "class='form-control'" "text" "手机号码：" true/>
                        </div>
                        <div class="col-md-5 col-md-offset-2">
                            <label class="control-label">职位（工种）：</label><br>
                            <select name="job.id" id="job.id">
                                <#list jobs as job>
                                    <option value="${job.id}" <#if (staff.job.id)?? && staff.job.id == job.id >selected</#if> >${job.name}</option>
                                </#list>
                            </select>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-5">
                            <@form.textInput "staff.entryDate" "class='form-control Wdate' readonly" "text" "入职日期：" true/>
                        </div>
                        <div class="col-md-5 col-md-offset-2">
                            <@form.textInput  "staff.probation" "class='form-control' onblur='validate(this)'" "text" "试用期（月）："  />
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-5">
                            <@form.textInput  "staff.workDay" "class='form-control' onblur='validate(this)'" "text" "月名义工作天数："  />
                        </div>
                        <div class="col-md-5 col-md-offset-2">
                            <label class="control-label">所属门店：</label><br>
                            <select name="shop.id">
                                <#list shops as shop>
                                    <option value="${shop.id}" <#if (staff.shop.id)?? && staff.shop.id == shop.id >selected</#if> >${shop.name}</option>
                                </#list>
                            </select>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-5">
                            <label class="control-label">员工状态：</label><br>
                            <select name="status" id="status" onchange="changeDimission()">
                                <option value="1" <#if staff.status?? && staff.status == "1" >selected</#if> >试用</option>
                                <option value="2" <#if staff.status?? && staff.status == "2" >selected</#if> >正式</option>
                                <option value="3" <#if staff.status?? && staff.status == "3" >selected</#if> >离职</option>
                            </select>
                        </div>
                        <div class="col-md-5 col-md-offset-2">
                            <@form.textInput "staff.dimissionDate" "class='form-control Wdate' readonly" "text" "离职日期：" />
                        </div>
                    </div>
                    <div class="row"  id="fingerPrintDiv">
                    <#--<div class="row"  id="fingerButton">-->
                        <div class="col-md-5">
                            <#--<button type="button" onclick="pRegist()">初始化控件</button>-->
                                <#if doType?? && doType == 0>
                                    <button type="button" onclick="pRegist()">登记指纹</button>
                                    <input type="text" name="forReg" id="forReg" readonly="readonly" style="width: 300px;">
                                    <input type="hidden" value="${staff.fingerPrint!""}" id="fingerprint" name="fingerPrint" style="width: 300px;" readonly="readonly" >
                                    <#--<@form.textInput "staff.fingerPrint" "class='form-control'" "text" "指纹：" />-->
                                <#elseif doType?? && doType == 1>
                                    <#if staff.fingerPrint?? >
                                        <button type="button" onclick="pRegist()">变更指纹</button>
                                        <input type="text" name="forReg" id="forReg" value="已登记" readonly="readonly" style="width: 300px;">
                                    <#else >
                                        <button type="button" onclick="pRegist()">登记指纹</button>
                                        <input type="text" name="forReg" id="forReg" readonly="readonly" style="width: 300px;">
                                    </#if>
                                    <input type="hidden" value="${staff.fingerPrint!""}" id="fingerprint" name="fingerPrint" style="width: 300px;" readonly="readonly" >
                                    <#--<@form.textInput "staff.fingerPrint" "class='form-control'" "text" "指纹：" />-->
                                <#else>
                                    <#--<@form.textInput "staff.fingerPrint" "class='form-control'" "text" "指纹：" />-->
                                    <input type="hidden" value="${staff.fingerPrint!""}" id="fingerprint" name="fingerPrint" style="width: 300px;" readonly="readonly" >
                                </#if>


                        </div>
                    </div>
                    <br/>
                    <#if doType?? && doType == 2>
                        <@form.btn_back "onclick='backList()'" "返回"/>
                    <#else>
                        <@form.btn_save "onclick='subForm()'" "确认保存"/>
                    </#if>

                </form>
                <div style="display: none" id="fingerObject" ></div>
        <div style="display: none" >
                <div id="initFinger">


                <object
                        id="myativx"
                        classid="clsid:CA69969C-2F27-41D3-954D-A48B941C3BA7"
                <#--codebase="<%=request.getContextPath()%>/ocx/TableListX.ocx#version=1,0,0,5"-->
                        width=100%
                        height=210
                        align=middle
                        hspace=0
                        vspace=0
                        onerror="onObjectError();">
                </object>
                </div>
        </div>
            </div>
        </div>
    </div>
    </@main.frame>

</#escape>