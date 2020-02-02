package com.github.danshan.asrassist.xfyun.service;

import com.github.danshan.asrassist.xfyun.event.EventType;
import com.github.danshan.asrassist.xfyun.event.UploadEvent;
import com.github.danshan.asrassist.xfyun.event.UploadEventHandler;
import com.github.danshan.asrassist.xfyun.exception.LfasrException;
import com.github.danshan.asrassist.xfyun.exception.SliceException;
import com.github.danshan.asrassist.xfyun.file.ChannelFileReader;
import com.github.danshan.asrassist.xfyun.http.dto.UploadReq;
import com.github.danshan.asrassist.xfyun.model.ErrorCode;
import com.github.danshan.asrassist.xfyun.model.ErrorMsg;
import com.github.danshan.asrassist.xfyun.util.DictUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;

/**
 * @author shanhonghao
 * @since 1.0.0
 */
@Slf4j
@Service
public class XfyunSliceServiceImpl implements XfyunSliceService {

    @Override
    public UploadEventHandler sliceFile(UploadReq uploadReq, ChannelFileReader reader, Set<String> finishedSliceIds) throws IOException {
        if (reader == null) {
            throw new SliceException(ErrorMsg.failed(ErrorCode.ASR_UPLOADFILE_PERMISSION_ERR));
        }

        DictUtil aTOz = new DictUtil("aaaaaaaaa`");
        UploadEventHandler eventHandler = new UploadEventHandler();
        try {
            while (reader.read() >= 0) {
                String sliceId = aTOz.getNextString();
                if (finishedSliceIds.contains(sliceId)) {
                    continue;
                } else {
                    eventHandler.modifySliceStatus(sliceId, false);
                }


                UploadReq req = buildReqForSlice(uploadReq, sliceId)
                    .orElseThrow(() -> new LfasrException(ErrorMsg.failed(ErrorCode.ASR_FILE_UPLOAD_ERR)));

                UploadEvent uploadEvent = new UploadEvent(EventType.DATA_CONTENT, req);
                uploadEvent.setContent(reader.getArray());

                eventHandler.addEvent(uploadEvent);
            }
        } finally {
            try {
                reader.close();
            } catch (IOException ex) {
                log.warn(ex.getMessage(), ex);
            }
        }
        return eventHandler;
    }

    @Override
    public void setFileEnd(UploadEventHandler eventHandler, UploadReq uploadReq) {
        UploadEvent event = new UploadEvent(EventType.DATA_END, uploadReq);
        eventHandler.addEvent(event);
    }

    private Optional<UploadReq> buildReqForSlice(UploadReq origin, String sliceId) {
        try {
            UploadReq req = origin.clone();
            req.setSliceId(sliceId);
            return Optional.of(req);
        } catch (CloneNotSupportedException e) {
            log.error(e.getMessage());
            return Optional.empty();
        }
    }

}
