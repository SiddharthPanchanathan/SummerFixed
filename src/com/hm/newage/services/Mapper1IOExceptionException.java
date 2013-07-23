
/**
 * Mapper1IOExceptionException.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.2  Built on : Apr 17, 2012 (05:33:49 IST)
 */

package com.hm.newage.services;

public class Mapper1IOExceptionException extends java.lang.Exception{

    private static final long serialVersionUID = 1373453050918L;
    
    private com.hm.newage.services.Mapper1Stub.Mapper1IOException faultMessage;

    
        public Mapper1IOExceptionException() {
            super("Mapper1IOExceptionException");
        }

        public Mapper1IOExceptionException(java.lang.String s) {
           super(s);
        }

        public Mapper1IOExceptionException(java.lang.String s, java.lang.Throwable ex) {
          super(s, ex);
        }

        public Mapper1IOExceptionException(java.lang.Throwable cause) {
            super(cause);
        }
    

    public void setFaultMessage(com.hm.newage.services.Mapper1Stub.Mapper1IOException msg){
       faultMessage = msg;
    }
    
    public com.hm.newage.services.Mapper1Stub.Mapper1IOException getFaultMessage(){
       return faultMessage;
    }
}
    