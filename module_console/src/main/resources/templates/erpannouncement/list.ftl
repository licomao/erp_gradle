<#import "../macros/mainFrame.ftl" as main />
<#import "../macros/formMacros.ftl" as form />
<#import "/spring.ftl" as spring />

<#escape x as x?html>
    <@main.frame>

    <script type="text/javascript">
        $('#collapsePt').collapse('show');
        $(function () {
            if (!${user.checkAuthority(AUTHORITY.MANAGE_ORG_FIXEDASSET)?c}) {
                window.location = "/noauthority";
            } else {
                var url = "/erpannouncement/list/data";
                url += "?title=" + $("#title").val() + "&publisher=" + $("#publisher").val()
                        + "&orgId=" + $("#orgId").val();
                showList(url);
            }
        });

        function showList(url) {
            $("#gridBody").jqGrid({
                url: url,
                colModel: [
                    { label: '公告标题', name: 'title', width: 75 ,align:"center" },
                    { label: '落款人', name: 'publisher', width: 80 ,align:"center"},
                    { label: '发布日期', name: 'publishDate', width: 75 ,align:"center" ,
                        formatter:"date",formatoptions: {srcformat:'Y-m-d',newformat:'Y-m-d'} },
                    { label: '公告状态', name: 'deleted', width: 50 ,align:"center",
                        formatter:function(cellvalue, options, rowObject){
                            if (cellvalue){
                                return "无效";
                            } else {
                                return "有效";
                            }

                        }
                    },
                    { label: '操作', name: 'id', width: 75 ,align:"center",
                        formatter:function(cellvalue, options, rowObject){
                            var modify = "<a href='####' onclick=\"editById("+ cellvalue +")\" style='text-decoration:underline;color:blue'>"+"修改"+"</a>";
                            var dele = "<a href='####'onclick=\"deleteById("+ cellvalue +")\" style='margin-left:30px;text-decoration:underline;color:blue'>"+"删除"+"</a>";
                            return modify + dele;
                        }
                    }
                ],
                rownumbers:true
            });
        }

        function queryList() {
            var url = "/erpannouncement/list/data";
            url += "?title=" + $("#title").val() + "&publisher=" + $("#publisher").val()
                    + "&orgId=" + $("#orgId").val();
            jQuery("#gridBody").setGridParam({url:url}).trigger("reloadGrid", [{ page: 1}]);
        }

        function editById(id) {
            window.location = "/erpannouncement/new?id=" + id;
        }
        function deleteById(id) {
            if(confirm("是否确认删除")){
                window.location = "/erpannouncement/delete?id=" + id;
            }
        }

        function toAdd(){
            window.location ="/erpannouncement/new";
        }

    </script>
    <div class="row">
        <legend>公告管理 -> 公告查询</legend>
    </div>

    <div class="row">
        <div class="col-md-5">
            <label   class="control-label">公告标题:</label>&nbsp;
            <input  type="text"  id="title">&nbsp;
            <label for="publisher" class="control-label">落款人:</label>&nbsp;
            <input  type="text"  id="publisher">&nbsp;
            <input type="hidden" id="orgId" value="${organization.id}" >&nbsp;

        </div>
        <div class="col-md-6">  <@form.btn_search "onclick='queryList();'" "查 询"/>
        <@form.btn_add "onclick='toAdd();'" "发布新公告"/>
        </div>
    </div>
<br>
    <table id="gridBody" class="scroll" cellpadding="0" cellspacing="0"></table>
    <div id="toolBar"></div>

    </@main.frame>
</#escape>