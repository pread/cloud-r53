package com.amazonaws.services.route53.model;

import java.util.Calendar;

public interface ChangeInfoFluentInterface {
	  public ChangeInfoFluentInterface id(String id);
	  public ChangeInfoFluentInterface status(String status);
	  public ChangeInfoFluentInterface submittedAt(Calendar submittedAt);
	  public ChangeInfo create();
}
