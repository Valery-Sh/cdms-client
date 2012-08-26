/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cdms.ui.common;

import org.cdms.shared.remoting.exception.RemoteConnectionException;
import org.cdms.shared.remoting.exception.RemoteDataAccessException;
import org.cdms.shared.remoting.exception.RemoteValidationException;
import org.cdms.shared.remoting.validation.RemoteConstraintViolation;
import org.openide.util.NbBundle;

/**
 *
 * @author Valery
 */
public class ErrorMessageBuilder {

    public static String get(Exception e) {
        String m;
        if (e instanceof RemoteConnectionException) {
            m = buildConnectionMessageFor(e);
        } else if (e instanceof RemoteValidationException) {
            return buildValidationMessageFor(e);
        } else if (e instanceof RemoteDataAccessException) {
            m = buildDataAccessMessageFor(e);
        } else {
            m = buildOtherMessageFor(e);
        }

        return m;

    }

    protected static String buildValidationMessageFor(Exception exception) {
        RemoteValidationException e = (RemoteValidationException) exception;
        String m = "";
        if (e.getViolations() == null || e.getViolations().isEmpty()) {
            m = ""; //TODO
            return m;
        }
        for (RemoteConstraintViolation v : e.getViolations()) {
            if (v.getPropertyPath() != null) {
                m += "'" + v.getPropertyPath() + "': ";
            }
            switch (v.getAnnotationCode()) {
                case RemoteConstraintViolation.MAX:
                case RemoteConstraintViolation.MIN:
                case RemoteConstraintViolation.SIZE:
                    m += NbBundle.getMessage(ErrorMessageBuilder.class, "ErrorMessageBuilder.RemoteValidationException.SIZE"); // NOI             
                    if (v.getSizeExpression() != null) {
                        m += v.getSizeExpression();
                    }
                    break;
                case RemoteConstraintViolation.DIGITS:
                    m += NbBundle.getMessage(ErrorMessageBuilder.class, "ErrorMessageBuilder.RemoteValidationException.DIGITS");
                    break;
                case RemoteConstraintViolation.NOTNULL:
                    m += NbBundle.getMessage(ErrorMessageBuilder.class, "ErrorMessageBuilder.RemoteValidationException.NOTNULL");
                    break;
                case RemoteConstraintViolation.PATTERN:
                    m += NbBundle.getMessage(ErrorMessageBuilder.class, "ErrorMessageBuilder.RemoteValidationException.PATTERN");
                    break;
                case RemoteConstraintViolation.OTHER:
                    m += NbBundle.getMessage(ErrorMessageBuilder.class, "ErrorMessageBuilder.RemoteValidationException.OTHER");
                    break;
            }//switch

        }

        return m;
    }

    protected static String buildConnectionMessageFor(Exception exception) {
        RemoteConnectionException e = (RemoteConnectionException) exception;
        String m = NbBundle.getMessage(ErrorMessageBuilder.class, "ErrorMessageBuilder.RemoteConnection.Refused"); // NOI             
        return m;
    }

    protected static String buildOtherMessageFor(Exception exception) {
        String m = NbBundle.getMessage(ErrorMessageBuilder.class, "ErrorMessageBuilder.Exception.Other"); // NOI             
        return m;
    }

    protected static String buildDataAccessMessageFor(Exception exception) {
        RemoteDataAccessException e = (RemoteDataAccessException) exception;
        String m = NbBundle.getMessage(ErrorMessageBuilder.class, "ErrorMessageBuilder.Exception.Other"); // NOI                     
        switch (e.getErrorCode()) {
            case RemoteDataAccessException.OPTIMISTIC_LOCKING:
                m = NbBundle.getMessage(ErrorMessageBuilder.class, "ErrorMessageBuilder.RemoteDataAccessException.OPTIMISTIC_LOCKING");
                break;
            case RemoteDataAccessException.OBJECT_RETRIEVAL:
                m = NbBundle.getMessage(ErrorMessageBuilder.class, "ErrorMessageBuilder.RemoteDataAccessException.OBJECT_RETRIEVAL");
                m += " " + e.getIdentifier();
                break;
            case RemoteDataAccessException.OBJECT_RETRIEVAL_DELETE:
                m = NbBundle.getMessage(ErrorMessageBuilder.class, "ErrorMessageBuilder.RemoteDataAccessException.OBJECT_RETRIEVAL_DELETE");
                String id = " " + e.getIdentifier();
                if ( m.contains("iiiiii")) { 
                    m = m.replace("iiiiii", id);
                } else {
                    m += id;
                }
                break;
                
            case RemoteDataAccessException.QUERY:
                m = NbBundle.getMessage(ErrorMessageBuilder.class, "ErrorMessageBuilder.RemoteDataAccessException.QUERY");
                break;
            case RemoteDataAccessException.SYSTEM:
                m = NbBundle.getMessage(ErrorMessageBuilder.class, "ErrorMessageBuilder.RemoteDataAccessException.SYSTEM");
                break;
            case RemoteDataAccessException.JDBC:
                m = NbBundle.getMessage(ErrorMessageBuilder.class, "ErrorMessageBuilder.RemoteDataAccessException.JDBC");
                break;

        }
        return m;
    }
}
