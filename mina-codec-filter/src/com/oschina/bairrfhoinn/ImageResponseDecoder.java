package com.oschina.bairrfhoinn;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

public class ImageResponseDecoder extends CumulativeProtocolDecoder {

	private static final String DECODE_STATE_KEY = ImageResponseDecoder.class.getName() + ".STATE";
	public static final int MAX_IMAGE_SIZE = 5 * 1024 * 1024;
	
	private static class DecoderState{
		BufferedImage image1;
	}
	
	@Override
	protected boolean doDecode(IoSession session, IoBuffer in,
			ProtocolDecoderOutput out) throws Exception {
		DecoderState decoderState = (DecoderState) session.getAttribute(DECODE_STATE_KEY);
		if(null == decoderState){
			decoderState = new DecoderState();
			session.setAttribute(DECODE_STATE_KEY, decoderState);
		}
		if(null == decoderState.image1){
			if(in.prefixedDataAvailable(4, MAX_IMAGE_SIZE)){
				decoderState.image1 = readImage(in);
			}else{
				return false;
			}
		}
		
		if(null != decoderState.image1){
			if(in.prefixedDataAvailable(4, MAX_IMAGE_SIZE)){
				BufferedImage image2 = readImage(in);
				ImageResponse imageRespone = new ImageResponse(decoderState.image1, image2);
				out.write(imageRespone);
				decoderState.image1 = null;
				return true;
			}else{
				return false;
			}
		}
		return false;
	}

	private BufferedImage readImage(IoBuffer in) throws IOException {
		int length = in.getInt();
		byte[] bytes = new byte[length];
		in.get(bytes);
		ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
		return ImageIO.read(bis);
	}
}