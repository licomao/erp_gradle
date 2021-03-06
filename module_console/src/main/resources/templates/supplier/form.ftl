<#import "../macros/mainFrame.ftl" as main />
<#import "../macros/formMacros.ftl" as form />
<#import "/spring.ftl" as spring />
<#escape x as x?html>

    <@main.frame>

    <meta http-equiv="Windows-Target" contect="_top">

    <script type="text/javascript">
        $(function () {
            $('#backstagemanagement').collapse('show');
        });

    </script>

        <#if pageContent?? && pageContent == "更新">
        <legend>更新公告</legend>
        <#else>
        <legend>新增公告</legend>
        </#if>

    <div class="row" style="margin-top: 10px;">
        <div class="col-md-5 col-md-offset-2">
            <div id="actions" class="form-action">
                <form class="" action='<@spring.url relativeUrl = "/supplier/save"/>' method="post">
                    <@form.textInput "supplier.id" "" "hidden"/>
                    <@form.textInput "supplier.organization.id" "" "hidden"/>
                    <div class="row">
                        <div class="col-md-5">
                            <@form.textInput "supplier.name" "class='form-control'" "text" "供应商名称" true/>
                            <@form.textInput "supplier.contactInfo" "class='form-control'" "text" "联系电话" true/>
                            <@form.textInput "supplier.fax" "class='form-control'" "text" "传真" false/>
                            <@form.textInput "supplier.email" "class='form-control'" "text" "邮箱" false/>
                            <@form.textArea "supplier.description" "class='form-control' style='width:615px;height:80px;'"  "供应商描述" />
                        </div>
                    </div>
                     <br/>
                    <#if pageContent?? && pageContent == "更新">
                        <input type="submit" id="searchsubmit" value="更新" class="btn btn-primary ">
                    <#else>
                        <input type="submit" id="searchsubmit" value="新增" class="btn btn-primary ">
                    </#if>

                </form>
            </div>
        </div>
    </div>
    </@main.frame>

</#escape>