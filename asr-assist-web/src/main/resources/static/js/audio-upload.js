projectfileoptions = {
    theme: "fas",
    allowedPreviewTypes: ['audio'],
    allowedFileExtensions: ['wav', 'flac', 'opus', 'mp3', 'm4a'],
    maxFileSize: 500000,
    browseClass: "btn btn-primary", //按钮样式
    enctype: 'multipart/form-data',
    validateInitialCount: true,
    msgFilesTooMany: "选择上传的文件数量({n}) 超过允许的最大数值{m}!",
};

$(function () {
    // 初始化fileinput
    var fileinput = new FileInput();
    fileinput.init("uploadfile", "/api/assets");
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
        control.on('fileuploaded', function (event, previewId, index, fileId) {
            console.log('File Uploaded', 'ID: ' + fileId + ', Thumb ID: ' + previewId);
        }).on('fileuploaderror', function (event, data, msg) {
            console.log('File Upload Error', 'ID: ' + data.fileId + ', Thumb ID: ' + data.previewId);
        }).on('filebatchuploadcomplete', function (event, preview, config, tags, extraData) {
            console.log('File Batch Uploaded', preview, config, tags, extraData);
        });
    };
    return file;
};
