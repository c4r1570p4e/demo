package org.cl.demo.mbean;

import java.lang.management.ManagementFactory;

import javax.annotation.PostConstruct;
import javax.management.MBeanServer;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.management.ManagementService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

//@Component
public class MBeanInit {

	private final Logger logger = LoggerFactory.getLogger(MBeanInit.class); 	
	
	//@PostConstruct
	public void init()
	{
		logger.info("init MBeanInit");
		
		CacheManager manager = CacheManager.getInstance();
		MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
		ManagementService.registerMBeans(manager, mBeanServer, false, false, false, true);		
	}
	
}
