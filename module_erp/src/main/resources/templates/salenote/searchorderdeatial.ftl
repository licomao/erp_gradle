<#import "../macros/mainFrame.ftl" as main />
<#import "../macros/formMacros.ftl" as form />
<#import "/spring.ftl" as spring />

<#escape x as x?html>
  <@main.frame>

<script type="text/javascript">
    var shopid = "";
    var createdDateStart = "";
    var createdDateEnd = "";
    $('#collapsePayment').collapse('show');
    $(function() {
        if (!${user.checkAuthority(AUTHORITY.MANAGE_ORG_SEARCHORDERDEATIAL)?c}) {
            window.location = "/noauthority"
        } else {
            $('.Wdate').datepicker();
            $('.Wdate').datepicker("option", $.datepicker.regional["zh-TW"]);
            var url = '/salenote/searchorderdeatiallist?createdDateStart=' + $("#createdDateStart").val()
                    + "&createdDateEnd=" + $("#createdDateEnd").val()
                    + "&skuItemName=" + $("#skuItemName").val();

            if ($("#shopId").val() != "") {
                url += "&shopid=" + $("#shopId").val();
            }
            showCustomCardList(encodeURI(url,"UTF-8"));
        }
    });

    function showCustomCardList(url) {
        $("#orderList").jqGrid({
            url: url,
            mtype: 'POST',
            colModel: [
//                { label: 'id', name: 'id', hidden:true },
                { label: '品名', name: '0.orderedItem.name',align:"center", width: 80 },
                { label: '品牌', name: '0.orderedItem.brandName',align:"center", width: 80 },
                { label: '供货商', name: '0.orderedItem.supplier.name',align:"center", width: 60 },
                { label: '成本价(元)', name: '0.cost',align:"center", width: 40 },
                { label: '数量', name: '0.count',align:"center", width: 20 },
                { label: '实际售价(元)', name: '0.receivable',align:"center", width: 40 ,
                    formatter:function(cellvalue, options, rowObject) {
                        return parseFloat(cellvalue).toFixed(2);
                    }},
                { label: '施工人员', name: '0.merchandier.name',align:"center", width: 30,sortable:false },
                { label: '销售单号', name: '1.saleNoView',align:"center", width: 60,sortable:false ,
                    formatter:function(cellvalue, options, rowObject) {
                        return "<a onclick=\"toView('"+cellvalue+"')\" href='#' style='margin-left:15px;text-decoration:underline;color:blue'>"+cellvalue+"</a>";
                    }
                }
            ],
            gridComplete : function(){
                shopid = $("#shopId").val() ;
                createdDateStart = $("#createdDateStart").val();
                createdDateEnd = $("#createdDateEnd").val();
            }
        }).trigger("reloadGrid");
    }
    function toView(saleNoView) {
        window.location = "/salenote/salenoteView?saleNoView="+ saleNoView;
    }
    function reloadGrid() {
        var url = '/salenote/searchorderdeatiallist?createdDateStart=' + $("#createdDateStart").val()
                + "&createdDateEnd=" + $("#createdDateEnd").val()
                + "&skuItemName=" + $("#skuItemName").val();
        if ($("#shopId").val() != "") {
            url += "&shopid=" + $("#shopId").val();
        }
        jQuery("#orderList").setGridParam({url:encodeURI(url,"UTF-8")}).trigger("reloadGrid", [{ page: 1}]);
    }

    function toexport() {
        window.location = "/erpuser/exportorderdeatiallist?shopid=" + shopid + "&createdDateStart=" + createdDateStart
                            + "&createdDateEnd=" + createdDateEnd;
    }

</script>
  <legend>销售开单 -> 销售开单明细查询</legend>
  <div class="row">
      <div class=" col-md-offset-1 col-md-8">
          <label class="control-label">品名：</label>&nbsp;
          <input name="skuItemName" id="skuItemName" type="text" value=""/>&nbsp;
          <label  class="control-label">所属门店: </label>&nbsp;
          <select name="shopId" id="shopId">
              <#list shops as shop>
                  <option value="${shop.id}">${shop.name}</option>
              </#list>
          </select>&nbsp;
          <label class="control-label">开单日期起始时间:</label>&nbsp;
          <input type="text" name="createdDateStart" id="createdDateStart" class="Wdate" value="${createdDateStart}" readonly>&nbsp;
          -
          <input type="text" name="createdDateEnd" id="createdDateEnd" class="Wdate" value="${createdDateEnd}" readonly>&nbsp;
      </div>
      <div class="col-md-2">
          <@form.btn_search 'onclick="reloadGrid()"' "搜索" />
          <@form.btn_print 'onclick="toexport()"' "导出" />
      </div>
  </div>
  <div class="row" style="margin-top: 1%">
      <div class="col-md-offset-1 col-md-10">
          <table id="orderList" class="scroll" cellpadding="0" cellspacing="0"></table>
          <div id="toolBar"></div>
      </div>
    </div>
  </@main.frame>
</#escape>