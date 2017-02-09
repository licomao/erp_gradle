<#import "../macros/mainFrame.ftl" as main />
<#import "../macros/formMacros.ftl" as form />
<#import "/spring.ftl" as spring />

<#escape x as x?html>
  <@main.frame>

<script type="text/javascript">
    $('#collapsePayment').collapse('show');
    $(function() {
        if (!${user.checkAuthority(AUTHORITY.MANAGE_ORG_SEARCHSETTLEINFO)?c}) {
            window.location = "/noauthority"
        } else {
            $('.Wdate').datepicker();
            $('.Wdate').datepicker("option", $.datepicker.regional["zh-TW"]);
            var url = '/salenote/settleList/searchdata?saleNoView=' + $("#saleNoView").val()
                    + "&tel=" + $("#tel").val()
                    + "&shopId=" + $("#shopId").val()
                    + "&isDeleted=" + $("#isDeleted").val()
                    + "&isFinished=" + $("#isFinished").val()
                    + "&plateNumber=" + $("#plateNumber").val();
            if ($("#createdDate").val() != "") {
                url += "&createdDate=" + $("#createdDate").val();
            }
            showCustomCardList(url);
        }
    });

    function showCustomCardList(url) {
        $("#settleList").jqGrid({
            url: url,
            mtype: 'POST',
            colModel: [
//                { label: 'id', index:'id', name: 'id', hidden:true },
                { label: '销售单号', name: '0',align:"center", width: 80,sortable:false,
                    formatter:function(cellvalue, options, rowObject) {
                        return "<a onclick=\"toView('"+cellvalue+"')\" href='#' style='margin-left:15px;text-decoration:underline;color:blue'>"+cellvalue+"</a>";
                    }
                },
                { label: '客户名称', name: '1',align:"center", width: 40,sortable:false },
                { label: '手机', name: '2',align:"center", width: 50,sortable:false },
                { label: '车牌号', name: '3',align:"center", width: 45,sortable:false },
                { label: '接车人', name: '4',align:"center", width: 30,sortable:false },
                { label: '开单日期', name: '5',align:"center", width: 70,sortable:false ,
                    formatter:function(cellvalue, options, rowObject) {
                        if(cellvalue!=null){
                            return formatterDateWithSecond(cellvalue);
                        } else {
                            return "";
                        }
                    }
                },
                { label: '结算状态', name: '6',align:"center", width: 30,sortable:false,
                formatter:function(value) {
                    if(value) {
                        return "已结算";
                    }else {
                        return "未结算";
                    }
                }},
                { label: '结算日期', name: '7',align:"center", width: 65,sortable:false,
                    formatter:function(cellvalue, options, rowObject) {
                        if(cellvalue!=null && rowObject['6'] == "1"){
                            return formatterDateWithSecond(cellvalue);
                        } else {
                            return "";
                        }
                    }
                },

                { label: '是否作废', name: '8',align:"center", width: 35,sortable:false,
                    formatter:function(cellvalue, options, rowObject) {
                        if(rowObject['8']) {
                            return "已作废";
                        } else {
                            return "未作废";
                        }
                    }
                },
                { label: '预约id', name: '9',align:"center", width: 40,sortable:false, hidden:true },
                { label: '会员卡id', name: '10',align:"center", width: 1,sortable:false, hidden:true },
                { label: '合计金额', name: '11',align:"center", width: 35,sortable:false},
                { label: '销售单据来源',align:"center", width: 45,sortable:false,
                    formatter:function(cellvalue, options, rowObject) {
                        if(rowObject['9'] != "" && rowObject['9'] != null) {
                            return "APP销售单";
                        } else if(rowObject['10'] != "" && rowObject['10'] != null) {
                            return "会员套餐销售单";
                        } else {
                            return "普通施工单";
                        }
                    }
                },
                { label: '最小折扣hid', name: '13',align:"center", width: 30, hidden:true },
                { label: '最小折扣', name: '14',align:"center", width: 30,sortable:false,
                    formatter:function(cellvalue, options, rowObject) {
                        return "<label id='dis_"+ rowObject['13']+"'></label>"
                    }
                },
                { label: '备注', name: '12',align:"center", width: 50,sortable:false},
                { label: '操作', name: '8',align:"center", width: 60,sortable:false,
                formatter:function(cellvalue, options, rowObject) {
                                    //显示的超链接
                    var hrefString="<div id='do_"+ rowObject['13'] +"'>";
                    if(!rowObject['8']) {
                        hrefString += '<Button class="btn btn-primary"  onclick="toDelete' +
                                '(\''+ rowObject['0'] + '\')">作 废<//Button>';
                        if(!rowObject['6']) {
                            if(rowObject['9'] != "" && rowObject['9'] != null) {
                                hrefString += '<Button class="btn btn-primary" id="btn-'+rowObject['0']+'" onclick="gotoCustomInfoapp' +
                                        '(\''+ rowObject['0'] + '\')">结 算<//Button>';
                            } else {
                                hrefString += '<Button class="btn btn-primary" id="btn-'+rowObject['0']+'" onclick="gotoCustomInfo' +
                                        '(\''+ rowObject['0'] + '\')">结 算<//Button>';
                            }
                        }else {
                            hrefString += '<Button class="btn btn-primary" id="btn-'+rowObject['0']+'" onclick="print' +
                                    '(\''+ rowObject['0'] + '\')">打 印<//Button>';
                        }

                    } else {
                        hrefString += '<Button class="btn btn-primary"  onclick="toRecover' +
                                '(\''+ rowObject['0'] + '\')">启 用<//Button>';
                    }

                    return hrefString + "</div>";
                }}
            ],
            gridComplete:function() {
                getDiscount();
            }
        }).trigger("reloadGrid");
    }
    function   getDiscount(){
        var obj = $("#settleList").jqGrid("getRowData");
        jQuery(obj).each(function(){
            var dislab = document.getElementById("dis_"+ this['13']);
            var doSize = document.getElementById("do_"+ this['13']);
            var isHuiYuan = this['10'];
            $.ajax( {
                url:"/salenote/settlelist/mindicount",
                data:{
                    settleId : this['13']
                },
                type:'get',
                success:function(data) {
                    var discount = data.discount;
                    dislab.innerHTML=discount;
                    var size = data.size;
                    if (size == 0 && isHuiYuan !=null && isHuiYuan != ""){
                        doSize.innerHTML = "";
                    }
                },
                error : function() {
                    dislab.innerHTML="100%";
                }
            });

        });
    }

    function reloadGrid() {
        var url = '/salenote/settleList/searchdata?saleNoView=' + $("#saleNoView").val()
                + "&tel=" + $("#tel").val()
                + "&shopId=" + $("#shopId").val()
                + "&isDeleted=" + $("#isDeleted").val()
                + "&isFinished=" + $("#isFinished").val()
                + "&plateNumber=" + $("#plateNumber").val();
        if ($("#createdDate").val() != "") {
            url += "&createdDate=" + $("#createdDate").val();
        }
        jQuery("#settleList").setGridParam({url:url}).trigger("reloadGrid", [{ page: 1}]);
    }

    //跳转到结算页面
    function gotoCustomInfo(saleNoView) {
        window.location = "/salenote/shownotsettleinfo?saleNoView="+ saleNoView;
    }

    //跳转到结算页面
    function gotoCustomInfoapp(saleNoView) {
        window.location = "/salenote/shownotsettleinfoapp?saleNoView="+ saleNoView;
    }

    function print(saleNoView) {
        window.open("/salenote/salenoteprint?saleNoView="+ saleNoView);
    }

    function toDelete(saleNoView) {
        if (confirm("是否确认?")){
            window.location = "/salenote/delete?saleNoView="+ saleNoView;
        }
    }
    function toRecover(saleNoView) {
        if (confirm("是否确认启用")){
            window.location = "/salenote/delete?saleNoView="+ saleNoView;
        }
    }

    function toView(saleNoView) {
        window.open("/salenote/salenoteView?saleNoView="+ saleNoView);
    }

</script>
  <legend>销售开单 -> 销售开单查询</legend>
  <div class="row">
      <div class="col-md-10">
          <label class="control-label">销售单:</label>&nbsp;
          <input type="text" name="saleNoView"size="11" id="saleNoView">&nbsp;
          <label class="control-label">客户手机:</label>&nbsp;
          <input type="text" name="tel" size="9" id="tel">&nbsp;
          <label class="control-label">车牌号:</label>&nbsp;
          <input type="text" name="plateNumber"size="6" value="" id="plateNumber">&nbsp;
          <label class="control-label">是否作废:</label>&nbsp;
          <select name="isDeleted" id="isDeleted">
              <option value="false">未作废</option>
              <option value="true">已作废</option>
          </select>&nbsp;
          <label class="control-label">结算状态:</label>&nbsp;
          <select name="isFinished" id="isFinished">
              <option value="">请选择</option>
              <option value="false">未结算</option>
              <option value="true">已结算</option>
          </select>&nbsp;

          <label class="control-label">开单日期:</label>&nbsp;
          <input type="text" name="createdDate" id="createdDate" size="11" class="Wdate" readonly>&nbsp;
          <label  class="control-label">所属门店: </label>&nbsp;
          <select style="width:170px;" name="shopId" id="shopId">
              <#list shops as shop>
                  <option value="${shop.id}">${shop.name}</option>
              </#list>
          </select>&nbsp;
      </div>
      <div class="col-md-2">
          <@form.btn_search 'onclick="reloadGrid()"' "搜索" />
      </div>
  </div>
  <div class="row" style="margin-top: 1%">
      <div class=" col-md-12">
          <table id="settleList" class="scroll" cellpadding="0" cellspacing="0"></table>
          <div id="toolBar"></div>
      </div>
    </div>
  </@main.frame>
</#escape>