package io.github.lunasaw.sip.common.conf.msg;

import gov.nist.javax.sip.parser.MessageParser;
import gov.nist.javax.sip.parser.MessageParserFactory;
import gov.nist.javax.sip.stack.SIPTransactionStack;

public class StringMsgParserFactory implements MessageParserFactory {

    /**
     * msg parser is completely stateless, reuse isntance for the whole stack
     * fixes https://github.com/RestComm/jain-sip/issues/92
     */
    private static StringMsgParser msgParser = new StringMsgParser();

    /*
     * (non-Javadoc)
     * @see gov.nist.javax.sip.parser.MessageParserFactory#createMessageParser(gov.nist.javax.sip.stack.SIPTransactionStack)
     */
    public MessageParser createMessageParser(SIPTransactionStack stack) {
        return msgParser;
    }
}
