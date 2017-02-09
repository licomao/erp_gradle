<#import "../macros/mainFrame.ftl" as main />
<#import "../macros/formMacros.ftl" as form />
<#import "/spring.ftl" as spring />
<#escape x as x?html>
    <@main.frame>
    <script>
        $(function() {
            $('#collapseOrg').collapse('show');
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
                    $("#fu" + i.toString()).prop("checked", "checked");
                }else{
                    $("#fu" + i.toString()).removeAttr("checked");
                }
            }
        }

        function isBigFuncCheck(minFunc, maxFunc, bigFuncNum) {
            var needCheck = false;
            for (var i = minFunc; i <= maxFunc; i++) {
                if ($("#fu" + i).is(":checked")) {
                    needCheck = true;
                    break;
                }
            }
            if (needCheck) {
                $("#bigfu" + bigFuncNum).prop("checked", "checked");
            }else{
                $("#bigfu" + bigFuncNum).removeAttr("checked");
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
                    <label class="control-label"><input type="checkbox" id="bigfu10" name ="function" value="bigfu10" onclick="allFuncCheck(91, 95, this.checked)"/>组织后台管理： </label>
                </div>
                <div class="row">
                    <div class="row">
                        <div class="col-md-2">
                            <input type="checkbox" id="fu91" name ="function" value="fu91" onclick="isBigFuncCheck(91,95,10)"/>  <label class="control-label">角色管理</label>
                        </div>
                        <div class="col-md-2">
                            <input type="checkbox" id="fu92" name ="function" value="fu92" onclick="isBigFuncCheck(91,95,10)"/> <label class="control-label">账户管理</label>
                        </div>
                        <div class="col-md-2">
                            <input type="checkbox" id="fu93" name ="function" value="fu93" onclick="isBigFuncCheck(91,95,10)"/> <label class="control-label">门店管理</label>
                        </div>
                        <div class="col-md-2">
                            <input type="checkbox" id="fu94" name ="function" value="fu94" onclick="isBigFuncCheck(91,95,10)"/> <label class="control-label">商品二级分类</label>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-2">
                            <input type="checkbox" id="fu95" name ="function" value="fu95" onclick="isBigFuncCheck(91,95,10)"/> <label class="control-label">ERP公告管理</label>
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
                    <label class="control-label"><input type="checkbox" id="bigfu1" name ="function" value="bigfu1" onclick="allFuncCheck(1, 6, this.checked)"/>门店后台管理： </label>
                </div>
                <div class="row">
                    <div class="row">
                        <div class="col-md-2">
                            <input type="checkbox" id="fu1" name ="function" value="fu1" onclick="isBigFuncCheck(1,6,1)"/>  <label class="control-label">固定资产管理</label>
                        </div>
                        <div class="col-md-2">
                            <input type="checkbox" id="fu2" name ="function" value="fu2" onclick="isBigFuncCheck(1,6,1)"/> <label class="control-label">耗材领用管理</label>
                        </div>
                        <div class="col-md-2">
                            <input type="checkbox" id="fu3" name ="function" value="fu3" onclick="isBigFuncCheck(1,6,1)"/> <label class="control-label">供应商管理</label>
                        </div>
                        <div class="col-md-2">
                            <input type="checkbox" id="fu4" name ="function" value="fu4" onclick="isBigFuncCheck(1,6,1)"/> <label class="control-label">商品管理</label>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-2">
                            <input type="checkbox" id="fu5" name ="function" value="fu5" onclick="isBigFuncCheck(1,6,1)"/> <label class="control-label">顾客管理</label>
                        </div>
                        <div class="col-md-2">
                            <input type="checkbox" id="fu6" name ="function" value="fu6" onclick="isBigFuncCheck(1,6,1)"/> <label class="control-label">费用管理</label>
                        </div>
                    </div>
                    <div class="row">

                    </div>
                </div>
            </div>

        </div>


        <div class="row" style="margin-top: 1%">
            <div  class="col-md-offset-1 col-md-1">

            </div>
            <div class="col-md-8">
                <div class="row">
                    <label class="control-label"><input type="checkbox" id="bigfu2" name ="function" value="bigfu2" onclick="allFuncCheck(11, 13, this.checked)"/>库存管理： </label>
                </div>
                <div class="row">
                    <div class="row">
                        <div class="col-md-2">
                            <input type="checkbox" id="fu11" name ="function" value="fu11" onclick="isBigFuncCheck(11,13,2)"/>  <label class="control-label">库存查询</label>
                        </div>
                        <div class="col-md-2">
                            <input type="checkbox" id="fu12" name ="function" value="fu12" onclick="isBigFuncCheck(11,13,2)"/>  <label class="control-label">库存盘点</label>
                        </div>
                        <div class="col-md-2">
                            <input type="checkbox" id="fu13" name ="function" value="fu13" onclick="isBigFuncCheck(11,13,2)"/>  <label class="control-label">库存调拨</label>
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
                    <label class="control-label"><input type="checkbox" id="bigfu3" name ="function" value="bigfu3" onclick="allFuncCheck(21, 22, this.checked)"/>决策： </label>
                </div>
                <div class="row">
                    <div class="row">
                        <div class="col-md-2">
                            <input type="checkbox" id="fu21" name ="function" value="fu21" onclick="isBigFuncCheck(21,22,3)"/>  <label class="control-label">基础数据设置</label>
                        </div>
                        <div class="col-md-2">
                            <input type="checkbox" id="fu22" name ="function" value="fu22" onclick="isBigFuncCheck(21,22,3)"/>  <label class="control-label">月度数据统计</label>
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
                    <label class="control-label"><input type="checkbox" id="bigfu4" name ="function" value="bigfu4" onclick="allFuncCheck(31, 36, this.checked)"/>采购单管理： </label>
                </div>
                <div class="row">
                    <div class="row">
                        <div class="col-md-2">
                            <input type="checkbox" id="fu31" name ="function" value="fu31" onclick="isBigFuncCheck(31,36,4)"/>  <label class="control-label">采购单申请及查询</label>
                        </div>
                        <div class="col-md-2">
                            <input type="checkbox" id="fu32" name ="function" value="fu32" onclick="isBigFuncCheck(31,36,4)"/>  <label class="control-label">采购单审批</label>
                        </div>
                        <div class="col-md-2">
                            <input type="checkbox" id="fu33" name ="function" value="fu33" onclick="isBigFuncCheck(31,36,4)"/>  <label class="control-label">采购单入库</label>
                        </div>
                        <div class="col-md-2">
                            <input type="checkbox" id="fu34" name ="function" value="fu34" onclick="isBigFuncCheck(31,36,4)"/>  <label class="control-label">未付款采购单查询</label>
                        </div>

                    </div>
                </div>
                <div class="row">
                    <div class="row">
                        <div class="col-md-2">
                            <input type="checkbox" id="fu35" name ="function" value="fu35" onclick="isBigFuncCheck(31,36,4)"/>  <label class="control-label">采购付款单</label>
                        </div>
                        <div class="col-md-2">
                            <input type="checkbox" id="fu36" name ="function" value="fu36" onclick="isBigFuncCheck(31,36,4)"/>  <label class="control-label">采购付款单(可作废)</label>
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
                    <label class="control-label"><input type="checkbox" id="bigfu5" name ="function" value="bigfu5" onclick="allFuncCheck(41, 42, this.checked)"/>供应商退货管理： </label>
                </div>
                <div class="row">
                    <div class="row">
                        <div class="col-md-2">
                            <input type="checkbox" id="fu41" name ="function" value="fu41" onclick="isBigFuncCheck(41,42,5)"/>  <label class="control-label">退货单管理</label>
                        </div>
                        <div class="col-md-2">
                            <input type="checkbox" id="fu42" name ="function" value="fu42" onclick="isBigFuncCheck(41,42,5)"/>  <label class="control-label">退货单审批</label>
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
                    <label class="control-label"><input type="checkbox" id="bigfu6" name ="function" value="bigfu6" onclick="allFuncCheck(51, 53, this.checked)"/>会员管理： </label>
                </div>
                <div class="row">
                    <div class="row">
                        <div class="col-md-2">
                            <input type="checkbox" id="fu51" name ="function" value="fu51" onclick="isBigFuncCheck(51,53,6)"/>  <label class="control-label">会员套餐管理</label>
                        </div>
                        <div class="col-md-2">
                            <input type="checkbox" id="fu52" name ="function" value="fu52" onclick="isBigFuncCheck(51,53,6)"/>  <label class="control-label">会员套餐异地消费</label>
                        </div>
                        <div class="col-md-2">
                            <input type="checkbox" id="fu53" name ="function" value="fu53" onclick="isBigFuncCheck(51,53,6)"/>  <label class="control-label">设置会员套餐种类</label>
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
                    <label class="control-label"><input type="checkbox" id="bigfu7" name ="function" value="bigfu7" onclick="allFuncCheck(61, 65, this.checked)"/>人事管理： </label>
                </div>
                <div class="row">
                    <div class="row">
                        <div class="col-md-2">
                            <input type="checkbox" id="fu61" name ="function" value="fu61" onclick="isBigFuncCheck(61,65,7)"/>  <label class="control-label">员工管理</label>
                        </div>
                        <div class="col-md-2">
                            <input type="checkbox" id="fu62" name ="function" value="fu62" onclick="isBigFuncCheck(61,65,7)"/>  <label class="control-label">职位设置</label>
                        </div>
                        <div class="col-md-2">
                            <input type="checkbox" id="fu63" name ="function" value="fu63" onclick="isBigFuncCheck(61,65,7)"/> <label class="control-label">员工考勤查询</label>
                        </div>
                        <div class="col-md-2">
                            <input type="checkbox" id="fu64" name ="function" value="fu64" onclick="isBigFuncCheck(61,65,7)"/> <label class="control-label">上下班考勤</label>
                        </div>
                    </div>
                </div>
                <div class="row">
                    <div class="row">
                        <div class="col-md-4">
                            <input type="checkbox" id="fu65" name ="function" value="fu65" onclick="isBigFuncCheck(61,65,7)"/>  <label class="control-label">指纹考勤</label>
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
                    <label class="control-label"><input type="checkbox" id="bigfu8" name ="function" value="bigfu8" onclick="allFuncCheck(71, 71, this.checked)"/>特殊功能： </label>
                </div>
                <div class="row">
                    <div class="row">
                        <div class="col-md-4">
                            <input type="checkbox" id="fu71" name ="function" value="fu71" onclick="isBigFuncCheck(71,72,8)"/>  <label class="control-label">折扣审批及采购单异常退回审批</label>
                            <#--<input type="checkbox" id="fu71" name ="function" value="fu71" onclick="isBigFuncCheck(71,71,8)"/>  <label class="control-label">折扣审批及采购单异常退回审批</label>-->
                        </div>
                        <div class="col-md-4">
                            <input type="checkbox" id="fu72" name ="function" value="fu72" onclick="isBigFuncCheck(71,72,8)"/>  <label class="control-label">员工入职异常审批</label>
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
                    <label class="control-label"><input type="checkbox" id="bigfu9" name ="function" value="bigfu9" onclick="allFuncCheck(81, 85, this.checked)"/>门店收银： </label>
                </div>

                <div class="row">
                    <div class="row">
                        <div class="col-md-2">
                            <input type="checkbox" id="fu81" name ="function" value="fu81" onclick="isBigFuncCheck(81,84,9)"/>  <label class="control-label">客户预约管理</label>
                        </div>
                        <div class="col-md-2">
                            <input type="checkbox" id="fu82" name ="function" value="fu82" onclick="isBigFuncCheck(81,84,9)"/>  <label class="control-label">销售开单</label>
                        </div>
                        <div class="col-md-2">
                            <input type="checkbox" id="fu83" name ="function" value="fu83" onclick="isBigFuncCheck(81,84,9)"/>  <label class="control-label">销售开单查询</label>
                        </div>
                        <div class="col-md-2">
                            <input type="checkbox" id="fu84" name ="function" value="fu84" onclick="isBigFuncCheck(81,84,9)"/>  <label class="control-label">营业额汇总分析</label>
                        </div>
                    </div>
                </div>
                <div class="row">
                    <div class="row">
                        <div class="col-md-4">
                            <input type="checkbox" id="fu85" name ="function" value="fu85" onclick="isBigFuncCheck(81,85,9)"/>  <label class="control-label">销售开单明细查询</label>
                        </div>
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