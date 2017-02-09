<#import "../macros/mainFrame.ftl" as main />
<#import "../macros/formMacros.ftl" as form />
<#import "/spring.ftl" as spring />

<#escape x as x?html>
    <@main.frame>

    <script type="text/javascript">
        var viewName = "";
        $('#collapsePt').collapse('show');
        $(function () {
            if (!${user.checkAuthority(AUTHORITY.MANAGE_ORG_STOCKINGORDER)?c}) {
                window.location = "/noauthority"
            } else {
                var url = '/fingerprint/list/data';
                showList(url);
            }
        });

        function showList(url) {
            $("#gridBody").jqGrid({
                url: url,
                colModel: [

                    {name: 'id', hidden: true},
                    {label: '设备序列号', name: 'sensorSN', width: 50, align: "center"},
//                    {label: '设备VID', name: 'vid', width: 100, align: "center"},
//                    {label: '设备PID', name: 'pid', width: 30, align: "center"},
                    {label: '门店', name: 'shop.name', width: 30, align: "center"},
                    {label: '组织', name: 'organization.name', width: 40, align: "center"}


                ],
                pager: '#toolBar',
                //multiselect:true,
                rownumbers: true
            });
        }

        /**
         * 条件查询方法
         */
        function queryList() {
            var url = '/fingerprint/list/data?&sensorSN=' + $("#sensorSN").val();

           /* if($("organizationId").val() != 0){
                url += '&organization.id=' + $("#organizationId").val();
            }
            if($("#shopId").val() != 0){
                url += '&shop.id=' + $("#shopId").val();
            }*/
            jQuery("#gridBody").setGridParam({url: url}).trigger("reloadGrid", [{ page: 1}]);
        }

        function update() {
            window.location = "/fingerprint/tosave";
        }

    </script>

    <legend>指纹机硬件信息管理 </legend>
    <div class="row">
        <div class="col-md-12">

            <label class="control-label">设备序列号: </label>
            <input type="text" name="sensorSN" style="width: 400px" id="sensorSN">

            <#--<label class="control-label">门店: </label>&nbsp;
            <select id="shopId">
                <option value="0">--请选择--</option>
                <#list shopList as shop >
                    <option value="${shop.id}">${shop.name}</option>
                </#list>
            </select>-->
            &nbsp; &nbsp;&nbsp; &nbsp;&nbsp; &nbsp;
            <@form.btn_search "onclick='queryList()'" "查 询"/>&nbsp;
            <@form.btn_search "onclick='update()'" "录 入"/>&nbsp;
        </div>
    </div>
    <br>
    <table id="gridBody" class="scroll" cellpadding="0" cellspacing="0"></table>
    <div id="toolBar"></div>

    </@main.frame>
</#escape>