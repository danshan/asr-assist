package com.github.danshan.asrassist.xfyun.service

import com.github.danshan.asrassist.xfyun.http.XfyunRepo
import com.github.danshan.asrassist.xfyun.model.Progress
import com.github.danshan.asrassist.xfyun.model.Signature
import spock.lang.Specification

/**
 * @author shanhonghao
 */
class XfyunAsrServiceImplTest extends Specification {

    XfyunAsrService xfyunAsrService

    def xfyunSignatureService = Mock(XfyunSignatureService)
    def xfyunRepo = Mock(XfyunRepo)

    def setup() {
        xfyunAsrService = new XfyunAsrServiceImpl(
                xfyunSignatureService: xfyunSignatureService,
                xfyunRepo: xfyunRepo
        )
    }

    def "get progress success"() {
        given:
        def taskId = UUID.randomUUID().toString()
        xfyunSignatureService.generateSignature() >> mockSignature()
        xfyunRepo.getProgress(_) >> Optional.of(new Progress(status: 0, desc: "ok"))

        when:
        def result = xfyunAsrService.getProgress(taskId)

        then:
        result.get().status == 0
        result.get().desc == "ok"
    }

    def mockSignature() {
        return new Signature("appId", "secretKey")
    }
}
