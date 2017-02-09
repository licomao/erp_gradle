<#import "../macros/mainFrame.ftl" as main />
<#import "../macros/formMacros.ftl" as form />
<#import "/spring.ftl" as spring />

<#escape x as x?html>
    <@main.frame>

    <script type="text/javascript">

        $(function () {
            $('#backstagemanagement').collapse('show');
            showList();
        });

        function showList() {
            $("#gridBody").jqGrid({
                url: '/suite/list/data',
                colModel: [
                    { label: '序号', name: 'id' , width: 30 ,align:"center"},
                    { label: '卡名称', name: 'name', width: 75 },
                    { label: '有效期(天)', name: 'expiation', width: 80},
                    { label: '套餐售价', name: 'price', width: 80},
                    { label: 'APP绑定', name: 'isBindApp', width: 80},
                    { label: '操作', name: 'id', width: 75 ,align:"center",
                        formatter:function(cellvalue, options, rowObject) {
                           return  getOptHtm(cellvalue ,rowObject );
                        }
                    }
                ],
                multiselect:true
            });
        }

        function queryList()
        {
            var nm = $("#name").val();
            if(nm == null || nm.trim() == '')  nm = '';
            var murl = '/suite/list/data?'
            murl += 'name=' + nm;
            jQuery("#gridBody").setGridParam({url:murl}).trigger("reloadGrid", [{ page: 1}]);
        }

        function getOptHtm(cellvalue , rowObject )
        {
            var modify = "<a onclick=\"editById("+ cellvalue +")\" style='text-decoration:underline;color:blue'>"+"修改"+"</a>";
            var dele = "";
            if(rowObject['isUse'] == 1) {
                var dele = "   <a onclick=\"changeState("+ cellvalue +" , 0 )\" style='text-decoration:underline;color:blue'>"+"作废"+"</a>";
            }else{
                var dele = "   <a onclick=\"changeState("+ cellvalue +" , 1 )\" style='text-decoration:underline;color:blue'>"+"启用"+"</a>";
            }
            return modify + dele;
        }

        function newSupplier() {
            window.location = "/suite/form";
        }

        function editById(id) {
//            window.location = "/supplier/new?id=" + id;
        }

        function changeState(id, state) {
//            window.location = "/erpannouncement/delete?id=" + id;
        }

    </script>

    <div class="row">
        <div class="col-md-5" style="margin-left: 40px;">
            <label for="tittle" class="control-label">卡名称</label>
            <input  type="text"  id="name">
        </div>
        <div class="col-md-5" style="margin-left: -300px;">
            <button class="btn btn-primary active" style="margin-left: 40px;" onclick="queryList()">查询</button>
            <button class="btn btn-primary active" style="margin-left: 20px;" onclick="newSupplier()" />新增会员卡种</button>
        </div>
    </div>

    <table id="gridBody" class="scroll" cellpadding="0" cellspacing="0"></table>
    <div id="toolBar"></div>

    </@main.frame>
</#escape>