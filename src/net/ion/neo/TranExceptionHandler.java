package net.ion.neo;

public interface TranExceptionHandler {

	public TranExceptionHandler PRINT = new TranExceptionHandler(){

		@Override
		public void handle(WriteSession tsession, Throwable ex) {
			ex.printStackTrace() ;
		}} ;
	
	public void handle(WriteSession tsession, Throwable ex) ;

}
