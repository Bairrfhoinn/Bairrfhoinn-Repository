package com.oschina.bairrfhoinn;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

public class ImageRequestEncoder implements ProtocolEncoder {

	@Override
	public void encode(IoSession session, Object message,
			ProtocolEncoderOutput out) throws Exception {
		ImageRequest request = (ImageRequest) message;
		IoBuffer buffer = IoBuffer.allocate(12, false);
		buffer.putInt(request.getWidth());
		buffer.putInt(request.getHeight());
		buffer.putInt(request.getNumberOfCharacters());
		buffer.flip();
		out.write(buffer);
	}

	@Override
	public void dispose(IoSession session) throws Exception {
		//do nothing here
	}

}
