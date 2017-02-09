<#import "../macros/mainFrame.ftl" as main />
<#import "../macros/formMacros.ftl" as form />
<#import "/spring.ftl" as spring />
<#escape x as x?html>
    <@main.frame>
    <script type="text/javascript">
        $('#collapseShop').collapse('show');



        function subForm(){
            if ($("#excelUpload").val().length==0){
                alert("请先选择文件");
                return;
            }
            if(confirm("是否确认导入数据!?")){
                $("#uploadForm").submit();
            }
            return;
        }

        function downloadFile(){
            window.location.href = "/file/shop.xlsx";
        }

    </script>

        <legend>商品管理 -> 商品批量导入</legend>

        <div class="row" >
            <form action="/customerstockitem/excel/upload" enctype="multipart/form-data" method="post" id="uploadForm">
                <div class="col-md-offset-1 col-md-7" >
                    <div class="col-md-3" ><input type="file" id="excelUpload" style="width: 200px;" name="file"  /></div>
                    <div class="col-md-6">
                    <@form.btn_pages "onclick='subForm();'" "导入"/>&nbsp; &nbsp;
                        <@form.btn_print "onclick='downloadFile();'" "导入模板下载"/>
                    </div>
                </div>
            </form>

        </div>

        <div class="row" style="margin-top: 1%">
            <div class="col-md-offset-1 col-md-7">
                <div class="form-group">
                    <label class="control-label" >导入结果信息查看</label>
                    <div class="controls">
                        <textarea id="description" name="description" style="width:745px;height:300px" readonly ><#if description>成功导入数据 ${success} 条
错误数据有 ${wrongSize} 条
                            <#if wrongs??>
                                <#list wrongs as wrong>
行:${wrong.line} &nbsp; &nbsp;<#if wrong.name??>商品名:${wrong.name}</#if> &nbsp; &nbsp; &nbsp;<#if wrong.barCode??>条形码:${wrong.barCode}</#if>&nbsp; &nbsp; &nbsp; <#if wrong.brandName??>品牌:${wrong.brandName}</#if>&nbsp; &nbsp; &nbsp;<#if wrong.supplier??>供应商:${wrong.supplier}</#if>
                                </#list>
                            </#if>
                        </#if>
                        </textArea>
                    </div>
                </div>

            </div>
        </div>
        <div class="row" style="margin-top: 1%">
            <div class="col-md-offset-1 col-md-7">
                <font color="red">注：<br> 1.本功能请谨慎使用，如果乱导入会使商品信息混乱！<br> 2.商品导入前必须完成对应商品的供应商信息，否则将作为错误数据处理(不导入，可在导入结果中查看错误数据)。<br>3.如果商品的二级分类无法匹配，可后期自行在商品管理中调整。
                <br>4.导入后的商品数据，本系统平台不提供修正服务，请谨慎使用。
                    <br>5.导入成功的数据，请勿重复导入！！！！！。
                </font>
            </div>
        </div>
    </@main.frame>
</#escape>
