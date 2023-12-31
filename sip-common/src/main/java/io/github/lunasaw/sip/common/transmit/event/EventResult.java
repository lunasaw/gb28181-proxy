package io.github.lunasaw.sip.common.transmit.event;

import javax.sip.*;
import javax.sip.header.CallIdHeader;
import javax.sip.message.Response;

import gov.nist.javax.sip.message.SIPRequest;
import io.github.lunasaw.sip.common.transmit.event.result.DeviceNotFoundEvent;
import lombok.Data;

/**
 * 事件结果
 * 
 * @author luna
 */
@Data
public class EventResult<T> {
    public int             statusCode;
    public EventResultType type;
    public String          msg;
    public String          callId;
    public Dialog dialog;
    public T               event;

    public EventResult() {}

    public EventResult(T event) {
        this.event = event;
        if (event instanceof ResponseEvent) {
            ResponseEvent responseEvent = (ResponseEvent)event;
            Response response = responseEvent.getResponse();
            this.type = EventResultType.response;
            if (response != null) {
                this.msg = response.getReasonPhrase();
                this.statusCode = response.getStatusCode();
            }
            assert response != null;
            this.callId = ((CallIdHeader)response.getHeader(CallIdHeader.NAME)).getCallId();
        } else if (event instanceof TimeoutEvent) {
            TimeoutEvent timeoutEvent = (TimeoutEvent)event;
            this.type = EventResultType.timeout;
            this.msg = "消息超时未回复";
            this.statusCode = -1024;
            if (timeoutEvent.isServerTransaction()) {
                this.callId = ((SIPRequest)timeoutEvent.getServerTransaction().getRequest()).getCallIdHeader().getCallId();
                this.dialog = timeoutEvent.getServerTransaction().getDialog();
            } else {
                this.callId = ((SIPRequest)timeoutEvent.getClientTransaction().getRequest()).getCallIdHeader().getCallId();
                this.dialog = timeoutEvent.getClientTransaction().getDialog();
            }
        } else if (event instanceof TransactionTerminatedEvent) {
            TransactionTerminatedEvent transactionTerminatedEvent = (TransactionTerminatedEvent)event;
            this.type = EventResultType.transactionTerminated;
            this.msg = "事务已结束";
            this.statusCode = -1024;
            if (transactionTerminatedEvent.isServerTransaction()) {
                this.callId = ((SIPRequest)transactionTerminatedEvent.getServerTransaction().getRequest()).getCallIdHeader().getCallId();
                this.dialog = transactionTerminatedEvent.getServerTransaction().getDialog();
            } else {
                this.callId = ((SIPRequest) transactionTerminatedEvent.getClientTransaction().getRequest()).getCallIdHeader().getCallId();
                this.dialog = transactionTerminatedEvent.getClientTransaction().getDialog();
                this.dialog = transactionTerminatedEvent.getClientTransaction().getDialog();
            }
        } else if (event instanceof DialogTerminatedEvent) {
            DialogTerminatedEvent dialogTerminatedEvent = (DialogTerminatedEvent)event;
            this.type = EventResultType.dialogTerminated;
            this.msg = "会话已结束";
            this.statusCode = -1024;
            this.callId = dialogTerminatedEvent.getDialog().getCallId().getCallId();
            this.dialog = dialogTerminatedEvent.getDialog();

        } else if (event instanceof RequestEvent) {
            RequestEvent requestEvent = (RequestEvent) event;
            this.type = EventResultType.ack;
            this.msg = "ack event";
            this.callId = requestEvent.getDialog().getCallId().getCallId();
            this.dialog = requestEvent.getDialog();
        } else if (event instanceof DeviceNotFoundEvent) {
            this.type = EventResultType.deviceNotFoundEvent;
            this.msg = "设备未找到";
            this.statusCode = -1024;
            this.callId = ((DeviceNotFoundEvent)event).getCallId();
        }
    }
}