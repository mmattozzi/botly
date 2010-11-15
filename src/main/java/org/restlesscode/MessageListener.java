package org.restlesscode;

import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Created: Apr 29, 2010
 *
 * @author mmattozzi
 */
public interface MessageListener {

    /**
     * @param messageContext
     * @return true to continue handling message, false to finish off message
     */
    public boolean handleMessage(MessageContext messageContext);

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate);
    
    public void doFirstInit();
    
    public void doInit();

    public void setProbability(float probability);

    public float getProbability();

    public boolean getActive();
    
    public void setActive(boolean active);
}
