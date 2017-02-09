<#import "../macros/mainFrame.ftl" as main />
<#import "../macros/formMacros.ftl" as form />
<#import "/spring.ftl" as spring />
<#escape x as x?html>
    <@main.frame>
    <script>
        $(function() {
            $().ready(function () {
                changeOrganization();
            });

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
            $("#organization.id").val($("#organization").val());
        }

    </script>

    <form class="" action='<@spring.url relativeUrl = "/account/save"/>' method="post">
        <div class="row">
            <@form.labelAndTextInput "erpUser.id" "class='form-control'" "hidden" ""/>
            <@form.labelAndTextInput "erpUser.organization.id" "class='form-control'" "hidden" ""/>
        </div>
        <div class="row">
            <div class="col-md-offset-1 col-md-3">
                <@form.labelAndTextInput "erpUser.username" "class='form-control'" "text" "账户名：" true/>
            </div>
            <div class="col-md-3">
                <@form.labelAndTextInput "erpUser.password" "class='form-control'" "text" "密码：" true/>
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
                        <option value= "${role.id}" <#if role.id==erpUser.role.id> selected </#if>${role.role}</option>
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

        <div class="row" style="margin-top: 1%">
            <div class="col-md-offset-3 col-md-1">
                <div class="col-md-offset-6 col-md--6">
                    <input class="btn btn-primary" type="submit" onclick="getOrgAndShops();" value="保 存"></input>
                </div>
            </div>
        </div>
    </form>
    </@main.frame>
</#escape>