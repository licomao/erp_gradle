<#import "../macros/mainFrame.ftl" as main />
<#import "../macros/formMacros.ftl" as form />
<#import "/spring.ftl" as spring />

<#escape x as x?html>
<@main.frame>

<script type="text/javascript">
    $('#collapseStaff').collapse('show');
    $(function () {
        if (!${user.checkAuthority(AUTHORITY.MANAGE_ORG_STAFFATTENDANCE)?c}) {
            window.location = "/noauthority"
        } else {
            $('.Wdate').datepicker();
            $('.Wdate').datepicker("option",$.datepicker.regional["zh-TW"]);
            var url = '/staffattendance/list/data?staff.name=' + $("#name").val()
                    + "&workDate=" + $("#workDate").val()
                    + "&workDateEnd=" + $("#workDateEnd").val();
            if ($("#shopId").val() != "") {
                url += "&staff.shop.id=" + $("#shopId").val();
            }
            url = encodeURI(url,"UTF-8");
            showList(url);
        }
    });

    function exportExcel (){
        var url = '/staffattendance/excel/export?staff.name=' + $("#name").val()
                + "&workDate=" + $("#workDate").val()
                + "&workDateEnd=" + $("#workDateEnd").val();
        if ($("#shopId").val() != "") {
            url += "&staff.shop.id=" + $("#shopId").val();
        }
        url = encodeURI(url,"UTF-8");
        window.location = url;
    }

    function showList(url) {
        $("#gridBody").jqGrid({
            url: url,
            colModel: [
                { label: '姓名', name: 'staff.name', width: 50, align:"center" },
                { label: '电话', name: 'staff.phone', width: 70, align:"center"},
                { label: '所属门店', name: 'staff.shop.name', width: 50, align:"center" },
                { label: '职位(工种)', name: 'staff.job.name', width: 50, align:"center"},
                { label: '上班日期', name: 'workDate', width: 50, align:"center",
                    formatter:function(cellvalue, option, rowObject){
                        return formatterDate(cellvalue.substr(0,19)+ ".000Z");
                    }
                },
                { label: '上班时间', name: 'arriveDate', width: 50, align:"center",
                    formatter:function(cellvalue, option, rowObject){
                        if (cellvalue==null ) {
                            return "";
                        } else {
                            return formatterDateWithSecond(cellvalue);
                        }
                    }
                },
                { label: '下班时间', name: 'leaveDate', width: 50, align:"center",
                    formatter:function(cellvalue, option, rowObject){
                        if (cellvalue==null ) {
                            return "";
                        } else {
                            return formatterDateWithSecond(cellvalue);
                        }
                    }
                }
            ],
            rownumbers: true
        });
    }

    function queryList(){
        var url = '/staffattendance/list/data?staff.name=' + $("#name").val()
                + "&workDate=" + $("#workDate").val()
                + "&workDateEnd=" + $("#workDateEnd").val();
        if ($("#shopId").val() != "") {
            url += "&staff.shop.id=" + $("#shopId").val();
        }
        url = encodeURI(url,"UTF-8");
        jQuery("#gridBody").setGridParam({url:url}).trigger("reloadGrid", [{ page: 1}]);
    }


</script>

<div class="row">
    <legend>员工考勤 -> 上下班考勤</legend>
</div>
<div class="row">
    <form class="" id="fm" action='<@spring.url relativeUrl = "/staffattendance/list"/>' method="GET">
        <div class="col-md-8">
            <label  class="control-label">员工姓名: </label>
            <input type="text" name="name" id="name">&nbsp;
            &nbsp;
            <label  class="control-label">所属门店: </label>
            <select name="shopId" id="shopId">
                <#if shops?size gt 1>
                    <legend><option value="">请选择</option></legend>
                </#if>
                <#list shops as shop>
                    <option value="${shop.id}">${shop.name}</option>
                </#list>
            </select>
            &nbsp;
            <label  class="control-label">上班日期: </label>
            <input type="text" name="workDate" id="workDate" class="Wdate" value="${workDate}" readonly>&nbsp;
            -&nbsp;
            <input type="text" name="workDateEnd" id="workDateEnd" class="Wdate" value="${workDate}" readonly>&nbsp;
        </div>
        <div class="col-md-2">
            <@form.btn_search "onclick='queryList();'" "搜索"/>&nbsp;&nbsp;
            <@form.btn_pages "onclick='exportExcel();'" "导出考勤"/>
        </div>
    </form>
</div>
<br>
<table id="gridBody" class="scroll" cellpadding="0" cellspacing="0"></table>
<div id="toolBar"></div>

</@main.frame>
</#escape>