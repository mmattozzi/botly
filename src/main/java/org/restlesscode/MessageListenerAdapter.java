package org.restlesscode;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

public abstract class MessageListenerAdapter implements MessageListener {

    protected float probability = 1.0f;
    protected boolean active = true;
    protected SimpleJdbcTemplate simpleJdbcTemplate;

    @Override
    public float getProbability() {
        return probability;
    }

    @Override
    public void setProbability(float probability) {
        this.probability = probability;
    }

    @Override
    public void doFirstInit() {
    }

    @Override
    public void doInit() {
    }

    @Override
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        simpleJdbcTemplate = new SimpleJdbcTemplate(jdbcTemplate);
    }

    @Override
    public boolean getActive() {
        return active;
    }

    @Override
    public void setActive(boolean active) {
        this.active = active;
    }
}
