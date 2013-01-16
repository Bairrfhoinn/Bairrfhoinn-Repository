package com.oschina.bairrfhoinn;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

public class ImageCodecFactory implements ProtocolCodecFactory {
	
	private ProtocolEncoder encoder;
	private ProtocolDecoder decoder;
	
	public ImageCodecFactory(boolean client){
		if(client){
			encoder = new ImageRequestEncoder();
            decoder = new ImageResponseDecoder();
		}else{
			encoder = new ImageResponseEncoder();
            decoder = new ImageRequestDecoder();
		}
	}

	@Override
	public ProtocolEncoder getEncoder(IoSession session) throws Exception {
		return encoder;
	}

	@Override
	public ProtocolDecoder getDecoder(IoSession session) throws Exception {
		return decoder;
	}
}