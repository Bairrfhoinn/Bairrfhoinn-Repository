/**
 * Copyright @2010 Shanghai MingLuck Co. Ltd. * All right reserved.
 */
package util;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

/**
 * FileName: TestContentMessageEncoder.java Description: This Class used for
 * BDBM
 * 
 * @Company: Shanghai MingLuck Co. Ltd. Developer: David.wang, @Date: 2010-10-13
 *           Modifier: NONE, @Date: NONE
 * @Version: V1.0
 * 
 */
public class ByteContentMessageEncoder extends ProtocolEncoderAdapter {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.mina.filter.codec.ProtocolEncoder#encode(org.apache.mina.core
	 * .session.IoSession, java.lang.Object,
	 * org.apache.mina.filter.codec.ProtocolEncoderOutput)
	 */
	public void encode(IoSession session, Object message,
			ProtocolEncoderOutput out) throws Exception {

		out.write(message);
	}
}
