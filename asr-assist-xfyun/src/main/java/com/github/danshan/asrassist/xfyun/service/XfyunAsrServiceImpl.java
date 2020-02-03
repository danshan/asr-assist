package com.github.danshan.asrassist.xfyun.service;

import com.github.danshan.asrassist.xfyun.config.XfyunAsrProperties;
import com.github.danshan.asrassist.xfyun.config.XfyunConstants;
import com.github.danshan.asrassist.xfyun.exception.LfasrException;
import com.github.danshan.asrassist.xfyun.file.ChannelFileReader;
import com.github.danshan.asrassist.xfyun.http.XfyunRepo;
import com.github.danshan.asrassist.xfyun.http.dto.PrepareReq;
import com.github.danshan.asrassist.xfyun.http.dto.ProgressReq;
import com.github.danshan.asrassist.xfyun.http.dto.ResultReq;
import com.github.danshan.asrassist.xfyun.http.dto.UploadReq;
import com.github.danshan.asrassist.xfyun.model.*;
import com.google.common.base.Preconditions;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class XfyunAsrServiceImpl implements XfyunAsrService {

    @Resource
    XfyunAsrProperties xfyunAsrProperties;
    @Resource
    XfyunSignatureService xfyunSignatureService;
    @Resource
    XfyunSliceService xfyunSliceService;
    @Resource
    XfyunRepo xfyunRepo;

    @Override
    public Optional<String> upload(File file) throws LfasrException, IOException {
        validateFile(file);

        log.info("file=[{}], preparing", file.getName());
        String taskId = this.prepareUpload(file)
            .orElseThrow(() -> new LfasrException(ErrorMsg.failed(ErrorCode.ASR_API_PREPARE_ERR)));
        log.info("taskId=[{}], file=[{}], prepare ok", taskId, file.getName());

        UploadReq uploadReq = buildUploadReq(taskId);

        ChannelFileReader reader = new ChannelFileReader(file, this.xfyunAsrProperties.getFilePieceSize());

        XfyunUploadThread uploadThread = new XfyunUploadThread(this.xfyunSliceService, uploadReq, reader);
        Thread thread = new Thread(uploadThread);
        thread.start();
        return Optional.of(taskId);
    }

    private void validateFile(File file) {
        Preconditions.checkArgument(file != null, "file should not be null");
        Preconditions.checkArgument(file.exists(), "file [%s] not exists", file.getAbsoluteFile());
        Preconditions.checkArgument(file.isFile(), "file [%s] is not a legal file", file.getAbsoluteFile());
        Preconditions.checkArgument(file.length() > 0, "file [%s] size is 0", file.getAbsoluteFile());
        Preconditions.checkArgument(file.length() < XfyunConstants.FILE_UPLOAD_MAXSIZE, "file [%s] size is too large, should be less than %s",
            file.getAbsoluteFile(), XfyunConstants.FILE_UPLOAD_MAXSIZE);

        Preconditions.checkArgument(XfyunConstants.LFASR_TYPE.isSupportedAudios(file), "file type not supported");
    }

    private UploadReq buildUploadReq(String taskId) {
        UploadReq uploadReq = new UploadReq(xfyunSignatureService.generateSignature());
        uploadReq.setTaskId(taskId);
        return uploadReq;
    }

    private Optional<String> prepareUpload(File file) {
        Signature signature = xfyunSignatureService.generateSignature();
        PrepareReq prepareReq = new PrepareReq(signature);
        prepareReq.setFileLength(file.length());
        prepareReq.setFileName(file.getName());
        prepareReq.setSliceCount(calcSliceCount(file));

        return xfyunRepo.prepare(prepareReq);
    }

    private int calcSliceCount(File file) {
        long fileLen = file.length();
        int filePieceSize = xfyunAsrProperties.getFilePieceSize();
        int flag = (int) fileLen % filePieceSize;
        int sliceCount = flag == 0 ? (int) fileLen / filePieceSize : (int) fileLen / filePieceSize + 1;
        log.debug("file=[{}] split to [{}] slices", file.getName(), sliceCount);
        return sliceCount;
    }


    @Override
    public Optional<Progress> getProgress(String taskId) throws LfasrException {
        ProgressReq req = new ProgressReq(xfyunSignatureService.generateSignature());
        req.setTaskId(taskId);
        return xfyunRepo.getProgress(req);

    }

    @Override
    public Optional<List<Result>> getResult(String taskId) throws LfasrException {
        ResultReq req = new ResultReq(xfyunSignatureService.generateSignature());
        req.setTaskId(taskId);
        return xfyunRepo.getResult(req);
    }

}
