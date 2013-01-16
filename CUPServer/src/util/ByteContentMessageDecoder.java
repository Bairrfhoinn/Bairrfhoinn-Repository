/**
 * Copyright @2010 Shanghai MingLuck Co. Ltd. * All right reserved.
 */
package util;

import org.apache.log4j.Logger;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

/**
 * FileName: TextContentMessageDecoder.java Description: This Class used for
 * BDBM
 * 
 * @Company: Shanghai MingLuck Co. Ltd. Developer: David.wang,
 * @Date: 2010-10-12 Modifier: NONE,
 * @Date: NONE
 * @Version: V1.0
 * 
 */
public class ByteContentMessageDecoder extends CumulativeProtocolDecoder {

	static Logger log = Logger.getLogger(ByteContentMessageDecoder.class);
	private final String CONTEXT = ByteContentMessageDecoder.class.toString();

	/**
	 * Creates a new instance with the specified <tt>charset</tt> and the
	 * specified <tt>delimiter</tt>.
	 */
	public ByteContentMessageDecoder() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.mina.filter.codec.CumulativeProtocolDecoder#
	 * doDecode(org.apache.mina.core.session.IoSession,
	 * org.apache.mina.core.buffer.IoBuffer,
	 * org.apache.mina.filter.codec.ProtocolDecoderOutput) Return true if and
	 * only if there's more to decode in the buffer and you want to have
	 * doDecode method invoked again. Return false if remaining data is not
	 * enough to decode, then this method will be invoked again when more data
	 * is cumulated.
	 */
	@Override
	protected boolean doDecode(IoSession session, IoBuffer in,
			ProtocolDecoderOutput out) throws Exception {
		MinaByteBean bean = getContext(session);
		ByteContext ctx = bean.getContext();
		ctx.append(in);// 把当前buffer中的数据追加到Context的buffer当中

		byte[] ctxByte = BytesUtils.writeByte(ctx.getBuffer(), out, bean);
		if (ctxByte == null || ctxByte.length == 0) {
			ctx.reset();
			session.setAttribute(CONTEXT, null);
			session.removeAttribute(CONTEXT);
			return false;
		} else {
			IoBuffer ioBuffer = IoBuffer.allocate(ctxByte.length);
			ioBuffer.put(ctxByte, 0, ctxByte.length);
			ioBuffer.flip();
			ctx.reset();
			ctx.append(ioBuffer);
			bean.setContext(ctx);
			session.setAttribute(CONTEXT, bean);
		}
		return true;

	}

	/**
	 * @param session
	 * @return
	 */
	private MinaByteBean getContext(IoSession session) {
		MinaByteBean bean = (MinaByteBean) session.getAttribute(CONTEXT);
		if (bean == null) {
			bean = new MinaByteBean();
			ByteContext ctx = new ByteContext();
			bean.setContext(ctx);
			session.setAttribute(CONTEXT, bean);
		}
		return bean;
	}
}
