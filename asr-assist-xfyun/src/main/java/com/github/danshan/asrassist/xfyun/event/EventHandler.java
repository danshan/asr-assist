package com.github.danshan.asrassist.xfyun.event;

import com.github.danshan.asrassist.xfyun.config.XfyunAsrProperties;
import com.github.danshan.asrassist.xfyun.exception.LfasrException;
import com.github.danshan.asrassist.xfyun.file.LocalPersistenceFile;
import com.github.danshan.asrassist.xfyun.model.ErrorCode;
import com.github.danshan.asrassist.xfyun.model.EventType;
import com.github.danshan.asrassist.xfyun.model.Message;
import com.github.danshan.asrassist.xfyun.model.UploadParams;
import com.github.danshan.asrassist.xfyun.worker.HttpWorker;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class EventHandler {

    private final XfyunAsrProperties xfyunAsrProperties;
    private final UploadParams upParams;

    private EventQueue<Event> queue;
    private HashMap<String, Boolean> slice_hm = new HashMap();
    private CountDownLatch latch = new CountDownLatch(1);
    private ExecutorService exec;
    private int threadNum = 10;

    public EventHandler(XfyunAsrProperties xfyunAsrProperties, UploadParams upParams) {
        this.xfyunAsrProperties = xfyunAsrProperties;
        this.upParams = upParams;

        this.queue = new EventQueue();
        this.exec = Executors.newFixedThreadPool(this.threadNum);
        this.start();
    }

    public void start() {
        for (int i = 0; i < this.threadNum; ++i) {
            this.exec.execute(new EventHandler.ProcessorThread());
        }
    }

    public void addEvent(Event event) {
        try {
            this.queue.put(event);
        } catch (InterruptedException error) {
            log.debug(error.getMessage());
        }

    }

    private void retryEvent(Event event) {
        try {
            long activeTimeMillis = System.currentTimeMillis();
            int retryTimes = event.getRetryTimes();
            switch (retryTimes) {
                case 0:
                case 1:
                case 2:
                    activeTimeMillis += 5000L;
                    log.warn("[{}] fail, freezing a while to [{}]",
                        (event.getType().value == 0 ? "slice_id:" + event.getFileSlice().getSliceId() + " upload" : "merge"),
                        new Date(activeTimeMillis));
                    break;
                case 3:
                case 4:
                    activeTimeMillis += 300000L;
                    log.warn("[{}] fail, freezing a long timeMillis to [{}]",
                        (event.getType().value == 0 ? "slice_id:" + event.getFileSlice().getSliceId() + " upload" : "merge"),
                        new Date(activeTimeMillis));
                    break;
                default:
                    log.warn("[{}] fail, retry times reach [{}], task fail",
                        (event.getType().value == 0 ? "slice_id:" + event.getFileSlice().getSliceId() + " upload" : "merge"),
                        retryTimes);
                    this.latch.countDown();
                    this.exec.shutdownNow();
                    return;
            }

            log.warn("[{}] fail, freeze event...and try again....current retry times {}",
                (event.getType().value == 0 ? "slice_id:" + event.getFileSlice().getSliceId() + " upload" : "merge"),
                retryTimes);
            event.setActiveTimeMillis(activeTimeMillis);
            event.addRetryTimes();
            this.queue.put(event);
        } catch (InterruptedException ex) {
            log.error("[{}] fail, retry Interrupted",
                (event.getType().value == 0 ? "slice_id:" + event.getFileSlice().getSliceId() + " upload" : "merge"));
        }

    }

    private void retryEvent(Event event, Message message) {
        try {
            if (message.getErrNo() == ErrorCode.ASR_FREQUENCY_EXCEED_ERR.code) {
                log.warn("[{}]  fail, access frequency over limit, sleep for a while and retry",
                    (event.getType().value == 0 ? "slice_id:" + event.getFileSlice().getSliceId() + " upload" : "merge"));
                Thread.sleep(10000L);
                this.queue.put(event);
            } else {
                this.retryEvent(event);
            }
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }

    }

    public void modifySliceHM(String slice_id, boolean bool) {
        this.slice_hm.put(slice_id, bool);
    }

    public boolean isSendAll() throws LfasrException, InterruptedException {
        if (this.exec.isShutdown()) {
            throw new LfasrException(Message.failed(ErrorCode.ASR_FILE_UPLOAD_ERR, null));
        } else {
            boolean isSend = true;
            if (this.slice_hm == null) {
                return isSend;
            } else {
                int sendNum = 0;
                Iterator iter = this.slice_hm.entrySet().iterator();

                while (iter.hasNext()) {
                    Entry entry = (Entry) iter.next();
                    if (entry.getValue().toString().equalsIgnoreCase("false")) {
                        isSend = false;
                    } else {
                        ++sendNum;
                    }
                }

                log.debug("upload file [{}/{}]", sendNum, this.slice_hm.size());
                return isSend;
            }
        }
    }

    public void await() throws InterruptedException {
        this.latch.await();
    }

    public void shutdownNow() {
        this.exec.shutdownNow();
    }

    public class ProcessorThread implements Runnable {
        public ProcessorThread() {
        }

        public void run() {
            while (true) {
                Event event;
                try {
                    event = EventHandler.this.queue.take();
                } catch (InterruptedException var12) {
                    return;
                }

                if (!event.canActive()) {
                    EventHandler.this.queue.add(event);
                } else {
                    if (event.getType() == EventType.LFASR_FILE_DATA_CONTENT) {
                        String slice_id = event.getFileSlice().getSliceId();
                        Message responseObj = null;

                        try {
                            responseObj = new HttpWorker(xfyunAsrProperties).handle(event);
                        } catch (Exception var9) {
                            EventHandler.this.retryEvent(event);
                            continue;
                        }

                        if (responseObj == null) {
                            EventHandler.this.retryEvent(event);
                        } else {
                            int codex = responseObj.getOk();
                            if (codex == 0) {
                                try {
                                    LocalPersistenceFile.writeNIO(EventHandler.this.xfyunAsrProperties.getStorePath() + "/" + EventHandler.this.upParams.getTaskId() + ".dat", slice_id);
                                    EventHandler.this.modifySliceHM(slice_id, true);
                                } catch (LfasrException ex) {
                                    log.warn(String.format("write meta info to [%s/%s.dat] error", EventHandler.this.xfyunAsrProperties.getStorePath(), EventHandler.this.upParams.getTaskId()), ex);
                                }
                                log.debug("upload slice send success. file=[{}], sliceId=[{}]", EventHandler.this.upParams.getFile().getAbsolutePath(), slice_id);
                            } else {
                                EventHandler.this.retryEvent(event, responseObj);
                            }
                        }
                    } else {
                        try {
                            Thread.sleep(1000L);
                        } catch (InterruptedException var11) {
                        }

                        Message responseObjx = null;
                        try {
                            responseObjx = new HttpWorker(xfyunAsrProperties).merge(event);
                        } catch (Exception var10) {
                            EventHandler.this.retryEvent(event);
                            continue;
                        }

                        if (responseObjx == null) {
                            EventHandler.this.retryEvent(event);
                        } else {
                            int code = responseObjx.getOk();
                            if (code == 0) {
                                LocalPersistenceFile.deleteFile(new File(EventHandler.this.xfyunAsrProperties.getStorePath() + "/" + EventHandler.this.upParams.getTaskId() + ".dat"));
                                log.info("taskId=[{}], merge success", event.getParams().getTaskId());
                                EventHandler.this.exec.shutdownNow();
                                EventHandler.this.latch.countDown();
                                return;
                            }

                            EventHandler.this.retryEvent(event, responseObjx);
                        }
                    }
                }
            }
        }
    }
}
