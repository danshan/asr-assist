package com.github.danshan.asrassist.xfyun.event;

import com.github.danshan.asrassist.xfyun.config.XfyunAsrProperties;
import com.github.danshan.asrassist.xfyun.exception.LfasrException;
import com.github.danshan.asrassist.xfyun.file.LocalPersistenceFile;
import com.github.danshan.asrassist.xfyun.model.ErrorCode;
import com.github.danshan.asrassist.xfyun.model.Message;
import com.github.danshan.asrassist.xfyun.model.UploadParams;
import com.github.danshan.asrassist.xfyun.worker.HttpWorker;
import com.iflytek.msp.cpdb.lfasr.client.LfasrClientImp;
import com.iflytek.msp.cpdb.lfasr.model.EventType;
import lombok.extern.slf4j.Slf4j;
import org.apache.log4j.Logger;

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
    private static final Logger LOGGER = Logger.getLogger(EventHandler.class);

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
        for(int i = 0; i < this.threadNum; ++i) {
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
            switch(retryTimes) {
            case 0:
            case 1:
            case 2:
                activeTimeMillis += 5000L;
                LOGGER.warn(String.format("[COMPENT]-%s [PROCESS]-%s [ID]-%s [STATUS]-%s [MEASURE]-%s [DEF]-%s", "CLIENT", "Event retry", event.getParams().getTaskId(), "", "(-1) ms", (event.getType().getValue() == 0 ? "slice_id:" + event.getFileSlice().getSliceId() + " upload" : "merge") + " fail, freezing a while to " + new Date(activeTimeMillis)));
                break;
            case 3:
            case 4:
                activeTimeMillis += 300000L;
                LOGGER.warn(String.format("[COMPENT]-%s [PROCESS]-%s [ID]-%s [STATUS]-%s [MEASURE]-%s [DEF]-%s", "CLIENT", "Event retry", event.getParams().getTaskId(), "", "(-1) ms", (event.getType().getValue() == 0 ? "slice_id:" + event.getFileSlice().getSliceId() + " upload" : "merge") + " fail, freezing a long timeMillis to " + new Date(activeTimeMillis)));
                break;
            default:
                LOGGER.error(String.format("[COMPENT]-%s [PROCESS]-%s [ID]-%s [STATUS]-%s [MEASURE]-%s [DEF]-%s", "CLIENT", "Event retry", event.getParams().getTaskId(), "", "(-1) ms", (event.getType().getValue() == 0 ? "slice_id:" + event.getFileSlice().getSliceId() + " upload" : "merge") + " fail, retry times reach " + retryTimes + ", task fail"));
                this.latch.countDown();
                this.exec.shutdownNow();
                return;
            }

            LOGGER.error(String.format("[COMPENT]-%s [PROCESS]-%s [ID]-%s [STATUS]-%s [MEASURE]-%s [DEF]-%s", "CLIENT", "Event retry", event.getParams().getTaskId(), "", "(-1) ms", (event.getType().getValue() == 0 ? "slice_id:" + event.getFileSlice().getSliceId() + " upload" : "merge") + " fail, freeze event...and try again....current retry times " + retryTimes));
            event.setActiveTimeMillis(activeTimeMillis);
            event.addRetryTimes();
            this.queue.put(event);
        } catch (InterruptedException var5) {
            LOGGER.error(String.format("[COMPENT]-%s [PROCESS]-%s [ID]-%s [STATUS]-%s [MEASURE]-%s [DEF]-%s", "CLIENT", "Event retry", event.getParams().getTaskId(), "", "(-1) ms", (event.getType().getValue() == 0 ? "slice_id:" + event.getFileSlice().getSliceId() + " upload" : "merge") + " retry Interrupted"));
        }

    }

    private void retryEvent(Event event, Message message) {
        try {
            if (message.getErrNo() == 26603) {
                LOGGER.warn(String.format("[COMPENT]-%s [PROCESS]-%s [ID]-%s [STATUS]-%s [MEASURE]-%s [DEF]-%s", "CLIENT", "Event retry", event.getParams().getTaskId(), "", "(-1) ms", (event.getType().getValue() == 0 ? "slice_id:" + event.getFileSlice().getSliceId() + " upload" : "merge") + " fail, access frequency over limit, sleep for a while and retry"));
                Thread.sleep(10000L);
                this.queue.put(event);
            } else {
                this.retryEvent(event);
            }
        } catch (Exception var4) {
            var4.printStackTrace();
        }

    }

    public void modifySliceHM(String slice_id, boolean bool) {
        this.slice_hm.put(slice_id, bool);
    }

    public boolean isSendAll() throws LfasrException {
        if (this.exec.isShutdown()) {
            throw new LfasrException(Message.failed(ErrorCode.ASR_FILE_UPLOAD_ERR, null));
        } else {
            boolean isSend = true;
            if (this.slice_hm == null) {
                return isSend;
            } else {
                int sendNum = 0;
                Iterator iter = this.slice_hm.entrySet().iterator();

                while(iter.hasNext()) {
                    Entry entry = (Entry)iter.next();
                    if (entry.getValue().toString().equalsIgnoreCase("false")) {
                        isSend = false;
                    } else {
                        ++sendNum;
                    }
                }

                LOGGER.debug(String.format("[COMPENT]-%s [PROCESS]-%s [ID]-%s [STATUS]-%s [MEASURE]-%s [DEF]-%s", "CLIENT", "EventHandler", "", "", "(-1) ms", "upload file " + sendNum + "/" + this.slice_hm.size()));
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
            while(true) {
                Event event;
                try {
                    event = EventHandler.this.queue.take();
                } catch (InterruptedException var12) {
                    return;
                }

                if (!event.canActive()) {
                    EventHandler.this.queue.add(event);
                } else {
                    if (event.getType().getValue() == EventType.LFASR_FILE_DATA_CONTENT.getValue()) {
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
                                    LocalPersistenceFile.writeNIO(LfasrClientImp.SERV_STORE_PATH_VAL + "/" + EventHandler.this.upParams.getTaskId() + ".dat", slice_id);
                                    EventHandler.this.modifySliceHM(slice_id, true);
                                } catch (LfasrException var8) {
                                    EventHandler.LOGGER.error(String.format("[COMPENT]-%s [PROCESS]-%s [ID]-%s [STATUS]-%s [MEASURE]-%s [DEF]-%s", "CLIENT", "EventHandler", event.getParams().getTaskId(), "", "(-1) ms", "write meta info to " + LfasrClientImp.SERV_STORE_PATH_VAL + "/" + EventHandler.this.upParams.getTaskId() + ".dat error"), var8);
                                }

                                EventHandler.LOGGER.debug(String.format("[COMPENT]-%s [PROCESS]-%s [ID]-%s [STATUS]-%s [MEASURE]-%s [DEF]-%s", "CLIENT", "EventHandler", event.getParams().getTaskId(), "", "(-1) ms", "upload slice send success.file:" + EventHandler.this.upParams.getFile().getAbsolutePath() + ", slice_id:" + slice_id));
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
                                LocalPersistenceFile.deleteFile(new File(LfasrClientImp.SERV_STORE_PATH_VAL + "/" + EventHandler.this.upParams.getTaskId() + ".dat"));
                                EventHandler.LOGGER.info(String.format("[COMPENT]-%s [PROCESS]-%s [ID]-%s [STATUS]-%s [MEASURE]-%s [DEF]-%s", "CLIENT", "EventHandler", event.getParams().getTaskId(), "", "(-1) ms", "merge success"));
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
