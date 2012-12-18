package net.ion.neo;

public class Credential {

	public static final Credential EMANON = new Credential("emanon", "emanon") ;
	
	private final String accessKey ;
	private final String secretKey ;

	public Credential(String accessKey, String secretKey){
		this.accessKey = accessKey ;
		this.secretKey = secretKey ;
	}
	
	public String accessKey() {
		return accessKey;
	}
	
	public String secretKey(){
		return secretKey ;
	}
	
	
}
