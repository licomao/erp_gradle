<#import "../macros/mainFrame.ftl" as main />
<#import "../macros/formMacros.ftl" as form />
<#import "/spring.ftl" as spring />

<#escape x as x?html>
<@main.frame>

<script type="text/javascript">
    $('#collapsePt').collapse('show');
    $(function () {
        if (!${user.checkAuthority(AUTHORITY.MANAGE_ORG_SUPPLIER)?c}) {
            window.location = "/noauthority"
        } else {
        $('.Wdate').datepicker();
        $('.Wdate').datepicker("option",$.datepicker.regional["zh-TW"]);
        var url = '/campaign/list/data?compaignType=' + $("#compaignType").val()
                + "&publishDate=" + $("#publishDate").val();
            showList(url);
        }
    });

    function showList(url) {
        $("#gridBody").jqGrid({
            url: url,
            colModel: [
                { label: '广告摘要', name: 'summary', width: 50, align:"center" },
                { label: '推广类型', name: 'compaignType', width: 70, align:"center",
                    formatter: function (cellvalue, options, rowObject) {
                        switch(cellvalue) {
                            case 0 :
                                return "全平台";
                            case 1 :
                                return "城市";
                            case 2 :
                                return "附近";
                        }
                    }
                },
                { label: '发布时间', name: 'publishDate', width: 70, align:"center",
                    formatter:function(cellvalue, option, rowObject){
                        return cellvalue != null ? cellvalue.substring(0,10) : "";
                    }
                },
                { label: '操作', name: 'id', width: 50, align:"center",
                    formatter:function(cellvalue, options, rowObject){
                        var enable = rowObject["deleted"];
                        //显示的超链接
                        var hrefString="<a onclick='toForm("+ cellvalue +",1)' href='#' style='margin-left:15px;text-decoration:underline;color:blue'>"+"修改"+"</a>";
                        if (enable) {
                            hrefString += "<a onclick='toDelete("+ cellvalue +")' href='#' style='margin-left:15px;text-decoration:underline;color:blue'>"+"启用"+"</a>";
                        } else {
                            hrefString += "<a onclick='toDelete("+ cellvalue +")' href='#' style='margin-left:15px;text-decoration:underline;color:blue'>"+"作废"+"</a>";
                        }
                        return hrefString;
                    }
                }
            ],
            rownumbers: true
        });
    }

    function toForm(id,doType) {
        window.location = "/campaign/form?id=" + id;
    }

    function toDelete(id) {
        if(confirm("是否确认删除")){
            window.location = "/campaign/enable?id=" + id;
        }
    }

    function queryList(){
        var url = '/campaign/list/data?compaignType=' + $("#compaignType").val()
                + "&publishDate=" + $("#publishDate").val();
        jQuery("#gridBody").setGridParam({url:url}).trigger("reloadGrid", [{ page: 1}]);
    }


</script>
<div class="row">
    <legend>APP公告管理 -> APP公告查询</legend>
</div>

<div class="row">
    <form class="" id="fm" action='<@spring.url relativeUrl = "/campaign/list"/>' method="GET">
        <div class="col-md-5">
            <label  class="control-label">推广类型: </label>&nbsp;
            <select name="compaignType" id="compaignType">
                <option value="0">全平台</option>
                <option value="1">城市</option>
                <option value="2">附近</option>
            </select>&nbsp;
            <label  class="control-label">发布时间: </label>&nbsp;
            <input type="text" name="publishDate" id="publishDate" class="Wdate" value="${publishDate}" readonly>&nbsp;
        </div>
        <div class="col-md-3">
            <@form.btn_search "onclick='queryList();'" "查 询"/>&nbsp;
            <@form.btn_search "onclick='toForm(0,0);'" "发布新公告"/>
        </div>
    </form>
</div>
<br>
<table id="gridBody" class="scroll" cellpadding="0" cellspacing="0"></table>
<div id="toolBar"></div>

</@main.frame>
</#escape>