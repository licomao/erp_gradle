<#import "../macros/mainFrame.ftl" as main />
<#import "../macros/formMacros.ftl" as form />
<#import "/spring.ftl" as spring />
<#escape x as x?html>

    <@main.frame>

    <meta http-equiv="Windows-Target" contect="_top">

    <script type="text/javascript">
        $('#collapsePt').collapse('show');
        function subForm(){
            if(confirm("是否确认发布")){
                $("#fm").submit();
            }
        }
    </script>

        <#if pageContent?? && pageContent == "更新">
        <legend>公告管理 -> 更新公告</legend>
        <#else>
        <legend>公告管理 -> 新增公告</legend>
        </#if>

    <div class="row" style="margin-top: 10px;">
        <div class="col-md-5 col-md-offset-2">
            <div id="actions" class="form-action">
                <form id="fm" action='<@spring.url relativeUrl = "/erpannouncement/new"/>' method="post">
                    <@form.textInput "erpAnnouncement.id" "" "hidden"/>
                    <div class="row">
                        <div class="col-md-5">
                            <@form.textInput "erpAnnouncement.title" "class='form-control'" "text" "公告标题" true/>
                            <@form.textArea "erpAnnouncement.content" "class='form-control' style='width:615px;height:80px;'"  "公告内容" true/>
                            <@form.textInput "erpAnnouncement.publisher" "class='form-control'" "text" "落款人" true/>
                        </div>
                    </div>
                    <@form.btn_pages "onclick='subForm();'" "确认发布"/>
                </form>
            </div>
        </div>
    </div>
    </@main.frame>

</#escape>