<#import "../macros/mainFrame.ftl" as main />
<#import "../macros/formMacros.ftl" as form />
<#import "/spring.ftl" as spring />
<#escape x as x?html>
    <@main.frame>
    <script>
        $(function() {
            $('#collapseOrgs').collapse('show');
            $().ready(function () {
                setStatus($("#authorityCheckbox").val());
            });
        });

        function setStatus(mask) {
            var str = new Array();
            var stringMask = mask;
            str = stringMask.split(',');
            for (i=0;i<str.length ;i++ )
            {
                $("#"+str[i]).prop("checked","checked");
            }

        }

        function allFuncCheck(minFunc, maxFunc, isChecked) {
            for (var i = minFunc; i <= maxFunc; i++) {
                if (isChecked) {
                    $("#func" + i.toString()).prop("checked", "checked");
                }else{
                    $("#func" + i.toString()).removeAttr("checked");
                }
            }
        }

        function isBigFuncCheck(minFunc, maxFunc, bigFuncNum) {
            var needCheck = false;
            for (var i = minFunc; i <= maxFunc; i++) {
                if ($("#func" + i).is(":checked")) {
                    needCheck = true;
                    break;
                }
            }
            if (needCheck) {
                $("#bigFunc" + bigFuncNum).prop("checked", "checked");
            }else{
                $("#bigFunc" + bigFuncNum).removeAttr("checked");
            }
        }

        function subForm() {
            if ($("#role").val() == ""){
                alert("角色名不能为空");
                return;
            }
            if(confirm("是否确认保存")){
                $("#fm").submit();
            }
        }

        <#if message??>
        alert('${message}');
        </#if>

    </script>

        <#if erpRole.role??>
        <legend>角色管理 -> 修改角色信息</legend>
        <#else>
        <legend>角色管理 -> 新增角色信息</legend>
        </#if>

    <form id="fm" action='<@spring.url relativeUrl = "/roleauthorization/save"/>' method="post">
        <div class="row">
            <@form.labelAndTextInput "erpRole.id" "class='form-control'" "hidden" ""/>
            <@form.labelAndTextInput "erpRole.authorityMask" "class='form-control'" "hidden" ""/>
            <input type="hidden" name="authorityCheckbox" id="authorityCheckbox" value="${authorityCheckbox}">
            <#--mav.addObject("authorityCheckbox", GetAuthorityCheck(userInfo.role.authorityMask));-->
        </div>
        <div class="row">
            <div class="col-md-offset-1 col-md-3">
                <@form.labelAndTextInput "erpRole.role" "class='form-control'" "text" "角色名：" true/>
            </div>
            <div class="col-md-3">
                <div class="col-md-5">
                    <label class="control-label">所属组织： </label>
                </div>
                <div class="col-md-7">
                    <select class="form-control"  id="organization" name="organization" disabled>
                        <option value= "${organization.id}">${organization.name}</option>
                    </select>
                </div>
            </div>
        </div>
        <div class="row" style="margin-top: 1%">
            <div  class="col-md-offset-1 col-md-1">
                <label class="control-label">功能选择： </label>
            </div>
            <div class="col-md-8">
                <div class="row">
                    <label class="control-label"><input type="checkbox" id="bigFunc1" name ="function" value="bigFunc1" onclick="allFuncCheck(1, 2, this.checked)"/>组织管理： </label>
                </div>
                <div class="row">
                    <div class="row">
                        <div class="col-md-2">
                            <input type="checkbox" id="func1" name ="function" value="func1" onclick="isBigFuncCheck(1,2,1)"/>  <label class="control-label">查看组织</label>
                        </div>
                        <div class="col-md-2">
                            <input type="checkbox" id="func2" name ="function" value="func2" onclick="isBigFuncCheck(1,2,1)"/> <label class="control-label">角色管理</label>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div class="row" style="margin-top: 1%">
            <div  class="col-md-offset-1 col-md-1">

            </div>
            <div class="col-md-8">
                <div class="row">
                <label class="control-label"><input type="checkbox" id="bigFunc2" name ="function" value="bigFunc2" onclick="allFuncCheck(11, 19, this.checked)"/>平台管理： </label>
                </div>
            <div class="row">
                <div class="row">
                    <div class="col-md-2">
                        <input type="checkbox" id="func11" name ="function" value="func11" onclick="isBigFuncCheck(11,19,2)"/>  <label class="control-label">平台公告管理</label>
                    </div>
                    <div class="col-md-2">
                        <input type="checkbox" id="func12" name ="function" value="func12" onclick="isBigFuncCheck(11,19,2)"/> <label class="control-label">代理商管理</label>
                    </div>
                    <div class="col-md-2">
                        <input type="checkbox" id="func13" name ="function" value="func13" onclick="isBigFuncCheck(11,19,2)"/> <label class="control-label">供应商管理</label>
                    </div>
                    <div class="col-md-2">
                        <input type="checkbox" id="func14" name ="function" value="func14" onclick="isBigFuncCheck(11,19,2)"/> <label class="control-label">APP公告管理</label>
                    </div>
                </div>
                <div class="row">
                    <div class="col-md-2">
                        <input type="checkbox" id="func15" name ="function" value="func15" onclick="isBigFuncCheck(11,19,2)"/>  <label class="control-label">设置会员套餐种类</label>
                    </div>
                    <div class="col-md-2">
                        <input type="checkbox" id="func16" name ="function" value="func16" onclick="isBigFuncCheck(11,19,2)"/> <label class="control-label">APP上架管理</label>
                    </div>
                    <div class="col-md-2">
                        <input type="checkbox" id="func17" name ="function" value="func17" onclick="isBigFuncCheck(11,19,2)"/> <label class="control-label">商品管理</label>
                    </div>
                    <div class="col-md-2">
                        <input type="checkbox" id="func18" name ="function" value="func18" onclick="isBigFuncCheck(11,19,2)"/> <label class="control-label">指纹机管理</label>
                    </div>
                </div>
                <div class="row">
                    <div class="col-md-2">
                        <input type="checkbox" id="func19" name ="function" value="func19" onclick="isBigFuncCheck(11,19,2)"/>  <label class="control-label">付款单流水查询</label>
                    </div>
                    <div class="col-md-2">
                    </div>
                    <div class="col-md-2">
                    </div>
                    <div class="col-md-2">
                    </div>
                </div>
            </div>
        </div>
        <div class="row text-center col-md-10" style="margin-top: 1%">
            <@form.btn_save "onclick='subForm();'" "保 存" />
        </div>
    </form>
    </@main.frame>
</#escape>