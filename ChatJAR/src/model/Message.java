package model;

import java.io.Serializable;
import java.util.Date;

public class Message implements Serializable{

	private User sender;
	private User reciver;
	private Date date;
	private String subject;
	private String content;
	
	public Message() {
		super();
		this.date = new Date();
	}
	
	public Message(User sender, User reciver, String subject, String content) {
		super();
		this.sender = sender;
		this.reciver = reciver;
		this.date = new Date();
		this.subject = subject;
		this.content = content;
	}
	
	public Message(User sender, User reciver, Date date, String subject, String content) {
		super();
		this.sender = sender;
		this.reciver = reciver;
		this.date = date;
		this.subject = subject;
		this.content = content;
	}

	public User getSender() {
		return sender;
	}

	public void setSender(User sender) {
		this.sender = sender;
	}

	public User getReciver() {
		return reciver;
	}

	public void setReciver(User reciver) {
		this.reciver = reciver;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	@Override
	public String toString() {
		return "Message[sender: " + sender + ", reciver: " + reciver + ", content: " + content + "]";
	}
}
