package com.thenexusreborn.nexuscore.util.updater;

public class UpdateException extends Exception {
	public UpdateException(){
		super();
	}

	public UpdateException(String message){
		super(message);
	}

	public UpdateException(String message, Throwable cause){
		super(message, cause);
	}

	public UpdateException(Throwable cause){
		super(cause);
	}

	protected UpdateException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace){
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
