/**
 * Copyright @2010 Shanghai MingLuck Co. Ltd. * All right reserved.
 */
package util;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

/**
 * FileName: TextContentCodecFactory.java Description: This Class used for BDBM
 * 
 * @Company: Shanghai MingLuck Co. Ltd. Developer: David.wang, @Date: 2010-10-12
 *           Modifier: NONE, @Date: NONE
 * @Version: V1.0
 * 
 */
public class ByteContentCodecFactory implements ProtocolCodecFactory {
	private ProtocolEncoder encoder;
	private ProtocolDecoder decoder;

	public ByteContentCodecFactory() {
		decoder = new ByteContentMessageDecoder();
		encoder = new ByteContentMessageEncoder();
	}

	public ProtocolDecoder getDecoder(IoSession session) throws Exception {
		return decoder;
	}

	public ProtocolEncoder getEncoder(IoSession session) throws Exception {
		return encoder;
	}

}
