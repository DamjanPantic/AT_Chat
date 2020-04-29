package model;

import java.io.Serializable;

public interface Agent extends Serializable{

	public void handleMessage(Message message);
}
