<#import "../macros/mainFrame.ftl" as main />
<#import "../macros/formMacros.ftl" as form />
<#import "/spring.ftl" as spring />

<#escape x as x?html>
<@main.frame>

<script type="text/javascript">
    $(function () {
        $('#collapseShop').collapse('show');
        showList();
    });

     function queryList(){
        var oName = $("#orgNanme").val();
        var sName = $("#shopNanme").val();

        if((oName == null || oName.trim() == '') && (sName == null || sName.trim() == '')){
            jQuery("#gridBody").setGridParam({url:'/shop/list/data'}).trigger("reloadGrid", [{ page: 1}]);
            return;
        }

        var murl = '/shop/list/data?'
        murl += 'orgName=' + oName + '&';
        murl +='shopName=' + sName;

        jQuery("#gridBody").setGridParam({url:murl}).trigger("reloadGrid", [{ page: 1}]);
    }

    function showList(){
        $("#gridBody").jqGrid({
            url: '/shop/list/data',
            colModel: [
                { label: '序号', name: 'id' , width: 30 ,align:"center"},
                { label: '门店名称', name: 'name', width: 75 },
                { label: '联系电话', name: 'phone', width: 80},
                { label: '门店地址', name: 'address', width: 90 },
                { label: '营业时间', name: 'openingHours', width: 100 },
                { label: '操作', name: 'id', width: 75 ,align:"center",
                    formatter:function(cellvalue, options, rowObject){
                        var modify = "<a onclick=\"editById("+ cellvalue +")\" style='text-decoration:underline;color:blue'>"+"修改"+"</a>";
                        var dele = "   <a onclick=\"deleteById("+ cellvalue +")\" style='text-decoration:underline;color:blue'>"+"删除"+"</a>";
                        return modify + dele;
                    }
                }
            ],
            multiselect:true
        });
    }

    function editById(id){
        window.location = "/shop/new?id=" + id;
    }
    function deleteById(id){
        window.location = "/shop/delete?id=" + id;
    }

    function viewInfo(cellvalue) {
        alert(cellvalue);
    }
    function showChooseId() {
        var ids = $('#gridBody').jqGrid('getGridParam','selarrrow');
        if (ids <= 0) {
            alert("请先选择门店");
            return;
        }
        var shopIds = "";
        for (i=0; i<ids.length; i++) {
            var shop = $('#gridBody').jqGrid('getRowData', ids[i]);
            shopIds += shop.id;
            if (i != ids.length-1) shopIds += ", ";
        }
        alert("选中的ID是："+shopIds);
        return shopIds;
    }
    function toEdit(){
            var shopIds = showChooseId();
            $("#shopId").val(shopIds);
            $("#f1").submit();
        }

</script>


<div class="row">
    <div class="col-md-5">
        <label for="username" class="control-label">门店</label>
        <input class="form-control" type="text" name="shopNanme" id="shopNanme">
    </div>
    <div class="col-md-5">
        <label for="username" class="control-label">组织名称</label>
        <input class="form-control" type="text" name="orgNanme" id="orgNanme">
    </div>
    <div class="col-md-5">
        <button class="btn btn-primary active" onclick="queryList()">查询</button>
        <a class="btn btn-primary active" href="/shop/new" ><span id="tclick">创建门店</span></a>
    </div>
</div>



<table id="gridBody" class="scroll" cellpadding="0" cellspacing="0"></table>
<div id="toolBar"></div>

</@main.frame>
</#escape>