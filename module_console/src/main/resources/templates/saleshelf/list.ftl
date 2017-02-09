<#import "../macros/mainFrame.ftl" as main />
<#import "../macros/formMacros.ftl" as form />
<#import "/spring.ftl" as spring />

<#escape x as x?html>
    <@main.frame>

    <script type="text/javascript">
        $('#collapsePt').collapse('show');
        $(function () {
            if (!${user.checkAuthority(AUTHORITY.MANAGE_ORG_EXPENSE)?c}) {
                window.location = "/noauthority"
            } else {
                var url = '/saleshelf/list/data?orgId=' + $("#orgId").val();
                if ($("#saleCategory").val() != "") {
                    url += "&saleCategory=" + $("#saleCategory").val();
                }
                showList(url);
            }
        });

        function showList(url) {
            $("#gridBody").jqGrid({
                url: url,
                colModel: [
                    { label: '分类', name: 'saleCategory', width: 50, align:"center" ,
                        formatter: function (cellvalue, options, rowObject) {
                            switch(cellvalue) {
                                case 1 :
                                    return "洗车";
                                case 2 :
                                    return "美容";
                                case 3 :
                                    return "保养";
                                case 4 :
                                    return "配件";
                                case 5 :
                                    return "维修";
                                case 6 :
                                    return "会员";
                            }
                        }
                    },
                    { label: '出售价格', name: 'price', width: 70, align:"center"},
                    { label: '所属组织', name: 'organization.name', width: 70, align:"center"},
                    { label: '商品名称', name: 'skuItem.name', width: 50, align:"center" },
                    { label: '套餐名称', name: 'suite.name', width: 50, align:"center"},
                    { label: '状态', name: 'deleted', width: 50,align:"center",
                        formatter:function(cellvalue, option, rowObject){
                            if (cellvalue == false){
                                return "有效";
                            }else{
                                return "无效";
                            }
                        }
                    },
                    { label: '操作', name: 'id', width: 50, align:"center",
                        formatter:function(cellvalue, options, rowObject){
                            var status = rowObject["deleted"];
                            //显示的超链接
                            var hrefString="<a onclick='toForm("+ cellvalue +",2)' href='#' style='margin-left:15px;text-decoration:underline;color:blue'>"+"查看"+"</a>"
                                    +"<a onclick='toForm("+ cellvalue +",1)' href='#' style='margin-left:15px;text-decoration:underline;color:blue'>"+"修改"+"</a>";
                            if (status) {
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
            window.location = "/saleshelf/form?id=" + id + "&doType=" + doType;
        }

        function toDelete(id) {
            if(confirm("是否确认删除")){
                window.location = "/saleshelf/delete?id=" + id;
            }
        }

        function queryList(){
            var url = '/saleshelf/list/data?orgId=' + $("#orgId").val();
            if ($("#saleCategory").val() != "") {
                url += "&saleCategory=" + $("#saleCategory").val();
            }
            jQuery("#gridBody").setGridParam({url:url}).trigger("reloadGrid", [{ page: 1}]);
        }


    </script>
    <div class="row">
        <legend>APP上架管理 -> APP上架信息查询</legend>
    </div>

    <input  type="hidden"  id="orgId" value="${orgId}">

    <div class="row">
        <form class="" id="fm" action='<@spring.url relativeUrl = "/saleshelf/list"/>' method="GET">
            <div class="col-md-5">
                <label  class="control-label">上架分类: </label>&nbsp;
                <select name="saleCategory" id="saleCategory">
                    <option value="">请选择</option>
                    <option value="1">洗车</option>
                    <option value="2">美容</option>
                    <option value="3">保养</option>
                    <option value="4">配件</option>
                    <option value="5">维修</option>
                    <option value="6">会员</option>
                </select>
            </div>
            <div class="col-md-3">
                <@form.btn_search "onclick='queryList();'" "搜索"/>&nbsp;
                <@form.btn_search "onclick='toForm(0,0);'" "新增"/>
            </div>
        </form>
    </div>
    <br>
    <table id="gridBody" class="scroll" cellpadding="0" cellspacing="0"></table>
    <div id="toolBar"></div>

    </@main.frame>
</#escape>