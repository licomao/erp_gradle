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
        $('#collapseStaff').collapse('show');

        function subForm(){
            if (confirm("是否确认保存!")){
                $("#fm").submit();
            }
        }

    </script>
    <div class="row" style="margin-top: -80px">
        <div class="col-md-5 col-md-offset-2">
            <div id="actions" class="form-action">
                <form class="" id="fm" action='<@spring.url relativeUrl = "/job/save"/>' method="post">
                    <@form.textInput "job.id" "" "hidden"/>
                    <br><br>

                    <#if job.name??>
                        <legend>职位设置 -> 修改职位信息</legend>
                    <#else>
                        <legend>职位设置 -> 新增职位信息</legend>
                    </#if>

                    <div class="row">
                        <div class="col-md-5">
                            <@form.textInput "job.name" "class='form-control'" "text" "职务名称：" true/>
                        </div>
                    </div>
                    <br/>
                    <@form.btn_save "onclick='subForm()'" "确认保存"/>

                </form>
            </div>
        </div>
    </div>
    </@main.frame>

</#escape>