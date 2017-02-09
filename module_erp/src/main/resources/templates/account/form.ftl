<#import "../macros/mainFrame.ftl" as main />
<#import "../macros/formMacros.ftl" as form />
<#import "/spring.ftl" as spring />
<#escape x as x?html>
    <@main.frame>

    <script type="text/javascript">
        var matx;  //控件
        $('#collapseOrg').collapse('show');
        /**
         * 初始化指纹机
         */
        function initFingerprint() {
            try {
                matx = document.getElementById("myativx");
                matx.FPEngineVersion = "9";
                matx.SensorIndex = 0;
                matx.EnrollCount = 2;   //登记指纹的次数
                if (matx.InitEngine() == 0) {
                    $.get("/fingerprint/checkauthority?sensorSN=" + matx.SensorSN, function(data){
                        if(!data){
                            $("#fingerButton").hide();
                        }
                    },'json')
                } else {
                    $("#fingerButton").hide();
                }
            }catch (e) {
                $("#fingerButton").hide();
            }
        }
        /**
         * 登记指纹
         */
        function pRegist() {
            alert("登记指纹开始啦");
            $("#forReg").val("请按指纹");
            try {
                matx.BeginEnroll();
            } catch (e) {
                alert(e.message);
            }
        }

        $(function() {
            $().ready(function () {
                changeOrganization();
            });
            initFingerprint();
            $("#organization").change(changeOrganization);

            function changeOrganization() {
                var erpuserid = $("#id").val();
                var organization = $("#organization").val();
                var murl = "/account/getshops?organization=" + organization + "&userid=" + erpuserid;
                $.ajax({
                    url:murl,
                    dataType:"json",
                    type:"post",
                    success: function(ret) {
                        var obj = $("#shopCheckBoxes");
                        obj.empty();

                        for(var i = 0 ; i < ret.shopList.length;i++) {
                            var checkstatus = ret.checkBoxStatus[i];
                            obj.append("<div class='row'>");
                            if(checkstatus == 1) {
                                obj.append("<div class='col-md-2'><input id='shopchk'name='shopchk' type='checkbox'value=" + ret.shopList[i].id + "  class='form-control' checked/></div>");
                            }else {
                                obj.append("<div class='col-md-2'><input id='shopchk'name='shopchk' type='checkbox' value=" + ret.shopList[i].id + "  class='form-control' /></div>");
                            }
                            obj.append("<div class='col-md-4'><label class='control-label' >"+ ret.shopList[i].name +"</label></div>");
                            obj.append("</div>");
                        }
                    }
                });
            }
        });

        function getOrgAndShops() {
            if ($("#username").val() == "") {
                alert("账户名不能为空");
                return;
            }
            if ($("#password").val() == "") {
                alert("密码不能为空");
                return;
            }
            if ($("#phone").val() == "") {
                alert("联系电话不能为空");
                return;
            }

            var cbs = $("input[name=shopchk]:checkbox");
            var b = false;
            for(var i=0;i<cbs.length;i++){
                if( cbs[i].checked){
                    b = true;
                }
            }
            if(!b){
                alert("请至少选择一个门店");
                return false;
            }

            $("#organization.id").val($("#organization").val());
            if(confirm("是否确认保存")){
                $("#fm").submit();
            }
        }

    </script>
    <SCRIPT type="text/javascript" FOR="myativx" EVENT="OnFeatureInfo(qulity)">
        // js 处理具体内容。
        //        $("#onFeatureInfoView").val(qulity);
//        $("#test").val(qulity);
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
    <SCRIPT type="text/javascript" FOR="myativx" EVENT="OnImageReceived(istrue)">

    </SCRIPT>
    <SCRIPT type="text/javascript" FOR="myativx" EVENT="OnEnroll(ActionResult) ">


        var tmp = matx.GetTemplateAsString();
        $("#fingerprint").val(tmp);
        $("#forReg").val('登记成功!')

    </SCRIPT>

        <#if erpUser.username?? >
        <legend>账户管理 -> 修改账户信息</legend>
        <#else>
        <legend>账户管理 -> 新增账户信息</legend>
        </#if>

    <form class="" id="fm" action='<@spring.url relativeUrl = "/account/save"/>' method="post">
        <div class="row">
            <@form.labelAndTextInput "erpUser.id" "class='form-control'" "hidden" ""/>
            <@form.labelAndTextInput "erpUser.organization.id" "class='form-control'" "hidden" ""/>
        </div>
        <div class="row">
            <div class="col-md-offset-1 col-md-3">
                <@form.labelAndTextInput "erpUser.username" "class='form-control'" "text" "账户名：" true/>
            </div>
            <div class="col-md-3">
                <#if !erpUser.password??>
                    <@form.labelAndTextInput "erpUser.password" "class='form-control'" "text" "密码：" true/>
                <#else >
                    <@form.labelAndTextInput "erpUser.password" "class='form-control'" "password" "密码：" true/>
                </#if>
            </div>
        </div>
        <div class="row" style="margin-top: 1%">
            <div class="col-md-offset-1 col-md-3">
                <@form.labelAndTextInput "erpUser.phone" "class='form-control'" "text" "联系电话： " true/>
            </div>
            <div class="col-md-1">
                <label class="control-label">用户角色： </label>
            </div>
            <div class="col-md-2">
                <select class="form-control" type="search" id="role" name="role">
                    <#list roleList as role>
                        <option <#if erpUser.role.id?c == role.id?c> selected</#if> value= "${role.id?c}">${role.role}</option>
                    </#list>
                </select>
            </div>
        </div>
        <div class="row" style="margin-top: 1%">
            <div class="col-md-offset-1 col-md-3">
                <div class="col-md-5">
                    <label class="control-label">组织： </label>
            </div>
                <div class="col-md-7">
                    <select class="form-control" type="search" id="organization" name="organization" disabled>
                        <option value= "${erpUser.organization.id}">${erpUser.organization.name}</option>
                    </select>
                </div>
            </div>
            <div  class="col-md-1">
                <label class="control-label">所属门店： </label>
            </div>
            <div id="shopCheckBoxes" class="col-md-4">
                <@spring.bind "erpUser.shops"/>
            </div>
        </div>
        <div class="row" id="fingerButton" style="margin-top: 1%" >
            <div class="col-md-offset-1 col-md-10" >
                <#--<div class="col-md-5">
                    <label class="control-label">组织： </label>
                </div>
                <div class="col-md-7">
                    <select class="form-control" type="search" id="organization" name="organization" disabled>
                        <option value= "${erpUser.organization.id}">${erpUser.organization.name}</option>
                    </select>
                </div>-->
                    <div class="col-md-2">
                        <label class="control-label">指纹： </label>
                    </div>
                    <#if erpUser.fingerPrint?? >
                        <button type="button" onclick="pRegist()">变更指纹</button>
                        <input type="text" name="forReg" id="forReg" value="已登记" readonly="readonly" style="width: 300px;">
                    <#else >
                        <button type="button" onclick="pRegist()">登记指纹</button>
                        <input type="text" name="forReg" id="forReg" readonly="readonly" style="width: 300px;">
                    </#if>
                <#--<button type="button" onclick="pRegist()">登记指纹</button>-->
                <#--<input type="text" name="forReg" id="forReg" readonly="readonly" style="width: 300px;">-->
                <input type="hidden" value="${erpUser.fingerPrint!""}" id="fingerprint" name="fingerPrint" style="width: 300px;" readonly="readonly" >
                <#--<input type="text" id="test" >-->
            </div>

        </div>
        <div class="row text-center col-lg-10" style="margin-top: 1%">
            <@form.btn_save "onclick='getOrgAndShops();'" "保 存" />
                    <#--<input class="btn btn-primary" type="submit" onclick="getOrgAndShops();" value="保 存"></input>-->
        </div>
    </form>
    <div style="display: none" >
        <object
                id="myativx"
                classid="clsid:CA69969C-2F27-41D3-954D-A48B941C3BA7"
                width=100%
                height=210
                align=middle
                hspace=0
                vspace=0
                onerror="onObjectError();">
        </object>
    </div>
    </@main.frame>
</#escape>