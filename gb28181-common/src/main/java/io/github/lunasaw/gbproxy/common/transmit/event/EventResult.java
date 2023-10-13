package io.github.lunasaw.gbproxy.common.transmit.event;

import gov.nist.javax.sip.message.SIPRequest;
import io.github.lunasaw.gbproxy.common.transmit.event.result.DeviceNotFoundEvent;

import javax.sip.DialogTerminatedEvent;
import javax.sip.ResponseEvent;
import javax.sip.TimeoutEvent;
import javax.sip.TransactionTerminatedEvent;
import javax.sip.header.CallIdHeader;
import javax.sip.message.Response;

/**
 * 事件结果
 * @author luna
 */
public class EventResult<EventObject> {
    public int                          statusCode;
    public EventResultType type;
    public String                       msg;
    public String                       callId;
    public EventObject                  event;

    public EventResult() {}

    public EventResult(EventObject event) {
        this.event = event;
        if (event instanceof ResponseEvent) {
            ResponseEvent responseEvent = (ResponseEvent)event;
            Response response = responseEvent.getResponse();
            this.type = EventResultType.response;
            if (response != null) {
                this.msg = response.getReasonPhrase();
                this.statusCode = response.getStatusCode();
            }
            this.callId = ((CallIdHeader)response.getHeader(CallIdHeader.NAME)).getCallId();

        } else if (event instanceof TimeoutEvent) {
            TimeoutEvent timeoutEvent = (TimeoutEvent)event;
            this.type = EventResultType.timeout;
            this.msg = "消息超时未回复";
            this.statusCode = -1024;
            if (timeoutEvent.isServerTransaction()) {
                this.callId = ((SIPRequest)timeoutEvent.getServerTransaction().getRequest()).getCallIdHeader().getCallId();
            } else {
                this.callId = ((SIPRequest)timeoutEvent.getClientTransaction().getRequest()).getCallIdHeader().getCallId();
            }
        } else if (event instanceof TransactionTerminatedEvent) {
            TransactionTerminatedEvent transactionTerminatedEvent = (TransactionTerminatedEvent)event;
            this.type = EventResultType.transactionTerminated;
            this.msg = "事务已结束";
            this.statusCode = -1024;
            if (transactionTerminatedEvent.isServerTransaction()) {
                this.callId = ((SIPRequest)transactionTerminatedEvent.getServerTransaction().getRequest()).getCallIdHeader().getCallId();
            } else {
                this.callId = ((SIPRequest)transactionTerminatedEvent.getClientTransaction().getRequest()).getCallIdHeader().getCallId();
            }
        } else if (event instanceof DialogTerminatedEvent) {
            DialogTerminatedEvent dialogTerminatedEvent = (DialogTerminatedEvent)event;
            this.type = EventResultType.dialogTerminated;
            this.msg = "会话已结束";
            this.statusCode = -1024;
            this.callId = dialogTerminatedEvent.getDialog().getCallId().getCallId();
        } else if (event instanceof DeviceNotFoundEvent) {
            this.type = EventResultType.deviceNotFoundEvent;
            this.msg = "设备未找到";
            this.statusCode = -1024;
            this.callId = ((DeviceNotFoundEvent)event).getCallId();
        }
    }
}