package com.github.danshan.asrassist.xfyun.event;

import com.github.danshan.asrassist.xfyun.config.XfyunConstants;
import com.github.danshan.asrassist.xfyun.config.XfyunContextHolder;
import com.github.danshan.asrassist.xfyun.exception.LfasrException;
import com.github.danshan.asrassist.xfyun.http.XfyunRepo;
import com.github.danshan.asrassist.xfyun.http.dto.MergeReq;
import com.github.danshan.asrassist.xfyun.model.ErrorCode;
import com.github.danshan.asrassist.xfyun.model.ErrorMsg;
import com.github.danshan.asrassist.xfyun.service.XfyunSignatureService;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.*;

/**
 * @author shanhonghao
 * @since 1.0.0
 */
@Slf4j
public class UploadEventHandler {

    private ExecutorService executor;

    private final BlockingQueue<UploadEvent> eventQueue;
    private CountDownLatch latch = new CountDownLatch(1);
    private static final int threadCount = 10;

    private HashMap<String, Boolean> sliceStatusMap = new HashMap();

    public UploadEventHandler() {
        this.eventQueue = new ArrayBlockingQueue<>(XfyunConstants.BLOCKINGQUEUE_MAXSIZE);
        this.executor = Executors.newFixedThreadPool(threadCount);

        this.start();
    }

    private void start() {
        for (int i = 0; i < threadCount; i++) {
            this.executor.execute(new UploadEventHandler.ProcessorThread());
        }
    }

    public void addEvent(UploadEvent event) {
        try {
            this.eventQueue.put(event);
        } catch (Exception error) {
            log.warn(error.getMessage());
        }
    }

    public void modifySliceStatus(String sliceId, boolean success) {
        this.sliceStatusMap.put(sliceId, success);
    }

    public boolean isSendAll() throws LfasrException {
        if (this.executor.isShutdown()) {
            throw new LfasrException(ErrorMsg.failed(ErrorCode.ASR_FILE_UPLOAD_ERR));
        } else {
            boolean isSend = true;
            if (this.sliceStatusMap == null) {
                return isSend;
            } else {
                int sendNum = 0;
                for (Boolean status : this.sliceStatusMap.values()) {
                    if (status) {
                        sendNum++;
                    } else {
                        isSend = false;
                    }
                }
                log.debug("upload file [{}/{}]", sendNum, this.sliceStatusMap.size());
                return isSend;
            }
        }
    }

    public void await() throws InterruptedException {
        this.latch.await();
    }

    public void shutdownNow() {
        this.executor.shutdownNow();
    }

    private void retryEvent(UploadEvent event) {
        long activeTimeMillis = System.currentTimeMillis();
        int retryTimes = event.getRetryTimes();
        String taskId = event.getUploadReq().getTaskId();
        String sliceId = event.getUploadReq().getSliceId();
        String operation = event.getType().value == 0 ? "upload" : "merge";
        if (retryTimes <= 2) {
            activeTimeMillis += 5000L;
            log.warn("taskId=[{}], sliceId=[{}] [{}] fail, freezing a while to [{}]",
                taskId, sliceId, operation, new Date(activeTimeMillis)
            );
        } else if (retryTimes <= 4) {
            activeTimeMillis += 300000L;
            log.warn("taskId=[{}], sliceId=[{}] [{}] fail, freezing a long timeMillis to [{}]",
                taskId, sliceId, operation, new Date(activeTimeMillis));
        } else {
            log.warn("taskId=[{}], sliceId=[{}] [{}] fail, retry times reach [{}], task fail",
                taskId, sliceId, operation, retryTimes);
            this.latch.countDown();
            this.executor.shutdownNow();
            return;
        }

        event.setActiveTimeMillis(activeTimeMillis);
        event.addRetryTimes();
        log.info("taskId=[{}], sliceId=[{}], retry [{}] times", event.getUploadReq().getTaskId(), event.getUploadReq().getSliceId(), event.getRetryTimes());
        try {
            this.eventQueue.put(event);
        } catch (Exception e) {
            log.error("taskId=[{}], sliceId=[{}] [{}] fail, retry Interrupted",
                taskId, sliceId, operation);
        }
    }

    public class ProcessorThread implements Runnable {

        @Override
        public void run() {
            while (true) {
                UploadEvent event;
                try {
                    event = eventQueue.take();
                } catch (Exception ex) {
                    return;
                }

                if (!event.canActive()) {
                    eventQueue.add(event);
                    continue;
                }

                if (event.getType() == EventType.DATA_CONTENT) {
                    try {
                        uploadSlice(event);
                    } catch (Exception e) {
                        retryEvent(event);
                        continue;
                    }

                    modifySliceStatus(event.getUploadReq().getSliceId(), true);
                    log.debug("taskId=[{}], sliceId=[{}], upload slice send success",
                        event.getUploadReq().getTaskId(), event.getUploadReq().getSliceId());
                } else {
                    try {
                        mergeFile(event);
                        log.info("taskId=[{}], merge success", event.getUploadReq().getTaskId());
                        executor.shutdownNow();
                        latch.countDown();
                        return;
                    } catch (Exception e) {
                        retryEvent(event);
                        continue;
                    }
                }
            }
        }

        private void uploadSlice(UploadEvent event) {
            XfyunRepo xfyunRepo = XfyunContextHolder.getBean(XfyunRepo.class);
            xfyunRepo.upload(event.getUploadReq(), event.getContent(), event.getUploadReq().getSliceId());
        }

        private void mergeFile(UploadEvent event) {
            try {
                Thread.sleep(1000L);
            } catch (Exception e) {
                // do nothing
            }

            XfyunSignatureService xfyunSignatureService = XfyunContextHolder.getBean(XfyunSignatureService.class);
            MergeReq mergeReq = new MergeReq(xfyunSignatureService.generateSignature());
            mergeReq.setTaskId(event.getUploadReq().getTaskId());

            XfyunRepo xfyunRepo = XfyunContextHolder.getBean(XfyunRepo.class);
            xfyunRepo.merge(mergeReq);
        }
    }

}
