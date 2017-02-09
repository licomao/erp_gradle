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


    <div class="row" style="margin-top: 10px;">
    form
    </div>

    </@main.frame>

</#escape>