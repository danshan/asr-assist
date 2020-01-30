projectfileoptions = {
    theme: "fas",
    // showUpload: true,
    // showRemove: true,
    // showCaption: true,
    allowedPreviewTypes: ['audio'],
    allowedFileExtensions: ['wav', 'flac', 'opus', 'mp3', 'm4a'],
    maxFileSize: 500000,
    browseClass: "btn btn-primary", //按钮样式
    dropZoneEnabled: true,//是否显示拖拽区域
    //minImageWidth: 50, //图片的最小宽度
    //minImageHeight: 50,//图片的最小高度
    //maxImageWidth: 1000,//图片的最大宽度
    //maxImageHeight: 1000,//图片的最大高度
    maxFileCount: 10, //表示允许同时上传的最大文件个数
    enctype: 'multipart/form-data',
    validateInitialCount: true,
    // previewFileIcon: "<i class='glyphicon glyphicon-king'></i>",
    msgFilesTooMany: "选择上传的文件数量({n}) 超过允许的最大数值{m}!",
};

$(function () {
    // 初始化fileinput
    var oFileInput = new FileInput();
    oFileInput.init("uploadfile", "/api/OrderApi/ImportOrder");
});

//初始化fileinput
var FileInput = function () {
    var file = new Object();

    //初始化fileinput控件（第一次初始化）
    file.init = function (ctrlName, uploadUrl) {
        var control = $('#' + ctrlName);

        var options = $.extend({
            uploadUrl: uploadUrl
        }, projectfileoptions);

        control.fileinput(options);

        //导入文件上传完成之后的事件
        $("#txt_file").on("fileuploaded", function (event, data, previewId, index) {
            $("#myModal").modal("hide");
            var data = data.response.lstOrderImport;
            if (data == undefined) {
                toastr.error('文件格式类型不正确');
                return;
            }
            //1.初始化表格
            var oTable = new TableInit();
            oTable.Init(data);
            $("#div_startimport").show();
        });
    }
    return file;
};
