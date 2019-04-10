package com.example.demo.dl;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

/**
 * Represents a message entity from database connected to the program.
 * 
 * @author serhii.shvets
 *
 */
@Entity
@Table(name = "messages")

@Getter
@Setter
public class MessageEntity {

	@Id
	private Long m_id;

	@Size(min = 0, max = 100)
	private String m_text;

	private Long gr_id;

	private LocalDateTime m_date;

	/**
	 * Setter for m_text variable of MessagEntity object.
	 * 
	 * @param text - the value of a m_text variable.
	 */
	public void setText(String text) {
		this.m_text = (text.length() >= 3) ? text : "Void message";
	}

	/**
	 * Getter for m_id variable, which is an ID of message entity.
	 * 
	 * @return ID of message entity as a variable of a Long type.
	 */
	public Long getMesId() {
		return this.m_id;
	}

	/**
	 * Setter for m_id variable of MessageENtity object.
	 * 
	 * @param id - value of a m_id variable, requires a Long type value.
	 */
	public void setMesId(Long id) {
		this.m_id = id;
	}

	/**
	 * Getter for m_text variable.
	 *
	 * @return m_text as a String type value.
	 */
	public String getText() {
		return this.m_text;
	}

	/**
	 * Getter for m_date variable.
	 * 
	 * @return m_date as a LocalDateTime type value.
	 */
	public LocalDateTime getDate() {
		return this.m_date;
	}

	/**
	 * Setter for m_date variable. No input value required, it will set a present
	 * date.
	 * 
	 */
	public void setDate() {
		this.m_date = LocalDateTime.now().withHour(0).withMinute(0).withNano(0).withSecond(0);
	}

	/**
	 * Setter for a gr_id variable.
	 * 
	 * @param grId - value of gr_id variable, requires a Long type value.
	 */
	public void setGrId(Long grId) {
		this.gr_id = grId;

	}

	/**
	 * Getter for a gr_id variable.
	 * 
	 * @return gr_id value of MessageEntity object as a Long type variable.
	 */
	public Long getGrId() {
		return this.gr_id;
	}

}
