package com.example.core.service;

import com.example.core.entity.ConfigContext;
import org.apache.velocity.VelocityContext;

public interface Callback {
	void write(ConfigContext configContext, VelocityContext context);
}
