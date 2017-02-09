<#import "../macros/generalFrame.ftl" as general />
<#import "../macros/formMacros.ftl" as form />
<#import "/spring.ftl" as spring />

<#escape x as x?html>

    <@general.frame title="预约明细">

    <style type="text/css">
        table.gridtable { font-family: verdana,arial,sans-serif; font-size: 11px; color: #333333; border-width: 1px; border-color: #666666; border-collapse: collapse; }
        table.gridtable th { border-width: 1px; padding: 8px; border-style: solid; border-color: #666666; background-color: #dedede; }
        table.gridtable td { border-width: 1px; padding: 8px; border-style: solid; border-color: #666666; background-color: #ffffff; }
    </style>

    <table class="gridtable">
        <tr> <th>序号</th><th>品名</th><th>单价</th><th>数量</th><th>小计</th> <th>会员套餐</th></tr>
        <#list orders as order >
            <tr>
                <td>${order.id}</td>
                <td>${order.orderedItem.name}</td>
                <td>${order.orderedItem.price}</td>
                <td>${order.count}</td>
                <td>${order.orderedItem.price*order.count}</td>
                <#if order.orderedSuite?? >
                    <td>${order.orderedSuite.name}</td>
                <#else>
                    <td>${order.referenceCareSuite.name}</td>
                </#if>
            </tr>
        </#list>

    </table>
    <br/><br/>
    <button class="btn btn-primary active" onclick="window.close();">关闭</button>
    </@general.frame>

</#escape>