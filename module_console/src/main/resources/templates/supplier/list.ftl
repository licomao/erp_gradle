<#import "../macros/mainFrame.ftl" as main />
<#import "../macros/formMacros.ftl" as form />
<#import "/spring.ftl" as spring />

<#escape x as x?html>
    <@main.frame>

    <script type="text/javascript">

        $(function () {

            $('#backstagemanagement').collapse('show');
            $("#SelectOrgs").change(function(){selectChange();});
            $("#SelectOrgs ").val($("#hOrgid ").val());  //初始化select

            showList();
        });

        function showList() {
            $("#gridBody").jqGrid({
                url: '/supplier/list/data',
                colModel: [
                    { label: '序号', name: 'id' , width: 30 ,align:"center"},
                    { label: '供应商名称', name: 'name', width: 75 },
                    { label: '联系方式', name: 'contactInfo', width: 80},
                    { label: '邮箱', name: 'email', width: 80},
                    { label: '传真', name: 'fax', width: 80},
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

        function queryList() {

            var nm = $("#name").val();
            var oid = $("#SelectOrgs").val();
            if(nm == null || nm.trim() == '')  nm = '';
            if(oid == null || oid.trim() == '') oid = '0';

            var murl = '/supplier/list/data?'
            murl += 'name=' + nm + '&';
            murl +='orgid=' + oid;

            jQuery("#gridBody").setGridParam({url:murl}).trigger("reloadGrid", [{ page: 1}]);
        }

        function editById(id) {
            window.location = "/supplier/new?id=" + id;
        }
        function deleteById(id) {
            window.location = "/erpannouncement/delete?id=" + id;
        }
        function newSupplier()
        {
            window.location = "/supplier/new" ;
        }
    </script>

    <div class="row">
        <div class="col-md-5" style="margin-left: 40px;">
            <label for="tittle" class="control-label">供应商名称</label>
            <input  type="text"  id="name">
        </div>
        <div class="col-md-5" style="margin-left: -300px;">
            <label for="publisher" class="control-label">所属组织</label>
            <input  type="hidden"  id="hOrgid" value="${curOrgid}">
            <select name="orgs" id="SelectOrgs" >
                <option value ="0"> 全部 </option>
                <#list orgs as org >
                    <option value ="${org.id}"> ${org.name}</option>
                </#list>
            </select>
            <button class="btn btn-primary active" style="margin-left: 40px;" onclick="queryList()">查询</button>
            <button class="btn btn-primary active" style="margin-left: 20px;" onclick="newSupplier()" />新增供应商</button>

        </div>
    </div>

    <table id="gridBody" class="scroll" cellpadding="0" cellspacing="0"></table>
    <div id="toolBar"></div>


    </@main.frame>
</#escape>