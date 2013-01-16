package com.oschina.bairrfhoinn;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

public class ImageResponseEncoder extends ProtocolEncoderAdapter {

	@Override
	public void encode(IoSession session, Object message,
			ProtocolEncoderOutput out) throws Exception {
		ImageResponse imageResponse = (ImageResponse) message;
		byte[] byte1 = getBytes(imageResponse.getImage1());
		byte[] byte2 = getBytes(imageResponse.getImage2());
		int capacity = byte1.length + byte2.length + 8;
		IoBuffer buffer = IoBuffer.allocate(capacity, false);
		buffer.setAutoExpand(true);
		buffer.putInt(byte1.length);
		buffer.put(byte1);
		buffer.putInt(byte2.length);
		buffer.put(byte2);
		buffer.flip();
		out.write(buffer);
	}

	private byte[] getBytes(BufferedImage image1) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ImageIO.write(image1, "JPG", bos);
		return bos.toByteArray();
	}

}
