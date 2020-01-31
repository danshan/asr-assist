package com.github.danshan.asrassist.xfyun.model;

/**
 * @author shanhonghao
 * @since
 */
public enum ErrorCode {
    OK(0, "成功"),
    ASR_INTERNAL_ERR(26000, "转写内部通用错误"),
    ASR_CONFIG_ERR(26100, "转写配置文件错误"),
    ASR_SECRET_ERR(26101, "转写配置文件app_id/secret_key为空"),
    ASR_HOST_ERR(26102, "转写配置文件lfasr_host错误"),
    ASR_FILE_PIECE_SIZE_ERR(26103, "转写配置文件file_piece_size错误"),
    ASR_FILE_PIECE_SIZE_EXCEED_ERR(26104, "转写配置文件file_piece_size建议设置10M-30M之间"),
    ASR_STORE_PATH_ERR(26105, "转写配置文件store_path错误，或目录不可读写"),
    ASR_UPLOAD_FILED_NOT_FOUND_ERR(26201, "转写参数上传文件不能为空或文件不存在"),
    ASR_PARAM_TYPE_ERR(26202, "转写参数类型不能为空"),
    ASR_SIGN_ERR(26203, "转写参数客户端生成签名错误"),
    ASR_BREAKPOINT_PERSISTENCE_ERR(26301, "转写断点续传持久化文件读写错误"),
    ASR_BREAKPOINT_FOLDER_ERR(26302,"转写断点续传文件夹读写错误"),
    ASR_BREAKPOINT_RESUME_ERR(26303,"转写恢复断点续传流程错误,请见日志"),
    ASR_UPLOADFILE_PATH_ERR(26401,"转写上传文件路径错误"),
    ASR_UPLOADFILE_TYPE_ERR(26402,"转写上传文件类型不支持错误"),
    ASR_UPLOADFILE_SIZE_ERR(26403,"转写本地文件上传超过限定大小500M"),
    ASR_UPLOADFILE_PERMISSION_ERR(26404,"转写上传文件读取错误"),
    ASR_API_ERR(26500,"HTTP请求失败"),
    ASR_API_VERSION_ERR(26501,"转写获取版本号接口错误"),
    ASR_API_PREPARE_ERR(26502,"转写预处理接口错误"),
    ASR_API_UPLOAD_ERR(26503,"转写上传文件接口错误"),
    ASR_API_MERGE_ERR(26504,"转写合并文件接口错误"),
    ASR_API_PROGRESS_ERR(26505,"转写获取进度接口错误"),
    ASR_API_RESULT_ERR(26506,"转写获取结果接口错误"),
    ASR_BIZ_ERR(26600,"转写业务通用错误"),
    ASR_ILLEGAL_APP_ERR(26601,"非法应用信息"),
    ASR_TASK_ID_NOT_EXISTS_ERR(26602,"任务ID不存在"),
    ASR_FREQUENCY_EXCEED_ERR(26603,"接口访问频率受限（默认1秒内不得超过20次）"),
    ASR_RESULT_EXCEED_ERR(26604,"获取结果次数超过限制，最多100次"),
    ASR_TASK_IN_PROGRESS_ERR(26605,"任务正在处理中，请稍后重试"),
    ASR_EMPTY_AUDIO_ERR(26606,"空音频，请检查"),
    ASR_PARAMS_ERR(26610,"请求参数错误"),
    ASR_PRE_FILE_SIZE_EXCEED_ERR(26621,"预处理文件大小受限（500M）"),
    ASR_PRE_FILE_LENGTH_EXCEED_ERR(26622,"预处理音频时长受限（5小时）"),
    ASR_PRE_FILE_FORMAT_ERR(26623,"预处理音频格式受限"),
    ASR_PRE_SERVICE_EXPIRES_ERR(26625,"预处理服务时长不足。您剩余的可用服务时长不足，请移步产品页http://www.xfyun.cn/services/lfasr 进行购买或者免费领取"),
    ASR_FILE_SIZE_EXCEED_ERR(26631,"音频文件大小受限（500M）"),
    ASR_FILE_LENGTH_EXCEED_ERR(26632,"音频时长受限（5小时）"),
    ASR_SERVICE_EXPIRES_ERR(26633,"音频服务时长不足。您剩余的可用服务时长不足，请移步产品页http://www.xfyun.cn/services/lfasr 进行购买或者免费领"),
    ASR_FILE_DOWNLOAD_ERR(26634,"文件下载失败"),
    ASR_FILE_LENGTH_VERIFY_ERR(26635,"文件长度校验失败"),
    ASR_FILE_UPLOAD_ERR(26640,"文件上传失败"),
    ASR_FILE_SLICE_EXCEED_ERR(26641,"上传分片超过限制"),
    ASR_FILE_SLICE_MERGE_ERR(26642,"分片合并失败"),
    ASR_FILE_LENGTH_CALC_ERR(26643,"计算音频时长失败,请检查您的音频是否加密或者损坏"),
    ASR_FILE_CONVERT_ERR(26650,"音频格式转换失败,请检查您的音频是否加密或者损坏"),
    ASR_BILLING_ERR(26660,"计费计量失败"),
    ASR_RESULT_PARSE_ERR(26670,"转写结果集解析失败"),
    ASR_ENGINE_ERR(26680,"引擎处理阶段错误"),
    ;

    public final int code;
    public final String error;

    ErrorCode(int code, String error) {
        this.code = code;
        this.error = error;
    }

}
