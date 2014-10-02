package com.clashwars.cwevents.config;

import com.clashwars.cwcore.config.internal.EasyConfig;

public class SqlInfo extends EasyConfig {
	public String	address;
	public String port;
	public String	user;
	public String	pass;
	public String	db;

    public SqlInfo(String fileName) {
        this.setFile(fileName);
    }

	public String getAddress() {
		return address;
	}
	
	public String getPort() {
		return port;
	}

	public String getUser() {
		return user;
	}

	public String getPass() {
		return pass;
	}

	public String getDb() {
		return db;
	}
}
