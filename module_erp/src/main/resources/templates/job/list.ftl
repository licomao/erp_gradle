<#import "../macros/mainFrame.ftl" as main />
<#import "../macros/formMacros.ftl" as form />
<#import "/spring.ftl" as spring />

<#escape x as x?html>
<@main.frame>

<script type="text/javascript">
    $('#collapseStaff').collapse('show');
    $(function () {
        if (!${user.checkAuthority(AUTHORITY.MANAGE_ORG_JOB)?c}) {
            window.location = "/noauthority"
        } else {
            var url = '/job/list/data?name=' + $("#name").val();
            url = encodeURI(url,"UTF-8");
            showList(url);
        }

    });

    function showList(url) {
        $("#gridBody").jqGrid({
            url: url,
            colModel: [
                { label: '职务名称', name: 'name', width: 50, align:"center" },
                { label: '状态', name: 'deleted', width: 50,align:"center",
                    formatter: function (cellvalue, options, rowObject) {
                        if (cellvalue) {
                            return "作废";
                        } else {
                            return "启用";
                        }
                    }
                },
                { label: '操作', name: 'id', width: 50, align:"center",
                    formatter:function(cellvalue, options, rowObject){
                        var enable = rowObject["deleted"];
                        //显示的超链接
                        var hrefString="<a onclick='toForm("+ cellvalue +")' href='#' style='margin-left:15px;text-decoration:underline;color:blue'>"+"修改"+"</a>"
//                        if (enable) {
//                            hrefString += "<a onclick='toEnable("+ cellvalue +")' href='#' style='margin-left:15px;text-decoration:underline;color:blue'>"+"启用"+"</a>";
//                        } else {
//                            hrefString += "<a onclick='toEnable("+ cellvalue +")' href='#' style='margin-left:15px;text-decoration:underline;color:blue'>"+"作废"+"</a>";
//                        }
                        return hrefString;
                    }
                }
            ],
            rownumbers: true
        });
    }

    function toForm(id) {
        window.open("/job/form?id=" + id);
    }

    function toEnable(id) {
        window.location = "/job/enable?id=" + id;
    }

    function queryList(){
        var url = '/job/list/data?name=' + $("#name").val();
        url = encodeURI(url,"UTF-8");
        jQuery("#gridBody").setGridParam({url:url}).trigger("reloadGrid", [{ page: 1}]);
    }


</script>
<div class="row">
    <legend>职位设置 -> 职位信息查询</legend>
</div>

<div class="row">
    <form class="" id="fm" action='<@spring.url relativeUrl = "/job/list"/>' method="GET">
        <div class="col-md-5">
            <label  class="control-label">职位名称: </label>
            <input type="text" name="name" id="name">&nbsp;
            &nbsp;
        </div>
        <div class="col-md-3">
            <@form.btn_search "onclick='queryList();'" "搜索"/>&nbsp;
            <@form.btn_search "onclick='toForm(0);'" "添加职位"/>
        </div>
    </form>
</div>
<br>
<table id="gridBody" class="scroll" cellpadding="0" cellspacing="0"></table>
<div id="toolBar"></div>

</@main.frame>
</#escape>