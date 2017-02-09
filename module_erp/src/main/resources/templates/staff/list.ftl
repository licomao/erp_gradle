<#import "../macros/mainFrame.ftl" as main />
<#import "../macros/formMacros.ftl" as form />
<#import "/spring.ftl" as spring />

<#escape x as x?html>
<@main.frame>

<script type="text/javascript">
    $('#collapseStaff').collapse('show');
    $(function () {
        if (!${user.checkAuthority(AUTHORITY.MANAGE_ORG_STAFF)?c}) {
            window.location = "/noauthority"
        } else {
            var url = '/staff/list/data?name=' + $("#name").val()
                    + "&status=" + $("#status").val();
            if ($("#shopId").val() != "") {
                url += "&shop.id=" + $("#shopId").val();
            }
            url = encodeURI(url,"UTF-8");
            showList(url);
        }
    });

    function showList(url) {
        $("#gridBody").jqGrid({
            url: url,
            colModel: [
                { label: '姓名', name: 'name', width: 50, align:"center" },
                { label: '电话', name: 'phone', width: 70, align:"center"},
                { label: '身份证', name: 'identityCard', width: 70, align:"center"},
                { label: '所属门店', name: 'shop.name', width: 50, align:"center" },
                { label: '职位(工种)', name: 'job.name', width: 50, align:"center"},
                { label: '入职日期', name: 'entryDate', width: 50, align:"center",
                    formatter:function(cellvalue, option, rowObject){
                        return cellvalue != null ?  formatterDate(cellvalue.substr(0,19)+ ".000Z") : "";
                    }
                },
                { label: '离职日期', name: 'dimissionDate', width: 50, align:"center",
                    formatter:function(cellvalue, option, rowObject){
                        return cellvalue != null ?  formatterDate(cellvalue.substr(0,19)+ ".000Z") : "";
                    }
                },
                { label: '状态', name: 'status', width: 50,align:"center",
                    formatter: function (cellvalue, options, rowObject) {
                        switch(cellvalue) {
                            case "1" :
                                return "试用";
                            case "2" :
                                return "正式";
                            case "3" :
                                return "离职";
                        }
                    }
                },
                { label: '操作', name: 'id', width: 50, align:"center",
                    formatter:function(cellvalue, options, rowObject){
                        var status = rowObject["status"];
                        //显示的超链接
                        var hrefString="<a onclick='toForm("+ cellvalue +",2)' href='#' style='margin-left:15px;text-decoration:underline;color:blue'>"+"查看"+"</a>"
                                          +"<a onclick='toForm("+ cellvalue +",1)' href='#' style='margin-left:15px;text-decoration:underline;color:blue'>"+"修改"+"</a>";
                        <#if AUTHORITYSTR??>
                            <#if AUTHORITYSTR?index_of(",fu72,") != -1>
                                if(status == 3) {
                                    hrefString += "<a onclick='toDelete("+ cellvalue +")' href='#' style='margin-left:15px;text-decoration:underline;color:blue'>"+"删除"+"</a>";
                                }
                            </#if>
                        </#if>


                        return hrefString;
                    }
                }
            ],
            rownumbers: true
        });
    }

    function toForm(id,doType) {
        window.open("/staff/form?id=" + id + "&doType=" + doType);
    }

    function toDelete(id) {
        if(confirm("是否确认删除")){
            window.location = "/staff/delete?id=" + id;
        }
    }

    function queryList(){
        var url = '/staff/list/data?name=' + $("#name").val()
                + "&status=" + $("#status").val();
        if ($("#shopId").val() != "") {
            url += "&shop.id=" + $("#shopId").val();
        }
        url = encodeURI(url,"UTF-8");
        jQuery("#gridBody").setGridParam({url:url}).trigger("reloadGrid", [{ page: 1}]);
    }


</script>
<div class="row">
    <legend>员工管理 -> 员工信息查询</legend>
</div>

<div class="row">
    <form class="" id="fm" action='<@spring.url relativeUrl = "/staff/list"/>' method="GET">
        <div class="col-md-5">
            <label  class="control-label">员工姓名: </label>&nbsp;
            <input type="text" name="name" id="name">&nbsp;
            &nbsp;
            <label  class="control-label">所属门店: </label>&nbsp;
            <select name="shopId" id="shopId">
                <#if shops?size gt 1>
                    <legend><option value="">请选择</option></legend>
                </#if>
                <#list shops as shop>
                    <option value="${shop.id}">${shop.name}</option>
                </#list>
            </select>
            &nbsp;
            <label  class="control-label">员工状态: </label>&nbsp;
            <select name="status" id="status">
                <option value="">请选择</option>
                <option value="1">试用</option>
                <option value="2">正式</option>
                <option value="3">离职</option>
            </select>
        </div>
        <div class="col-md-3">
            <@form.btn_search "onclick='queryList();'" "搜索"/>&nbsp;
            <@form.btn_search "onclick='toForm(0,0);'" "员工入职"/>
        </div>
    </form>
</div>
<br>
<table id="gridBody" class="scroll" cellpadding="0" cellspacing="0"></table>
<div id="toolBar"></div>

</@main.frame>
</#escape>