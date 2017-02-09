<#import "../macros/mainFrame.ftl" as main />
<#import "../macros/formMacros.ftl" as form />
<#import "/spring.ftl" as spring />

<#escape x as x?html>
<@main.frame>

<script type="text/javascript">
    $('#collapseOrg').collapse('show');
    $(function () {
        if (!${user.checkAuthority(AUTHORITY.MANAGE_ORG_SHOPMANAGE)?c}) {
            window.location = "/noauthority"
        } else {
            var url = "/shop/list/data?orgId=" + $("#orgId").val() + "&name=" + $("#shopName").val();
            url = encodeURI(url,"UTF-8");
            showList(url);
        }
    });

     function queryList(){
        var url = "/shop/list/data?orgId=" + $("#orgId").val() + "&name=" + $("#shopName").val();
         url = encodeURI(url,"UTF-8");
        jQuery("#gridBody").setGridParam({url:url}).trigger("reloadGrid", [{ page: 1}]);
    }

    function showList(url){
        $("#gridBody").jqGrid({
            url: url,
            colModel: [
                { label: '门店名称', name: 'name', width: 60, align:"center"},
                { label: '门店地址', name: 'address', width: 90 , align:"center"},
                { label: '联系电话', name: 'phone', width: 50, align:"center"},
                { label: '营业时间', name: 'openingHours', width: 30 , align:"center"},
                { label: '门店类型', name: 'shopType', width: 30 , align:"center",
                    formatter:function(cellvalue,options,rowObject) {
                        if(cellvalue == 0) return "总店";
                        return "分店";
                    }

                },
                { label: '门店描述', name: 'description', width: 90 , align:"center"},
                { label: '操作', name: 'id', width: 40 ,align:"center",
                    formatter:function(cellvalue, options, rowObject){
                        var modify = "<a href='####' onclick=\"editById("+ cellvalue +")\" style='text-decoration:underline;color:blue'>"+"修改"+"</a>";
                        var dele = "   <a href='####' onclick=\"deleteById("+ cellvalue +")\" style='margin-left:30px;text-decoration:underline;color:blue'>"+"删除"+"</a>";
                        return modify + dele;
                    }
                }
            ],
            rownumbers:true
        });
    }

    function editById(id){
        window.location = "/shop/new?id=" + id;
    }
    function deleteById(id){
        if (confirm("是否确认删除!")){
            window.location = "/shop/delete?id=" + id;
        }
    }

    function viewInfo(cellvalue) {
        alert(cellvalue);
    }

    function toNew() {
        window.location = "/shop/new";
    }

</script>

<legend>门店管理 -> 门店查询</legend>

<div class="row">
    <div class="col-md-5">
        <label class="control-label">门店:</label>&nbsp;
        <input type="text" name="shopName" id="shopName">&nbsp;
        &nbsp; &nbsp;
        <label class="control-label">组织名称:</label>&nbsp;
        <input type="text" name="orgNanme" id="orgNanme" disabled="disabled" value="${organization.name}">&nbsp;
        <input type="hidden" id="orgId" value="${organization.id}">
    </div>
    <div class="col-md-5">
        <@form.btn_search "onclick='queryList();'" "查 询" />&nbsp;&nbsp;
        <@form.btn_add "onclick='toNew()'" "创建门店"/>
    </div>
</div>
<br>


<table id="gridBody" class="scroll" cellpadding="0" cellspacing="0"></table>
<div id="toolBar"></div>

</@main.frame>
</#escape>