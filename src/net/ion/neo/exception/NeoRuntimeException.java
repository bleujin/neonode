package net.ion.neo.exception;

import org.apache.ecs.vxml.Throw;

public class NeoRuntimeException extends RuntimeException{

	private static final long serialVersionUID = -1045452247302983608L;
	
	public NeoRuntimeException(Throwable cause){
		super(cause) ;
	}
	
	public final static NeoRuntimeException from(Throwable cause){
		return new NeoRuntimeException(cause) ;
	}
	
	
	public final static NeoRuntimeException throwIt(Throwable cause) throws NeoRuntimeException{
		throw new NeoRuntimeException(cause) ;
	}

}
