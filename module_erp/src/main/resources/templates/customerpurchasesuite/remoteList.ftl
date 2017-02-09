<#import "../macros/mainFrame.ftl" as main />
<#import "../macros/formMacros.ftl" as form />
<#import "/spring.ftl" as spring />

<#escape x as x?html>
    <@main.frame>

    <script type="text/javascript">
        $('#collapseCustomerPurchasedSuite').collapse('show');
        $(function () {
            if (!${user.checkAuthority(AUTHORITY.MANAGE_ORG_CUSTOMERPURCHASESUITEREMOTE)?c}) {
                window.location = "/noauthority";
            } else {
                var url = '/customerpurchasesuite/remote/list/data?keyWord=';
                showList(url);
            }
        });

        function showList(url) {
            $("#gridBody").jqGrid({
                url: url,
                colModel: [

                    {label: '单号', name: 'settleOrder.saleNoView', width: 70, align: "center",
                        formatter:function(cellvalue, options, rowObject) {
                            return "<a onclick=\"toView('"+cellvalue+"')\" href='#' style='margin-left:15px;text-decoration:underline;color:blue'>"+cellvalue+"</a>";
                        }
                    },
                    {name: 'id', hidden: true},
                    {label: '车牌号', name: 'settleOrder.vehicleInfo.plateNumber', width: 70, align: "center"},
//            { label: '车牌号', name: 'model', width: 50, align:"center"},
                    {label: '客户', name: 'settleOrder.updatedBy', width: 40, align: "center"},
                    {label: '手机号', name: 'settleOrder.customer.mobile', width: 70, align: "center"},
                    {label: '消费金额', name: 'settleOrder.payment.amount', width: 50, align: "center"},
                    {label: '所属门店', name: 'belongShop.name', width: 50, align: "center"},
                    {label: '消费门店', name: 'shop.name', width: 50, align: "center"},
                    {
                        label: '日期', name: 'createdDate', width: 50, align: "center",
                        formatter: function (cellvalue) {
                            if (cellvalue != null && cellvalue != "") {

                                var newDate = cellvalue.substr(0, 10);
                                return newDate;
                            } else {
                                return "";
                            }
                        }
                    },
                    {
                        label: '操作', name: 'isSignFor', width: 50, align: "center",
                        formatter: function (cellvalue, options, rowObject) {
                            if (!cellvalue) {
                                return "<a onclick=\"changeStatus(" + rowObject["id"] + ")\" href='#' style='text-decoration:underline;color:blue'>签收</a>";
                            } else {
                                return "已签收";
                            }
//                    var modify = "<a onclick=\"update("+ rowObject["6"] +")\" href='#' style='text-decoration:underline;color:blue'>"+"修改"+"</a>&nbsp;&nbsp;&nbsp;";

                        }
                    }
                ],
                pager: '#toolBar',
                //multiselect:true,
                rownumbers: true
            });
        }


        function toView(saleNoView) {
            window.open("/salenote/salenoteView?saleNoView="+ saleNoView);
        }
        /**
        * 签收方法
        * */
        function changeStatus(id) {
            if (confirm("是否确认签收!"))
                $.get('/customerpurchasesuite/remote/confirm?id='+id,function(data){
                    if(data){
                        jQuery("#gridBody").trigger("reloadGrid", [{ page: 1}]);
                    }
                },'json');
        }

        /**
         * 条件查询方法
         */
        function queryList() {
            var url = '/customerpurchasesuite/remote/list/data?keyWord=' + $("#keyWord").val() + "&shopId=" + $("#shopId").val();
            url = encodeURI(url,"UTF-8");
            jQuery("#gridBody").setGridParam({url: url}).trigger("reloadGrid", [{ page: 1}]);
        }

    </script>

    <legend>会员套餐异地消费</legend>
    <div class="row">

        <div class="col-md-12">

            <label class="control-label">关键字: </label>
            <input type="text" name="keyWord" style="width: 300px" placeholder="可按车牌,手机号,客户,单号进行搜索" id="keyWord">
            &nbsp;&nbsp; &nbsp;&nbsp;&nbsp; &nbsp;
            <label class="control-label">消费门店: </label>
            <select id="shopId">
                <option value="">--请选择--</option>
                <#list shopList as shop >
                    <option value="${shop.id}">${shop.name}</option>
                </#list>
            </select>
            &nbsp; &nbsp;&nbsp;
            <@form.btn_search "onclick='queryList()'" "查 询"/>&nbsp;
        </div>
    </div>
    <br>
    <table id="gridBody" class="scroll" cellpadding="0" cellspacing="0"></table>
    <div id="toolBar"></div>

    </@main.frame>
</#escape>