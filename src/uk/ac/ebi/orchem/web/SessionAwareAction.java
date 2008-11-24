package uk.ac.ebi.orchem.web;

import com.opensymphony.xwork2.ActionSupport;

import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;

/**
 * Abstract Struts Action class that can be extended by other Action classes that
 * need to be 'session aware'. Are you session aware? I'm not.
 */
public abstract class SessionAwareAction extends ActionSupport implements SessionAware {

    private Map session;

    public void setSession(Map session) {
        this.session = session;
    }
    public Map getSession() {
        return session;
    }

}
